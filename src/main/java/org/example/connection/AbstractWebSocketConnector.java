package org.example.connection;

import org.example.Liner;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;

public abstract class AbstractWebSocketConnector  extends WebSocketClient {
    public AbstractWebSocketConnector (URI serverUri) {
        super(serverUri);
    }
    public abstract void useFlash(Liner liner) throws IOException, InterruptedException;
    public abstract void flashOn(Liner liner) throws IOException, InterruptedException;
}
