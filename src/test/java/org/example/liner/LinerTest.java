package org.example.liner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CounterLabel;
import org.example.Liner;
import org.example.connection.AbstractWebSocketConnector;
import org.example.connection.Connector;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class LinerTest {
    static ObjectMapper mapper = new ObjectMapper();

    @Mock
    private AbstractWebSocketConnector mocSocketClient;

    @Test
    public void createLinerTest1() {
        Liner liner = new Liner();
        assertNotNull(liner.getConnector());
        assertNull(liner.getLineIcon());
        assertNull(liner.getFlashIcon());
        assertNull(liner.getName());
        assertNotNull(liner.getFlash());
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
        });
        assertNotNull(liner.getConnector());
        assertNotEquals(liner.getConnector(), Connector.getInstance());
        assertNotNull(liner.getLineIcon());
        assertNotNull(liner.getFlashIcon());
        assertNotNull(liner.getName());
        assertNotNull(liner.getFlash());
    }


    @Test
    public void writeLinerAsString() throws JsonProcessingException {
        Connector mockConnector = Mockito.mock(Connector.class);
        Liner liner = new Liner("top", mockConnector);
        String json = mapper.writeValueAsString(liner);
        assertEquals("""
                {"name":"top","flash":{"coolTime":300,"on":true}}""", json);
    }


    @Test
    public void startCount() throws URISyntaxException {
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
        });
        // CounterLabel mock 생성
        CounterLabel mockCounterLabel = Mockito.spy(liner.getFlashIcon());
        // liner 객체의 flashIcon 필드를 mockCounterLabel로 교체
        liner.setFlashIcon(mockCounterLabel);

        Mockito.verify(mockCounterLabel, Mockito.never()).startCount();

        try {
            liner.startCount();
        } catch (Exception e) {
        }

        Mockito.verify(mockCounterLabel, Mockito.times(1)).startCount();
    }
}
