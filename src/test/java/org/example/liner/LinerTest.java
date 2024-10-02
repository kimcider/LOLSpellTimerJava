package org.example.liner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CounterLabel;
import org.example.Flash;
import org.example.Liner;
import org.example.connection.AbstractWebSocketConnector;
import org.example.connection.Connector;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.event.MouseAdapter;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

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


    //TODO: 얘를 완성하던, mousePressed의 if조건문들을 빼든 둘 중 하나는 해야겄다.
//    @Test
//    public void createLinerTest3() throws URISyntaxException {
//        Liner liner = new Liner("top", new AbstractWebSocketConnector(new URI("tempUrl")) {
//            @Override
//            public void onOpen(ServerHandshake serverHandshake) {
//
//            }
//
//            @Override
//            public void onMessage(String s) {
//
//            }
//
//            @Override
//            public void onClose(int i, String s, boolean b) {
//
//            }
//
//            @Override
//            public void onError(Exception e) {
//
//            }
//        });
//        Flash flash = Mockito.spy(liner.getFlash());
//        CounterLabel flashIcon = flash.getFlashIcon();
//
//        assertTrue(flash.isOn());
//
//        MouseEvent mouseEvent = new MouseEvent(
//                flashIcon, // 이벤트가 발생하는 컴포넌트
//                MouseEvent.MOUSE_PRESSED, // 이벤트 타입
//                System.currentTimeMillis(), // 현재 시간
//                0, // modifier (예: Shift 키)
//                10, 10, // x, y 좌표
//                1, // 클릭 횟수
//                false, // 팝업 트리거 여부 (오른쪽 클릭)
//                MouseEvent.BUTTON1 // 클릭한 버튼 (왼쪽 버튼)
//        );
//
//        for (MouseListener listener : flashIcon.getMouseListeners()) {
//            listener.mousePressed(mouseEvent);
//        }
//
//        verify(flash, times(1)).startCount(liner);
//        verify(flash, times(1)).off();
//    }


    @Test
    public void writeLinerAsString() throws JsonProcessingException {
        Liner liner = new Liner("top", connector);
        String json = mapper.writeValueAsString(liner);
        assertEquals("""
                {"name":"top","flash":{"flashCoolTime":300,"coolTime":0}}""", json);
    }

    @Test
    public void testSendFlashStatus() throws URISyntaxException, JsonProcessingException {
        AbstractWebSocketConnector mockConnector = Mockito.spy(Mockito.mock(
                AbstractWebSocketConnector.class,
                withSettings()
                        .useConstructor(new URI("testURI"))
                        .defaultAnswer(CALLS_REAL_METHODS)));

        Liner top = new Liner("top", mockConnector);
        top.sendLinerStatus();

        String json = mapper.writeValueAsString(top);
        verify(mockConnector, times(1)).sendMessage("sendLinerStatus", json);
    }

    @Test
    public void useFlash() {
        Liner liner = new Liner("top", connector);
        // CounterLabel mock 생성
        Flash mockFlash = Mockito.spy(liner.getFlash());
        // liner 객체의 flashIcon 필드를 mockCounterLabel로 교체
        liner.setFlash(mockFlash);

        Mockito.verify(mockFlash, Mockito.never()).startCount(liner);

        try {
            liner.useFlash();
        } catch (Exception e) {
        }

        Mockito.verify(mockFlash, Mockito.times(1)).startCount(liner);
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
