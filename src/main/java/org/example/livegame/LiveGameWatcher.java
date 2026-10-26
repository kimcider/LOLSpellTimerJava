package org.example.livegame;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.display.Board;
import org.example.liner.Liner;
import org.example.liner.spell.Spell;
import org.example.liner.spell.impl.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.*;
import java.io.InputStream;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 롤 클라이언트가 게임 중에 여는 로컬 API(Live Client Data API)를 주기적으로 읽어서
 * 상대 5명의 스펠, 장화, 10분 강화 순간이동을 오버레이에 자동으로 반영한다.
 *
 * 여기서 반영한 값은 서버로 보내지 않는다. 같은 게임의 모든 PC가 같은 데이터를 직접 읽기 때문이다.
 * 서버로는 지금처럼 사람이 누른 것(스펠 사용, 우통)만 오간다.
 */
public class LiveGameWatcher {
    private static final String ENDPOINT = "https://127.0.0.1:2999/liveclientdata/allgamedata";
    private static final int POLL_SECONDS = 2;
    private static final int UNLEASHED_TELEPORT_SECONDS = 600;

    private static final int IONIAN_BOOTS = 3158;
    private static final int CRIMSON_LUCIDITY = 3171;
    private static final int INSPIRATION = 8300;

    // 라인 순서. API의 position 값과 같은 순서로 맞춘다.
    private static final List<String> LINES = List.of("top", "jg", "mid", "bot", "sup");
    private static final List<String> POSITIONS = List.of("TOP", "JUNGLE", "MIDDLE", "BOTTOM", "UTILITY");

    private final ObjectMapper mapper = new ObjectMapper();
    private final SSLContext sslContext = trustAllContext();

    // 라인별로 마지막에 자동 적용한 스펠. 값이 바뀔 때만 적용해서 2초마다 보드를 다시 그리지 않는다.
    private final Map<String, List<Class<? extends Spell>>> appliedSpells = new HashMap<>();

    public static void start() {
        LiveGameWatcher watcher = new LiveGameWatcher();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "LiveGameWatcher");
            thread.setDaemon(true);
            return thread;
        });
        executor.scheduleWithFixedDelay(watcher::poll, 0, POLL_SECONDS, TimeUnit.SECONDS);
    }

    private void poll() {
        JsonNode game;
        try {
            game = fetch();
        } catch (Exception e) {
            // 게임 중이 아니면 연결이 거부된다. 다음 게임에서 처음부터 다시 적용하도록 기록을 지우고,
            // 룬을 모르는 상태이므로 우통 토글은 다시 모두 보여준다.
            SwingUtilities.invokeLater(() -> {
                appliedSpells.clear();
                Board.linerList.values().forEach(liner -> liner.applyGameInspiration(true));
            });
            return;
        }

        try {
            Map<String, JsonNode> enemiesByLine = assignLines(findEnemies(game));
            double gameTime = game.path("gameData").path("gameTime").asDouble();
            SwingUtilities.invokeLater(() -> apply(enemiesByLine, gameTime));
        } catch (Exception e) {
            // 로딩 화면처럼 데이터가 아직 덜 채워진 순간에는 다음 주기에 다시 시도한다
            e.printStackTrace();
        }
    }

    private JsonNode fetch() throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) URI.create(ENDPOINT).toURL().openConnection();
        // 롤 클라이언트의 인증서는 자체 서명이라 이 로컬 연결에서만 검증을 끈다
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setHostnameVerifier((host, session) -> true);
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(2000);
        try (InputStream in = connection.getInputStream()) {
            return mapper.readTree(in);
        }
    }

    private List<JsonNode> findEnemies(JsonNode game) {
        JsonNode activePlayer = game.path("activePlayer");
        String myRiotId = activePlayer.path("riotId").asText("");
        String mySummonerName = activePlayer.path("summonerName").asText("");

        String myTeam = null;
        for (JsonNode player : game.path("allPlayers")) {
            String riotId = player.path("riotId").asText("");
            String summonerName = player.path("summonerName").asText("");
            if ((!myRiotId.isEmpty() && myRiotId.equals(riotId))
                    || (!mySummonerName.isEmpty() && mySummonerName.equals(summonerName))) {
                myTeam = player.path("team").asText();
                break;
            }
        }
        if (myTeam == null) {
            throw new IllegalStateException("내 플레이어를 찾지 못함");
        }

        List<JsonNode> enemies = new ArrayList<>();
        for (JsonNode player : game.path("allPlayers")) {
            if (!myTeam.equals(player.path("team").asText())) {
                enemies.add(player);
            }
        }
        return enemies;
    }

    /**
     * 상대를 라인에 배치한다. 같은 게임의 모든 PC가 같은 결과를 내야 방 공유가 맞으므로 API 순서만으로 결정한다.
     * 1. position 값이 있으면 그 라인
     * 2. 남은 사람 중 강타를 든 사람은 정글
     * 3. 나머지는 API 순서대로 빈 라인
     */
    static Map<String, JsonNode> assignLines(List<JsonNode> enemies) {
        Map<String, JsonNode> result = new HashMap<>();
        List<JsonNode> unassigned = new ArrayList<>();

        for (JsonNode enemy : enemies) {
            int index = POSITIONS.indexOf(enemy.path("position").asText(""));
            if (index >= 0 && !result.containsKey(LINES.get(index))) {
                result.put(LINES.get(index), enemy);
            } else {
                unassigned.add(enemy);
            }
        }

        if (!result.containsKey("jg")) {
            for (JsonNode enemy : unassigned) {
                if (hasSmite(enemy)) {
                    result.put("jg", enemy);
                    unassigned.remove(enemy);
                    break;
                }
            }
        }

        Iterator<JsonNode> rest = unassigned.iterator();
        for (String line : LINES) {
            if (!result.containsKey(line) && rest.hasNext()) {
                result.put(line, rest.next());
            }
        }
        return result;
    }

    private void apply(Map<String, JsonNode> enemiesByLine, double gameTime) {
        for (Map.Entry<String, JsonNode> entry : enemiesByLine.entrySet()) {
            Liner liner = Board.linerList.get(entry.getKey());
            if (liner == null) {
                continue;
            }
            JsonNode enemy = entry.getValue();

            Class<? extends Spell> spellOne = toSpell(enemy.path("summonerSpells").path("summonerSpellOne"), gameTime);
            Class<? extends Spell> spellTwo = toSpell(enemy.path("summonerSpells").path("summonerSpellTwo"), gameTime);
            // 점멸은 D/F 어느 키에 들었든 항상 왼쪽(spell1)에 둔다
            List<Class<? extends Spell>> spells = spellTwo == Flash.class
                    ? List.of(spellTwo, spellOne)
                    : List.of(spellOne, spellTwo);
            if (!spells.equals(appliedSpells.get(entry.getKey()))) {
                liner.applyGameSpells(spells.get(0), spells.get(1));
                appliedSpells.put(entry.getKey(), spells);
            }

            // 장화는 게임 데이터가 정답이므로 매번 맞춘다. 다른 PC에서 온 메시지가 잠깐 덮어써도 다음 주기에 돌아온다.
            liner.applyGameBoots(bootsHaste(enemy));
            liner.applyGameInspiration(hasInspiration(enemy));
        }
    }

    private static boolean hasSmite(JsonNode enemy) {
        JsonNode spells = enemy.path("summonerSpells");
        return spellKey(spells.path("summonerSpellOne")).contains("Smite")
                || spellKey(spells.path("summonerSpellTwo")).contains("Smite");
    }

    /** "GeneratedTip_SummonerSpell_SummonerFlash_DisplayName" → "SummonerFlash" */
    private static String spellKey(JsonNode spell) {
        String raw = spell.path("rawDisplayName").asText("");
        return raw.replace("GeneratedTip_SummonerSpell_", "").replace("_DisplayName", "");
    }

    private static Class<? extends Spell> toSpell(JsonNode spell, double gameTime) {
        String key = spellKey(spell);
        if (key.contains("TeleportUpgrade")) {
            return UpgradeTeleport.class;
        }
        if (key.contains("Teleport")) {
            // 10분이 지나도 API의 스펠 이름이 바뀌는지 확인되지 않아서 게임 시간으로도 판단한다
            return gameTime >= UNLEASHED_TELEPORT_SECONDS ? UpgradeTeleport.class : Teleport.class;
        }
        if (key.contains("Smite")) {
            return Smite.class;
        }
        switch (key) {
            case "SummonerFlash": return Flash.class;
            case "SummonerDot": return Ignite.class;
            case "SummonerBarrier": return Barrier.class;
            case "SummonerExhaust": return Exhaustion.class;
            case "SummonerHaste": return Ghost.class;
            case "SummonerHeal": return Heal.class;
            case "SummonerBoost": return Cleanse.class;
            default: return NoSpell.class;
        }
    }

    /** 주 트리나 보조 트리가 영감(8300)인지. 룬 정보가 없으면 판단할 수 없으므로 true. */
    private static boolean hasInspiration(JsonNode enemy) {
        JsonNode runes = enemy.path("runes");
        if (runes.isMissingNode()) {
            return true;
        }
        return runes.path("primaryRuneTree").path("id").asInt() == INSPIRATION
                || runes.path("secondaryRuneTree").path("id").asInt() == INSPIRATION;
    }

    private static int bootsHaste(JsonNode enemy) {
        int haste = 0;
        for (JsonNode item : enemy.path("items")) {
            int id = item.path("itemID").asInt();
            if (id == CRIMSON_LUCIDITY) {
                haste = 20;
            } else if (id == IONIAN_BOOTS && haste == 0) {
                haste = 10;
            }
        }
        return haste;
    }

    private static SSLContext trustAllContext() {
        try {
            TrustManager trustAll = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{trustAll}, null);
            return context;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
