package com.xmstr.electrocount.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.xmstr.electrocount.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xmast_000 on 08.07.2015.
 */
public class CountsDataSource {
    // Database fields
    private myDb dbHelper;
    private String[] allColumns = {myDb.COLUMN_ID,
            myDb.COLUMN_DATE, myDb.COLUMN_TEXT};

    public CountsDataSource(Context context) {
        dbHelper = new myDb(context);
    }

    public void close() {
        dbHelper.close();
    }

    public void createItem(String text, String date) {
        ContentValues values = new ContentValues();
        values.put(myDb.COLUMN_DATE, date);
        values.put(myDb.COLUMN_TEXT, text);
        long insertId = dbHelper.getWritableDatabase().insert(myDb.TABLE, null, values);
    }
    public String getLastItemNumber(){
        Cursor cursor = dbHelper.getReadableDatabase().query(myDb.TABLE, allColumns, null, null, null, null, myDb.COLUMN_TEXT + " DESC", "1");
        String lastText = null;
        if (cursor.moveToFirst()) {
            lastText = cursor.getString(cursor.getColumnIndex(myDb.COLUMN_TEXT));
            cursor.close();
        }
        return lastText;
    }

    public void updateItemText(Item item, String text) {
        ContentValues values = new ContentValues();
        values.put(myDb.COLUMN_TEXT, text);
        long updateId = dbHelper.getWritableDatabase().update(myDb.TABLE, values, myDb.COLUMN_ID + " = " + item.id, null);
    }

    public void deleteItem(Item item) {
        long id = item.id;
        System.out.println("Item deleted with id: " + id);
        dbHelper.getWritableDatabase().delete(myDb.TABLE, myDb.COLUMN_ID
                + " = " + id, null);
    }

    public List<Item> getAllItems() {
        List<Item> comments = new ArrayList<>();
        Cursor cursor = dbHelper.getWritableDatabase().query(myDb.TABLE, allColumns, null, null, null, null, null);
        cursor.moveToLast();
        while (!cursor.isBeforeFirst()) {
            Item comment = cursorToItem(cursor);
            comments.add(comment);
            cursor.moveToPrevious();
        }
        cursor.close();
        return comments;
    }

    private Item cursorToItem(Cursor cursor) {
        Item comment = new Item();
        comment.id = cursor.getLong(cursor.getColumnIndexOrThrow(myDb.COLUMN_ID));
        comment.text = cursor.getString(cursor.getColumnIndexOrThrow(myDb.COLUMN_TEXT));
        comment.date = cursor.getString(cursor.getColumnIndexOrThrow(myDb.COLUMN_DATE));
        return comment;
    }
}
