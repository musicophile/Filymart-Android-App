package com.mart.filymart.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.util.Log;

import com.mart.filymart.helper.SQLiteHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class dbFunctions {
    protected SQLiteHandler mySQLiteHelper;
    protected SQLiteDatabase db;
    protected Cursor cursor;
    protected SharedPreferences pref;
    protected static  String str_route_from;
    protected String TAG="DBActivity";


    public dbFunctions(Context context) {
        mySQLiteHelper =  SQLiteHandler.getInstance(context);
    }

    public void open() throws SQLException {
        db = mySQLiteHelper.getWritableDatabase();
    }

    //closing database
    public void close() {
        mySQLiteHelper.close();
    }

//
    public long recordSession(String session){
        this.open();
        long id=-1;
        String _date,time="";
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HHmmss");
        _date = sdf.format(cal.getTime());
        time = sdf2.format(cal.getTime());

        try
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put("session",  session);
            contentValues.put("date", _date);
            contentValues.put("time", time);
            id = db.insert("tbl_splash_screen", null, contentValues);
        }
        catch (IllegalStateException e)
        {
            Log.v(TAG, "Error" + e.getMessage());
        }

        this.close();
        return id;
    }

    public String checkSession() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");

        String _date=sdf.format(cal.getTime());
        String session = "";

        final String sql = "SELECT session FROM tbl_splash_screen WHERE date=?";
        this.open();
        cursor = db.rawQuery(sql, new String[]{_date});

        if (cursor.moveToFirst()) {

            session = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return session;
    }
}
