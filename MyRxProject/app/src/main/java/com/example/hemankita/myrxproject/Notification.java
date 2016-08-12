package com.example.hemankita.myrxproject;

/**
 * Created by Hemankita on 8/6/2016.
 */
public class Notification {
    public static class LogIn extends Notification {
        public final String username;
        public LogIn(String username){this.username = username;}
    }
    public static class LogOut extends Notification {
        public final String username;
        public LogOut(String username){this.username = username;}
    }
    public static class Messages extends Notification {
        public final int id;
        public final String senderName;
        public final String subjectLine;
        public final String message;
        public final long timeToLive_ms;
        public Messages(int id,String senderName,String subjectLine,String message,long timeToLive_ms)
        {
            this.id=id;
            this.senderName=senderName;
            this.subjectLine=subjectLine;
            this.message=message;
            this.timeToLive_ms=timeToLive_ms;}
    }
}
