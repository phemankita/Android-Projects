package com.example.hemankita.myproject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.RSAKeyGenParameterSpec;

/**
 * Created by Hemankita on 7/4/2016.
 */
public class MyApp extends Application {
    KeyPair keyPair;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPreferences" ;
    public static final String PUBLICKEY = "PublicKey";
    public static final String PRIVATEKEY = "PrivateKey";
    String publickey,privatekey;
    @Override
    public void onCreate() {
        super.onCreate();

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here
        Toast.makeText(MyApp.this, "hello", Toast.LENGTH_SHORT).show();
        MessageDBHelper msgdb = new MessageDBHelper(this);
        msgdb.insertMessages();
        msgdb.close();
            // mark first time has runned.
           // SharedPreferences.Editor editor = prefs.edit();
            //editor.putBoolean("firstTime", true);
            //editor.commit();
        try {
            SecureRandom random = new SecureRandom();
            RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA","SC");
            generator.initialize(spec,random);
            keyPair = generator.generateKeyPair();
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
            sharedpreferences.getString(PUBLICKEY,publickey);

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

    }
    }

