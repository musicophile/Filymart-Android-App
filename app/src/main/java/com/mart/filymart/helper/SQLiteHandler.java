package com.mart.filymart.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api.db";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String CREATE_TABLE_FOR_EVENTS = "CREATE TABLE IF NOT EXISTS tbl_events " +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, event_info_id VARCHAR(10), event VARCHAR(100), event_id VARCHAR(10)," +
            "title VARCHAR(100), image VARCHAR(100), extra VARCHAR(100), extra2 VARCHAR(100), date VARCHAR(30),time VARCHAR(10));";
    private static SQLiteHandler sInstance;
    private static final String CREATE_TABLE_FOR_SPLASH_SCREEN = "CREATE TABLE IF NOT EXISTS tbl_splash_screen " +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, session VARCHAR(10),date VARCHAR(30),time VARCHAR(10));";

//    private static final String CREATE_TABLE_FOR_GIFT_CARD = "CREATE TABLE IF NOT EXISTS tbl_gift " +
//            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, session VARCHAR(10),date VARCHAR(30),time VARCHAR(10));";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized SQLiteHandler getInstance(Context context) {

        // Use the application context, which will ensure that you
        if (sInstance == null) {
            if (context != null) {
                sInstance = new SQLiteHandler(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
     String CREATE_TABLE_FOR_SPLASH_SCREEN = "CREATE TABLE IF NOT EXISTS tbl_splash_screen" +
             " (_id INTEGER PRIMARY KEY AUTOINCREMENT, session VARCHAR(10),date VARCHAR(30),time VARCHAR(10));";

        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_TABLE_FOR_SPLASH_SCREEN);
        db.execSQL(CREATE_TABLE_FOR_EVENTS);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL(CREATE_TABLE_FOR_SPLASH_SCREEN);
        db.execSQL(CREATE_TABLE_FOR_EVENTS);


        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Storing user details in database
     * */
    public void addEvent(String event_id, int event_info_id, String event, String title,String image,  String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("event_id", event_id); // Name
        values.put("event_info_id", event_info_id); // Email
        values.put("event", event); // Email
        values.put("title", title); // Created At
        values.put("image", image); // Name
        values.put("date", date); // Email
        values.put("time", time); // Email

        // Inserting Row
        long id = db.insert("tbl_events", null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    public boolean getUserDetailsInfo(String Email) {
        String id = "";
        boolean status = false;
        String selectQuery = "SELECT  id FROM user WHERE email = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            id = cursor.getString(1);
            if (!id.isEmpty()){
                status = true;
            }
            cursor.close();
            db.close();
            // return user
            Log.d(TAG, "Fetching user from Sqlite: " + id);


        }
        return status;
    }

    public String updateUserInformations(String Email, String operatorId) {
        String id = "";
        String selectQuery = "UPDATE  user SET email = ? WHERE id = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{Email, operatorId});
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
//            id = cursor.getString(1);
            cursor.close();
            db.close();
            // return user
            Log.d(TAG, "Fetching user from Sqlite: " + id);


        }
        return operatorId;
    }
    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public void deleteEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete("tbl_events", null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public List<String> getEvents(){
        List<String> labels = new ArrayList<String>();
        String[] columns={"event"};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("tbl_events",columns,null, null,"event_id",null,null);
        if (cursor.moveToFirst())
        {
            do
            {
                labels.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return labels;
    }

    public List<String> getImages(){
        List<String> labels = new ArrayList<String>();
        String[] columns={"image"};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("tbl_events",columns,null, null,null,null,null);
        if (cursor.moveToFirst())
        {
            do
            {
                labels.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return labels;
    }


}
