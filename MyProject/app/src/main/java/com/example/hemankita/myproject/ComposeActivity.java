package com.example.hemankita.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ComposeActivity extends AppCompatActivity {
    ImageButton trash_button,hourglass_button;
    Button save_button;
    EditText to_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        to_text = (EditText) findViewById(R.id.toText);

        // Capture button clicks
        to_text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent textIntent = new Intent(ComposeActivity.this,
                        ContactsActivity.class);
                startActivity(textIntent);
            }
        });

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

}
