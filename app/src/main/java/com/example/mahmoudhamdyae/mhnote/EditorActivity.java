package com.example.mahmoudhamdyae.mhnote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditorActivity extends AppCompatActivity {

    /**
     * Content URI for the existing note ("" if it's a new note)
     */
    private String noteID = "";

    /**
     * EditText field to enter the note's title
     */
    private EditText mTitleEditText;

    /**
     * EditText field to enter the note's description
     */
    private EditText mDescriptionEditText;

    /**
     * ScrollView field for the color of the note
     */
    private ScrollView scrollView;

    /**
     * Boolean flag that keeps track of whether the Note has been edited (true) or not (false)
     */
    private boolean mNoteHasChanged = false;

    /**
     * Constant for red color
     */
    private final String COLOR_RED = "#ff0000";

    /**
     * Constant for blue color
     */
    private final String COLOR_BLUE = "#0000FF";

    /**
     * Constant for green color
     */
    private final String COLOR_GREEN = "#008000";

    /**
     * String for note color
     */
    String color = COLOR_RED;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNoteDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mNoteHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mNoteHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mTitleEditText = findViewById(R.id.edit_title);
        mDescriptionEditText = findViewById(R.id.edit_description);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new note or editing an existing one.
        Intent intent = getIntent();
        noteID = intent.getStringExtra("noteID");

        // If the intent DOES NOT contain a note content URI, then we know that we are
        // creating a new note.
        if (noteID .equals("")) {
            // This is a new Note, so change the app bar to say "Add a Note"
            setTitle(R.string.editor_activity_title_new_note);
        } else {
            // Otherwise this is an existing note, so change app bar to say "Edit Note"
            setTitle(getString(R.string.editor_activity_title_edit_note));

            mTitleEditText.setText(noteID);
            mDescriptionEditText.setText(intent.getStringExtra("noteDescription"));
        }

        scrollView = findViewById(R.id.edit_scroll);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mTitleEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mNoteDatabaseReference = mFirebaseDatabase.getReference().child("notes");
    }

    /**
     * Get user input from editor and save Note into database
     */
    private void saveNote() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String titleString = mTitleEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();

        // Check if this is supposed to be a new note
        // and check if all the fields in the editor are blank
        if (noteID.equals("") &&
                TextUtils.isEmpty(titleString) && TextUtils.isEmpty(descriptionString)) {
            // Since no fields were modified, we can return early without creating a new note.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Determine if this is a new or existing note by checking if mCurrentNoteUri is null or not
        if (noteID .equals("")) {
            // This is a NEW Note
            Note note = new Note(mTitleEditText.getText().toString(), mDescriptionEditText.getText().toString(), color, "", false, "");
            mNoteDatabaseReference.push().setValue(note);
        } else {
            if (mNoteHasChanged) {
                // Otherwise this is an EXISTING note, so update the Note
                // todo update firebase
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new Note, hide the "Delete" menu item.
        if (noteID.equals("")) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save Note to database
                saveNote();
                // Exit activity
                finish();
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on "Color" menu option
            case R.id.action_color:
                chooseColor();
                return true;

            // Respond to a click on the "Alarm" menu option
            case R.id.action_alarm:
                alarmSet();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the Note hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mNoteHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the Note hasn't changed, continue with handling back button press
        if (!mNoteHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog to choose color of the note
     */
    private void chooseColor(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_color_title)
                .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                color = COLOR_RED;
                                scrollView.setBackgroundColor(Color.RED);
                                mNoteHasChanged = true;
                                break;
                            case 1:
                                color = COLOR_BLUE;
                                scrollView.setBackgroundColor(Color.BLUE);
                                mNoteHasChanged = true;
                                break;
                            case 2:
                                color = COLOR_GREEN;
                                scrollView.setBackgroundColor(Color.GREEN);
                                mNoteHasChanged = true;
                                break;
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Set alarm
     */
    private void alarmSet(){
        // todo alarm
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_msg_unsaved_changes);
        builder.setPositiveButton(R.string.dialog_discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.dialog_keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the Note.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_msg_delete);
        builder.setPositiveButton(R.string.dialog_delete_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the Note.
                deleteNote();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the Note.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the Note in the database.
     */
    private void deleteNote() {
        // Only perform the delete if this is an existing Note.
        if (noteID.equals("")) {
            // Call the ContentResolver to delete the Note at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentNoteUri
            // content URI already identifies the Note that we want.
//            int rowsDeleted = getContentResolver().delete(mCurrentNoteUri, null, null);
        }

        // Close the activity
        finish();
    }
}