package org.example.connection.connector.onMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.connection.Connector;
import org.example.liner.Liner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class getLinerStatusTest {
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
    public void getLinerStatus() throws JsonProcessingException {
        try{
            connector.onMessage(connector.wrapMethodJson("getLinerStatus", ""));
        } catch (Exception e){
            e.printStackTrace();
        }

        String linerListJson = mapper.writeValueAsString(connector.getLinerList().values().toArray());
        String wrappedJson = connector.wrapMethodJson("getLinerStatusResponse", linerListJson);
        verify(connector, times(1)).send(wrappedJson);
    }
}
