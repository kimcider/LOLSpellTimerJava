package org.example;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties({"flashCoolTime"})
public class Flash {
    static final int flashCoolTime = 300;
    int coolTime = flashCoolTime;
    boolean on = true;
}
