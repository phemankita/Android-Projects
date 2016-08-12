package com.example.hemankita.myrxproject;

/**
 * Created by Hemankita on 6/17/2016.
 */
public class Message {
    private int id;
    private String senderName;
    private String subjectLine;
    private String message;
    private long bornTime_ms;
    private long timeToLive_ms;

    public int getId() {return id;}
    public String getSenderName() {return senderName;}
    public String getSubjectLine() {return subjectLine;}
    public String getMessage()
    {
        return message;
    }
    public long getBornTime_ms() {return bornTime_ms;}
    public  long getTimeToLive_ms()
    {
        return timeToLive_ms;
    }
    public void setSenderName(String senderName) {this.senderName=senderName;}
    public void setSubjectLine(String subjectLine) {this.subjectLine=subjectLine;}
    public void setMessage(String message){
        this.message=message;
    }
    public void setTimeToLive_ms(long timeToLive_ms){
        this.timeToLive_ms=timeToLive_ms;
    }
    public void setBornTime_ms(long bornTime_ms){this.bornTime_ms = bornTime_ms;}
    public void setId(int id){this.id=id;}



    public float percentLeftToLive(){
        float percent =  1 - (System.currentTimeMillis() - bornTime_ms)/((float)timeToLive_ms);
        if (percent < 0 ) return 0.f;
        return percent;
    }
    public Message(){}

    public Message(int id, String senderName, String subjectLine, String message, long timeToLive_ms){
        this.id=id;
        this.senderName=senderName;
        this.subjectLine=subjectLine;
        this.message=message;
        this.timeToLive_ms=timeToLive_ms;
        //this.bornTime_ms = System.currentTimeMillis();
    }

    public String toString(){
        return message;
    }

}
