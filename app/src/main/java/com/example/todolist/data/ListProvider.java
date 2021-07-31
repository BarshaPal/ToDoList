package com.example.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.Nullable;

import com.example.todolist.data.ListContract.ListEntry;

/**
 * {@link ContentProvider} for Pets app.
 */
public class ListProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ListProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the pets table
     */
    private static final int LISTS = 100;

    /**
     * URI matcher code for the content URI for a single pet in the pets table
     */
    private static final int LIST_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(ListContract.CONTENT_AUTHORITY, ListContract.PATH_LIST, LISTS);
        sUriMatcher.addURI(ListContract.CONTENT_AUTHORITY, ListContract.PATH_LIST + "/#", LIST_ID);
    }

    /**
     * Database helper object
     */
    private ListDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Create and initialize a ListDbHelper object to gain access to the pets database.
        mDbHelper = new ListDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case LISTS:

                cursor = database.query(ListEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case LIST_ID:

                selection = ListEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ListEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }


        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LISTS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        String todos = values.getAsString(ListEntry.TODOS);

        String date = values.getAsString(ListEntry.DATE);

        String time = values.getAsString(ListEntry.TIME);
        if (todos == null || todos.length() == 0)
        {
            Toast.makeText(getContext(),"no input",Toast.LENGTH_LONG).show();
}
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ListEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LISTS:
                return updatePet(uri, values, selection, selectionArgs);
            case LIST_ID:
                if (values.containsKey(ListEntry.TODOS)) {

                    String todos = values.getAsString(ListEntry.TODOS);

                }
                if (values.containsKey(ListEntry.DATE)) {
                    String date = values.getAsString(ListEntry.DATE);

                }
                if (values.containsKey(ListEntry.TIME)) {
                    String time = values.getAsString(ListEntry.TIME);

                }
                selection = ListEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();


        int rows=database.update(ListEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rows!=0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }



    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match=sUriMatcher.match(uri);
        int rows;
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        switch (match) {
            case LISTS:
                rows=db.delete(ListEntry.TABLE_NAME,null,null);
                break;
            case LIST_ID:
                selection = ListEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rows= db.delete(ListEntry.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }
        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rows;
    }
    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LISTS:
                return ListEntry.CONTENT_LIST_TYPE;
            case LIST_ID:
                return ListEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
