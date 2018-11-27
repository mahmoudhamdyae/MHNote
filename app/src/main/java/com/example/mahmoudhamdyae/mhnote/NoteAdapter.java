package com.example.mahmoudhamdyae.mhnote;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note> {

    public NoteAdapter(Context context, int resource, List<Note> objects) {
        super(context, resource, objects);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.list_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.title_list);
        TextView descriptionTextView = convertView.findViewById(R.id.description_list);
        CardView cardView = convertView.findViewById(R.id.card_view);

        Note note = getItem(position);

        titleTextView.setText(note.getTitle());
        descriptionTextView.setText(note.getDescription());
        switch (note.getColor()){
            case "COLOR_RED":
                cardView.setBackgroundColor(Color.RED);
            case "COLOR_BLUE":
                cardView.setBackgroundColor(Color.BLUE);
            case "COLOR_GREEN":
                cardView.setBackgroundColor(Color.GREEN);
        }

        return convertView;
    }
}
