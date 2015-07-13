package com.xmstr.electrocount.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by xmast_000 on 08.07.2015.
 */
public class myDb extends SQLiteOpenHelper {

    public static final String TABLE = "data";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TEXT = "txt";

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_DATE + " text not null, "
            + COLUMN_TEXT + " text not null);";

    public myDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
