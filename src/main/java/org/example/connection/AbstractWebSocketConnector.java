package org.example.connection;

import lombok.Getter;
import lombok.Setter;
import org.example.Liner;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.Map;

@Getter
@Setter
public abstract class AbstractWebSocketConnector  extends WebSocketClient {
    public HttpClient client;
    public String serverURI;
    public Map<String, Liner> linerList;  //얘가 여기에 있는것도 이상하네..
    public AbstractWebSocketConnector (URI serverUri) {
        super(serverUri);
    }
}
