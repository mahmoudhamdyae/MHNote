package com.example.mahmoudhamdyae.mhnote.data;

/**
 * Created by mahmoudhamdyae on 1/23/18.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import org.w3c.dom.Text;

/**
 * API Contract for the Notes app.
 */
public class NoteContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.mahmoudhamdyae.mhnote";
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.mahmoudhamdyae.mhnote/notes/ is a valid path for
     * looking at note data. content://com.example.mahmoudhamdyae.mhnote/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_NOTES = "notes";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public NoteContract() {
    }

    /**
     * Inner class that defines constant values for the notes database table.
     * Each entry in the table represents a single note.
     */
    public static final class NoteEntry implements BaseColumns {

        /**
         * The content URI to access the pet data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of notes.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single note.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

        /**
         * Name of database table for notes
         */
        public final static String TABLE_NAME = "notes";

        /**
         * Unique ID number for the note (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Title of the note
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_NOTE_TITLE = "title";

        /**
         * Name of the note.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_NOTE_DESCRIPTION = "name";

        /**
         * Color of the note.
         * <p>
         * Type: Text
         */
        public final static String COlUMN_NOTE_COLOR = "color";

        /**
         * Alert time of the note.
         * <p>
         * Type: Date
         */
        public final static String COLUMN_NOTE_TIME = "time";

        /**
         * Is the note important.
         * <p>
         * Type: Integer (1 important_0 not important)
         */
        public final static String COLUMN_NOTE_IMPORTANT = "is_important";

        /**
         * Label of the note
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_NOTE_LABEL = "label";
    }
}
