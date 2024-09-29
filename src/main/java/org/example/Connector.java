package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;


@Getter
@Setter
public class Connector extends WebSocketClient {
    Map<String, Liner> linerList;

    static ObjectMapper mapper = new ObjectMapper();
    private static String serverURI = "ec2-3-36-116-203.ap-northeast-2.compute.amazonaws.com:8080";

    public Connector() throws InterruptedException, URISyntaxException {
        super(new URI("ws://" + serverURI + "/ws"));
        connectBlocking();
    }


    public void setLinerList(Map<String, Liner> linerList) {
        this.linerList = linerList;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        //TODO: open시 서버의 정보를 가져와서 업데이트
        System.out.println("Connected to WebSocket server");
    }

    @Override
    public void onMessage(String json) {
        try {
            List<Liner> liners = mapper.readValue(json, new TypeReference<List<Liner>>() {});
            for (Liner it : liners) {
                Liner clientLiner = linerList.get(it.name);
                if (clientLiner.flash.on != it.flash.on) {
                    // TODO: LinerList와 LineList가 따로 존재하는 문제 해결 요망.
                    // Liner는 Line의 하위 객체이다.
                    // 그런데 여기는 Liner의 아래에 Line이 있는 것 처럼 사용하고 있다.
                    // 두개의 리스트가 따로 있어서 생기는 문제.
                    startCountFlash(linerList.get(it.name));
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

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static void sendPostRequest(String message) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + serverURI + "/test"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"message\":\"" + message + "\"}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    }

    public void useFlash(Liner liner) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String json = mapper.writeValueAsString(liner);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + serverURI + "/useFlash"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void flashOn(Liner liner) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String json = mapper.writeValueAsString(liner);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + serverURI + "/useFlash"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void startCountFlash(Liner liner) {
        if (liner.flash.on) {
            liner.flash.on = false;
            liner.flash.coolTime = liner.flash.flashCoolTime;

            liner.flashIcon.repaint();
            if (liner.flashIcon.timer != null) {
                liner.flashIcon.timer.stop();
            }

            liner.flashIcon.timer = new Timer(1000, e -> {
                liner.flash.coolTime -= 1;
                liner.flashIcon.repaint();

                if (liner.flash.coolTime <= 0) {
                    liner.flash.on = true;
                    try {
                        flashOn(liner);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    if (liner.flashIcon.timer != null) {
                        liner.flashIcon.timer.stop();
                    }
                }

            });

            liner.flashIcon.timer.start();
        } else {
            liner.flash.on = true;
            liner.flashIcon.repaint();
            liner.flashIcon.timer.stop();
        }
    }
}
