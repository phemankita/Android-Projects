package com.example.hemankita.myproject;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ReadActivity extends AppCompatActivity {
    ImageButton trash_read_button;
    Button reply_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        Intent intent = getIntent();
        String sname = intent.getStringExtra("SNAME");
        Log.i("SNAME",sname);
        long ttl = intent.getLongExtra("TTL",0);
        Log.i("TTL",String.valueOf(ttl));
        final String body = intent.getStringExtra("BODY");
        Log.i("BODY",body);
        String subject = intent.getStringExtra("SUBJECT");
        Log.i("SUB",subject);
        final TextView sender_name = (TextView) findViewById(R.id.senderName);
        sender_name.setText(sname);
        final TextView time_to_live = (TextView) findViewById(R.id.countDown);
        time_to_live.setText(String.valueOf(ttl));
        final TextView subject_line = (TextView) findViewById(R.id.subjectLineText);
        subject_line.setText(subject);
        final TextView msg_body = (TextView) findViewById(R.id.bodyRead);
        msg_body.setText(body);

        trash_read_button = (ImageButton) findViewById(R.id.trashReadButton);

        // Capture button clicks
        trash_read_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent trashreadIntent = new Intent(ReadActivity.this,
                        MainActivity.class);
                MessageDBHelper msgdb = new MessageDBHelper(getApplicationContext());
                SQLiteDatabase db = msgdb.getWritableDatabase();
                Message msg=new Message(sender_name.getText().toString(),
                        subject_line.getText().toString(),
                        msg_body.getText().toString(),
                        Long.valueOf(time_to_live.getText().toString()));
                msgdb.deleteMessage(msg);
                msgdb.close();
                //Toast.makeText(getApplicationContext(), "Saving a contact to the DB " + result, Toast.LENGTH_LONG).show();
                finish();
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
                String sname = sender_name.getText().toString();
                replyIntent.putExtra("REPLY",sname);
                startActivity(replyIntent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
