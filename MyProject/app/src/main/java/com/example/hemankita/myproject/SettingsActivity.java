package com.example.hemankita.myproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.spongycastle.util.encoders.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    EditText serverName,userName;
    PublicKey pkey;
    ImageView contact_image;
    public static ServerAPI serverAPI;
    public static boolean login= false;
    public static boolean logout= false;
    Crypto myCrypto;
    public static HashMap<String,ServerAPI.UserInfo> myUserMap = new HashMap<>();
    HashMap<String,String> logStatus = new HashMap<>();
    String user;
    public static boolean register = false;
    ArrayList<Message> msgupd = new ArrayList<>();
    Handler handler ;
    HashMap<String,String> m = new HashMap();
    ArrayList<Message> muplist ;
    public static ArrayList<Message> nmuplist = new ArrayList<>();
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
        this.handler = new Handler();

        this.handler.postDelayed(m_Runnable,3000);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String sname = getPreferences(Context.MODE_PRIVATE).getString("ServerName","129.115.27.54");
        ((EditText)findViewById(R.id.serverAddress)).setText(sname);

        userName = (EditText)findViewById(R.id.personName);
        EditText keyValue = (EditText) findViewById(R.id.keyPair);
        //Intent intent = new Intent(SettingsActivity.this,SettingsActivity.class);

        String uName = "Hemankita";
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
        } /*catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }*/ catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        serverAPI = ServerAPI.getInstance(this.getApplicationContext(),
                myCrypto);

        serverAPI.setServerName(getServerName());
        serverAPI.setServerPort("25666");
        userName.setText(uName);
        contact_image = (ImageView) findViewById(R.id.imagePerson);
        contact_image.setImageResource(R.drawable.contact);
        keyValue.setText(pkey.toString());
        //startActivity(intent);

        serverAPI.registerListener(new ServerAPI.Listener() {
            @Override
            public void onCommandFailed(String commandName, VolleyError volleyError) {
                Toast.makeText(SettingsActivity.this,String.format("command %s failed!",commandName),
                        Toast.LENGTH_SHORT).show();
                volleyError.printStackTrace();
            }

            @Override
            public void onGoodAPIVersion() {
                Toast.makeText(SettingsActivity.this,"API Version Matched!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBadAPIVersion() {
                Toast.makeText(SettingsActivity.this,"API Version Mismatch!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRegistrationSucceeded() {
                Toast.makeText(SettingsActivity.this,"Registered!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRegistrationFailed(String reason) {
                Toast.makeText(SettingsActivity.this,"Not registered!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoginSucceeded() {
                Toast.makeText(SettingsActivity.this,"Logged in!", Toast.LENGTH_SHORT).show();
                login=true;
            }

            @Override
            public void onLoginFailed(String reason) {
                Toast.makeText(SettingsActivity.this,"Not logged in : "+reason, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLogoutSucceeded() {
                Toast.makeText(SettingsActivity.this,"Logged out!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLogoutFailed(String reason) {
                Toast.makeText(SettingsActivity.this,"Not logged out!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserInfo(ServerAPI.UserInfo info) {
                myUserMap.put(info.username,info);
                Log.i("infousername",info.username);
            }


            @Override
            public void onUserNotFound(String username) {
                Toast.makeText(SettingsActivity.this,String.format("user %s not found!",username),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onContactLogin(String username) {
                Toast.makeText(SettingsActivity.this,String.format("user %s logged in",username),Toast.LENGTH_SHORT).show();
                ArrayList<String> loginUsers = new ArrayList<String>();
                loginUsers.add(username);
                Log.i("Logged in users list",loginUsers.toString());
                for(int i=0;i<loginUsers.size();i++){
                    ContactDBHelper c = new ContactDBHelper(getApplicationContext());
                    c.updateContact(loginUsers.get(i),"logged-in");
                    c.close();
                }
            }

            @Override
            public void onContactLogout(String username) {
                Toast.makeText(SettingsActivity.this,String.format("user %s logged out",username),Toast.LENGTH_SHORT).show();
                ArrayList<String> logoutUsers = new ArrayList<String>();
                logoutUsers.add(username);
                Log.i("Logged in users list",logoutUsers.toString());
                for(int i=0;i<logoutUsers.size();i++){
                    ContactDBHelper c = new ContactDBHelper(getApplicationContext());
                    c.updateContact(logoutUsers.get(i),"logged-out");
                    c.close();
                }
            }

            @Override
            public void onSendMessageSucceeded(Object key) {
                Toast.makeText(SettingsActivity.this,String.format("sent a message"),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSendMessageFailed(Object key, String reason) {
                Toast.makeText(SettingsActivity.this,String.format("failed to send a message"),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessageDelivered(String sender, String recipient, String subject, String body, long born_on_date, long time_to_live) {
                Toast.makeText(SettingsActivity.this,String.format("got message from %s",sender),Toast.LENGTH_SHORT).show();
                MessageDBHelper msg = new MessageDBHelper(getApplicationContext());
                msg.addMessage(new Message(MainActivity.counter++,sender,subject,body,System.currentTimeMillis()+time_to_live));
                //System.currentTimeMillis()+(born_on_date/17460000)+time_to_live
                msg.close();
            }
        });


        registerButton = (Button) findViewById(R.id.RegisterButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverAPI.setServerName(getServerName());

                //API check
                serverAPI.checkAPIVersion();

                //Registering
                InputStream is;
                byte[] buffer = new byte[0];
                try {
                    is = getAssets().open("images/smilereader.png");
                    buffer = new byte[is.available()];
                    is.read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String username = ((EditText)findViewById(R.id.personName)).getText().toString();
                serverAPI.register(username, android.util.Base64.encodeToString(buffer, android.util.Base64.DEFAULT).trim(), myCrypto.getPublicKeyString());
                serverAPI.login(getUserName(),myCrypto);
                ContactDBHelper contList = new ContactDBHelper(getApplicationContext());
                List<Contact> clist = new ArrayList<Contact>();
                clist=contList.getAllContacts();
                //Register contacts
                ArrayList<String> contacts = new ArrayList<>();
                for(int i=0;i<clist.size();i++){
                    contacts.add(clist.get(i).getContact());
                }
                contList.close();
                //contacts.add("alice");
                //contacts.add("cathy");
                //contacts.add("mouli");
                //contacts.add("bob");
                //Log.i("dataaaaaaaaaaaaa",""+contacts);
                serverAPI.registerContacts(getUserName(), contacts);
                login=true;
                //push listener

                //serverAPI.getUserInfo("alice");
                //serverAPI.getUserInfo("cathy");
                //serverAPI.getUserInfo("bob");
                //serverAPI.getUserInfo("kbaldor2");

            }
        });

        loginButton = (Button) findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverAPI.setServerName(getServerName());
                serverAPI.startPushListener(getUserName());

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
                serverAPI.logout(getUserName(),myCrypto);
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

    private final Runnable m_Runnable = new Runnable() {
        public void run()

        {
            //SettingsActivity.serverAPI.startPushListener("Hemankita");
            if (login && !logout) {
                ContactDBHelper contList = new ContactDBHelper(getApplicationContext());
                List<Contact> clist = new ArrayList<Contact>();
                clist = contList.getAllContacts();
                ArrayList<String> contacts = new ArrayList<>();
                for (int i = 0; i < clist.size(); i++) {
                    contacts.add(clist.get(i).getContact());
                }

                serverAPI.startPushListener("Hemankita");
            }




        SettingsActivity.this.handler.postDelayed(m_Runnable,3000);
    }

    };


}
