package com.example.hemankita.myproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hemankita on 6/29/2016.
 */
public class MessageDBHelper extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "messageManager";

    // Contacts table name
    private static final String TABLE_MESSAGES = "messages";

    // Contacts Table Columns names
    private static final String KEY_SENDER = "senderName";
    private static final String KEY_SUBJECT = "subjectLine";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TTL = "timeToLive_ms";


    public MessageDBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        insertMessages();
        //clean(context);



    }
    public void clean(Context context){
        if(DATABASE_NAME.equals("messageManager")){
            context.deleteDatabase(DATABASE_NAME);
        }
    }

    public void insertMessages()
    {

        addMessage(new Message("Mouli","Hello !!!","How are you",System.currentTimeMillis()+50000));
        addMessage(new Message("Tinnu","See you","Coming to see you",System.currentTimeMillis()+15000));
        addMessage(new Message("Vamsi","HI :)","Where are you now",System.currentTimeMillis()+300000));
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + KEY_SENDER + " TEXT ," + KEY_SUBJECT + " TEXT ,"
                + KEY_MESSAGE + " TEXT ," + KEY_TTL + " INTEGER" + ")";
        db.execSQL(CREATE_MESSAGES_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);

        // Create tables again
        onCreate(db);
        //http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
    }
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    void addMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SENDER, message.getSenderName()); // Sender Name
        values.put(KEY_SUBJECT, message.getSubjectLine());// Subject
        values.put(KEY_MESSAGE,message.getMessage()); //message body
        values.put(KEY_TTL,message.getTimeToLive_ms()); //TTL

        // Inserting Row
        db.insert(TABLE_MESSAGES, null, values);
        db.close(); // Closing database connection
    }

    // Getting single Message
    Message getMessage(String senderName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MESSAGES, new String[] { KEY_SENDER,
                        KEY_SUBJECT, KEY_MESSAGE, KEY_TTL }, KEY_SENDER + "=?",
                new String[] { senderName }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Message message = new Message(cursor.getString(0), cursor.getString(1), cursor.getString(2),cursor.getLong(3));

        return message;
    }

    // Getting All Message
    public List<Message> getAllMessages() {
        List<Message> messageList = new ArrayList<Message>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setSenderName(cursor.getString(0));
                message.setSubjectLine(cursor.getString(1));
                message.setMessage(cursor.getString(2));
                message.setTimeToLive_ms(cursor.getLong(3));
                // Adding messages to list
                messageList.add(message);
            } while (cursor.moveToNext());
        }

        // return message list
        return messageList;
    }

    // Updating single message
    public int updateMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SENDER, message.getSenderName());
        values.put(KEY_SUBJECT, message.getSubjectLine());
        values.put(KEY_MESSAGE, message.getMessage());
        values.put(KEY_TTL, message.getTimeToLive_ms());

        // updating row
        return db.update(TABLE_MESSAGES, values, KEY_SENDER + " = ?",
                new String[] { message.getSenderName() });
    }

    // Deleting single message
    public void deleteMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGES, KEY_SENDER + " = ?",
                new String[] { message.getSenderName() });
        db.close();
    }

    // Getting messages Count
    public int getMessagesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MESSAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
