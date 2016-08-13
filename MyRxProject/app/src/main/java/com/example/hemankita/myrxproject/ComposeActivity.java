package com.example.hemankita.myrxproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.hemankita.myrxproject.stages.GetServerKeyStage;
import com.example.hemankita.myrxproject.stages.SendMessage;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ComposeActivity extends AppCompatActivity {
    ImageButton trash_button,hourglass_button;
    Button save_button;
    EditText to_text,to_subject,to_body;
    String cId,cName;
    PublicKey pKey;
    final String uName = "Hemankita";
    final String server_name = "http://129.115.27.54:25666";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        to_text = (EditText) findViewById(R.id.toText);
        to_body = ((EditText)findViewById(R.id.bodyCompose));
        to_subject = ((EditText)findViewById(R.id.subjectText));
        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            cId = intent.getStringExtra("NAME");
            cName = intent.getStringExtra("REPLY");
            //Log.i("Value of contact name", cId);
            if(cId!=null){
            to_text.setText(cId);}
            else{
                to_text.setText(cName);
            }
        }
        //String h=intent.getIntExtra("URTEXT", 0);
        // Capture button clicks
        to_text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent textIntent = new Intent(ComposeActivity.this,
                        ContactsActivity.class);
                startActivity(textIntent);
            }
        });

       /* String init = "";
        for(int i=0;i<85;i++){
            init += "a";
        }*/



        trash_button = (ImageButton) findViewById(R.id.trashButton);

        // Capture button clicks
        trash_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent trashIntent = new Intent(ComposeActivity.this,
                        MainActivity.class);
                startActivity(trashIntent);
            }
        });

        save_button = (Button) findViewById(R.id.saveComposeButton);

        // Capture button clicks
        save_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent saveIntent = new Intent(ComposeActivity.this,
                        MainActivity.class);
                String body =to_body.getText().toString();
                Log.i("DEBUG",body);
               // byte[] binCpk = body.getBytes();
               // String base64 = Base64.encodeToString(binCpk, Base64.DEFAULT);
                final String encrypt = encryptToBase64(body);
                Log.i("BASE64",body);
                Log.i("hello",encryptToBase64(body));
                String name = to_text.getText().toString();
                ContactDBHelper con = new ContactDBHelper(getApplicationContext());
                Contact c = con.getContact(name);
                con.close();
                //if(c.getStatus().equals("logged-in")){
                   /* if(SettingsActivity.myUserMap.containsKey(c.getContact())) {
                    SettingsActivity.serverAPI.sendMessage(new Object(), // I don't have an object to keep track of, but I need one!
                            SettingsActivity.myUserMap.get(c.getContact()).publicKey,
                            "Hemankita",
                            to_text.getText().toString(),
                            to_subject.getText().toString(),
                            to_body.getText().toString(),
                            System.currentTimeMillis(),
                            (long) 15000)
                        Log.i("Message","Sent");
                } else {
                    Log.d("Main","User info not available");
                }*/
                    String pkey = c.getPublickey();
                    byte[] key = org.spongycastle.util.encoders.Base64.decode(pkey);

                    X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
                    KeyFactory keyFactory = null;
                    PublicKey keyP = null;
                    try {
                        keyFactory = KeyFactory.getInstance("RSA");

                        keyP = keyFactory.generatePublic(spec);
                        Log.d("tag", "keyyyy   " + keyP);


                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }

                if(SettingsActivity.login) {
                    Observable.just(0) // the value doesn't matter, it just kicks things off
                            .observeOn(Schedulers.newThread())
                            //.subscribeOn(Schedulers.newThread())
                            .flatMap(new SendMessage(new Object(), keyP, uName, to_text.getText().toString()
                                    , to_subject.getText().toString(),
                                    to_body.getText().toString(),
                                    System.currentTimeMillis(),
                                    (long) 15000))
                            // .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<String>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d("LOGGG", "ERROR" + e.getMessage());
                                }

                                @Override
                                public void onNext(String s) {
                                    Log.i("Message encrypted", "" + encrypt);
                                    //Toast.makeText(getApplicationContext(), "Message Encrypted " +encrypt , Toast.LENGTH_LONG).show();
                                }
                            });
                }
               // }
                /*else{
                    Toast.makeText(getApplicationContext(), "Message not sent ... USer is offline " , Toast.LENGTH_LONG).show();
                }*/


                startActivity(saveIntent);
            }
        });

        hourglass_button = (ImageButton) findViewById(R.id.hourglassButton);

        // Capture button clicks
        hourglass_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ComposeActivity.this, hourglass_button);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.ttl_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(
                                ComposeActivity.this,
                                "You Clicked : " + item.getTitle(),
                                Toast.LENGTH_SHORT
                        ).show();
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public String encryptToBase64(String clearText){
        try {
            Log.i("DEBUG","clear text is of length "+clearText.getBytes().length);
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding","SC");
            String username = to_text.getText().toString();
            ContactDBHelper condb = new ContactDBHelper(this);
            Contact con = condb.getContact(username);
            String publickey = con.getPublickey();
            Log.i("keyyyyyyy",publickey);
            byte[] binCpk = org.spongycastle.util.encoders.Base64.decode(publickey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(binCpk);
            pKey = keyFactory.generatePublic(publicKeySpec);
            Log.i("pKey",pKey.toString());
            rsaCipher.init(Cipher.ENCRYPT_MODE,pKey);
            Log.i("Done", "I am done");
            byte[] bytes = rsaCipher.doFinal(clearText.getBytes());
            Log.d("DEBUG","cipher bytes is of length "+bytes.length);
            Log.i("bytes", Arrays.toString(bytes));

            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }  catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return "";
    }

}
