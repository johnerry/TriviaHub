package com.example.johnfash.triviahub;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBAdapterForPlayers {
    private static String TAG = "DBAdapterForPlayers";

    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_SCORE = "score";
    public static final String[] ALL_KEYS = new String[]{KEY_ROWID,KEY_NAME,KEY_SCORE};
    public static final int COL_ROWID = 0;
    public static final int COL_ROWNAME = 1;
    public static final int COL_ROWSCORE = 2;

    public static final String DATABASE_NAME = "player_details";
    public static final String DATABASE_TABLE = "player_record";
    public static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE_SQL =
            "CREATE TABLE " + DATABASE_TABLE
            + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT NOT NULL, "
            + KEY_SCORE + " TEXT"
            + ");";
    private final Context context;
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    public DBAdapterForPlayers(Context ctx){
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    public DBAdapterForPlayers open(){
        db = myDBHelper.getWritableDatabase();
        return this;
    }
    public void  close(){
        myDBHelper.close();
    }

    public long inserRow(String name, String score){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME,name);
        initialValues.put(KEY_SCORE,score);
        return db.insert(DATABASE_TABLE,null,initialValues);
    }

    public Cursor getRowByName(String name){
        Cursor cc = db.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE name = '"+name+"'",null);
        cc.moveToFirst();
        return cc;
    }

    public Cursor getAllRows(){
        String where = null;
        Cursor c = db.query(true,DATABASE_TABLE,ALL_KEYS,where,null,null,null,null,null);
        if(c != null){
            c.moveToNext();
        }
    return c;
    }
    public boolean updateRow(long rowId, String name, String score){
        String where = KEY_ROWID + "=" + rowId;
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME,name);
        newValues.put(KEY_SCORE,score);
        return db.update(DATABASE_TABLE,newValues,where, null) !=0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(_db);

        }
    }

}

