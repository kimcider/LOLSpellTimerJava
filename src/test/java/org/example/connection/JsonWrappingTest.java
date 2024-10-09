package org.example.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.liner.Liner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonWrappingTest {
    static ObjectMapper mapper = new ObjectMapper();
    AbstractWebSocketConnector connector;
    Liner liner;

    @BeforeEach
    public void setup() {
        connector = Mockito.mock(AbstractWebSocketConnector.class);
        liner = new Liner("top", connector);
    }

    @Test
    void splitJson() throws JsonProcessingException {
        String wrappedJson = """
                {"hash":"hashValue","data":{"name":"top","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false}}""";
        JsonNode rootNode = mapper.readTree(wrappedJson);

        String hash = rootNode.get("hash").asText();

        JsonNode dataNode = rootNode.get("data");
        String dataJson = mapper.writeValueAsString(dataNode);

        assertEquals("hashValue", hash);
        assertEquals("""
                {"name":"top","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false}""", dataJson);
    }

}
