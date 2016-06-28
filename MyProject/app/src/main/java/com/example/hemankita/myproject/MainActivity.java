package com.example.hemankita.myproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity {

    private static String DEBUG = "PUBLIC/PRIVATE KEY";
    public static final String MyPREFERENCES = "MyPreferences" ;
    public static final String PUBLICKEY = "PublicKey";
    public static final String PRIVATEKEY = "PrivateKey";
    SharedPreferences sharedpreferences;

    KeyPair keyPair;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }
    ImageButton settings_button,contact_button,compose_button;
    ArrayList<Message> msgArray = new ArrayList<Message>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            String public_key=keyPair.getPublic().toString();
            String private_key=keyPair.getPrivate().toString();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(PUBLICKEY, public_key);
            editor.putString(PRIVATEKEY, private_key);
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



        msgArray.add(new Message("Message1 | Subject1",5));
        msgArray.add(new Message("Message2 | Subject2",60));
        msgArray.add(new Message("Message3 | Subject3",15));
        //ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_contactlistview, mobileArray);
       MessageListAdapter adapter=new MessageListAdapter(MainActivity.this,R.layout.activity_listview,msgArray);

        ListView listView = (ListView) findViewById(R.id.message_list);
        listView.setAdapter(adapter);



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
        Intent intent = new Intent(MainActivity.this, ReadActivity.class);
        startActivity(intent);
    }
}
