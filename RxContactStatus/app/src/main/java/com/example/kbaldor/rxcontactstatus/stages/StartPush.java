package com.example.kbaldor.rxcontactstatus.stages;

import android.util.Log;

import com.example.kbaldor.rxcontactstatus.Notification;
import com.example.kbaldor.rxcontactstatus.WebHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Hemankita on 7/29/2016.
 */
public class StartPush implements Func1<Long, Observable<Notification>> {
    final String server;
    final String username;
    final List<String> contacts;
    ArrayList<Notification> notifications = new ArrayList<>();

    public StartPush(String server, String username, List<String> contacts){
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
           /* if(type.equals("message")){
                handleMessage(notification.getJSONObject("content"));
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    @Override
    public Observable<Notification> call(Long aLong) {
        try {

            String h = WebHelper.StringGet(server+"/wait-for-push/"+username);
            Log.i("inside observable",h);

            JSONObject response = new JSONObject(h);
            //JSONObject response = WebHelper.JSONPut(server+"/wait-for-push/"+username,json);


            // notifications = new ArrayList<>();
            handleNotifications(response);
            //Log.i("done done",response.toString());

          /*  JSONObject status = response.getJSONObject("friend-status-map");
            for(String contact : contacts){
                if(status.getString(contact).equals("logged-in")){
                    notifications.add(new Notification.LogIn(contact));
                } else {
                    notifications.add(new Notification.LogOut(contact));
                }
            }*/
            // Log.i("inside observable",notifications.toString());
            return Observable.from(notifications);
        } catch (Exception e) {
            e.printStackTrace();
            return Observable.error(e);
        }
    }
}
