package com.example.mahmoudhamdyae.mhnote;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Displays list of notes that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    String extraTitle = "";
    String extraDescription = "";

    private NoteAdapter mNoteAdapter;
    private ListView mNoteListView;

    public static final int RC_SIGN_IN = 1;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNoteDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ChildEventListener mChildEventListener;

    // todo offline
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // todo extra String
                intent.putExtra("noteID", "");
                intent.putExtra("noteDescription", "");

                // Launch the {@link EditorActivity} to create a new note.
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the note data
        mNoteListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        mNoteListView.setEmptyView(emptyView);

        // Initialize note ListView and its adapter
        final List<Note> notes = new ArrayList<>();
        mNoteAdapter = new NoteAdapter(this, R.layout.list_item, notes);
        mNoteListView.setAdapter(mNoteAdapter);

        // Setup item click listener
        mNoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // todo extra String
                intent.putExtra("noteID", extraTitle);
                intent.putExtra("noteDescription", extraDescription);

                // Launch the {@link EditorActivity} to display the data for the current note.
                startActivity(intent);
            }
        });

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mNoteDatabaseReference = mFirebaseDatabase.getReference().child("notes").child(mFirebaseAuth.getCurrentUser().getUid());

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.FacebookBuilder().build(),
                                            new AuthUI.IdpConfig.TwitterBuilder().build(),
                                            new AuthUI.IdpConfig.PhoneBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    // todo why empty view appears
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        mNoteAdapter.clear();
        detachDatabaseReadListener();
    }

    private void onSignedInInitialize(String username) {
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        mNoteAdapter.clear();
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                    Note note = dataSnapshot.getValue(Note.class);
                    mNoteAdapter.add(note);
                }
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
            mNoteDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mNoteDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled, Please connect to network", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * Helper method to delete all notes in the database.
     */
    private void deleteAllNotes() {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting_item:
                startActivity(new Intent(CatalogActivity.this, SettingActivity.class));
                return true;
            case R.id.delete_all_notes:
                deleteAllNotes();
                return true;
            case R.id.sign_out_item:
                AuthUI.getInstance().signOut(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}