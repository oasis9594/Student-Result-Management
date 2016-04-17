package com.example.dell.studentresultmanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper{

    // instance
    private static MyDBHandler sInstance;

    // Database Info
    private static final String DATABASE_NAME = "userDetails.db";
    private static final int DATABASE_VERSION = 3;

    // Table Names
    private static final String TABLE_NAME = "UserDetails";

    //Column Names
    private static final String COLUMN_ID="id";
    private static final String COLUMN_PASS="password";
    private static final String COLUMN_SUB1="subject1";
    private static final String COLUMN_SUB2="subject2";
    private static final String COLUMN_SUB3="subject3";
    private static final String COLUMN_SUB4="subject4";
    private static final String COLUMN_SUB5="subject5";
    private static final String COLUMN_SUB6="subject6";
    private static final String COLUMN_MAXMARKS="maxmarks";
    private static final String COLUMN_REQUEST="request";
    private static final String COLUMN_ADMIN="isadmin";
    private static final String COLUMN_NAME="name";

    //make your database instance a singleton instance across the entire application's lifecycle
    public static synchronized MyDBHandler getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        Log.w(myUtils.TAG, "getInstance");
        if (sInstance == null) {
            sInstance = new MyDBHandler(context.getApplicationContext());
            Log.w(myUtils.TAG, "Instance null");
        }
        return sInstance;
    }
    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="CREATE TABLE "+TABLE_NAME+"("+
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PASS + " TEXT, " +
                COLUMN_REQUEST + " TEXT, " + //accepted, rejected, or waiting
                COLUMN_ADMIN + " INTEGER, " + //0 or 1
                COLUMN_MAXMARKS + " REAL, " +
                COLUMN_SUB1 + " REAL, " +
                COLUMN_SUB2 + " REAL, " +
                COLUMN_SUB3 + " REAL, " +
                COLUMN_SUB4 + " REAL, " +
                COLUMN_SUB5 + " REAL, " +
                COLUMN_SUB6 + " REAL " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion!=newVersion)
        {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
    public void addUser(UserDetails item)
    {
        Log.w(myUtils.TAG, "addUser in database");
        SQLiteDatabase db=getWritableDatabase();
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try{
            ContentValues values=new ContentValues();
            values.put(COLUMN_ID, item.getId());
            values.put(COLUMN_NAME, item.getName());
            values.put(COLUMN_PASS, item.getPassword());
            values.put(COLUMN_ADMIN, item.isAdmin());
            values.put(COLUMN_MAXMARKS, item.getMaxMarks());
            values.put(COLUMN_REQUEST, item.getRequest());
            values.put(COLUMN_SUB1, item.getResult().get(0));
            values.put(COLUMN_SUB2, item.getResult().get(1));
            values.put(COLUMN_SUB3, item.getResult().get(2));
            values.put(COLUMN_SUB4, item.getResult().get(3));
            values.put(COLUMN_SUB5, item.getResult().get(4));
            values.put(COLUMN_SUB6, item.getResult().get(5));
            db.insertOrThrow(TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        }catch (Exception e) {
            Log.w(myUtils.TAG, "Error while adding user to database");
        }finally {
            db.endTransaction();
        }
    }
    public void deleteUser(UserDetails item)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.beginTransaction();
        try
        {
            db.delete(TABLE_NAME, COLUMN_ID + " = " + item.getId(), null);
            db.setTransactionSuccessful();
        }catch (Exception e) {
            Log.w(myUtils.TAG, "Error while deleting alarm from database");
        }
        finally {
            db.endTransaction();
        }
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=\"" + item.getId() + "\";");
    }

    public UserDetails getUser(String username) {
        UserDetails item=new UserDetails();
        item.setId(username);
        item.setName(getName(username));
        item.setIsAdmin(isAdmin(username));
        item.setMaxMarks(getMaxMarks(username));
        item.setRequest(requestType(username));
        item.setResult(getMarks(username));
        item.setPassword(getPass(username));
        return item;
    }
    public String getPass(String username)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ID +"=\"" + username + "\";";
        Cursor c=db.rawQuery(query, null);
        try {
            c.moveToFirst();
            String s= c.getString(c.getColumnIndex(COLUMN_PASS));
            c.close();
            return s;
        } catch (Exception e) {
            Log.w(myUtils.TAG, "Error while getting password");
            return null;
        }
    }

    public boolean userExists(String username)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ID +"=\"" + username + "\";";
        Cursor c=db.rawQuery(query, null);
        Log.w(myUtils.TAG, "userExists");
        boolean b= c.moveToFirst();
        c.close();
        return b;
    }

    public boolean validate(String username, String password)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ID +"=\"" + username + "\" AND "+
                COLUMN_PASS+"=\""+password+"\";";
        Cursor c=db.rawQuery(query, null);
        Log.w(myUtils.TAG, "validate");
        boolean b= c.moveToFirst();
        c.close();
        return b;
    }

    public boolean isAdmin(String username) {
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ID +"=\"" + username +"\";";
        Cursor c=db.rawQuery(query, null);
        try {
            c.moveToFirst();
            boolean x = c.getInt(c.getColumnIndex(COLUMN_ADMIN))==1;
            c.close();
            return x;
        }catch (Exception e)
        {
            Log.w(myUtils.TAG, "Error while checking if user is admin");
            return false;
        }
    }
    public String requestType(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=\"" + username +"\";";
        Cursor c = db.rawQuery(query, null);
        try {
            c.moveToFirst();
            String s= c.getString(c.getColumnIndex(COLUMN_REQUEST));
            c.close();
            return s;
        } catch (Exception e) {
            Log.w(myUtils.TAG, "Error while checking requestType");
            return null;
        }
    }
    public boolean containsAnyData()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        boolean b;
        Cursor c;
        try{
            c = db.rawQuery(query, null);
            b= c.moveToFirst();
        }catch (Exception e)
        {
            Log.w(myUtils.TAG,"containsAnyData: "+e.getMessage());
            return false;
        }
        c.close();
        return b;
    }

    public String getName(String username)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=\"" + username +"\";";
        Cursor c = db.rawQuery(query, null);
        try {
            c.moveToFirst();
            String s= c.getString(c.getColumnIndex(COLUMN_NAME));
            c.close();
            return s;
        } catch (Exception e) {
            Log.w(myUtils.TAG, "Error while getting Name from database");
            return null;
        }
    }
    public ArrayList<Double> getMarks(String username)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=\"" + username +"\";";
        Cursor c = db.rawQuery(query, null);
        ArrayList<Double> mlist=new ArrayList<>(6);
        try {
            c.moveToFirst();
            mlist.add(c.getDouble(c.getColumnIndex(COLUMN_SUB1)));
            mlist.add(c.getDouble(c.getColumnIndex(COLUMN_SUB2)));
            mlist.add(c.getDouble(c.getColumnIndex(COLUMN_SUB3)));
            mlist.add(c.getDouble(c.getColumnIndex(COLUMN_SUB4)));
            mlist.add(c.getDouble(c.getColumnIndex(COLUMN_SUB5)));
            mlist.add(c.getDouble(c.getColumnIndex(COLUMN_SUB6)));
            c.close();
        } catch (Exception e) {
            Log.w(myUtils.TAG, "Error while getting marks from database");
        }
        return mlist;
    }
    public double getMaxMarks(String username)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=\"" + username +"\";";
        Cursor c = db.rawQuery(query, null);
        try {
            c.moveToFirst();
            return c.getDouble(c.getColumnIndex(COLUMN_MAXMARKS));
        } catch (Exception e) {
            Log.w(myUtils.TAG, "Error while getting MaxMarks from database");
            return -1.0;
        }
    }
    public ArrayList<String> getAllUsers()
    {
        ArrayList<String> mlist=new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ADMIN + "= 0;";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        try {
            if(c.moveToFirst())
            do {
                String s=c.getString(c.getColumnIndex(COLUMN_ID));
                mlist.add(s);
            }while (c.moveToNext());
        }catch (Exception e)
        {
            Log.w(myUtils.TAG, "getAllUsers: "+e.getMessage());
        }
        c.close();
        return mlist;
    }

    public int updateUser(UserDetails item)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, item.getId());
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_PASS, item.getPassword());
        values.put(COLUMN_ADMIN, item.isAdmin());
        values.put(COLUMN_MAXMARKS, item.getMaxMarks());
        values.put(COLUMN_REQUEST, item.getRequest());
        values.put(COLUMN_SUB1, item.getResult().get(0));
        values.put(COLUMN_SUB2, item.getResult().get(1));
        values.put(COLUMN_SUB3, item.getResult().get(2));
        values.put(COLUMN_SUB4, item.getResult().get(3));
        values.put(COLUMN_SUB5, item.getResult().get(4));
        values.put(COLUMN_SUB6, item.getResult().get(5));

        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(item.getId()) });
    }
}
