package org.example.counterLabel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Connector;
import org.example.Liner;

import java.util.ArrayList;
import java.util.HashMap;

public class TestCounterLabel {
    static ObjectMapper mapper = new ObjectMapper();
    private Connector connector;

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

    private HashMap<String, Liner> getLinerList(){
        ArrayList<String> nameList = getNameList();

        HashMap<String, Liner> linerList = new HashMap();
        for(String name : nameList){
            linerList.put(name, new Liner(name));
        }

        return linerList;
    }
}
