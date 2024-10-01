package org.example;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.example.connection.AbstractWebSocketConnector;

@Getter
@Setter
@JsonIgnoreProperties({"mapper", "flashCoolTime"})
@EqualsAndHashCode(exclude = {"mapper", "flashCoolTime"})
public class Flash {
    private static ObjectMapper mapper = new ObjectMapper();
    public static final int flashCoolTime = 300;
    private int coolTime = flashCoolTime;
    private boolean on = true;

    public void on() {
        on = true;
    }

    public void off() {
        on = false;
    }

    public void sendFlashStatus(Liner liner, AbstractWebSocketConnector connector) {
        try {
            String json = mapper.writeValueAsString(liner);
            connector.sendMessage("useFlash", json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
