package org.example;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Liner {
    String name;
    Flash flash;

    public Liner(){
        flash = new Flash();
    }
    public Liner(String name){
        this.name = name;
        flash = new Flash();
    }
}
