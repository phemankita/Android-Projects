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
    private static MessageDBHelper sInstance;
    // Database Name
    private static final String DATABASE_NAME = "messageManager";

    // Contacts table name
    private static final String TABLE_MESSAGES = "messages";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SENDER = "senderName";
    private static final String KEY_SUBJECT = "subjectLine";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TTL = "timeToLive_ms";
    static long time;
    //private Context mcxt;
    public static final String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "(" + KEY_ID + " INTEGER ,"
            + KEY_SENDER + " TEXT ," + KEY_SUBJECT + " TEXT ,"
            + KEY_MESSAGE + " TEXT ," + KEY_TTL + " INTEGER" + ")";

    public static synchronized MessageDBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you

        if (sInstance == null) {

            sInstance = new MessageDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }
    public MessageDBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //this.mcxt = context;

       // clean(context);

    }
    public void clean(Context context){
        if(DATABASE_NAME.equals("messageManager")){
            context.deleteDatabase(DATABASE_NAME);
        }
    }

    public void insertMessages()
    {
        List<Message> msg = getAllMessages();
        if(!msg.isEmpty()){
            for(int i=0;i<msg.size();i++)
            {
                Message m = msg.get(i);
                deleteMessage(m);
            }
        }
        time=System.currentTimeMillis();
        addMessage(new Message(MainActivity.counter++,"Mouli","Hello !!!","How are you",System.currentTimeMillis()+5000));
        addMessage(new Message(MainActivity.counter++,"Hems","See you","Coming to see you",System.currentTimeMillis()+15000));
        addMessage(new Message(MainActivity.counter++,"Tinnu","HI :)","Where are you now",System.currentTimeMillis()+300000));
    }
    public long timing(){
        return time;
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
       // db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);

        db.execSQL(CREATE_MESSAGES_TABLE);
        //db.execSQL("insert into " + TABLE_MESSAGES + "(" + KEY_SENDER + "," + KEY_SUBJECT + "," + KEY_MESSAGE + "," + KEY_TTL + ") values('Hema','Hello','How are you',System.currentTimeMillis()+50000)");
        //ContentValues[] values = new ContentValues[3];
       /* for(int i=1;i<=3;i++){
            values[i-1].put(KEY_SENDER, "Raji");
            values[i-1].put(KEY_NAME, "name"+i); // Contact Name
            values[i-1].put(KEY_PH_NO, "ph"+1);// Contact Phone
            values[i-1].put(KEY_EMAIL_ID, "id"+i);
            db.insert(TABLE_EMERGENCY_CONTACTS, null, values[i-1]);

        }*/

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);

        // Create tables again
        onCreate(db);

    }
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact

    void addMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, message.getId());
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

        Cursor cursor = db.query(TABLE_MESSAGES, new String[] { KEY_ID, KEY_SENDER,
                        KEY_SUBJECT, KEY_MESSAGE, KEY_TTL }, KEY_SENDER + "=?",
                new String[] { senderName }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Message message = new Message(cursor.getInt(0), cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getLong(4));

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
                message.setId(cursor.getInt(0));
                message.setSenderName(cursor.getString(1));
                message.setSubjectLine(cursor.getString(2));
                message.setMessage(cursor.getString(3));
                message.setTimeToLive_ms(cursor.getLong(4));
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
        //db.delete(TABLE_MESSAGES, KEY_SENDER + " = ?",
               // new String[] { message.getSenderName() });
        SQLiteDatabase db = this.getWritableDatabase();
        String query=" DELETE FROM " + TABLE_MESSAGES+
                " WHERE "+KEY_ID+ " =  '" + message.getId()+"'";
        db.execSQL(query);
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
