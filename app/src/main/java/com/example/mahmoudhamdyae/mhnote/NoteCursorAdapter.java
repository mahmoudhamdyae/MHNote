package com.example.mahmoudhamdyae.mhnote;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.mahmoudhamdyae.mhnote.data.NoteContract.NoteEntry;

/**
 * {@link NoteCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of note data as its data source. This adapter knows
 * how to create list items for each row of Note data in the {@link Cursor}.
 */
public class NoteCursorAdapter extends CursorAdapter {
    /**
     * Constructs a new {@link NoteCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public NoteCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the Note data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current Note can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView titleTextView = view.findViewById(R.id.title_list);
        TextView descriptionTextView = view.findViewById(R.id.description_list);
        CardView cardView = view.findViewById(R.id.card_view);

        // Find the columns of Note attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(NoteEntry.COLUMN_NOTE_TITLE);
        int descriptionColumnIndex = cursor.getColumnIndex(NoteEntry.COLUMN_NOTE_DESCRIPTION);
        int colorColumnIndex = cursor.getColumnIndex(NoteEntry.COlUMN_NOTE_COLOR);

        // Read the Note attributes from the Cursor for the current Note
        String NoteTitle = cursor.getString(titleColumnIndex);
        String NoteDescription = cursor.getString(descriptionColumnIndex);
        String color = cursor.getString(colorColumnIndex);

        // Update the TextViews with the attributes for the current Note
        titleTextView.setText(NoteTitle);
        descriptionTextView.setText(NoteDescription);
        switch (color){
            case "#ff0000":
                cardView.setBackgroundColor(Color.RED);
                break;
            case "#0000FF":
                cardView.setBackgroundColor(Color.BLUE);
                break;
            case "#008000":
                cardView.setBackgroundColor(Color.GREEN);
                break;
            default:
                cardView.setBackgroundColor(Color.WHITE);
                break;
        }

    }
}