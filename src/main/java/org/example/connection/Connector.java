package org.example.connection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.example.liner.Liner;
import org.example.liner.spell.Spell;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.List;


@Getter
@Setter
public class Connector extends AbstractWebSocketConnector {
    private static Connector connector;
    private static String serverURI;

    private static ObjectMapper mapper = new ObjectMapper();

    public Connector(String serverURI, String hashValue) throws InterruptedException, URISyntaxException {
        super(new URI("ws://" + serverURI + "/ws"));
        this.serverURI = serverURI;
        super.setServerURI(serverURI);
        super.setHashValue(hashValue);
        super.setClient(HttpClient.newHttpClient());
        connectBlocking();

        connector = this;
    }

    public static Connector getInstance() {
        return connector;
    }

    @Override
    public void onMessage(String json) {
        try {
            JsonNode rootNode = mapper.readTree(json);

            String method = rootNode.get("method").asText();

            if("sendLinerStatus".equals(method)){
                JsonNode dataNode = rootNode.get("data");

                String dataJson = mapper.writeValueAsString(dataNode);

                Liner serverLiner = mapper.readValue(dataJson, new TypeReference<Liner>() {
                });
                Liner clientLiner = linerList.get(serverLiner.getName());
                Spell clientFlash = clientLiner.getFlash();
                if (clientLiner.equals(serverLiner) == false) {
                    clientLiner.setLiner(serverLiner);
                    //TODO: 얘가 여기있는게 맞나?
                    if (clientFlash.isOn()) {
                        clientFlash.stopCount();
                    } else {
                        clientFlash.startCount(clientLiner);
                    }
                }
            }else if("getLinerStatus".equals(method)){
                String linerListJson = mapper.writeValueAsString(getLinerList().values().toArray());
                send(wrapMethodJson("getLinerStatusResponse", linerListJson));
            }else if("getLinerStatusResponse".equals(method)){
                JsonNode dataNode = rootNode.get("data");
                String dataJson = mapper.writeValueAsString(dataNode);
                List<Liner> liners = mapper.readValue(dataJson, new TypeReference<List<Liner>>() {
                });
                for(Liner liner : liners){
                    linerList.get(liner.getName()).setLiner(liner);
                    linerList.get(liner.getName()).getFlash().startCount(linerList.get(liner.getName()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  // 예외 처리
        }
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from WebSocket server");
    }
}
