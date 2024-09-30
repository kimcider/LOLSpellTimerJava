package org.example.connection;

import lombok.Getter;
import lombok.Setter;
import org.example.Liner;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;

@Getter
@Setter
public abstract class AbstractWebSocketConnector  extends WebSocketClient {
    public HttpClient client;
    public String serverURI;
//    public static String serverURI = "ec2-3-36-116-203.ap-northeast-2.compute.amazonaws.com:8080";
//    public HttpClient client = HttpClient.newHttpClient();

    public AbstractWebSocketConnector (URI serverUri) {
        super(serverUri);
    }
}
