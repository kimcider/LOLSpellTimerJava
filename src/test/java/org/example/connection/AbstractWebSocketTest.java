package org.example.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AbstractWebSocketTest {
    private static ObjectMapper mapper = new ObjectMapper();
    AbstractWebSocketConnector connector;
    HttpClient mockClient;
    @BeforeEach
    public void setUp() throws URISyntaxException {
        connector = Mockito.mock(
                AbstractWebSocketConnector.class,
                withSettings()
                        .useConstructor(new URI("testURI"))
                        .defaultAnswer(CALLS_REAL_METHODS));
        mockClient = Mockito.spy(Mockito.mock(HttpClient.class));


        connector.setHashValue("hashValue");
        connector.setClient(mockClient);
    }


    @Test
    public void testSendMessage() throws IOException, InterruptedException, URISyntaxException {
        connector.sendMessage("anyMessage", """
        {"key": "value"}""");

        Mockito.verify(mockClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
}
