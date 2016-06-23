package com.example.hemankita.myproject;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ReadActivity extends AppCompatActivity {
    ImageButton trash_read_button;
    Button reply_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        trash_read_button = (ImageButton) findViewById(R.id.trashReadButton);

        // Capture button clicks
        trash_read_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent trashreadIntent = new Intent(ReadActivity.this,
                        MainActivity.class);
                startActivity(trashreadIntent);
            }
        });

        reply_button = (Button) findViewById(R.id.replyButton);

        // Capture button clicks
        reply_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent replyIntent = new Intent(ReadActivity.this,
                        ComposeActivity.class);
                startActivity(replyIntent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
