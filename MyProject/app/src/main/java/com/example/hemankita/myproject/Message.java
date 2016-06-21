package com.example.hemankita.myproject;

/**
 * Created by Hemankita on 6/17/2016.
 */
public class Message {
    private String message;
    private long bornTime_ms;
    private long timeToLive_ms;

    public String getMessage()
    {
        return message;
    }
    public  long getTimeToLive_ms()
    {
        return timeToLive_ms;
    }
    public void setMessage(String message){
        this.message=message;
    }
    public void setTimeToLive_ms(long timeToLive_ms){
        this.timeToLive_ms=timeToLive_ms;
    }


    public float percentLeftToLive(){
        float percent =  1 - (System.currentTimeMillis() - bornTime_ms)/((float)timeToLive_ms);
        if (percent < 0 ) return 0.f;
        return percent;
    }

    public Message(String message, long timeToLive_ms){
        this.setMessage(message);
        this.setTimeToLive_ms(timeToLive_ms);
        this.bornTime_ms = System.currentTimeMillis();
    }

    public String toString(){
        return message;
    }

}
