package org.example.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Liner;

import java.util.List;
import java.util.Map;

public interface ServerForTest {
//    private Map<String, Liner> lineList;
//    private ObjectMapper mapper = new ObjectMapper();
//
//    public ServerForTest(Map<String, Liner> lineList) {
//        this.lineList = lineList;
//    }
//
//    public String getJsonLineList() throws JsonProcessingException {
//        List newList = lineList.values().stream().toList();
//        return mapper.writeValueAsString(newList);
//    }
//
//    //이 코드를 여기서 테스트하면 안될것같은데
//    public String whenUseFlash(String json) throws JsonProcessingException {
//        Liner liner = mapper.readValue(json, Liner.class);
//
//        lineList.get(liner.getName()).getFlash().setOn(liner.getFlash().isOn());
//
//        return getJsonLineList();
//    }

    public String whenUseFlash(String json);
}
