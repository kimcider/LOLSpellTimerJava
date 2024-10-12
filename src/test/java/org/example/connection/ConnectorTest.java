package org.example.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.liner.spell.CounterLabel;
import org.example.liner.Liner;
import org.example.liner.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConnectorTest {
    static ObjectMapper mapper = new ObjectMapper();
    private Connector connector = Connector.getInstance();

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

    // 이 테스트는 서버가 열려있을때만 통과한다.
    // TODO: 이거 나중에 방법 찾아보기.
    @Test
    public void testConnectToServer() throws JsonProcessingException {
        Connector connector = Connector.getInstance();

        HttpResponse<String> response = connector.sendMessage("anyMessage", """
        {"key": "value"}""");
        assertNotNull(response);
    }

    @BeforeEach
    public void setUp() throws Exception {
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
    public void testOnMessage_OneChange() throws JsonProcessingException {
        serverLiner.offSpell(serverLiner.getFlash());
        assertFalse( serverLiner.getFlash().isOn());

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(json);

        assertFalse(connector.getLinerList().get("sup").getFlash().isOn());
    }

    @Test
    public void testOnMessage_NoChange() throws JsonProcessingException {
        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(json);

        assertTrue(connector.getLinerList().get("sup").getFlash().isOn());
    }

    @Test
    public void testOnMessageCallTouchSpell() throws JsonProcessingException {
        serverLiner.offSpell(serverLiner.getFlash());

        Liner mockConnectorLinerSup = Mockito.spy(connector.getLinerList().get("sup"));
        Spell mockConnectorSpell = Mockito.spy(connector.getLinerList().get("sup").getFlash());
        mockConnectorLinerSup.setFlash(mockConnectorSpell);
        connectorLinerList.put("sup", mockConnectorLinerSup);

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(json);

        assertFalse(connector.getLinerList().get("sup").getFlash().isOn());
        Mockito.verify(mockConnectorLinerSup, times(1)).setLiner(any(Liner.class));
        verify(mockConnectorSpell, times(1)).setSpell(any(Spell.class));
    }

    @Test
    public void testOnMessageCallTouchSpell_WhenNothingChanged() throws JsonProcessingException {
        Liner mockConnectorLinerSup = Mockito.spy(connector.getLinerList().get("sup"));
        connectorLinerList.put("sup", mockConnectorLinerSup);

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(json);

        assertTrue(connector.getLinerList().get("sup").getFlash().isOn());
        Mockito.verify(mockConnectorLinerSup, never()).touchSpell(any(Spell.class));
    }

    @Test
    public void testOnMessageSetCoolTime_WhenServerFlashIsOff() throws JsonProcessingException {
        serverLiner.offSpell(serverLiner.getFlash());

        Spell mockClientFlashSup = Mockito.spy(connector.getLinerList().get("sup").getFlash());
        connectorLinerList.get("sup").setFlash(mockClientFlashSup);

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(json);

        assertFalse(connector.getLinerList().get("sup").getFlash().isOn());
        verify(mockClientFlashSup, times(1)).startCount(connectorLinerList.get("sup"));
    }
    @Test
    public void testOnMessageSetCoolTime_WhenServerFlashIsOn() throws JsonProcessingException {
        serverLiner.onSpell(serverLiner.getFlash());

        Spell mockClientFlashSup = Mockito.spy(connector.getLinerList().get("sup").getFlash());
        connectorLinerList.get("sup").setFlash(mockClientFlashSup);

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(json);
        assertTrue(connector.getLinerList().get("sup").getFlash().isOn());
        assertTrue(mockClientFlashSup.isOn());


        verify(mockClientFlashSup, times(1)).stopCount();
    }

    @Test
    public void assertOnMessageNotChangeFlashIcon() throws JsonProcessingException {
        CounterLabel supFlashIcon = connector.getLinerList().get("sup").getFlash().getSpellIcon();

        serverLiner.offSpell(serverLiner.getFlash());
        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(json);

        assertEquals(supFlashIcon, connector.getLinerList().get("sup").getFlash().getSpellIcon());
    }

    @Test
    public void testOnMessage_supF_IsAlreadyUsed() throws JsonProcessingException, URISyntaxException, InterruptedException {
        serverLiner.offSpell(serverLiner.getFlash());

        // 서폿이 플을 썼는지를 주시하기 위한 spy mock Liner
        //서폿 플 이미 사용함
        Liner mockSupLiner = Mockito.spy(connector.getLinerList().get("sup"));
        connector.getLinerList().put("sup", mockSupLiner);
        connector.getLinerList().get("sup").offSpell(connector.getLinerList().get("sup").getFlash());


        String json = mapper.writeValueAsString(serverLiner);

        connector.onMessage(json);
        assertFalse(connector.getLinerList().get("sup").getFlash().isOn());

        verify(mockSupLiner, never()).touchSpell(any(Spell.class));

    }

    @Test
    public void testOnMessage_WithCosmicInsights() throws JsonProcessingException {
        serverLiner.setCosmicInsight(true);

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(json);

        assertTrue(connector.getLinerList().get("sup").isCosmicInsight());
    }

    @Test
    public void testOnMessage_WithIonianBoots() throws JsonProcessingException {
        serverLiner.setIonianBoots(true);

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(json);

        assertTrue(connector.getLinerList().get("sup").isIonianBoots());
    }

    @Test
    public void testOnMessage_WithCoolTimeReducer() throws JsonProcessingException {
        serverLiner.setCosmicInsight(true);
        serverLiner.setIonianBoots(true);

        String json = mapper.writeValueAsString(serverLiner);
        connector.onMessage(json);

        assertTrue(connector.getLinerList().get("sup").isCosmicInsight());
        assertTrue(connector.getLinerList().get("sup").isIonianBoots());
    }
}
