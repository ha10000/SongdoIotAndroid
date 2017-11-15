package kr.linkb.helloworld;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SPTek on 2017-10-26.
 */

public class Dictionary extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="dictionary";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME ="voca";
    public static final String WORD = "word";
    public static final String DEFINITION = "definition";

    public Dictionary(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TABLE_NAME + "(" +
                "_id integer PRIMARY KEY autoincrement," +
                WORD + " text," + DEFINITION + " text)";
        try {
            db.execSQL(sql);
        } catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL("drop table if exists "+TABLE_NAME);
        }
        catch(Exception e){e.printStackTrace();}
        onCreate(db);
    }
}
