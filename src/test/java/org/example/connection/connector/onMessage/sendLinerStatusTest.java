package org.example.connection.connector.onMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.connection.Connector;
import org.example.liner.Liner;
import org.example.liner.spell.CounterLabel;
import org.example.liner.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class sendLinerStatusTest {
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
        Connector.serverURI = "testURI";
        Connector.hashValue = "hashValue";
        connector = Connector.getInstance();
        connector = Mockito.spy(connector);
        connectorLinerList = getLinerList();
        connector.setLinerList(connectorLinerList);

        serverLiner = new Liner("sup", connector);
    }

    @Test
    public void OneChange() throws JsonProcessingException {
        serverLiner.offSpell(serverLiner.getSpell1());
        assertFalse( serverLiner.getSpell1().isOn());

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(connector.wrapMethodJson("sendLinerStatus", json));

        assertFalse(connector.getLinerList().get("sup").getSpell1().isOn());
    }

    @Test
    public void NoChange() throws JsonProcessingException {
        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(connector.wrapMethodJson("sendLinerStatus", json));

        assertTrue(connector.getLinerList().get("sup").getSpell1().isOn());
    }

    @Test
    public void CallTouchSpell() throws JsonProcessingException {
        serverLiner.offSpell(serverLiner.getSpell1());

        Liner mockConnectorLinerSup = Mockito.spy(connector.getLinerList().get("sup"));
        Spell mockConnectorSpell = Mockito.spy(connector.getLinerList().get("sup").getSpell1());
        mockConnectorLinerSup.setSpell1(mockConnectorSpell);
        connectorLinerList.put("sup", mockConnectorLinerSup);

        String serverSupportJson = mapper.writeValueAsString(serverLiner);
        connector.onMessage(connector.wrapMethodJson("sendLinerStatus", serverSupportJson));

        assertFalse(connector.getLinerList().get("sup").getSpell1().isOn());
        Mockito.verify(mockConnectorLinerSup, times(1)).setLiner(any(Liner.class));
        verify(mockConnectorSpell, times(1)).setSpell(any(Spell.class));
    }

    @Test
    public void CallTouchSpell_WhenNothingChanged() throws JsonProcessingException {
        Liner mockConnectorLinerSup = Mockito.spy(connector.getLinerList().get("sup"));
        connectorLinerList.put("sup", mockConnectorLinerSup);

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(connector.wrapMethodJson("sendLinerStatus", json));

        assertTrue(connector.getLinerList().get("sup").getSpell1().isOn());
    }

    @Test
    public void SetCoolTime_WhenServerFlashIsOff() throws JsonProcessingException {
        serverLiner.offSpell(serverLiner.getSpell1());

        Spell mockClientFlashSup = Mockito.spy(connector.getLinerList().get("sup").getSpell1());
        connectorLinerList.get("sup").setSpell1(mockClientFlashSup);

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(connector.wrapMethodJson("sendLinerStatus", json));

        assertFalse(connector.getLinerList().get("sup").getSpell1().isOn());
    }
    @Test
    public void SetCoolTime_WhenServerFlashIsOn() throws JsonProcessingException {
        serverLiner.onSpell(serverLiner.getSpell1());

        Spell mockClientFlashSup = Mockito.spy(connector.getLinerList().get("sup").getSpell1());
        connectorLinerList.get("sup").setSpell1(mockClientFlashSup);

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(connector.wrapMethodJson("sendLinerStatus", json));
        assertTrue(connector.getLinerList().get("sup").getSpell1().isOn());
        assertTrue(mockClientFlashSup.isOn());
    }

    @Test
    public void NotChangeFlashIcon() throws JsonProcessingException {
        CounterLabel supFlashIcon = connector.getLinerList().get("sup").getSpell1().getSpellIcon();

        serverLiner.offSpell(serverLiner.getSpell1());
        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(connector.wrapMethodJson("sendLinerStatus", json));

        assertEquals(supFlashIcon, connector.getLinerList().get("sup").getSpell1().getSpellIcon());
    }

    @Test
    public void supF_IsAlreadyUsed() throws JsonProcessingException, URISyntaxException, InterruptedException {
        serverLiner.offSpell(serverLiner.getSpell1());

        // 서폿이 플을 썼는지를 주시하기 위한 spy mock Liner
        //서폿 플 이미 사용함
        Liner mockSupLiner = Mockito.spy(connector.getLinerList().get("sup"));
        connector.getLinerList().put("sup", mockSupLiner);
        connector.getLinerList().get("sup").offSpell(connector.getLinerList().get("sup").getSpell1());


        String json = mapper.writeValueAsString(serverLiner);

        connector.onMessage(connector.wrapMethodJson("sendLinerStatus", json));
        assertFalse(connector.getLinerList().get("sup").getSpell1().isOn());
    }

    @Test
    public void WithCosmicInsights() throws JsonProcessingException {
        serverLiner.setCosmicInsight(true);

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(connector.wrapMethodJson("sendLinerStatus", json));

        assertTrue(connector.getLinerList().get("sup").isCosmicInsight());
    }

    @Test
    public void WithIonianBoots() throws JsonProcessingException {
        serverLiner.setIonianBoots(true);

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(connector.wrapMethodJson("sendLinerStatus", json));

        assertTrue(connector.getLinerList().get("sup").isIonianBoots());
    }

    @Test
    public void WithCoolTimeReducer() throws JsonProcessingException {
        serverLiner.setCosmicInsight(true);
        serverLiner.setIonianBoots(true);

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(connector.wrapMethodJson("sendLinerStatus", json));

        assertTrue(connector.getLinerList().get("sup").isCosmicInsight());
        assertTrue(connector.getLinerList().get("sup").isIonianBoots());
    }
}
