package com.example.hemankita.myrxproject.stages;

import android.util.Base64;
import android.util.Log;

import com.example.hemankita.myrxproject.Crypto;
import com.example.hemankita.myrxproject.MainActivity;
import com.example.hemankita.myrxproject.Message;
import com.example.hemankita.myrxproject.MessageDBHelper;
import com.example.hemankita.myrxproject.Notification;
import com.example.hemankita.myrxproject.WebHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Hemankita on 7/29/2016.
 */
public class StartPush implements Func1<Long, Observable<Notification>> {
    final Crypto myCrypto;
    final String server;
    final String username;
    final List<String> contacts;
    ArrayList<Notification> notifications = new ArrayList<>();
    ArrayList<Message> messages = new ArrayList<>();

    public StartPush(Crypto myCrypto,String server, String username, List<String> contacts){
        this.myCrypto = myCrypto;
        this.server = server;
        this.username = username;
        this.contacts = contacts;
    }

    private void handleNotifications(JSONObject notifications) {

        JSONArray array = null;

        try {
            array = notifications.getJSONArray("notifications");
            for(int index = 0; index < array.length(); index++){
                handleNotification(array.getJSONObject(index));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
    }

    private void handleNotification(JSONObject notification) {
        //Log.i("inside observable",notification.toString());
        try {
            String type = notification.getString("type");
            if(type.equals("login")){
                notifications.add(new Notification.LogIn(notification.getString("username")));
                //sendContactLogin(notification.getString("username"));
            }
            if(type.equals("logout")){
                notifications.add(new Notification.LogOut(notification.getString("username")));
                //sendContactLogout(notification.getString("username"));
            }
            if(type.equals("message")){
                handleMessage(notification.getJSONObject("content"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(JSONObject message) {
        Log.d("LOG","Got message "+message);
        try{
            SecretKey aesKey = Crypto.getAESSecretKeyFromBytes(myCrypto.decryptRSA(Base64.decode(message.getString("aes-key"),Base64.NO_WRAP)));
            String sender = decryptAES64ToString(message.getString("sender"),aesKey);
            String recipient = decryptAES64ToString(message.getString("recipient"),aesKey);
            String body = decryptAES64ToString(message.getString("body"),aesKey);
            String subject = decryptAES64ToString(message.getString("subject-line"),aesKey);
            Long born = Long.parseLong(decryptAES64ToString(message.getString("born-on-date"),aesKey));
            Long ttl = Long.parseLong(decryptAES64ToString(message.getString("time-to-live"),aesKey));
            Log.d("LOG",sender+" says:");
            Log.d("LOG",subject+":");
            Log.d("LOG",body);
            Log.d("LOG","ttl: "+ttl);
            sendMessageDelivered(sender,recipient,subject,body,born,ttl);
        } catch (Exception e) {
            Log.d("LOG","Failed to parse message",e);
        }
    }

    private void sendMessageDelivered(String sender, String recipient, String subject, String body, Long born, Long ttl) {
    Log.i("LOG","Got message from"+sender);
        notifications.add(new Notification.Messages(MainActivity.counter++,sender,subject,body,ttl));
    }

    private String decryptAES64ToString(String aes64, SecretKey aesKey) throws UnsupportedEncodingException {
        byte[] bytes = Base64.decode(aes64,Base64.NO_WRAP);
        if(bytes==null) return null;
        bytes = Crypto.decryptAES(bytes, aesKey);
        if(bytes==null) return null;
        return new String(bytes,"UTF-8");
    }


    @Override
    public Observable<Notification> call(Long aLong) {
        try {

            String h = WebHelper.StringGet(server+"/wait-for-push/"+username);
            Log.i("inside observable",h);

            JSONObject response = new JSONObject(h);

            notifications.clear();
            handleNotifications(response);

            return Observable.from(notifications);
        } catch (Exception e) {
            e.printStackTrace();
            return Observable.error(e);
        }
    }
}
