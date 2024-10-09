package org.example.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.example.liner.Liner;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Getter
@Setter
public abstract class AbstractWebSocketConnector extends WebSocketClient {
    private static ObjectMapper mapper = new ObjectMapper();
    private HttpClient client;
    private String serverURI;
    protected Map<String, Liner> linerList;  // 테스트를 위해서 Connector에 linerList를 설정하려고 여기다뒀네... 애매하다
    static String hashValue;

    public AbstractWebSocketConnector(URI serverUri) {
        super(serverUri);
    }


    public HttpResponse<String> sendMessage(String api, String json) {
        String hashedJson = wrapJson(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + getServerURI() + "/" + api))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(hashedJson))
                .build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshake){
        send("hashValue");
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    static String wrapJson(String json) {
        ObjectNode wrappedData = mapper.createObjectNode();
        try{
            wrappedData.put("hash", hashValue);

            JsonNode jsonNode = mapper.readTree(json);
            wrappedData.set("data", jsonNode);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return wrappedData.toString();
    }
}
