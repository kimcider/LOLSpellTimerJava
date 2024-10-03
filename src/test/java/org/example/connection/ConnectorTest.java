package org.example.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CounterLabel;
import org.example.Flash;
import org.example.Liner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.ConnectException;
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

    // 이 테스트는 서버가 열려있을때만 통과한다.
    // TODO: 이거 나중에 방법 찾아보기.
    @Test
    public void testConnectToServer() throws JsonProcessingException {
        Connector connector = Connector.getInstance();

        HttpResponse<String> response = connector.sendMessage("anyMessage", "str");
        assertNotNull(response);
    }

    @BeforeEach
    public void setUp() throws Exception {
        connector = Mockito.spy(connector);
        connectorLinerList = getLinerList();
        connector.setLinerList(connectorLinerList);

        serverLinerList = getLinerList();
    }

    @Test
    public void testSetUp() throws JsonProcessingException {
        String json = mapper.writeValueAsString(connectorLinerList);
        assertEquals("""
                        {"top":{"name":"top","flash":{"flashCoolTime":300,"coolTime":0}},"bot":{"name":"bot","flash":{"flashCoolTime":300,"coolTime":0}},"mid":{"name":"mid","flash":{"flashCoolTime":300,"coolTime":0}},"jg":{"name":"jg","flash":{"flashCoolTime":300,"coolTime":0}},"sup":{"name":"sup","flash":{"flashCoolTime":300,"coolTime":0}}}"""
                , json);
    }

    @Test
    public void testJsonToLinerList() throws JsonProcessingException {
        List<Liner> defaultList = new ArrayList<>();
        ArrayList<String> nameList = getNameList();
        for (String name : nameList) {
            defaultList.add(new Liner(name, connector));
        }

        String json = """
                [{"name":"top","flash":{"coolTime":0}},{"name":"jg","flash":{"coolTime":0}},{"name":"mid","flash":{"coolTime":0}},{"name":"bot","flash":{"coolTime":0}},{"name":"sup","flash":{"coolTime":0}}]""";

        List<Liner> liners = mapper.readValue(json, new TypeReference<List<Liner>>() {
        });

        //TODO: 얘들 어떻게 못맞추나?
        assertEquals(defaultList, liners);
    }

    @Test
    public void testOnMessage1() throws JsonProcessingException {
        Liner mockSupLiner = Mockito.spy(connector.getLinerList().get("sup"));
        HashMap<String, Liner> mockLinerList = getLinerList();
        mockLinerList.get("sup").getFlash().off();
        assertEquals(false, mockLinerList.get("sup").getFlash().isOn());
        String json = mapper.writeValueAsString(mockLinerList.values().stream().toList());
        connector.onMessage(json);

        assertEquals(false, connector.getLinerList().get("sup").getFlash().isOn());
        verify(mockSupLiner, times(0)).touchFlash();
    }

    @Test
    public void testOnMessage2() throws JsonProcessingException {
        serverLinerList.get("sup").getFlash().off();
        serverLinerList.get("jg").getFlash().off();

        String json = mapper.writeValueAsString(serverLinerList.values().stream().toList());
        connector.onMessage(json);

        assertEquals(false, connector.getLinerList().get("sup").getFlash().isOn());
        assertEquals(false, connector.getLinerList().get("jg").getFlash().isOn());
    }

    @Test
    public void assertOnMessageNeverCallTouchFlash() throws JsonProcessingException {
        Liner mockClientLinerSup = Mockito.spy(connector.getLinerList().get("sup"));
        connectorLinerList.put("sup", mockClientLinerSup);

        String json = mapper.writeValueAsString(getLinerList().values().stream().toList());
        connector.onMessage(json);

        verify(mockClientLinerSup, never()).touchFlash();
    }

    @Test
    public void testOnMessageSetCoolTime_WhenServerFlashIsOff() throws JsonProcessingException {
        serverLinerList.get("sup").getFlash().off();


        Flash mockClientFlashSup = Mockito.spy(connector.getLinerList().get("sup").getFlash());
        connectorLinerList.get("sup").setFlash(mockClientFlashSup);

        String json = mapper.writeValueAsString(serverLinerList.values().stream().toList());
        connector.onMessage(json);

        verify(mockClientFlashSup, times(1)).setCoolTime(serverLinerList.get("sup").getFlash().getCoolTime());
        verify(mockClientFlashSup, times(1)).setFlashCoolTime(serverLinerList.get("sup").getFlash().getFlashCoolTime());
    }
    @Test
    public void testOnMessageSetCoolTime_WhenServerFlashIsOn() throws JsonProcessingException {
        Flash mockClientFlashSup = Mockito.spy(connector.getLinerList().get("sup").getFlash());
        connectorLinerList.get("sup").setFlash(mockClientFlashSup);
        connectorLinerList.get("sup").getFlash().off();

        String json = mapper.writeValueAsString(serverLinerList.values().stream().toList());
        connector.onMessage(json);

        verify(mockClientFlashSup, times(1)).setCoolTime(serverLinerList.get("sup").getFlash().getCoolTime());
        verify(mockClientFlashSup, times(1)).setFlashCoolTime(serverLinerList.get("sup").getFlash().getFlashCoolTime());
    }

    @Test
    public void testOnMessageUseFlashsStartCount() throws JsonProcessingException {
        serverLinerList.get("sup").getFlash().off();
        serverLinerList.get("jg").getFlash().off();


        Flash mockClientFlashSup = Mockito.spy(connector.getLinerList().get("sup").getFlash());
        Flash mockClientFlashJg = Mockito.spy(connector.getLinerList().get("jg").getFlash());
        connectorLinerList.get("sup").setFlash(mockClientFlashSup);
        connectorLinerList.get("jg").setFlash(mockClientFlashJg);

        String json = mapper.writeValueAsString(serverLinerList.values().stream().toList());
        connector.onMessage(json);

        assertFalse(mockClientFlashSup.isOn());
        verify(mockClientFlashSup, times(1)).startCount(connectorLinerList.get("sup"));
        verify(mockClientFlashJg, times(1)).startCount(connectorLinerList.get("jg"));
    }

    @Test
    public void testOnMessageUseFlashsStopCount() throws JsonProcessingException {
        serverLinerList.get("sup").getFlash().on();
        serverLinerList.get("jg").getFlash().on();


        Flash mockClientFlashSup = Mockito.spy(connector.getLinerList().get("sup").getFlash());
        Flash mockClientFlashJg = Mockito.spy(connector.getLinerList().get("jg").getFlash());
        connectorLinerList.get("sup").setFlash(mockClientFlashSup);
        connectorLinerList.get("jg").setFlash(mockClientFlashJg);

        String json = mapper.writeValueAsString(serverLinerList.values().stream().toList());
        connector.onMessage(json);

        assertTrue(mockClientFlashSup.isOn());
        assertTrue(mockClientFlashJg.isOn());


        verify(mockClientFlashSup, times(1)).stopCount();
        verify(mockClientFlashJg, times(1)).stopCount();
    }

    @Test
    public void testOnMessage4() throws JsonProcessingException {
        serverLinerList.get("sup").getFlash().off();
        serverLinerList.get("jg").getFlash().off();
        Flash mockSupFlash = Mockito.spy(connectorLinerList.get("sup").getFlash());
        Flash mockJgFlash = Mockito.spy(connectorLinerList.get("jg").getFlash());
        connectorLinerList.get("sup").setFlash(mockSupFlash);
        connectorLinerList.get("jg").setFlash(mockJgFlash);

        String json = mapper.writeValueAsString(serverLinerList.values().stream().toList());
        connector.onMessage(json);

        verify(mockSupFlash, times(1)).setCoolTime(serverLinerList.get("sup").getFlash().getCoolTime());
        verify(mockSupFlash, times(1)).setFlashCoolTime(serverLinerList.get("sup").getFlash().getFlashCoolTime());
        verify(mockJgFlash, times(1)).setCoolTime(serverLinerList.get("jg").getFlash().getCoolTime());
        verify(mockJgFlash, times(1)).setFlashCoolTime(serverLinerList.get("jg").getFlash().getFlashCoolTime());
    }

    @Test
    public void assertOnMessageNotChangeFlashIcon() throws JsonProcessingException {
        CounterLabel supFlashIcon = connector.getLinerList().get("sup").getFlash().getFlashIcon();

        serverLinerList.get("sup").getFlash().off();
        String json = mapper.writeValueAsString(serverLinerList.values().stream().toList());
        connector.onMessage(json);

        assertEquals(supFlashIcon, connector.getLinerList().get("sup").getFlash().getFlashIcon());
    }

    @Test
    public void testOnMessage_supF_IsAlreadyUsed() throws JsonProcessingException, URISyntaxException, InterruptedException {
        // 서폿이 플을 썼는지를 주시하기 위한 spy mock Liner
        Liner mockSupLiner = Mockito.spy(connector.getLinerList().get("sup"));

        HashMap<String, Liner> mockLinerList = getLinerList();

        //서폿 플 이미 사용함
        connector.getLinerList().get("sup").getFlash().off();

        mockLinerList.get("sup").getFlash().off();
        String json = mapper.writeValueAsString(mockLinerList.values().stream().toList());

        connector.onMessage(json);
        assertEquals(false, connector.getLinerList().get("sup").getFlash().isOn());

        // 서폿 플이 이미 사용되었기 떄문에 서폿 플이 없는 메세지가 와도 서폿에 대한 startCountFlash를 호출하지 않음
        verify(mockSupLiner, never()).touchFlash();

    }

}
