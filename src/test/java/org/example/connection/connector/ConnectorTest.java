package org.example.connection.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.connection.Connector;
import org.example.liner.spell.CounterLabel;
import org.example.liner.Liner;
import org.example.liner.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConnectorTest {
    static ObjectMapper mapper = new ObjectMapper();
    private Connector connector;

    private HashMap<String, Liner> connectorLinerList;
    private Liner serverLiner;

    private ArrayList<String> getNameList() {
        ArrayList<String> listNames = new ArrayList();
        listNames.add("top");
        listNames.add("jg");
        listNames.add("mid");
        listNames.add("bot");
        listNames.add("sup");
        return listNames;
    }

    private HashMap<String, Liner> getLinerList() {
        ArrayList<String> nameList = getNameList();

        HashMap<String, Liner> linerList = new HashMap();
        for (String name : nameList) {
            linerList.put(name, new Liner(name, connector));
        }

        return linerList;
    }

    @BeforeEach
    public void setUp() throws Exception {
        connector = new Connector("testURI", "hashValue");
        connector = Mockito.spy(connector);
        connectorLinerList = getLinerList();
        connector.setLinerList(connectorLinerList);

        serverLiner = new Liner("sup", connector);
    }

    @Test
    public void testSetUp() throws JsonProcessingException {
        String json = mapper.writeValueAsString(connectorLinerList);
        assertEquals("""
                        {"top":{"name":"top","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false},"bot":{"name":"bot","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false},"mid":{"name":"mid","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false},"jg":{"name":"jg","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false},"sup":{"name":"sup","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false}}"""
                , json);
    }

    @Test
    public void testJsonToLinerList() throws JsonProcessingException {
        String json = """
                [{"name":"top","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false},
                {"name":"bot","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false},
                {"name":"mid","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false},
                {"name":"jg","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false},
                {"name":"sup","flash":{"type":"flash","coolTime":0},"cosmicInsight":false,"ionianBoots":false}]""";

        List<Liner> liners = mapper.readValue(json, new TypeReference<List<Liner>>() {
        });

        HashMap<String, Liner> serverLinerList = new HashMap<>();
        for(Liner liner : liners) {
            serverLinerList.put(liner.getName(), liner);
        }

        assertEquals(connectorLinerList, serverLinerList);
    }

    @Test
    public void wrapJsonTest(){
        String json = """
                {"key": "value"}""";
        String wrappedJson = connector.wrapJson(json);
        assertEquals("""
                {"hash":"hashValue","data":{"key":"value"}}""", wrappedJson);
    }
    @Test
    public void wrapJsonTestEmptyString(){
        String json = "";
        String wrappedJson = connector.wrapJson(json);
        assertEquals("""
                {"hash":"hashValue"}""", wrappedJson);
    }

    @Test
    public void testWrapMethodJson(){
        String result = connector.wrapMethodJson("method", """
                {"key": "value"}""");
        assertEquals("""
                {"method":"method","hash":"hashValue","data":{"key":"value"}}""", result);
    }

    @Test
    public void testWrapMethodJson_WithoutJson(){
        String result = connector.wrapMethodJson("method", "");
        assertEquals("""
                {"method":"method","hash":"hashValue"}""", result);
    }



}
