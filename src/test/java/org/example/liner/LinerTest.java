package org.example.liner;

import org.example.Connector;
import org.example.Liner;
import org.example.connection.AbstractWebSocketConnector;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class LinerTest {
    @Mock
    private AbstractWebSocketConnector mocSocketClient;

    @Test
    public void createLinerTest1(){
        Liner liner = new Liner();
        assertNotNull(liner.getConnector());
        assertNull(liner.lineIcon);
        assertNull(liner.flashIcon);
        assertNull(liner.name);
        assertNotNull(liner.flash);
    }

    @Test
    public void createLinerTest2() throws URISyntaxException {
        Liner liner = new Liner("top", new AbstractWebSocketConnector(new URI("tempUrl")) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {

            }

            @Override
            public void onMessage(String s) {

            }

            @Override
            public void onClose(int i, String s, boolean b) {

            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void useFlash(Liner liner) throws IOException, InterruptedException {

            }

            @Override
            public void flashOn(Liner liner) throws IOException, InterruptedException {

            }
        });
        assertNotNull(liner.getConnector());
        assertNotEquals(liner.getConnector(), Connector.getInstance());
        assertNotNull(liner.lineIcon);
        assertNotNull(liner.flashIcon);
        assertNotNull(liner.name);
        assertNotNull(liner.flash);
    }

    @Test
    public void startCount() throws URISyntaxException {
        Liner mockLiner = Mockito.spy(new Liner("top", new AbstractWebSocketConnector(new URI("tempUrl")) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {

            }

            @Override
            public void onMessage(String s) {

            }

            @Override
            public void onClose(int i, String s, boolean b) {

            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void useFlash(Liner liner) throws IOException, InterruptedException {

            }

            @Override
            public void flashOn(Liner liner) throws IOException, InterruptedException {

            }
        }));

    }
}
