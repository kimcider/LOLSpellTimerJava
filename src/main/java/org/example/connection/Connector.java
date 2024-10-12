package org.example.connection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.example.liner.Liner;
import org.example.liner.spell.Spell;
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
        if (connector == null) {
            try {
                connector = new Connector("localhost:8080", "hashValue");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        return connector;
    }

    @Override
    public void onMessage(String json) {
        try {
            Liner serverLiner = mapper.readValue(json, new TypeReference<Liner>() {});
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
        } catch (IOException e) {
            e.printStackTrace();  // 예외 처리
        }
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from WebSocket server");
    }
}
