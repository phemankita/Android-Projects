package com.example.hemankita.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    ImageButton settings_button,contact_button,compose_button;
    ArrayList<Message> msgArray = new ArrayList<Message>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
