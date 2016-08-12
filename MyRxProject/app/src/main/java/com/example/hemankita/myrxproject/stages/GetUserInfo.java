package com.example.hemankita.myrxproject.stages;

import android.util.Log;

import com.example.hemankita.myrxproject.WebHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Hemankita on 8/7/2016.
 */
public class GetUserInfo implements Func1<String,Observable<HashMap<String,UserInfo>>> {
    final String username;
    final String server;
    public static HashMap<String,UserInfo> myUserMap = new HashMap<>();

    public GetUserInfo(String username,String server){
        this.username=username;
        this.server = server;
    }


    @Override
    public Observable<HashMap<String, UserInfo>> call(String user_response) {
        try {
            JSONObject userDetails = new JSONObject();
            userDetails.put("username",username);
            userDetails.put("response",user_response);
            //JSONObject response = WebHelper.JSONPut(server+"/get-contact-info/"+username,userDetails);
            String userInfo = WebHelper.StringGet(server+"/get-contact-info/"+username);

            JSONObject response = new JSONObject(userInfo);
            String status = response.getString("status");
            if(status.equals("ok")) {
                sendUserInfo(new UserInfo(response.getString("username"),
                        response.getString("image"),
                        response.getString("key")));
            } else {
                sendUserNotFound(username);
            }
            return Observable.just(myUserMap);
        }catch (Exception e) {
            e.printStackTrace();
            return Observable.error(e);
        }


    }

    private void sendUserNotFound(String username) {
        Log.i("GETUSERINFO","User not found");
    }


    private void sendUserInfo(UserInfo userInfo) {
        myUserMap.put(userInfo.username,userInfo);
    }
}
