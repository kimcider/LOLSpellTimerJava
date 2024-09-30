package org.example.flash;

import org.example.Flash;
import org.example.Liner;
import org.example.connection.AbstractWebSocketConnector;
import org.example.connection.Connector;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FlashTest {
    private AbstractWebSocketConnector connector = Connector.getInstance();

    public FlashTest() throws URISyntaxException {
    }

    @Test
    public void createFlash() {
        Flash flash = new Flash();
        assertEquals(300, flash.flashCoolTime);
        assertEquals(300, flash.getCoolTime());
        assertEquals(true, flash.isOn());
    }


    //GPT가 짜준 코드. 이거 베이스로 테스트들 진행하면 될듯?
    @Test
    public void useFlash() throws Exception {
        // Mock HttpClient와 HttpResponse를 생성
        HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
        HttpResponse<String> mockHttpResponse = Mockito.mock(HttpResponse.class);
        CompletableFuture<HttpResponse<String>> mockFutureResponse = CompletableFuture.completedFuture(mockHttpResponse);

        // Mocked Connector 설정
        Connector mockConnector = Mockito.mock(Connector.class);

        // mockConnector의 client 필드에 mockHttpClient 설정
        when(mockConnector.getClient()).thenReturn(mockHttpClient);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockFutureResponse);

        // Liner 객체 생성
        Liner top = new Liner("top", mockConnector);
        top.getFlash().off();

        // Flash 상태 전송
        top.getFlash().sendFlashStatus(top, mockConnector);

        // HttpRequest 전송이 호출되었는지 검증
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

//    @Test
//    public void useFlash1() {
//        //TODO: 얘는 내가 mock을 잘못써서 운영서버에 보내지는 않는듯.... 그래도 잘못되고있는것
//        Liner top = new Liner("top", connector);
//        Flash topFlash = Mockito.spy(top.getFlash());
//        topFlash.sendFlashStatus(top, connector);
//        Mockito.verify(topFlash, Mockito.times(1)).sendFlashStatus(top, connector);
//    }

//    @Test
//    public void useFlash2() {
//        //TODO: 현재 이 메소드를 수행하면 운영서버에 전송한다... 진정한 의미의 테스트가 되지 못하는것....
//        Liner top = new Liner("top", connector);
//        top.getFlash().setOn(false);
//        top.getFlash().sendFlashStatus(top, connector);
//    }

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
