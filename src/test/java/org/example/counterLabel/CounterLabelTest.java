package org.example.counterLabel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Connector;
import org.example.Liner;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class CounterLabelTest {
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

    private HashMap<String, Liner> getLinerList() throws URISyntaxException, InterruptedException {
        ArrayList<String> nameList = getNameList();

        HashMap<String, Liner> linerList = new HashMap();
        for(String name : nameList){
            linerList.put(name, new Liner(name, connector));
        }

        return linerList;
    }
}
