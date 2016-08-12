package com.example.kbaldor.rxcontactstatus;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.kbaldor.rxcontactstatus.stages.GetChallengeStage;
import com.example.kbaldor.rxcontactstatus.stages.GetServerKeyStage;
import com.example.kbaldor.rxcontactstatus.stages.LogInStage;
import com.example.kbaldor.rxcontactstatus.stages.RegisterContactsStage;
import com.example.kbaldor.rxcontactstatus.stages.RegistrationStage;
import com.example.kbaldor.rxcontactstatus.stages.StartPush;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.TimeInterval;

public class MainActivity extends AppCompatActivity {

    Crypto myCrypto;

    String username = "RxHemankita";
    String server_name = "http://129.115.27.54:25666";
    listAdapter adapter;
    ListView listView;
    ArrayList<Notification> notifications;
    public static ArrayList<String> login = new ArrayList<>();
    public static ArrayList<String> logout = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final ArrayList<String> contacts = new ArrayList<>();
        contacts.add("alice");
        contacts.add("bob");
        //contacts.add("test");

        adapter=new listAdapter(MainActivity.this,R.layout.activity_contactlist,contacts);

        listView = (ListView) findViewById(R.id.message_list);
        listView.setAdapter(adapter);


        myCrypto = new Crypto(getPreferences(Context.MODE_PRIVATE));

        Observable.just(0) // the value doesn't matter, it just kicks things off
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .flatMap(new GetServerKeyStage(server_name))
                .flatMap(new RegistrationStage(server_name, username,
                                               getBase64Image(), myCrypto.getPublicKeyString()))
                .flatMap(new GetChallengeStage(server_name,username,myCrypto))
                .flatMap(new LogInStage(server_name, username))
                .flatMap(new RegisterContactsStage(server_name, username, contacts))
                .subscribe(new Observer<Notification>() {
            @Override
            public void onCompleted() {

                // now that we have the initial state, start polling for updates

                Observable.interval(0,1, TimeUnit.SECONDS, Schedulers.newThread())
                        .flatMap(new StartPush(server_name,username,contacts))
//                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                          //.take(5)// would only poll five times
                        //.takeWhile(<predicate>)
                        // could stop based on a flag variable
                        .subscribe(new Observer<Notification>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {}

                            @Override
                            public void onNext(Notification notification) {
                                Log.d("LOG","Next "+ notification);
                                if(notification instanceof Notification.LogIn) {
                                    Log.d("LOG","User "+((Notification.LogIn)notification).username+" is logged in");
                                    if(logout!=null) {
                                        if (logout.contains(((Notification.LogIn) notification).username)) {
                                            logout.remove(((Notification.LogIn) notification).username);
                                        }
                                    }

                                    if(login!=null)
                                    {
                                        if(!login.contains(((Notification.LogIn)notification).username))
                                        {

                                            login.add(((Notification.LogIn)notification).username);
                                        }
                                    }

                                    if(login==null && (((Notification.LogIn)notification).username!=null)){
                                        login.add(((Notification.LogIn)notification).username);
                                    }
                                }
                                if(notification instanceof Notification.LogOut) {
                                    Log.d("LOG","User "+((Notification.LogOut)notification).username+" is logged out");
                                    if (login != null) {
                                        if (login.contains(((Notification.LogOut) notification).username)) {
                                            login.remove(((Notification.LogOut) notification).username);
                                        }
                                    }
                                    if (logout != null){
                                        if (!logout.contains(((Notification.LogOut) notification).username)) {
                                            logout.add(((Notification.LogOut) notification).username);
                                        }
                                    }
                                    if(logout==null && ((Notification.LogOut)notification).username!=null){
                                        logout.add(((Notification.LogOut)notification).username);
                                    }
                                }
                               // Observable.from(Notification).flatMap(new StartPush(server_name,username,contacts));
                                //Log.d("OBSERVER","Got a message (probably from bob)");
                                try {
                                    adapter.notifyDataSetChanged();
                                }catch (Exception ex){
                                   // Log.d("NOT EXCEPTION","What?");
                                    Log.d("EXCEPTION","It broke!",ex);
                                }



                            }
                        });
            }

            @Override
            public void onError(Throwable e) {
                Log.d("LOG","Error: ",e);
            }

            @Override
            public void onNext(Notification notification) {
                // handle initial state here
                Log.d("LOG","Next "+ notification);
                if(notification instanceof Notification.LogIn) {
                    Log.d("LOG","User "+((Notification.LogIn)notification).username+" is logged in");
                    if(logout!=null) {
                        if (logout.contains(((Notification.LogIn) notification).username)) {
                            logout.remove(((Notification.LogIn) notification).username);
                        }
                    }

                    if(login!=null)
                    {
                    if(!login.contains(((Notification.LogIn)notification).username))
                        {

                            login.add(((Notification.LogIn)notification).username);
                        }
                    }

                    if(login==null && (((Notification.LogIn)notification).username!=null)){
                        login.add(((Notification.LogIn)notification).username);
                    }
                }
                if(notification instanceof Notification.LogOut) {
                    Log.d("LOG", "User " + ((Notification.LogOut) notification).username + " is logged out");
                    if (login != null) {
                        if (login.contains(((Notification.LogOut) notification).username)) {
                            login.remove(((Notification.LogOut) notification).username);
                        }
                    }
                    if (logout != null){
                        if (!logout.contains(((Notification.LogOut) notification).username)) {
                            logout.add(((Notification.LogOut) notification).username);
                        }
                    }
                    if(logout==null && ((Notification.LogOut)notification).username!=null){
                        logout.add(((Notification.LogOut)notification).username);
                    }
                }
            }
        });

    }



    String getBase64Image(){
        InputStream is;
        byte[] buffer = new byte[0];
        try {
            is = getAssets().open("images/smilereader.png");
            buffer = new byte[is.available()];
            is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(buffer,Base64.DEFAULT).trim();
    }

    boolean register(String username, String base64Image, String keyString){
        return true;
    }
}
