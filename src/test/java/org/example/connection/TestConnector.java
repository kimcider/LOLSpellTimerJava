package org.example.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Connector;
import org.example.Liner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TestConnector {
    static ObjectMapper mapper = new ObjectMapper();
    private Connector connector;

    private HashMap<String, Liner> connectorLinerList;

    private HashMap<String, Liner> mockLinerList;

    private void initConnectorLists() {
        ArrayList<String> listNames = new ArrayList();
        listNames.add("top");
        listNames.add("jg");
        listNames.add("mid");
        listNames.add("bot");
        listNames.add("sup");

        connectorLinerList = new HashMap<>();

        for (int i = 0; i < listNames.size(); i++) {
            Liner liner = new Liner(listNames.get(i));
            connectorLinerList.put(listNames.get(i), liner);
        }
    }

    private void initMockLists() {
        ArrayList<String> listNames = new ArrayList();
        listNames.add("top");
        listNames.add("jg");
        listNames.add("mid");
        listNames.add("bot");
        listNames.add("sup");

        mockLinerList = new HashMap<>();

        for (int i = 0; i < listNames.size(); i++) {
            Liner liner = new Liner(listNames.get(i));
            mockLinerList.put(listNames.get(i), liner);
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        connector = Mockito.spy(new Connector());

        initConnectorLists();
        connector.setLinerList(connectorLinerList);

        initMockLists();
    }

    @Test
    public void testSetUp() throws JsonProcessingException {
        String json = mapper.writeValueAsString(connectorLinerList);
        assertEquals("""
                        {"top":{"name":"top","flash":{"coolTime":300,"on":true}},"bot":{"name":"bot","flash":{"coolTime":300,"on":true}},"mid":{"name":"mid","flash":{"coolTime":300,"on":true}},"jg":{"name":"jg","flash":{"coolTime":300,"on":true}},"sup":{"name":"sup","flash":{"coolTime":300,"on":true}}}"""
                , json);
    }

    @Test
    public void testOnMessage1() throws JsonProcessingException {
        // top의 플이 없다고 useFlash를 서버에 전송된 이후, 서버에서 클라이언트들에게 보낸 메세지
        mockLinerList.get("sup").getFlash().setOn(false);
        assertEquals(false, mockLinerList.get("sup").getFlash().isOn());
        String json = mapper.writeValueAsString(mockLinerList.values().stream().toList());

        connector.onMessage(json);
        assertEquals(false, connector.getLinerList().get("sup").getFlash().isOn());
        Mockito.verify(connector, Mockito.times(1)).startCountFlash(connectorLinerList.get("sup"));
    }

    @Test
    public void testOnMessage2() throws JsonProcessingException {
        // top의 플이 없다고 useFlash를 서버에 전송된 이후, 서버에서 클라이언트들에게 보낸 메세지
        mockLinerList.get("sup").getFlash().setOn(false);
        mockLinerList.get("jg").getFlash().setOn(false);
        String json = mapper.writeValueAsString(mockLinerList.values().stream().toList());

        connector.onMessage(json);
        assertEquals(false, connector.getLinerList().get("sup").getFlash().isOn());
        assertEquals(false, connector.getLinerList().get("jg").getFlash().isOn());
    }

    @Test
    public void testOnMessage_supF_IsAlreadyUsed() throws JsonProcessingException {
        //서폿 플 이미 사용함
        connector.getLinerList().get("sup").getFlash().setOn(false);

        mockLinerList.get("sup").getFlash().setOn(false);
        String json = mapper.writeValueAsString(mockLinerList.values().stream().toList());

        connector.onMessage(json);
        assertEquals(false, connector.getLinerList().get("sup").getFlash().isOn());

        // 서폿 플이 이미 사용되었기 떄문에 서폿 플이 없는 메세지가 와도 서폿에 대한 startCountFlash를 호출하지 않음
        Mockito.verify(connector, Mockito.times(0)).startCountFlash(connectorLinerList.get("sup"));
    }

    @Test
    public void useFlash() throws Exception {
        connector.useFlash(mockLinerList.get("testliner"));
        Mockito.verify(connector, Mockito.times(1)).useFlash(connectorLinerList.get("fd"));
    }

    @Test
    public void flashOn() throws Exception {
        connector.flashOn(mockLinerList.get("testliner"));
        Mockito.verify(connector, Mockito.times(1)).flashOn(connectorLinerList.get("fd"));
    }

    @Test
    public void startCountFlashWhenFlashIsAlreadyUsed() throws IOException, InterruptedException {
        connectorLinerList.get("sup").getFlash().setOn(false);
        connector.startCountFlash(connectorLinerList.get("sup"));

        assertEquals("null", "g");

    }

    @Test
    public void startCountFlashWhenFlash() throws IOException, InterruptedException {
        connector.startCountFlash(connectorLinerList.get("sup"));
        assertEquals("null", "g");
    }

}
