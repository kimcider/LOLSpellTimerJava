package org.example.liner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Flash;
import org.example.Liner;
import org.example.connection.AbstractWebSocketConnector;
import org.example.connection.Connector;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class LinerTest {
    static ObjectMapper mapper = new ObjectMapper();
    AbstractWebSocketConnector connector;

    @BeforeEach
    public void setup() {
        connector = Mockito.mock(AbstractWebSocketConnector.class);
    }

    @Test
    public void createLinerTest1() {
        Liner liner = new Liner();
        assertNotNull(liner.getConnector());
        assertNull(liner.getLineIcon());
        assertNull(liner.getFlash().getFlashIcon());
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
        assertNotNull(liner.getFlash().getFlashIcon());
        assertNotNull(liner.getName());
        assertNotNull(liner.getFlash());
    }


    @Test
    public void writeLinerAsString() throws JsonProcessingException {
        Liner liner = new Liner("top", connector);
        String json = mapper.writeValueAsString(liner);
        assertEquals("""
                {"name":"top","flash":{"coolTime":300,"on":true}}""", json);
    }


    @Test
    public void useFlash() {
        Liner liner = new Liner("top", connector);
        // CounterLabel mock 생성
        Flash mockFlash = Mockito.spy(liner.getFlash());
        // liner 객체의 flashIcon 필드를 mockCounterLabel로 교체
        liner.setFlash(mockFlash);

        Mockito.verify(mockFlash, Mockito.never()).startCount(liner, connector);

        try {
            liner.useFlash();
        } catch (Exception e) {
        }

        Mockito.verify(mockFlash, Mockito.times(1)).startCount(liner, connector);
    }

    @Test
    public void equals() {
        Liner l1 = new Liner("top", connector);
        Liner l2 = new Liner("top", connector);

        assertEquals(l1, l2);

        l1.setName("jg");
        assertNotEquals(l1, l2);

        l2.setName("jg");
        assertEquals(l1, l2);

        l1.useFlash();
        assertNotEquals(l1, l2);
        l2.useFlash();
        assertEquals(l1, l2);
    }
}
