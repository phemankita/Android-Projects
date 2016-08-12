package com.example.hemankita.myrxproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hemankita.myrxproject.stages.AddContact;
import com.example.hemankita.myrxproject.stages.GetUserInfo;
import com.example.hemankita.myrxproject.stages.RemoveContact;
import com.example.hemankita.myrxproject.stages.UserInfo;

import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContactActivity extends AppCompatActivity {
    /*public static final String MyPREFERENCES = "MyPreferences";
    public static final String PUBLICKEY = "PublicKey";
    public static final String PRIVATEKEY = "PrivateKey";
    SharedPreferences sharedpreferences;*/
    private static String DEBUG = "PUBLIC/PRIVATE KEY";
    public static final String MyPREFERENCES = "MyPreferences" ;
    public static final String PUBLICKEY = "PublicKey";
    public static final String PRIVATEKEY = "PrivateKey";
    SharedPreferences sharedpreferences;
    SettingsActivity settings;
    static String conimage;
    static PublicKey pkey;
    Bitmap decByte;
    String pk,status;
    ArrayList<String> cons = new ArrayList<>();

    KeyPair keyPair;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }
    ImageButton delete_button, search_button;
    Button save_button;
    TextView public_key,loginText;
    ImageView contact_image;
    EditText person_name;
    byte[] imageData = {};
    String cname,cimage,cpubkey,publickey,privatekey;
    PublicKey pKey = null;
    final String uName = "Hemankita";
    final String server_name = "http://129.115.27.54:25666";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        try {
            SecureRandom random = new SecureRandom();
            RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA","SC");
            generator.initialize(spec,random);

            keyPair = generator.generateKeyPair();

            Log.i(DEBUG,"GOT KEY!");

            StringWriter writer = new StringWriter();
            PemWriter pemWriter = new PemWriter(writer);
            pemWriter.writeObject(new PemObject("PUBLIC KEY",keyPair.getPublic().getEncoded()));
            pemWriter.flush();
            pemWriter.close();
            Log.i("Public",keyPair.getPrivate().toString());
            Log.i("Private",keyPair.getPublic().toString());
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            publickey=keyPair.getPublic().toString();
            privatekey=keyPair.getPrivate().toString();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(PUBLICKEY, publickey);
            editor.putString(PRIVATEKEY, privatekey);
            editor.commit();
            //Toast.makeText(MainActivity.this,"Thanks", Toast.LENGTH_LONG).show();
            //assert ((TextView)findViewById(R.id.keyPair)) != null;
            //((TextView)findViewById(R.id.publickeyfield)).setText(writer.toString());


            //writer = new StringWriter();
            //pemWriter = new PemWriter(writer);
            //pemWriter.writeObject(new PemObject("PRIVATE KEY",myKeyPair.getPrivate().getEncoded()));
            //pemWriter.flush();
            //pemWriter.close();
            //((TextView)findViewById(R.id.private_key_field)).setText(writer.toString());
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

       /* String init = "";
        for(int i=0;i<85;i++){
            init += "a";
        }
        Log.d(DEBUG,init);
        encryptToBase64(init);*/
        //((EditText)findViewById(R.id.keyPair)).setText(init);
        // Locate the button in activity_main.xml
       /* SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        final String publickey = sharedPreferences.getString(PUBLICKEY, "");
        final String privatekey = sharedPreferences.getString(PRIVATEKEY, "");
        Log.d("AddNewRecord", "Size: " + publickey);
        Log.d("AddNewRecord", "Size: " + privatekey);*/
        person_name = (EditText) findViewById(R.id.personName);

        save_button = (Button) findViewById(R.id.saveButton);

       Intent intent = getIntent();
        if( intent.getExtras() != null) {
            cname = intent.getStringExtra("UNAME");
            cimage = intent.getStringExtra("IMAGE");
            cpubkey = intent.getStringExtra("PUBKEY");
            Log.i("Value of contact name", cpubkey);
            person_name.setText(cname);
            public_key = (TextView) findViewById(R.id.publickeyfield);
            try {
                byte[] binCpk = org.spongycastle.util.encoders.Base64.decode(cpubkey);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(binCpk);
                pKey = keyFactory.generatePublic(publicKeySpec);
                Log.i("pKey",pKey.toString());
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
            public_key.setText(pKey.toString());
            contact_image = (ImageView) findViewById(R.id.contactImage);
            contact_image.setImageResource(R.drawable.contact);
            save_button.setEnabled(false);
        }
        search_button = (ImageButton) findViewById(R.id.searchButton);
        search_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                final String userName = person_name.getText().toString();
                Log.i("Personnameeee",userName);
                public_key = (TextView) findViewById(R.id.publickeyfield);
                contact_image = (ImageView) findViewById(R.id.contactImage);
               // settings.serverAPI.getUserInfo(userName);

                loginText = (TextView)findViewById(R.id.loginText);

                //Log.i("hghfdsfghjk",settings.serverAPI.umap.toString());

                if (TextUtils.isEmpty(userName)) {
                    person_name.setError("Please enter the name of the person");
                }
                else if (!SettingsActivity.login){
                    loginText.setText("You are not logged in ... This contact is just added to your contact list");
                    pkey=keyPair.getPublic();
                    public_key.setText(pk);
                    contact_image.setImageDrawable(getResources().getDrawable(R.drawable.contact));
                    //String imageUri = "drawable://" + R.drawable.contact;
                    //conimage=imageUri;

                }
                else if (SettingsActivity.login) {

                    Observable.just("") // the value doesn't matter, it just kicks things off
                            .observeOn(Schedulers.newThread())
                            .flatMap(new GetUserInfo(userName,server_name))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<HashMap<String, UserInfo>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(HashMap<String, UserInfo> stringUserInfoHashMap) {
                                    UserInfo userInfo = stringUserInfoHashMap.get(userName);
                                    if(userInfo!=null){
                                        conimage = userInfo.image;
                                        Log.i("image",conimage);
                                        pkey = userInfo.publicKey;
                                        Log.i("key",pkey.toString());
                                        byte[] decString = android.util.Base64.decode(conimage, android.util.Base64.DEFAULT);
                                        decByte = BitmapFactory.decodeByteArray(decString, 0, decString.length);
                                        byte[] publicKeyBytes = pkey.getEncoded();
                                        pk = org.spongycastle.util.encoders.Base64.toBase64String(publicKeyBytes);
                                        Log.i("PUBLIC KEY",pk);
                                        public_key.setText(pk);
                                        contact_image.setImageBitmap(decByte);

                                    }

                                }
                            });

                }
                else{
                       loginText.setText("Only logged in users can be added at this time");
                    }
            }

        });


        delete_button = (ImageButton) findViewById(R.id.deleteButton);

        // Capture button clicks
        delete_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent deleteIntent = new Intent(ContactActivity.this,
                        ContactsActivity.class);
                ContactDBHelper condb = new ContactDBHelper(getApplicationContext());

                if(SettingsActivity.login){
                    Observable.just(" ") // the value doesn't matter, it just kicks things off
                            .observeOn(Schedulers.newThread())
                            .flatMap(new RemoveContact(server_name,uName,person_name.getText().toString()))
                            .subscribe(new Observer<String>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(String s) {
                                    Log.i("CONTACT ACTIVITY","Unregistered"+person_name.getText().toString());
                                }
                            });


                }

               /* Contact con=new Contact(person_name.getText().toString(),
                        public_key.getText().toString(),
                        imageData.toString(),"logout");*/
                Contact con = condb.getContact(person_name.getText().toString());

                    condb.deleteContact(con);

                /*else{
                    Toast.makeText(getApplicationContext(), "Contact is empty ", Toast.LENGTH_LONG).show();
                }*/
                condb.close();

                finish();
                startActivity(deleteIntent);
            }
        });


        save_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent saveIntent = new Intent(ContactActivity.this,
                        ContactsActivity.class);
                final ContactDBHelper condb = new ContactDBHelper(getApplicationContext());
                byte[] pubKey = pkey.getEncoded();//keyPair.getPublic().getEncoded();
                String pubKeyString = org.spongycastle.util.encoders.Base64.toBase64String(pubKey);
                Log.i("PK",pubKeyString);
                Contact con=new Contact(person_name.getText().toString(),
                        imageData.toString(),
                        pubKeyString,"logged-out"
                );
                condb.addContact(con);

                if(SettingsActivity.login) {
                    cons.add(person_name.getText().toString());
                    Observable.just(" ") // the value doesn't matter, it just kicks things off
                            .observeOn(Schedulers.newThread())
                            .flatMap(new AddContact(server_name,uName,person_name.getText().toString()))
                            .subscribe(new Observer<Notification>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Notification notification) {
                                    if(notification instanceof Notification.LogIn) {
                                        Log.d("LOG","User "+((Notification.LogIn)notification).username+" is logged in");
                                       // status = "logged-in";
                                        condb.updateContact(((Notification.LogIn)notification).username,"logged-in");
                                    }
                                    if(notification instanceof Notification.LogOut) {
                                        Log.d("LOG","User "+((Notification.LogOut)notification).username+" is logged out");
                                       // status = "logged-out";
                                        condb.updateContact(((Notification.LogOut)notification).username,"logged-out");
                                    }
                                }
                            });


                    //SettingsActivity.serverAPI.registerContacts("Hemankita", cons);
                }
                condb.close();
                //Toast.makeText(getApplicationContext(), "Saving a contact to the DB " + result, Toast.LENGTH_LONG).show();
                finish();

                // Start NewActivity.class

                startActivity(saveIntent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onActivityResult(int reqCode, int resCode, Intent data)
    {
        if (resCode == RESULT_OK)
        {
            if (reqCode == 1)
            {

                contact_image.setImageURI(data.getData());

                InputStream iStream = null;
                try
                {
                    iStream = getContentResolver().openInputStream(data.getData());
                    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];

                    int len = 0;
                    while ((len = iStream.read(buffer)) != -1)
                    {
                        byteBuffer.write(buffer, 0, len);
                    }
                    imageData = byteBuffer.toByteArray();
                } catch (java.io.IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}