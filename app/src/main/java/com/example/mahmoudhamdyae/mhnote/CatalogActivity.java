package com.example.mahmoudhamdyae.mhnote;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mahmoudhamdyae.mhnote.data.NoteContract.NoteEntry;

/**
 * Displays list of notes that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int NOTE_Loader = 0; // Can be any value

    NoteCursorAdapter mCursorAdapter;

    /**
     * Database helper that will provide us access to the database
     * private NoteDbHelper mDbHelper;
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Find the ListView which will be populated with the note data
        ListView noteListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        noteListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of note  data in the cursor.
        // There is no note data yet (until the loader finishes) so pass is null for the Cursor.
        mCursorAdapter = new NoteCursorAdapter(this, null);
        noteListView.setAdapter(mCursorAdapter);

        // Setup item click listener
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific note that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // P@link NoteEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.mahmoudhamdyae.mhnote/notes/2"
                // If the note with ID 2 was clicked on
                Uri currentNoteUri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentNoteUri);

                // Launch the {@link EditorActivity} to display the data for the current note.
                startActivity(intent);
            }
        });
        // Kick off the loader
        getLoaderManager().initLoader(NOTE_Loader, null, this);
    }


    /**
     * Helper method to delete all notes in the database.
     */
    private void deleteAllNotes() {

        String[] projection = {
                NoteEntry._ID,
                NoteEntry.COLUMN_NOTE_TITLE,
                NoteEntry.COLUMN_NOTE_DESCRIPTION,
        };

        // Perform a query on the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to access the pet data.
        Cursor cursor = getContentResolver().query(
                NoteEntry.CONTENT_URI,   // The content URI of the words table
                projection,             // The columns to return for each row
                null,                   // Selection criteria
                null,                   // Selection criteria
                null);                  // The sort order for the returned rows

        if (cursor.getCount() > 0) {
            // Create an AlertDialog.Builder and set the message, and click listeners
            // for the positive and negative buttons on the dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_all_dialog_msg);
            builder.setPositiveButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Cancel" button, so dismiss the dialog
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            builder.setNegativeButton(R.string.delete_all_notes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Delete all notes" button
                    int rowsDeleted = getContentResolver().delete(NoteEntry.CONTENT_URI, null, null);
                    //toastShow(rowsDeleted);
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            Toast.makeText(this, R.string.no_notes, Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting_item:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.new_note_item:
                startActivity(new Intent(this, EditorActivity.class));
                return true;
            case R.id.delete_all_notes:
                deleteAllNotes();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about
        String[] projection = {
                NoteEntry._ID,
                NoteEntry.COLUMN_NOTE_TITLE,
                NoteEntry.COLUMN_NOTE_DESCRIPTION};

        // This loader will execute the ContentProvider a query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                NoteEntry.CONTENT_URI,           // Provider content URI to query
                projection,                     // Columns to include in the resulting cursor
                null,                   // No selection clause
                null,               // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link NoteCursorAdapter} with this new cursor containing updated note data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}