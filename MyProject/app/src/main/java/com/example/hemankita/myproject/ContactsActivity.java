package com.example.hemankita.myproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ContactsActivity extends AppCompatActivity {

    ImageButton add_button, settings_contact;
    ListView listView;
    static String cn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ContactDBHelper condb = new ContactDBHelper(this);

        ArrayList<Contact> conArray = new ArrayList<>();
        /**
         * CRUD Operations
         * */
        // Inserting Contacts
        Log.i("Insert: ", "Inserting ..");
        //condb.addContact(new Contact("Ravi", "img1" , "publickey"));
        //condb.addContact(new Contact("Srinivas", "img2","publickey"));

        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");

       List<Contact> contacts = condb.getAllContacts();

       for (Contact cn : contacts) {
            String log = "Id: "+cn.getContact()+" ,Image: "+"\n" + cn.getImage()+"\n" + " Publickey: " + cn.getPublickey();
            // Writing Contacts to log
            Log.d("Name: ", log);
        }

        for (Contact cn : contacts) {
            conArray.add(cn);
        }

        /*conArray.add(new Contact("Contact1","image1","h"));
        conArray.add(new Contact("Contact2","image2","j"));
        conArray.add(new Contact("Contact3","image3","l"));
        conArray.add(new Contact("Contact4","image4","o"));
        conArray.add(new Contact("Contact5","image5","t"));*/
        //ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_contactlistview, mobileArray);
        ContactListAdapter adapter=new ContactListAdapter(ContactsActivity.this,R.layout.activity_contactlistview,conArray);

        listView = (ListView) findViewById(R.id.contacts_list);
        listView.setAdapter(adapter);

      /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String contactName = ((TextView) findViewById(R.id.name_Contact)).getText().toString();
                Intent cintent = new Intent(ContactsActivity.this, ComposeActivity.class);
                cintent.putExtra("NAME",contactName);
                startActivity(cintent);

            }
        });*/

        add_button = (ImageButton) findViewById(R.id.addButton);

        // Capture button clicks
        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent addIntent = new Intent(ContactsActivity.this,
                        ContactActivity.class);
                startActivity(addIntent);
            }
        });


       condb.close();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void settingsContact(View view){
    /*Intent intent = new Intent(ContactsActivity.this, ContactActivity.class);
    startActivity(intent);*/
        String contactName = ((TextView) view.findViewById(R.id.name_Contact)).getText().toString();
        cn=contactName;
        Intent cintent = new Intent(ContactsActivity.this, ComposeActivity.class);
        cintent.putExtra("NAME",contactName);
        startActivity(cintent);
    }
    public void composeContact(View view){
        ContactDBHelper condb = new ContactDBHelper(this);
        View parentRow = (View) view.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        //Log.i("UI","number"+position);
        List<Contact> contacts = condb.getAllContacts();
        Contact con=contacts.get(position);
        //Log.i("UI","number"+con.getContact());
        String name = con.getContact();
        String image = con.getImage();
        String publickey = con.getPublickey();
        condb.close();
        Log.i("PPPPPP",publickey);
        Intent sintent = new Intent(ContactsActivity.this, ContactActivity.class);
        sintent.putExtra("UNAME",name);
        sintent.putExtra("IMAGE",image);
        sintent.putExtra("PUBKEY",publickey);
        startActivity(sintent);
    }

}
