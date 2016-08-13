package com.example.hemankita.myrxproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hemankita.myrxproject.stages.GetChallengeStage;
import com.example.hemankita.myrxproject.stages.GetServerKeyStage;
import com.example.hemankita.myrxproject.stages.LogInStage;
import com.example.hemankita.myrxproject.stages.LogOutStage;
import com.example.hemankita.myrxproject.stages.RegisterContactsStage;
import com.example.hemankita.myrxproject.stages.RegistrationStage;
import com.example.hemankita.myrxproject.stages.StartPush;

import org.spongycastle.util.encoders.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class SettingsActivity extends AppCompatActivity {
    EditText serverName,userName;
    PublicKey pkey;
    ImageView contact_image;
    public static boolean login= false;
    public static boolean logout= false;
    Crypto myCrypto;
    Button loginButton,registerButton,LogoutButton;
    private String getUserName(){
        return ((EditText)findViewById(R.id.personName)).getText().toString();
    }

    private String getServerName(){
        return ((EditText)findViewById(R.id.serverAddress)).getText().toString();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String sname = getPreferences(Context.MODE_PRIVATE).getString("ServerName","129.115.27.54");
        ((EditText)findViewById(R.id.serverAddress)).setText(sname);

        userName = (EditText)findViewById(R.id.personName);
        EditText keyValue = (EditText) findViewById(R.id.keyPair);
        //Intent intent = new Intent(SettingsActivity.this,SettingsActivity.class);

        final String uName = "Hemankita";
        final String server_name = "http://129.115.27.54:25666";
        myCrypto = new Crypto(getPreferences(Context.MODE_PRIVATE));
        myCrypto.saveKeys(getPreferences(Context.MODE_PRIVATE));
        String keypair = myCrypto.getPublicKeyString();
        Log.i("myKey Settings",keypair);
        //intent.putExtra("KeyPair",keypair);
        //String kpair = intent.getStringExtra("KeyPair");
        try {
            byte[] binCpk = Base64.decode(keypair);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(binCpk);
            pkey = keyFactory.generatePublic(publicKeySpec);
            Log.i("pKey",pkey.toString());
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }  catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        userName.setText(uName);
        contact_image = (ImageView) findViewById(R.id.imagePerson);
        contact_image.setImageResource(R.drawable.contact);
        keyValue.setText(pkey.toString());
        //startActivity(intent);

        final ContactDBHelper con =new ContactDBHelper(getApplicationContext());
        final List<Contact> cont = con.getAllContacts();
        final List<String> contacts = new ArrayList<>();
        for(int i=0;i<cont.size();i++){
            contacts.add(cont.get(i).getContact());
        }

        con.close();

        registerButton = (Button) findViewById(R.id.RegisterButton);

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Observable.just(0) // the value doesn't matter, it just kicks things off
                        .observeOn(Schedulers.newThread())
                        //.subscribeOn(Schedulers.newThread())
                        .flatMap(new GetServerKeyStage(server_name))
                        .flatMap(new RegistrationStage(server_name, uName,
                                getBase64Image(), myCrypto.getPublicKeyString()))
                        .flatMap(new GetChallengeStage(server_name,uName,myCrypto))
                        .flatMap(new LogInStage(server_name, uName))
                        .flatMap(new RegisterContactsStage(server_name, uName, contacts))
                        //.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Notification>() {
                            @Override
                            public void onCompleted() {

                                // now that we have the initial state, start polling for updates
                                if(!logout) {
                                Observable.interval(0, 5, TimeUnit.SECONDS, Schedulers.newThread())
                                        .subscribeOn(Schedulers.newThread())
                                        .flatMap(new StartPush(myCrypto, server_name, uName, contacts))
//                        .subscribeOn(AndroidSchedulers.mainThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        //.take(5)// would only poll five times
                                       /* .takeWhile(new Func1<Notification, Boolean>() {
                                            @Override
                                            public Boolean call(Notification notification) {
                                                return logout;
                                            }
                                        })*/ // could stop based on a flag variable
                                        .subscribe(new Observer<Notification>() {
                                            @Override
                                            public void onCompleted() {

                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.e("ERROR_LOG", "a bad thing");
                                                Log.i("ERROR LOG", "e.getMessage(): " + e.getMessage());
                                                Log.e("ERROR_LOG", "a bad thing", e);
                                            }

                                            @Override
                                            public void onNext(Notification notification) {
                                                //Log.d("LOG","Next "+ notification);
                                                if (notification instanceof Notification.LogIn) {
                                                    Log.d("LOG", "User " + ((Notification.LogIn) notification).username + " is logged in");
                                                    ContactDBHelper con = new ContactDBHelper(getApplicationContext());
                                                    con.updateContact(((Notification.LogIn) notification).username, "logged-in");
                                                    con.close();
                                                }
                                                if (notification instanceof Notification.LogOut) {
                                                    Log.d("LOG", "User " + ((Notification.LogOut) notification).username + " is logged out");
                                                    ContactDBHelper con = new ContactDBHelper(getApplicationContext());
                                                    con.updateContact(((Notification.LogOut) notification).username, "logged-out");
                                                    con.close();
                                                }
                                                if (notification instanceof Notification.Messages) {
                                                    Log.d("LOG", "User " + ((Notification.Messages) notification).senderName + " sent you a message");
                                                    MessageDBHelper msg = new MessageDBHelper(getApplicationContext());
                                                    Message m = new Message(MainActivity.counter++,
                                                            ((Notification.Messages) notification).senderName,
                                                            ((Notification.Messages) notification).subjectLine,
                                                            ((Notification.Messages) notification).message,
                                                            ((Notification.Messages) notification).timeToLive_ms + System.currentTimeMillis());
                                                    msg.addMessage(m);
                                                    msg.close();
                                                }

                                            }
                                        });
                            }
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
                                    ContactDBHelper con = new ContactDBHelper(getApplicationContext());
                                    con.updateContact(((Notification.LogIn)notification).username,"logged-in");
                                    con.close();

                                }
                                if(notification instanceof Notification.LogOut) {
                                    Log.d("LOG", "User " + ((Notification.LogOut) notification).username + " is logged out");
                                    ContactDBHelper con = new ContactDBHelper(getApplicationContext());
                                    con.updateContact(((Notification.LogOut)notification).username,"logged-out");
                                    con.close();
                                }
                            }
                        });


            }
        });

        loginButton = (Button) findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SettingsActivity.this,
                        MainActivity.class);
                startActivity(in);

            }
        });
        LogoutButton = (Button) findViewById(R.id.LogoutButton);
        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout = true;
                Observable.just(0) // the value doesn't matter, it just kicks things off
                        .observeOn(Schedulers.newThread())
                        .subscribeOn(Schedulers.newThread())
                        .flatMap(new GetServerKeyStage(server_name))
                        .flatMap(new RegistrationStage(server_name, uName,
                                getBase64Image(), myCrypto.getPublicKeyString()))
                        .flatMap(new GetChallengeStage(server_name,uName,myCrypto))
                        .flatMap(new LogOutStage(server_name, uName))
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(String s) {
                                logout=true;
                                Toast.makeText(SettingsActivity.this,"Logged Out",Toast.LENGTH_SHORT).show();
                                Log.i("LOGGED OUT","logged out");
                            }
                        });

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        String serverName = ((EditText)findViewById(R.id.serverAddress)).getText().toString();
        getPreferences(Context.MODE_PRIVATE).edit().putString("ServerName",serverName).commit();
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
        return android.util.Base64.encodeToString(buffer, android.util.Base64.DEFAULT).trim();
    }

}
