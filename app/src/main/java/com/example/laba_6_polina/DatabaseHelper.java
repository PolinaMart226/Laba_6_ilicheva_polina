package com.example.laba_6_polina;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "reminders.db";
    public static final String TABLE_NAME = "reminder_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TITLE";
    public static final String COL_3 = "MESSAGE";
    public static final String COL_4 = "DATE";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, MESSAGE TEXT, DATE TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addReminder(String title, String message, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, title);
        contentValues.put(COL_3, message);
        contentValues.put(COL_4, date);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        int columnCount = cursor.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            Log.d("DatabaseHelper", "Column " + i + ": " + cursor.getColumnName(i));
        }

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_1));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_2));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(COL_3));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_4));
                reminders.add(new Reminder(id, title, message, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reminders;
    }

    public boolean deleteReminder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_1 + "=?", new String[]{String.valueOf(id)}) > 0;
    }
}
