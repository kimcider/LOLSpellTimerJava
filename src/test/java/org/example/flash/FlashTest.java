package org.example.flash;

import org.example.Connector;
import org.example.Flash;
import org.example.Liner;
import org.example.connection.AbstractWebSocketConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlashTest {
    private AbstractWebSocketConnector connector = Connector.getInstance();

    public FlashTest() throws URISyntaxException {}

    @Test
    public void createFlash(){
        Flash flash = new Flash();
        assertEquals(300, flash.flashCoolTime);
        assertEquals(300, flash.coolTime);
        assertEquals(true, flash.on);
    }

    @Test
    public void useFlash(){
        Liner top = new Liner("top", connector);
        Flash topFlash = Mockito.spy(top.flash);
        topFlash.sendFlashStatus(top, connector);
        Mockito.verify(topFlash, Mockito.times(1)).sendFlashStatus(top, connector);
    }

    @Test
    public void useFlash2(){
        //TODO: 현재 이 메소드를 수행하면 운영서버에
        Liner top = new Liner("top", connector);
        top.flash.setOn(false);
        top.flash.sendFlashStatus(top, connector);
    }

//    @Test
//    public void useFlash() throws Exception {
//        HashMap<String, Liner> mockLinerList = getLinerList();
//
//        mockLinerList.get("top").flash.useFlash();
//        Mockito.verify(connectorLinerList.get("top"), Mockito.times(1)).flash.useFlash();
//        Mockito.verify(connectorLinerList.get("as"), Mockito.times(1)).flash.useFlash();
//
//    }
//    @Test
//    public void flashOn() throws Exception {
//        HashMap<String, Liner> mockLinerList = getLinerList();
//
//        mockLinerList.get("testliner").flash.flashOn();
//        Mockito.verify(connectorLinerList.get("testliner"), Mockito.times(1)).flash.flashOn();
//    }
//
//    @Test
//    public void startCountFlashWhenIsAlreadyUsed() throws IOException, InterruptedException {
//        connectorLinerList.get("sup").getFlash().setOn(false);
//        connectorLinerList.get("sup").startCount();
//
//        assertEquals("null", "g");
//
//    }
//
//    @Test
//    public void startCountFlashWhen() throws IOException, InterruptedException {
//        connectorLinerList.get("sup").startCount();
//        assertEquals("null", "g");
//    }

}
