package org.example;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Flash getFlash() {
        return flash;
    }

    public void setFlash(Flash flash) {
        this.flash = flash;
    }
}
