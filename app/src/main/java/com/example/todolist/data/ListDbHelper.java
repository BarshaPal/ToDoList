package com.example.todolist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todolist.data.ListContract.ListEntry;

/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class ListDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ListDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "todolist4.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 2;


    public ListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + ListEntry.TABLE_NAME + " ("
                + ListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ListEntry.TODOS + " TEXT NOT NULL, "
                + ListEntry.DATE + " TEXT, "
                + ListEntry.TIME + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
