package org.example.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Liner;
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

@ExtendWith(MockitoExtension.class)
public class ConnectorTest {
    static ObjectMapper mapper = new ObjectMapper();
    private Connector connector = Connector.getInstance();

    private HashMap<String, Liner> connectorLinerList;

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
    }

    @Test
    public void testSetUp() throws JsonProcessingException {
        String json = mapper.writeValueAsString(connectorLinerList);
        assertEquals("""
                        {"top":{"name":"top","flash":{"coolTime":0}},"bot":{"name":"bot","flash":{"coolTime":0}},"mid":{"name":"mid","flash":{"coolTime":0}},"jg":{"name":"jg","flash":{"coolTime":0}},"sup":{"name":"sup","flash":{"coolTime":0}}}"""
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
    public void testOnMessage1() throws JsonProcessingException, URISyntaxException, InterruptedException {
        Liner mockSupLiner = Mockito.spy(connector.getLinerList().get("sup"));
        HashMap<String, Liner> mockLinerList = getLinerList();

        // top의 플이 없다고 useFlash를 서버에 전송된 이후, 서버에서 클라이언트들에게 보낸 메세지
        mockLinerList.get("sup").getFlash().off();
        assertEquals(false, mockLinerList.get("sup").getFlash().isOn());
        String json = mapper.writeValueAsString(mockLinerList.values().stream().toList());

        connector.onMessage(json);
        assertEquals(false, connector.getLinerList().get("sup").getFlash().isOn());
        Mockito.verify(mockSupLiner, Mockito.times(0)).useFlash();
    }

    @Test
    public void testOnMessage2() throws JsonProcessingException, URISyntaxException, InterruptedException {
        HashMap<String, Liner> mockLinerList = getLinerList();

        // top의 플이 없다고 useFlash를 서버에 전송된 이후, 서버에서 클라이언트들에게 보낸 메세지
        mockLinerList.get("sup").getFlash().off();
        mockLinerList.get("jg").getFlash().off();
        String json = mapper.writeValueAsString(mockLinerList.values().stream().toList());

        connector.onMessage(json);
        assertEquals(false, connector.getLinerList().get("sup").getFlash().isOn());
        assertEquals(false, connector.getLinerList().get("jg").getFlash().isOn());
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
        Mockito.verify(mockSupLiner, Mockito.never()).useFlash();

    }

}
