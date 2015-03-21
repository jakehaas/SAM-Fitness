/**
 * DatabaseHandler.java
 * Wellness-App-MQP
 *
 * @version 1.0.0
 *
 * @author Jake Haas
 * @author Evan Safford
 * @author Nate Ford
 * @author Haley Andrews
 *
 * Copyright (c) 2014, 2015. Wellness-App-MQP. All Rights Reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY 
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package edu.wpi.wellnessapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SAMDB";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MOOD = "MoodData";
    public static final String TABLE_HOURSSLEPT = "SleepData";

    public static final String COLUMN_DATE = "Date";
    public static final String COLUMN_STEPS = "Mood";
    public static final String COLUMN_HOURS = "Hours";

    private static final String KEY_ID = "id";

    private static final String[] MOOD_COLUMNS = {KEY_ID, COLUMN_DATE, COLUMN_STEPS};
    private static final String[] HOURSSLEPT_COLUMNS = {KEY_ID, COLUMN_DATE, COLUMN_HOURS};

    private static final String CREATE_MOOD_TABLE = "CREATE TABLE MoodData ( "
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "date TEXT, "
            + "mood TEXT )";

    private static final String CREATE_STEPS_TABLE = "CREATE TABLE SleepData ( "
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "date TEXT, "
            + "hours TEXT )";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MOOD_TABLE);
        db.execSQL(CREATE_STEPS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOURSSLEPT);
        onCreate(db);
    }


    public void addMood(MoodTic moodTic) {
        Log.d("Saving Mood to DB...", moodTic.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("date", moodTic.getDate()); // get title
        values.put("mood", moodTic.getMood()); // get author

        // 3. insert
        db.insert(TABLE_MOOD, // table
                null, // nullColumnHack
                values); // key/value -> keys = column names/ values = column
        // values

        // 4. close
        db.close();
    }

    public float getTodaysMoodAvg() {

        Log.d("DD", "Getting todays mood avg from DB...");
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);

        String selArgs = " date=" + String.valueOf(date);

        // 2. build query
        Cursor cursor = db.query(TABLE_MOOD, // a. table
                MOOD_COLUMNS, // b. column names
                selArgs, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        // 3. if we got results loop through them
        if (cursor != null) {
            cursor.moveToFirst();
            int rowCount = 0;
            float moodTotal = 0;
            while (cursor.isAfterLast() == false) {
                moodTotal += Float.valueOf(cursor.getString(2));
                rowCount++;
                cursor.moveToNext();
            }
            cursor.close();

            float todaysAvg = (moodTotal / rowCount);

            if (rowCount == 0) {
                return 0.00F;
            } else {
                return todaysAvg;
            }
        } else {
            return 0.00F;
        }
    }

    public void addHoursSlept(HoursSlept hoursSlept) {
        Log.d("Saving SleepHours to DB...", hoursSlept.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("date", hoursSlept.getDate()); // get title
        values.put("hours", hoursSlept.getHours()); // get author

        // 3. insert
        db.insert(TABLE_HOURSSLEPT, // table
                null, // nullColumnHack
                values); // key/value -> keys = column names/ values = column
        // values

        // 4. close
        db.close();
    }

    public float getTodaysSleepTotal() {

        Log.d("DatabaseHandler", "Getting todays sleep total from DB...");
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);

        String selArgs = " date=" + String.valueOf(date);

        // 2. build query
        Cursor cursor = db.query(TABLE_HOURSSLEPT, // a. table
                HOURSSLEPT_COLUMNS, // b. column names
                selArgs, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        // 3. if we got results loop through them
        if (cursor != null) {
            cursor.moveToFirst();
            int rowCount = 0;
            float sleepTotal = 0;
            while (cursor.isAfterLast() == false) {
                sleepTotal += Float.valueOf(cursor.getString(2));
                rowCount++;
                cursor.moveToNext();
            }
            cursor.close();

            if (rowCount == 0) {
                return 0.00F;
            } else {
                return sleepTotal;
            }
        } else {
            return 0.00F;
        }
    }

}