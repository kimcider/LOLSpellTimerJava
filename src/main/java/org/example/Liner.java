package org.example;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Liner {
    String name;
    Flash flash = new Flash();

    public Liner(){

    }
    public Liner(String name){
        this.name = name;
    }
    public Liner(String name, Flash flash){
        this.name = name;
        this.flash = flash;
    }
}
