package com.example.quicknotes.Interface;

import androidx.cardview.widget.CardView;

import com.example.quicknotes.Model.Notes;

public interface NotesClickListner {

    void onClick(Notes notes);
    void onLongPress(Notes notes, CardView cardView, int Position);

}
