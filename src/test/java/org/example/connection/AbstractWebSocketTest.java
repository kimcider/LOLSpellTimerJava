package org.example.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AbstractWebSocketTest {
    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSendMessage() throws IOException, InterruptedException, URISyntaxException {
        AbstractWebSocketConnector connector = Mockito.mock(
                AbstractWebSocketConnector.class,
                withSettings()
                        .useConstructor(new URI("testURI"))
                        .defaultAnswer(CALLS_REAL_METHODS));
        HttpClient mockClient = Mockito.spy(Mockito.mock(HttpClient.class));
        connector.setClient(mockClient);
        connector.setServerURI(Connector.getInstance().getServerURI());
        connector.sendMessage("anyMessage", "str");

        Mockito.verify(mockClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
}
