package org.example;

public class Flash {
    static final int flashCoolTime = 300;
    int coolTime = flashCoolTime;
    boolean on = true;

    public int getFlashCoolTime(){
        return flashCoolTime;
    }

    public int getCoolTime(){
        return coolTime;
    }

    public void setFlashCoolTime(int time){
        coolTime = time;
    }

    boolean getOn(){
        return on;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
