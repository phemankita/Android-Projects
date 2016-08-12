package com.example.hemankita.myrxproject.stages;

import com.example.hemankita.myrxproject.Notification;
import com.example.hemankita.myrxproject.WebHelper;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Hemankita on 8/7/2016.
 */
public class AddContact implements Func1<String, Observable<Notification>> {
    final String server;
    final String username;
    final String friend;

    public AddContact(String server,String username,String friend){
        this.server=server;
        this.username=username;
        this.friend=friend;
    }


    @Override
    public Observable<Notification> call(String s) {

        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("friend", friend);
            JSONObject response = WebHelper.JSONPut(server+"/add-friend", json);
            Notification notification;
            JSONObject status = response.getJSONObject("friend-status-map");
            if(status.getString(friend).equals("logged-in")){
                notification = new Notification.LogIn(friend);
            } else {
                notification = new Notification.LogOut(friend);
            }
            return Observable.just(notification);
        }  catch (Exception e) {
            e.printStackTrace();
            return Observable.error(e);
        }

    }
}
