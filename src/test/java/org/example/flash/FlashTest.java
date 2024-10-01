package org.example.flash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Flash;
import org.example.Liner;
import org.example.connection.AbstractWebSocketConnector;
import org.example.connection.Connector;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FlashTest {
    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void createFlash() {
        Flash flash = new Flash();
        assertEquals(300, flash.flashCoolTime);
        assertEquals(300, flash.getCoolTime());
        assertEquals(true, flash.isOn());
    }

    @Test
    public void testSendFlashStatus() throws URISyntaxException, JsonProcessingException {
        Flash flash = new Flash();
        AbstractWebSocketConnector mockConnector = Mockito.spy(Mockito.mock(
                AbstractWebSocketConnector.class,
                withSettings()
                        .useConstructor(new URI("testURI"))
                        .defaultAnswer(CALLS_REAL_METHODS)));

        Liner top = new Liner("top", mockConnector);
        flash.sendFlashStatus(top, mockConnector);

        String json = mapper.writeValueAsString(top);
        verify(mockConnector, times(1)).sendMessage("useFlash", json);
    }

}
