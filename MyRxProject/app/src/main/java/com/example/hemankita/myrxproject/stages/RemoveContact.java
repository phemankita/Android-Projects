package com.example.hemankita.myrxproject.stages;

import com.example.hemankita.myrxproject.WebHelper;

import org.json.JSONObject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Hemankita on 8/7/2016.
 */
public class RemoveContact implements Func1<String, Observable<String>> {

    final String server;
    final String username;
    final String friend;

    public RemoveContact(String server,String username,String friend){
        this.server=server;
        this.username=username;
        this.friend=friend;
    }

    @Override
    public Observable<String> call(String s) {
        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("friend", friend);
            JSONObject response = WebHelper.JSONPut(server + "/remove-friend", json);

            return Observable.just("User Removed"+response);
        }  catch (Exception e) {
            e.printStackTrace();
            return Observable.error(e);
        }

    }
}
