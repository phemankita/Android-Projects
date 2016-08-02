package com.example.hemankita.myproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;
import android.util.Base64;

import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
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
import java.util.Iterator;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;

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
    String pk;
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
                String userName = person_name.getText().toString();
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
                    conimage="[B@c191089";
                }
                else if (SettingsActivity.login) {

                    SettingsActivity.serverAPI.getUserInfo(userName);
                    ServerAPI.UserInfo userinfo = SettingsActivity.myUserMap.get(userName);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                    alertDialogBuilder.setMessage("Please press the search button");
                    if(userinfo !=null){
                        conimage = userinfo.image;
                        Log.i("image",conimage);
                        pkey = userinfo.publicKey;
                        Log.i("key",pkey.toString());
                        byte[] decString = android.util.Base64.decode(conimage, android.util.Base64.DEFAULT);
                        decByte = BitmapFactory.decodeByteArray(decString, 0, decString.length);
                        byte[] publicKeyBytes = pkey.getEncoded();
                        pk = org.spongycastle.util.encoders.Base64.toBase64String(publicKeyBytes);


                        public_key.setText(pk);


                        contact_image.setImageBitmap(decByte);
                    }

                   /* Iterator entries = settings.serverAPI.umap.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        if (entry.getKey().equals(userName)) {
                            Log.d("tag", "key  " + entry.getKey().toString() + " value " + entry.getValue().toString());
                            ServerAPI.UserInfo in = (ServerAPI.UserInfo) entry.getValue();

                            conimage = in.image;
                            Log.i("image",conimage);
                            pkey = in.publicKey;
                            Log.i("key",pkey.toString());

                            // Toast.makeText(Contact.this," "+cname+" is ",Toast.LENGTH_SHORT).show();

                        }


                        /*else {
                            Toast.makeText(ContactActivity.this,"This contact is not logged in or he is not in the server list ... Press save if you want to add him as a different user(not the one on server)",Toast.LENGTH_LONG).show();
                            pkey=keyPair.getPublic();
                            conimage="[B@c191089";

                        }*/





                   /* byte[] decString = android.util.Base64.decode(conimage, android.util.Base64.DEFAULT);
                    decByte = BitmapFactory.decodeByteArray(decString, 0, decString.length);
                    byte[] publicKeyBytes = pkey.getEncoded();
                    pk = org.spongycastle.util.encoders.Base64.toBase64String(publicKeyBytes);


                    public_key.setText(pk);


                    contact_image.setImageBitmap(decByte);*/


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
                SQLiteDatabase db = condb.getWritableDatabase();
                Contact con=new Contact(person_name.getText().toString(),
                        public_key.getText().toString(),
                        imageData.toString(),"logout");

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
                ContactDBHelper condb = new ContactDBHelper(getApplicationContext());
                SQLiteDatabase db = condb.getWritableDatabase();
                byte[] pubKey = keyPair.getPublic().getEncoded();
                String pubKeyString = org.spongycastle.util.encoders.Base64.toBase64String(pubKey);
                Log.i("PK",pubKeyString);

               /* Contact con=new Contact(person_name.getText().toString(),
                        imageData.toString(),
                        public_key.getText().toString()
                        );*/
                Contact con=new Contact(person_name.getText().toString(),
                        imageData.toString(),
                        pubKeyString,"logout"
                );
                condb.addContact(con);
                if(SettingsActivity.login) {
                    cons.add(person_name.getText().toString());
                    SettingsActivity.serverAPI.registerContacts("Hemankita", cons);
                }
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