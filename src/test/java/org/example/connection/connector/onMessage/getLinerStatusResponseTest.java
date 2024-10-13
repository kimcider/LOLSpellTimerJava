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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class getLinerStatusResponseTest {
    static ObjectMapper mapper = new ObjectMapper();
    private Connector connector;

    private HashMap<String, Liner> connectorLinerList;
    private HashMap<String, Liner> serverLinerList;

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

        serverLinerList = getLinerList();
    }

    @Test
    public void getLinerStatusResponse() throws JsonProcessingException {
        Liner serverTop = serverLinerList.get("top");
        serverTop.setIonianBoots(true);
        serverTop.offSpell(serverTop.getFlash());

        String json = mapper.writeValueAsString(serverLinerList.values().toArray());
        String wrappedJson = connector.wrapMethodJson("getLinerStatusResponse", json);
        System.out.println(wrappedJson);
        connector.onMessage(wrappedJson);

        assertTrue(connector.getLinerList().get("top").isIonianBoots());
        assertEquals(272, connector.getLinerList().get("top").getFlash().getCoolTime());
    }
}
