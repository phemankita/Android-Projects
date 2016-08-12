package com.example.hemankita.myrxproject.stages;

import com.example.hemankita.myrxproject.SettingsActivity;
import com.example.hemankita.myrxproject.WebHelper;


import org.json.JSONObject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by kbaldor on 7/28/16.
 */
public class LogInStage implements Func1<String, Observable<String>> {

    final String server;
    final String username;


    public LogInStage(String server, String username){
        this.server = server;
        this.username = username;
    }

    @Override
    public Observable<String> call(String challenge_response)  {
        try {
            JSONObject userDetails = new JSONObject();
            userDetails.put("username",username);
            userDetails.put("response",challenge_response);
            JSONObject response = WebHelper.JSONPut(server+"/login",userDetails);
            if (response.getString("status").equals("ok")) {
                SettingsActivity.login=true;
            }
            return Observable.just(response.getString("status"));
        } catch (Exception e) {
            e.printStackTrace();
            return Observable.error(e);
        }
    }
}

