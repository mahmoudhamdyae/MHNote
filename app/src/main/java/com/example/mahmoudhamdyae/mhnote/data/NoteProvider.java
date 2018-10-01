package com.example.mahmoudhamdyae.mhnote.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.mahmoudhamdyae.mhnote.data.NoteContract.NoteEntry;

/**
 * Created by mahmoudhamdyae on 3/3/18.
 */

/**
 * {@link ContentProvider} for NOTES app.
 */

public class NoteProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the NOTES table
     */
    private static final int NOTES = 100;

    /**
     * URI matcher code for the content URI for a single Note in the NOTES table
     */
    private static final int NOTE_ID = 101;

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

        // The content URI of the form "content://com.example.android.NOTES/NOTES" will map to the
        // integer code {@link #NOTES}. This URI is used to provide access to MULTIPLE rows
        // of the NOTES table.
        sUriMatcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTES, NOTES);

        // The content URI of the form "content://com.example.android.NOTES/NOTES/#" will map to the
        // integer code {@link #NOTE_ID}. This URI is used to provide access to ONE single row
        // of the NOTES table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.mahmoudhamdyae.mhnote/NOTES/3" matches, but
        // "content://com.example.mahmoudhamdyae.mhnote/NOTES" (without a number at the end) doesn't match.
        sUriMatcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTES + "/#", NOTE_ID);
    }

    /**
     * Database helper object
     */
    private NoteDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new NoteDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                // For the NOTES code, query the NOTES table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the NOTES table.
                cursor = database.query(NoteEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case NOTE_ID:
                // For the NOTE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.NOTES/NOTES/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = NoteEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the NOTES table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(NoteEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the cursor,
        // so we know what content URI the cursor was created for.
        // If the date at the URI vhanges, then we know we need to update the cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                return insertNote(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a Note into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertNote(Uri uri, ContentValues values) {

        // Sanity checking the attributes in ContentValues
        // Check that the title is not null
        /* Sanity checking is in insert and update only */
        String title = values.getAsString(NoteEntry.COLUMN_NOTE_TITLE);
        if (title == null)
            throw new IllegalArgumentException("Note requires a name");

        String description = values.getAsString(NoteEntry.COLUMN_NOTE_DESCRIPTION);
        if (description == null)
            throw new IllegalArgumentException("Note requires s description");

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new Note with the given values
        long id = database.insert(NoteEntry.TABLE_NAME, null, values);

        // Notify all listeners that the data has changed for the Note content URI
        // uri: content://com.example.mahmoudhamdyae.mhnote.NOTES/NOTES
        getContext().getContentResolver().notifyChange(uri, null);

        // return the new URI with the ID (of the newly inserted row) appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                return updateNote(uri, contentValues, selection, selectionArgs);
            case NOTE_ID:
                // For the NOTE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = NoteEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateNote(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update NOTES in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more NOTES).
     * Return the number of rows that were successfully updated.
     */
    private int updateNote(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        /**
         * Another way to think about this code change is that we’re wrapping an “if” check
         * around the code block for each Note attribute (from the insertNote() method)
         * and making sure the attribute is present first.
         */

        // If the {@link NoteEntry#COLUMN_Note_Title} key is present,
        // check that the name value is not null.
        if (values.containsKey(NoteEntry.COLUMN_NOTE_TITLE)) {
            String title = values.getAsString(NoteEntry.COLUMN_NOTE_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Note requires a name");
            }
        }

        // If the {@link NoteEntry#COLUMN_Note_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(NoteEntry.COLUMN_NOTE_TITLE)) {
            String description = values.getAsString(NoteEntry.COLUMN_NOTE_DESCRIPTION);
            if (description == null) {
                throw new IllegalArgumentException("Note requires a description");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        /*// Returns the number of database rows affected by the update statement
        return database.update(NoteEntry.TABLE_NAME, values, selection, selectionArgs);*/

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(NoteEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                // Delete all rows that match the selection and selection args
                /*return database.delete(NoteEntry.TABLE_NAME, selection, selectionArgs);*/
                rowsDeleted = database.delete(NoteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case NOTE_ID:
                // Delete a single row given by the ID in the URI
                selection = NoteEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                /*return database.delete(NoteEntry.TABLE_NAME, selection, selectionArgs);*/
                rowsDeleted = database.delete(NoteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                return NoteEntry.CONTENT_LIST_TYPE;
            case NOTE_ID:
                return NoteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
