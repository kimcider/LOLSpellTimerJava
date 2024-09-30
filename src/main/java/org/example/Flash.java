package org.example;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.example.connection.AbstractWebSocketConnector;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Getter
@Setter
@JsonIgnoreProperties({"mapper", "flashCoolTime"})
public class Flash {
    private static ObjectMapper mapper = new ObjectMapper();
    public static final int flashCoolTime = 300;
    private int coolTime = flashCoolTime;
    private boolean on = true;

    public void sendFlashStatus(Liner liner, AbstractWebSocketConnector connector){
        try{
            String json = mapper.writeValueAsString(liner);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + connector.getServerURI() + "/useFlash"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            connector.getClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void on(){
        on = true;
    }
    public void off(){
        on = false;
    }
}
