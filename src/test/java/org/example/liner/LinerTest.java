package org.example.liner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Liner;
import org.example.connection.AbstractWebSocketConnector;
import org.example.connection.Connector;
import org.example.spell.Spell;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        assertNotNull(liner.getFlash().getSpellIcon());
        assertNull(liner.getName());
        assertNotNull(liner.getFlash());

        assertFalse(liner.isCosmicInsight());
        assertFalse(liner.isIonianBoots());
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
        assertNotNull(liner.getFlash().getSpellIcon());
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
                {"name":"top","flash":{"type":"flash","spellCoolTime":300,"coolTime":0},"cosmicInsight":false,"ionianBoots":false}""", json);
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
    public void touchFlash() {
        Liner liner = new Liner("top", connector);
        // CounterLabel mock 생성
        Spell mockFlash = Mockito.spy(liner.getFlash());
        // liner 객체의 flashIcon 필드를 mockCounterLabel로 교체
        liner.setFlash(mockFlash);

        Mockito.verify(mockFlash, Mockito.never()).startCount(liner);

        try {
            liner.touchFlash();
        } catch (Exception e) {
        }

        Mockito.verify(mockFlash, Mockito.times(1)).startCount(liner);
    }

    @Test
    public void touchFlashWhenFlashIsOn() {
        Liner liner = new Liner("top", connector);
        Liner mockLiner = Mockito.spy(liner);
        Spell mockFlash = Mockito.spy(mockLiner.getFlash());
        mockLiner.setFlash(mockFlash);

        mockLiner.touchFlash();

        verify(mockFlash, times(1)).isOn();
        verify(mockFlash, times(1)).off();
        verify(mockFlash, times(1)).startCount(mockLiner);

        assertFalse(mockLiner.getFlash().isOn());
    }

    @Test
    public void touchFlashWhenFlashIsOff() {
        Liner liner = new Liner("top", connector);
        liner.getFlash().off();

        Liner mockLiner = Mockito.spy(liner);
        Spell mockFlash = Mockito.spy(mockLiner.getFlash());
        mockLiner.setFlash(mockFlash);

        mockLiner.touchFlash();

        verify(mockFlash, times(1)).isOn();
        verify(mockFlash, times(1)).on();
        verify(mockFlash, times(1)).stopCount();

        assertTrue(mockLiner.getFlash().isOn());
    }

    @Test
    public void jsonToLinerWithCosmicInsight() throws JsonProcessingException {
        String json = """
                 {"name":"top","flash":{"type":"flash","spellCoolTime":300,"coolTime":0},"cosmicInsight":true,"ionianBoots":false}""";

        Liner liner = mapper.readValue(json, new TypeReference<Liner>() {
        });
        Liner expectedLiner = new Liner("top", connector);
        expectedLiner.setCosmicInsight(true);
        assertEquals(expectedLiner, liner);
    }

    @Test
    public void jsonToLinerWithIonianBoots() throws JsonProcessingException {
        String json = """
                 {"name":"top","flash":{"type":"flash","spellCoolTime":300,"coolTime":0},"cosmicInsight":false,"ionianBoots":true}""";

        Liner liner = mapper.readValue(json, new TypeReference<Liner>() {
        });
        Liner expectedLiner = new Liner("top", connector);
        expectedLiner.setIonianBoots(true);
        assertEquals(expectedLiner, liner);
    }

    @Test
    public void jsonToLinerWithIonianBootsAndCosmicInsight() throws JsonProcessingException {
        String json = """
                 {"name":"top","flash":{"type":"flash","spellCoolTime":300,"coolTime":0},"cosmicInsight":true,"ionianBoots":true}""";

        Liner liner = mapper.readValue(json, new TypeReference<Liner>() {
        });
        Liner expectedLiner = new Liner("top", connector);
        expectedLiner.setIonianBoots(true);
        expectedLiner.setCosmicInsight(true);
        assertEquals(expectedLiner, liner);
    }

    @Test
    public void testTouchCosmicInsight() {
        //제대로 상태값 변하나 테스트
        assertNotNull(null);
    }

    @Test
    public void testBuyIonianBoots() {
        //제대로 상태값 변하나 테스트
        assertNotNull(null);
    }

    @Test
    public void testBothCoolTimeReduce() {
        //제대로 상태값 변하나 테스트
        assertNotNull(null);
    }

    @Test
    public void testTouchCosmicInsightJSON() {
        //Json으로 제대로 변환되나 테스트
        // touch하고, 해당 객체를 json으로 변환할 떄 
        //SpellTest의 JSON을 가져와서 일치하는지 확인
        assertNotNull(null);
    }

    @Test
    public void testBuyIonianBootsJSON() {
        //Json으로 제대로 변환되나 테스트
        assertNotNull(null);
    }

    @Test
    public void testBothCoolTimeReduceJSON() {
        //Json으로 제대로 변환되나 테스트
        assertNotNull(null);
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

        l1.getFlash().off();
        assertNotEquals(l1, l2);
        l2.getFlash().off();
        assertEquals(l1, l2);

        l1.getFlash().setSpellCoolTime(5);
        assertNotEquals(l1, l2);
        l2.getFlash().setSpellCoolTime(5);
        assertEquals(l1, l2);
    }

    @Test
    public void equalsCoolTimeReducer() {
        assertNotNull(null);
    }
}
