package com.example.hemankita.myproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactActivity extends AppCompatActivity {
    public static final String MyPREFERENCES = "MyPreferences" ;
    public static final String PUBLICKEY = "PublicKey";
    public static final String PRIVATEKEY = "PrivateKey";
    SharedPreferences sharedpreferences;
    ImageButton delete_button,search_button;
    Button save_button;
    TextView public_key;
    ImageView contact_image;
    EditText person_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        // Locate the button in activity_main.xml
        SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        final String publickey=sharedPreferences.getString(PUBLICKEY,"");
        final String privatekey=sharedPreferences.getString(PRIVATEKEY,"");
        Log.d("AddNewRecord", "Size: " + publickey);
        Log.d("AddNewRecord", "Size: " + privatekey);
        person_name = (EditText) findViewById(R.id.personName);

        //String sUsername = person_name.getText().toString();


           /* search_button = (ImageButton) findViewById(R.id.searchButton);
            search_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    public_key = (TextView) findViewById(R.id.publickeyfield);
                    public_key.setText(publickey);
                    contact_image = (ImageView) findViewById(R.id.contactImage);

                    contact_image.setImageResource(R.drawable.contact);
                }
            });*/


        delete_button = (ImageButton) findViewById(R.id.deleteButton);

        // Capture button clicks
        delete_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent deleteIntent = new Intent(ContactActivity.this,
                        ContactsActivity.class);
                startActivity(deleteIntent);
            }
        });

        save_button=(Button) findViewById(R.id.saveButton);
        save_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent saveIntent = new Intent(ContactActivity.this,
                        ContactsActivity.class);
                startActivity(saveIntent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
