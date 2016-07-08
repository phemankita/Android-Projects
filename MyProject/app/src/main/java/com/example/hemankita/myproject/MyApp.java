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
import java.security.Security;
import java.security.spec.RSAKeyGenParameterSpec;


/**
 * Created by Hemankita on 7/4/2016.
 */
public class MyApp extends Application {
    KeyPair keyPair;
    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

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

        MessageDBHelper msgdb = new MessageDBHelper(this);
        msgdb.insertMessages();
        msgdb.close();
        sharedpreferences = this.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        if(sharedpreferences.getBoolean("keyv",true)) {
            try {
                SecureRandom random = new SecureRandom();
                RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "SC");
                generator.initialize(spec, random);
                keyPair = generator.generateKeyPair();
                Log.i("key pair",keyPair.getPublic().toString());

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
            sharedpreferences.edit().putBoolean("keyv",false).commit();
        }

    }
    }

