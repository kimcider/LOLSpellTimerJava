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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ConnectorTest {
    static ObjectMapper mapper = new ObjectMapper();
    private Connector connector = Connector.getInstance();

    private HashMap<String, Liner> connectorLinerList;

    private ArrayList<String> getNameList(){
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
        for(String name : nameList){
            linerList.put(name, new Liner(name, connector));
        }

        return linerList;
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
                        {"top":{"name":"top","flash":{"coolTime":300,"on":true}},"bot":{"name":"bot","flash":{"coolTime":300,"on":true}},"mid":{"name":"mid","flash":{"coolTime":300,"on":true}},"jg":{"name":"jg","flash":{"coolTime":300,"on":true}},"sup":{"name":"sup","flash":{"coolTime":300,"on":true}}}"""
                , json);
    }

    @Test
    public void testOnMessage1() throws JsonProcessingException, URISyntaxException, InterruptedException {
        Liner mockSupLiner = Mockito.spy(connector.getLinerList().get("sup"));
        HashMap<String, Liner> mockLinerList = getLinerList();

        // top의 플이 없다고 useFlash를 서버에 전송된 이후, 서버에서 클라이언트들에게 보낸 메세지
        mockLinerList.get("sup").getFlash().setOn(false);
        assertEquals(false, mockLinerList.get("sup").getFlash().isOn());
        String json = mapper.writeValueAsString(mockLinerList.values().stream().toList());

        connector.onMessage(json);
        assertEquals(false, connector.getLinerList().get("sup").getFlash().isOn());
        Mockito.verify(mockSupLiner, Mockito.times(0)).startCount();
    }

    @Test
    public void testOnMessage2() throws JsonProcessingException, URISyntaxException, InterruptedException {
        HashMap<String, Liner> mockLinerList = getLinerList();

        // top의 플이 없다고 useFlash를 서버에 전송된 이후, 서버에서 클라이언트들에게 보낸 메세지
        mockLinerList.get("sup").getFlash().setOn(false);
        mockLinerList.get("jg").getFlash().setOn(false);
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
        connector.getLinerList().get("sup").getFlash().setOn(false);

        mockLinerList.get("sup").getFlash().setOn(false);
        String json = mapper.writeValueAsString(mockLinerList.values().stream().toList());

        connector.onMessage(json);
        assertEquals(false, connector.getLinerList().get("sup").getFlash().isOn());

        // 서폿 플이 이미 사용되었기 떄문에 서폿 플이 없는 메세지가 와도 서폿에 대한 startCountFlash를 호출하지 않음
        Mockito.verify(mockSupLiner, Mockito.never()).startCount();

    }


    // TODO: TestableConnector를 새로 만들어야겠다.
    // 지금 서버에 쏘면 게임하고있는애들한테 방해될까바 가짜테스터로 쏘고있는거자나
    // 이렇게 하지 말고, 각 Liner가
    @Test
    public void useFlash() throws Exception {
        HashMap<String, Liner> mockLinerList = getLinerList();

        mockLinerList.get("top").useFlash();
        //TODO: 이거 잘 되는건지 확인하기
        // TODO: 얘         Mockito.verify(mockSupLiner, Mockito.never()).startCount(); 이런거로 바꿔야겠네
        //왜 얘도되고
        Mockito.verify(connectorLinerList.get("top"), Mockito.times(1)).useFlash();
        //얘도되지?
        //  이거 혹시 null이라서 되는건가?
        Mockito.verify(connectorLinerList.get("as"), Mockito.times(1)).useFlash();

    }
    @Test
    public void flashOn() throws Exception {
        HashMap<String, Liner> mockLinerList = getLinerList();

        mockLinerList.get("testliner").flashOn();
        //TODO: 이거 잘 되는건지 확인하기
        Mockito.verify(connectorLinerList.get("testliner"), Mockito.times(1)).flashOn();
    }

    @Test
    public void startCountFlashWhenIsAlreadyUsed() throws IOException, InterruptedException {
        connectorLinerList.get("sup").getFlash().setOn(false);
        connectorLinerList.get("sup").startCount();

        assertEquals("null", "g");

    }

    @Test
    public void startCountFlashWhen() throws IOException, InterruptedException {
        connectorLinerList.get("sup").startCount();
        assertEquals("null", "g");
    }

}
