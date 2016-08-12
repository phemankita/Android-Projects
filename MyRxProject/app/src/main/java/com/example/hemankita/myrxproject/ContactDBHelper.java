package com.example.hemankita.myrxproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hemankita on 6/24/2016.
 */
public class ContactDBHelper extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contactManager";

    // Contacts table name
    private static final String TABLE_CONTACTS = "contact";

    // Contacts Table Columns names
    private static final String KEY_USERNAME = "id";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_PUBLICKEY = "publickey";
    private static final String STATUS = "status";

    private static final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
            + KEY_USERNAME + " ," + KEY_IMAGE + " ,"
            + KEY_PUBLICKEY + " ," + STATUS + ")";

    public ContactDBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //addContact(new Contact("minti","img2","Publickey"));
        /*if(DATABASE_NAME.equals("contactManager")){
            context.deleteDatabase(DATABASE_NAME);
        }*/

    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);

    }
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    public void insertContacts()
    {
        List<Contact> con = getAllContacts();
        if(!con.isEmpty()){
            for(int i=0;i<con.size();i++)
            {
                Contact m = con.get(i);
                deleteContact(m);
            }
        }
        String imageUri = "drawable://" + R.drawable.contact;
        addContact(new Contact("Eswar",imageUri,"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChOCVZ78gM2uhNyIzU2AgLq9vFSwOLN3UOObULUG2jZavo1pxrc7iKFXiSFPe2J++A6bs9CDSQw0Ud2V2DTuJT3i+laW4Ko+0dyigA/Y8lzlnS1ksClttGBo7UclCmhhPMJiPOYsQOztrmFj393CyR1UT8AUh/10Y92mwIJ7kF3wIDAQAB","logout"));
        addContact(new Contact("Tinnu",imageUri,"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDKq+CiJStmZz4swHfzJe2boZe/9JJ0KHjxbUX7AVvteAXl3tXFU2WHkesB2zMqTbPbcMhZA69rmktOUKxwiYGem2LdVzXmMOoDgmageCkCjfd38ScZWENk7yL9m4UUlQwjhV0XQ+A1WYD3/QBKIlmJVlevO+c4h5RpXRWmu1beeQIDAQAB","logout"));
        addContact(new Contact("Hems",imageUri,"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCkJDWPwcO/6gaTF1XEX8mrnwdD//T+mnEYhecVK7UuIl8teefDAZ9hLkvqsSMpt8LIyAt6zTo4JuoOXQijKZj0TqiX1psD+xK/ygVTCHT5+oMfdqbVvZvaSbbqFnmY2rns629EP1V9cbjRpFVnqFcyhXY5IfqZRDEKCfex5pdqzwIDAQAB","logout"));
        addContact(new Contact("Mouli",imageUri,"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgqsJryE0IX4AL6/Z+1Ii2oz/3k/hH7mR0jndmLH4hT7gZIheaUG+mnzNR0umH/Psa1zFIAMv31SI16yeX9VEtVUGq//uryMlobVKo0jrXivXVc+/nRh0LBNOaulL6qF38hWH1HY4yXyTbe5c0eI0YC3WZOyNmJlT0zr6MGmGN1wIDAQAB","logout"));
    }

    // Adding new contact
    void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, contact.getContact()); // Contact Name
        values.put(KEY_IMAGE, contact.getImage());// Contact Image
        values.put(KEY_PUBLICKEY,contact.getPublickey()); //Contact Publickey
        values.put(STATUS,"loggedout");
        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    Contact getContact(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_USERNAME,
                        KEY_IMAGE, KEY_PUBLICKEY, STATUS }, KEY_USERNAME + "=?",
                new String[] { userName }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return contact
        return contact;
    }

    // Getting All Contacts
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setContact(cursor.getString(0));
                contact.setImage(cursor.getString(1));
                contact.setPublickey(cursor.getString(2));
                contact.setStatus(cursor.getString(3));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Updating single contact
    public void updateContact(String name, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query=" UPDATE " + TABLE_CONTACTS+
                " SET STATUS  = '"+status+"' WHERE "+KEY_USERNAME+ " =  '" + name+"'";
        db.execSQL(query);
        db.close();

    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_USERNAME + " = ?",
                new String[] { String.valueOf(contact.getContact()) });
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
