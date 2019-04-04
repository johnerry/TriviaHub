package com.example.johnfash.triviahub;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBAdapterForSettings {
    private static String TAG = "DBAdapterForSettings";

    public static final String KEY_ROWID = "_id";
    public static final String KEY_SOUND = "sound";
    public static final String KEY_MUSIC = "music";
    public static final String KEY_TIMER = "timer";
    public static final String[] ALL_KEYS = new String[]{KEY_ROWID,KEY_SOUND,KEY_MUSIC,KEY_TIMER};
    public static final int COL_ROWID = 0;
    public static final int COL_ROWSOUND = 1;
    public static final int COL_ROWMUSIC = 2;
    public static final int COL_ROWTIMER = 3;

    public static final String DATABASE_NAME = "settings_details";
    public static final String DATABASE_TABLE = "settings_record";
    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_SQL =
            "CREATE TABLE " + DATABASE_TABLE
                    + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_SOUND + " TEXT NOT NULL, "
                    + KEY_MUSIC + " TEXT NOT NULL, "
                    + KEY_TIMER + " TEXT"
                    + ");";

    private static final String CREATE_INIT_VALUES =
            "INSERT INTO "+DATABASE_TABLE+" VALUES (1,'on','on','7 seconds')";

    private final Context context;
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    public DBAdapterForSettings(Context ctx){
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    public DBAdapterForSettings open(){
        db = myDBHelper.getWritableDatabase();
        return this;
    }
    public void  close(){
        myDBHelper.close();
    }

    public long inserRow(String sound, String music, String timer){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SOUND,sound);
        initialValues.put(KEY_MUSIC,music);
        initialValues.put(KEY_TIMER,timer);
        return db.insert(DATABASE_TABLE,null,initialValues);
    }
    public Cursor getAllRows(){
        String where = null;
        Cursor c = db.query(true,DATABASE_TABLE,ALL_KEYS,where,null,null,null,null,null);
        if(c != null){
            c.moveToNext();
        }
        return c;
    }
    public boolean updateRow(long rowId, String sound, String music, String timer){
        String where = KEY_ROWID + "=" + rowId;
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_SOUND,sound);
        newValues.put(KEY_MUSIC,music);
        newValues.put(KEY_TIMER,timer);
        return db.update(DATABASE_TABLE,newValues,where, null) !=0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
            _db.execSQL(CREATE_INIT_VALUES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(_db);

        }
    }

}

