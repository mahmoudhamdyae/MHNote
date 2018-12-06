package com.example.mahmoudhamdyae.mhnote;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Note {

    // Title of the note.
    private String title;
    // Description of the note.
    private String description;
    // Color of the note.
    // todo Color not String
    private String color;
    // Alert time of the note.
    private String time;
    // True if the note is important, false if not.
    private boolean is_important;
    // Label of the note
    private String label;

    public Note(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Note(String title, String description, String color, String time, boolean is_important, String label){
        this.title = title;
        this.description = description;
        this.color = color;
        this.time = time;
        this.is_important = is_important;
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean is_important() {
        return is_important;
    }

    public void setIs_important(boolean is_important) {
        this.is_important = is_important;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
