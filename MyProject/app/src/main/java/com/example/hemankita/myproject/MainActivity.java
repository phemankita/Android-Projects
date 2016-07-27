package com.example.hemankita.myproject;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.Security;
import java.security.SecureRandom;
import java.security.spec.RSAKeyGenParameterSpec;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemWriter;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String DEBUG = "PUBLIC/PRIVATE KEY";
    public static final String MyPREFERENCES = "MyPreferences" ;
    public static final String prefs = "PreferencesName" ;
    public static final String PREF_FIRST1 = "BooleanName";
    public static final String PUBLICKEY = "PublicKey";
    public static final String PRIVATEKEY = "PrivateKey";
    SharedPreferences sharedpreferences;
    Handler handler ;
    Runnable refresh;

    KeyPair keyPair;
    List<Message> messages;
    MessageListAdapter adapter;
    ArrayList<Message> msgArray,muplist;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }
    ImageButton settings_button,contact_button,compose_button;
    ListView listView;
    String publickey,privatekey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.handler = new Handler();

        this.handler.postDelayed(m_Runnable,1000);

        MessageDBHelper msgdb = MessageDBHelper.getInstance(this);
        //if(msgdb == null){
        // msgdb.insertMessages();
        //}

        //if (!prefs.getBoolean("firstTime", false)) {

        // SharedPreferences.Editor editor = prefs.edit();
        // editor.putBoolean("firstTime", true);
        // editor.commit();
        // }

        //msgdb.clean(this);
        //msgdb.insertMessages();
        /**
         * CRUD Operations
         * */
        // Inserting Contacts
        Log.i("Insert: ", "Inserting ..");
        //condb.addContact(new Contact("Ravi", "img1" , "publickey"));
        //condb.addContact(new Contact("Srinivas", "img2","publickey"));

        // Reading all contacts
        Log.d("Reading: ", "Reading all messages..");
        //msgdb.insertMessages();
        //Message mh = new Message("Hi","Hello","How are you",System.currentTimeMillis()+50000);
        //msgdb.addMessage(mh);


        messages = msgdb.getAllMessages();
        //finish();
        for (Message m : messages) {
            String log = "Id: "+m.getSenderName()+" ,Name: " + m.getSubjectLine();
            // Writing Contacts to log
            Log.d("Message: ", log);
        }
        msgArray = new ArrayList<Message>();
        for (Message m : messages) {
            msgArray.add(m);
        }

        //msgArray.add(new Message("Message1 | Subject1",5));
        //msgArray.add(new Message("Message2 | Subject2",60));
        //msgArray.add(new Message("Message3 | Subject3",15));
        //ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_contactlistview, mobileArray);
        adapter=new MessageListAdapter(MainActivity.this,R.layout.activity_listview,msgArray);

        listView = (ListView) findViewById(R.id.message_list);
        listView.setAdapter(adapter);


        msgdb.close();
        Log.i("My value",Boolean.valueOf(SettingsActivity.login).toString());
       /* if(SettingsActivity.login == true) {
            ArrayList<Message> msgupd = new ArrayList<>();
            //msgupd = SettingsActivity.serverAPI.startPushListener("Hemankita");
            msgupd = SettingsActivity.muplist;
            if(msgupd!=null)
            {
                Log.i("Pushhhhhhhhhhhhh",""+msgupd);
                MessageDBHelper mupd = new MessageDBHelper(getApplicationContext());
                for (int i=0;i<msgupd.size();i++){
                    Log.i("Pushhhhhhhhhhhhh",""+msgupd.get(i).getSenderName());
                    Message m = msgupd.get(i);
                    mupd.addMessage(m);
                }
                mupd.close();
            }


        }*/
        // Locate the button in activity_main.xml
        settings_button = (ImageButton) findViewById(R.id.settingsButton);

        // Capture button clicks
        settings_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this,
                        SettingsActivity.class);
                startActivity(myIntent);
            }
        });

        contact_button = (ImageButton) findViewById(R.id.contactButton);

        contact_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contact_intent=new Intent(MainActivity.this,ContactsActivity.class);
                startActivity(contact_intent);
            }
        });

        compose_button = (ImageButton) findViewById(R.id.composeButton);

        compose_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent compose_intent=new Intent(MainActivity.this,ComposeActivity.class);
                startActivity(compose_intent);
            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {

                   /* muplist = SettingsActivity.serverAPI.msgval;
                    if (muplist != null) {
                        MessageDBHelper msg = new MessageDBHelper(getApplicationContext());
                        for (int i = 0; i < muplist.size(); i++) {
                            msg.addMessage(muplist.get(i));
                        }
                        msg.close();
                    }*/
            messages.clear();
            MessageDBHelper d =MessageDBHelper.getInstance(getApplicationContext());
            messages = d.getAllMessages();
            msgArray.clear();
            for (Message m : messages) {
                msgArray.add(m);
            }
            ((ArrayAdapter<Message>) listView.getAdapter()).notifyDataSetChanged();
            //onRestart();
            adapter.notifyDataSetChanged();
            MainActivity.this.handler.postDelayed(m_Runnable, 1000);
        }

    };




    private String encryptToBase64(String clearText){
        try {
            Log.d(DEBUG,"clear text is of length "+clearText.getBytes().length);
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding","SC");
            rsaCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] bytes = rsaCipher.doFinal(clearText.getBytes());
            Log.d(DEBUG,"cipher bytes is of length "+bytes.length);
            Log.d(DEBUG,"");

            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void readMessage(View view){
        MessageDBHelper msgdb = MessageDBHelper.getInstance(this);
        long time = msgdb.timing();
        View parentRow = (View) view.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        //Log.i("UI","number"+position);
        List<Message> messages = msgdb.getAllMessages();
        Message msg=messages.get(position);
        //Log.i("UI","number"+con.getContact());
        String senderName = msg.getSenderName();
        String subjectLine = msg.getSubjectLine();
        String message = msg.getMessage();

        long time_to_live = msg.getTimeToLive_ms()-time;
        long seconds =  (time_to_live / 1000);
        Log.i("time",Long.valueOf(time).toString());
        Intent rintent = new Intent(MainActivity.this, ReadActivity.class);
        rintent.putExtra("SNAME",senderName);
        rintent.putExtra("TTL",seconds);
        rintent.putExtra("SUBJECT",subjectLine);
        rintent.putExtra("BODY",message);
        startActivity(rintent);
    }
}