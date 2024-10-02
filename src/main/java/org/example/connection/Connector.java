package org.example.connection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.example.Liner;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.List;


@Getter
@Setter
public class Connector extends AbstractWebSocketConnector {
    private static Connector connector;
        private static String serverURI = "localhost:8080";
//    private static String serverURI = "ec2-3-36-116-203.ap-northeast-2.compute.amazonaws.com:8080";

    private static ObjectMapper mapper = new ObjectMapper();

    private Connector() throws InterruptedException, URISyntaxException {
        super(new URI("ws://" + serverURI + "/ws"));
        super.setServerURI(serverURI);
        super.setClient(HttpClient.newHttpClient());
        connectBlocking();
    }

    public static Connector getInstance() {
        if (connector == null) {
            try {
                connector = new Connector();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        return connector;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        //TODO: open시 서버의 정보를 가져와서 업데이트
        System.out.println("Connected to WebSocket server");
    }

    @Override
    public void onMessage(String json) {
        if("getAllLinerStatus".equals(json)){
            connector.sendMessage("getAllLinerStatus", "test");

            return;
        }

        try {
            List<Liner> liners = mapper.readValue(json, new TypeReference<List<Liner>>() {
            });
            for (Liner it : liners) {
                Liner clientLiner = linerList.get(it.getName());
                if (clientLiner.getFlash().isOn() != it.getFlash().isOn()) {
                    linerList.get(it.getName()).useFlash();
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
}
