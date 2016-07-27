package com.example.hemankita.myproject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

/**
 * Created by Hemankita on 7/23/2016.
 */
public class ServerAPI {
    public final String API_VERSION = "0.4.0";
    private static String LOG       = "ServerAPI";
    public static HashMap<String,String> m = new HashMap();
    public static ArrayList<Message> msg = new ArrayList<>();
    public static HashMap<String,ServerAPI.UserInfo> umap = new HashMap<>();
    public static ArrayList<Message> msgval = new ArrayList<>();

    private static ServerAPI ourInstance;

    PublicKey serverKey=null;

    RequestQueue commandQueue;
    RequestQueue pseudoPushQueue;

    Crypto myCrypto;

    public static ServerAPI getInstance(Context context, Crypto crypto) {

        if(ourInstance==null){
            ourInstance = new ServerAPI(context);
            ourInstance.myCrypto = crypto;
        }
        return ourInstance;
    }

    private ServerAPI(Context context) {
        commandQueue = Volley.newRequestQueue(context);
        pseudoPushQueue = Volley.newRequestQueue(context);
    }

    private String myServerName = "SERVER_NAME_NOT_SPECIFIED";
    private String myServerPort = "25666";

    private String makeURL(String... args){
        return "http://"+myServerName+":"+myServerPort+"/"+ TextUtils.join("/",args);
    }

    private void getServerAddress(final String servername){
        (new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Log.d(LOG,"Address is: "+ InetAddress.getByName(servername).getHostAddress());
                    getStringCommand(makeURL("get-key"),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String key) {
                                    serverKey = Crypto.getPublicKeyFromString(key);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(LOG,"Couldn't get key",error);
                                }
                            });

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void setServerPort(final String serverPort){
        myServerPort = serverPort;
    }

    public void setServerName(final String serverName){

        myServerName = serverName;
        getServerAddress(serverName);
    }

    public void checkAPIVersion(){
        getStringCommand(makeURL("api-version"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if(s.equals(API_VERSION)) sendGoodAPIVersion(); else sendBadAPIVersion();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendCommandFailed("checkAPIVersion",error);
                    }
                });
    }

    public void register(final String username, String image, String publicKey) {
        putJSONCommand(makeURL("register"), keyValuePairs("username",username,
                "image",image,
                "public-key",publicKey),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        handleRegisterResponse(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendCommandFailed("register",error);
                    }
                });

    }

    private void handleRegisterResponse(JSONObject response){
        try {
            Log.d(LOG,"Response: status: " + response.getString("status") +
                    " reason: " + response.getString("reason"));
            if(response.getString("status").equals("ok"))
                sendRegistrationSucceeded();
            else
                sendRegistrationFailed(response.getString("reason"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getUserInfo(final String username){
        String url = makeURL("get-contact-info",username);
        Log.d(LOG,"getting user info with "+url);

        getJSONCommand(makeURL("get-contact-info",username),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if(status.equals("ok")) {
                                umap.put(response.getString("username"),new UserInfo(response.getString("username"),
                                        response.getString("image"),
                                        response.getString("key")));
                                Log.i("tagtagtag",umap.toString());
                                sendUserInfo(new UserInfo(response.getString("username"),
                                        response.getString("image"),
                                        response.getString("key")));
                            } else {
                                sendUserNotFound(username);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(LOG,"Response: " + response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendCommandFailed("getUserInfo",error);
                    }
                });
    }

    public boolean login(final String username,final Crypto crypto) {
        boolean login = false;
        if(serverKey!=null) {
            getStringCommand(makeURL("get-challenge", username),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String challenge) {
                            Log.d(LOG,"challenge string "+challenge);
                            if(challenge.equals("user-not-registered")){
                                sendLoginFailed("user not registered");
                            } else {
                                processChallengeAndLogin(challenge, username, crypto);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            sendCommandFailed("login", error);
                        }
                    });
            login = true;
        } else {
            sendLoginFailed("server key was null");
        }
        return login;
    }


    private void processChallengeAndLogin(final String challenge, final String username,final Crypto crypto) {
        try {
            byte[] decrypted = crypto.decryptRSA(Base64.decode(challenge, Base64.NO_WRAP));
            String response = Base64.encodeToString(Crypto.encryptRSA(decrypted, serverKey), Base64.NO_WRAP);

            putJSONCommand(makeURL("login"), keyValuePairs("username", username,
                    "response", response),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            handleLoginResponse(response);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            sendCommandFailed("login", error);
                        }
                    });
        } catch (Exception ex) {
            sendLoginFailed(ex.getMessage());
        }

    }

    private void handleLoginResponse(JSONObject response){
        try {
            if(response.getString("status").equals("ok"))
                sendLoginSucceeded();
            else
                sendLoginFailed(response.getString("reason"));
        } catch (JSONException e) {
            e.printStackTrace();
            sendLoginFailed("unable to parse JSON response");
        }
    }


    public void logout(final String username,final Crypto crypto) {
        if(serverKey!=null) {
            getStringCommand(makeURL("get-challenge", username),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String challenge) {
                            if(challenge.equals("user-not-registered")){
                                sendLogoutFailed("user not registered");
                            } else {
                                processChallengeAndLogout(challenge, username, crypto);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            sendCommandFailed("logout", error);
                        }
                    });
        } else {
            sendLogoutFailed("server key was null");
        }
    }


    private void processChallengeAndLogout(final String challenge, final String username,final Crypto crypto) {
        try{
            byte[] decrypted = crypto.decryptRSA(Base64.decode(challenge, Base64.NO_WRAP));
            String response = Base64.encodeToString(Crypto.encryptRSA(decrypted,serverKey),Base64.NO_WRAP);

            putJSONCommand(makeURL("logout"), keyValuePairs("username",username,
                    "response",response),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            handleLogoutResponse(response);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            sendCommandFailed("logout",error);
                        }
                    });
        } catch (Exception ex) {
            sendLogoutFailed(ex.getMessage());
        }
    }

    private void handleLogoutResponse(JSONObject response){
        try {
            if(response.getString("status").equals("ok"))
                sendLogoutSucceeded();
            else
                sendLogoutFailed(response.getString("reason"));
        } catch (JSONException e) {
            e.printStackTrace();
            sendLogoutFailed("unable to parse JSON response");
        }
    }

    public HashMap<String,String> registerContacts(final String username, ArrayList<String> names){
        final JSONObject json = new JSONObject();
        final ArrayList<String> n = names;
        Log.i("Namesssssssssssss",""+n);

        try {
            json.put("username",username);
            json.put("friends",new JSONArray(names));

            putJSONCommand(makeURL("register-friends"), json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {

                            Log.d(LOG, "register friends response " + jsonObject);
                            for(int i=0;i<n.size();i++) {
                                try {
                                    Log.d("tag", "register friendssss   "+n.get(i)+" " + jsonObject.getJSONObject("friend-status-map").getString(n.get(i)));
                                    m.put(n.get(i),jsonObject.getJSONObject("friend-status-map").getString(n.get(i)));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.d(LOG, "register friends error", volleyError);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("fffffff", " m  "+m);
        return m;
    }

    public Map returnMap()
    {
        Log.d("mappppp"," m in return  "+m);
        return m;
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    /*
     * TODO: This currently only supports polling
     */
    public ArrayList<Message> startPushListener(final String username){
        msgval.clear();
        String url = makeURL("wait-for-push",username);
        Log.d(LOG,"waiting for push with "+url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        msgval.addAll(handleNotifications(response));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                sendCommandFailed("pushListener",error);
            }
        });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        pseudoPushQueue.add(jsObjRequest);

        return msgval;

    }

    private ArrayList<Message> handleNotification(JSONObject notification){
        ArrayList<Message> messageval = new ArrayList<>();
        try {
            String type = notification.getString("type");
            if(type.equals("login")){
                sendContactLogin(notification.getString("username"));
            }
            if(type.equals("logout")){
                sendContactLogout(notification.getString("username"));
            }
            if(type.equals("message")){
                messageval = handleMessage(notification.getJSONObject("content"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messageval;
    }

    private String decryptAES64ToString(String aes64, SecretKey aesKey) throws UnsupportedEncodingException {
        byte[] bytes = Base64.decode(aes64,Base64.NO_WRAP);
        if(bytes==null) return null;
        bytes = Crypto.decryptAES(bytes, aesKey);
        if(bytes==null) return null;
        return new String(bytes,"UTF-8");
    }

    private ArrayList<Message> handleMessage(JSONObject message){

        Log.d(LOG,"Got message "+message);
        try{
            SecretKey aesKey = Crypto.getAESSecretKeyFromBytes(myCrypto.decryptRSA(Base64.decode(message.getString("aes-key"),Base64.NO_WRAP)));
            String sender = decryptAES64ToString(message.getString("sender"),aesKey);
            String recipient = decryptAES64ToString(message.getString("recipient"),aesKey);
            String body = decryptAES64ToString(message.getString("body"),aesKey);
            String subject = decryptAES64ToString(message.getString("subject-line"),aesKey);
            Long born = Long.parseLong(decryptAES64ToString(message.getString("born-on-date"),aesKey));
            Long ttl = Long.parseLong(decryptAES64ToString(message.getString("time-to-live"),aesKey));
            Log.d(LOG,sender+" says:");
            Log.d(LOG,subject+":");
            Log.d(LOG,body);
            Log.d(LOG,"ttl: "+ttl);
            Message m = new Message(sender+subject,subject,body,System.currentTimeMillis()+ttl);

            msg.add(m);

            sendMessageDelivered(sender,recipient,subject,body,born,ttl);
        } catch (Exception e) {
            Log.d(LOG,"Failed to parse message",e);
        }
        for(int i=0;i<msg.size();i++){
            Log.i("Message in ServerAPI",msg.get(i).getSenderName()+" "+msg.get(i).getSubjectLine());
        }
      return msg;
    }


    private ArrayList<Message> handleNotifications(JSONObject notifications){
        ArrayList<Message> msgval = new ArrayList<>();
        try {
            JSONArray array = notifications.getJSONArray("notifications");
            for(int index = 0; index < array.length(); index++){
               msgval = handleNotification(array.getJSONObject(index));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
       return msgval;
    }

    private String base64AESEncrypted(String clearText, SecretKey aesKey){
        try {
            return Base64.encodeToString(Crypto.encryptAES(clearText.getBytes("UTF-8"),aesKey), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /*
     * The messageReference can be any object. It is used to keep track of which messages
     * succeeded or failed
     */
    public void sendMessage(final Object messageReference,
                            PublicKey recipientKey,
                            String sender,
                            String recipient,
                            String subjectLine,
                            String body,
                            Long bornOnDate,
                            Long timeToLive) {
        SecretKey aesKey = Crypto.createAESKey();
        byte[] aesKeyBytes = aesKey.getEncoded();
        if(aesKeyBytes==null){
            Log.d(LOG,"AES key failed (this should never happen)");
            sendSendMessageFailed(messageReference,"AES key failed");
            return;
        }
        String base64encryptedAESKey =
                Base64.encodeToString(Crypto.encryptRSA(aesKeyBytes,recipientKey),
                        Base64.NO_WRAP);

        putJSONCommand(makeURL("send-message",recipient),
                keyValuePairs("aes-key", base64encryptedAESKey,
                        "sender",  base64AESEncrypted(sender, aesKey),
                        "recipient",  base64AESEncrypted(recipient, aesKey),
                        "subject-line",  base64AESEncrypted(subjectLine, aesKey),
                        "body",  base64AESEncrypted(body, aesKey),
                        "born-on-date",  base64AESEncrypted(bornOnDate.toString(), aesKey),
                        "time-to-live",  base64AESEncrypted(timeToLive.toString(), aesKey)),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handleSendResponse(messageReference, response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        sendCommandFailed("sendMessage",volleyError);
                        sendSendMessageFailed(messageReference,volleyError.getMessage());
                    }
                });

    }

    private void handleSendResponse(Object reference, JSONObject response){
        try {
            if (response.getString("status").equals("ok")) {
                sendSendMessageSucceeded(reference);
            } else {
                sendSendMessageFailed(reference, response.getString("reason"));
            }
        } catch (JSONException e) {
            sendSendMessageFailed(reference,"possible failure: unable to parse JSON response");
        }
    }

    private void getStringCommand(String url,
                                  Response.Listener<String> listener,
                                  Response.ErrorListener errorListener) {
        commandQueue.add(new StringRequest(Request.Method.GET, url, listener, errorListener));

    }

    private void getJSONCommand(String url,
                                Response.Listener<JSONObject> listener,
                                Response.ErrorListener errorListener){
        commandQueue.add(
                new JsonObjectRequest(Request.Method.GET, url, null, listener,errorListener));

    }

    private void putJSONCommand(String url, JSONObject json,
                                Response.Listener<JSONObject> listener,
                                Response.ErrorListener errorListener){
        commandQueue.add(
                new JsonObjectRequest(Request.Method.PUT, url, json, listener,errorListener));

    }

    private JSONObject keyValuePairs(String... args){
        JSONObject json = new JSONObject();
        try {
            for(int i=0; i+1<args.length;i+=2){
                json.put(args[i],args[i+1]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public class UserInfo{
        public final String username;
        public final String image;
        public final PublicKey publicKey;
        public UserInfo(String username, String image, String keyString){
            this.username = username;
            this.image = image;
            this.publicKey = Crypto.getPublicKeyFromString(keyString);
        }
    }



    public interface Listener {
        void onCommandFailed(String commandName, VolleyError volleyError);
        void onGoodAPIVersion();
        void onBadAPIVersion();
        void onRegistrationSucceeded();
        void onRegistrationFailed(String reason);
        void onLoginSucceeded();
        void onLoginFailed(String reason);
        void onLogoutSucceeded();
        void onLogoutFailed(String reason);
        void onUserInfo(UserInfo info);
        void onUserNotFound(String username);
        void onContactLogin(String username);
        void onContactLogout(String username);
        void onSendMessageSucceeded(Object key);
        void onSendMessageFailed(Object key, String reason);
        void onMessageDelivered(String sender,String recipient,
                                String subject,String body,
                                long born_on_date,
                                long time_to_live);

    }

    private ArrayList<Listener> myListeners = new ArrayList<>();

    public void registerListener(Listener listener){myListeners.add(listener);}
    public void unregisterListener(Listener listener){myListeners.remove(listener);}

    private void sendCommandFailed(String commandName, VolleyError volleyError){
        for(Listener listener : myListeners){
            listener.onCommandFailed(commandName,volleyError);
        }
    }

    private void sendGoodAPIVersion(){
        for(Listener listener : myListeners){
            listener.onGoodAPIVersion();
        }
    }

    private void sendBadAPIVersion(){
        for(Listener listener : myListeners){
            listener.onBadAPIVersion();
        }
    }

    private void sendRegistrationSucceeded(){
        for(Listener listener : myListeners){
            listener.onRegistrationSucceeded();
        }
    }
    private void sendRegistrationFailed(String reason){
        for(Listener listener : myListeners){
            listener.onRegistrationFailed(reason);
        }
    }

    private void sendLoginSucceeded(){
        for(Listener listener : myListeners){
            listener.onLoginSucceeded();
        }
    }

    private void sendLoginFailed(String reason){
        for(Listener listener : myListeners){
            listener.onLoginFailed(reason);
        }
    }

    private void sendLogoutSucceeded(){
        for(Listener listener : myListeners){
            listener.onLogoutSucceeded();
        }
    }

    private void sendLogoutFailed(String reason){
        for(Listener listener : myListeners){
            listener.onLogoutFailed(reason);
        }
    }
    private void sendUserInfo(UserInfo info){
        Log.i("User Info","I am in");
        for(Listener listener : myListeners){
            listener.onUserInfo(info);
        }
    }
    private void sendUserNotFound(String username){
        for(Listener listener : myListeners){
            listener.onUserNotFound(username);
        }
    }
    private void sendContactLogin(String username){
        for(Listener listener : myListeners){
            listener.onContactLogin(username);
        }
    }
    private void sendContactLogout(String username){
        for(Listener listener : myListeners){
            listener.onContactLogout(username);
        }
    }

    private void sendSendMessageSucceeded(Object key){
        for(Listener listener: myListeners){
            listener.onSendMessageSucceeded(key);
        }
    }

    private void sendSendMessageFailed(Object key, String reason){
        for(Listener listener: myListeners){
            listener.onSendMessageFailed(key, reason);
        }
    }

    private void sendMessageDelivered(String sender,String recipient,
                                      String subject,String body,
                                      long born_on_date,
                                      long time_to_live){
        for(Listener listener : myListeners) {
            listener.onMessageDelivered(
                    sender,recipient,
                    subject,body,
                    born_on_date,time_to_live);
        }
    }

}