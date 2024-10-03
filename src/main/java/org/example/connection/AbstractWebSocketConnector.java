package org.example.connection;

import lombok.Getter;
import lombok.Setter;
import org.example.Liner;
import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Getter
@Setter
public abstract class AbstractWebSocketConnector extends WebSocketClient {
    private HttpClient client;
    private String serverURI;
    protected Map<String, Liner> linerList;  // 테스트를 위해서 Connector에 linerList를 설정하려고 여기다뒀네... 애매하다

    public AbstractWebSocketConnector(URI serverUri) {
        super(serverUri);
    }


    public HttpResponse<String> sendMessage(String api, String json) {
        //System.out.println("toServer: " + json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + getServerURI() + "/" + api))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
