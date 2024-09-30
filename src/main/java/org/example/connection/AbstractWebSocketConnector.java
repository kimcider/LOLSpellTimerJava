package org.example.connection;

import lombok.Getter;
import lombok.Setter;
import org.example.Liner;
import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.Map;

@Getter
@Setter
public abstract class AbstractWebSocketConnector extends WebSocketClient {
    private HttpClient client;
    private String serverURI;
    protected Map<String, Liner> linerList;  //얘가 여기에 있는것도 이상하네..

    public AbstractWebSocketConnector(URI serverUri) {
        super(serverUri);
    }
}
