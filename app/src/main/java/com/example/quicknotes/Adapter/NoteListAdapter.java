package com.example.quicknotes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quicknotes.Database.RoomDb;
import com.example.quicknotes.Interface.NotesClickListner;
import com.example.quicknotes.Model.Notes;
import com.example.quicknotes.R;

import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NotesViewHolder> {

    Context context;
    List<Notes> notesList;
    List<Notes> originalNotesList; // Store the original list
    NotesClickListner listner;
    RoomDb database;

    public NoteListAdapter(Context context, List<Notes> notesList, NotesClickListner listner) {
        this.context = context;
        this.notesList = notesList;
        this.originalNotesList = new ArrayList<>(notesList); // Store the original list
        this.listner = listner;
        this.database = RoomDb.getInstance(context); // Initialize the database
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.note_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        Notes currentNote = notesList.get(position);
        holder.titleTxt.setText(currentNote.getTitle());
        holder.descriptionTxt.setText(currentNote.getDescription());
        holder.dateTxt.setText(currentNote.getDate());
        holder.dateTxt.setSelected(true);

        holder.cardView.setOnClickListener(v -> listner.onClick(notesList.get(holder.getAdapterPosition())));

        holder.cardView.setOnLongClickListener(v -> {
            listner.onLongPress(notesList.get(holder.getAdapterPosition()), holder.cardView, position);
            return true; // Ensure it's handled
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    // Method to filter the list based on the search query
    public void filterList(String query) {
        if (query.isEmpty()) {
            // Restore the original list when search is cleared
            notesList.clear();
            notesList.addAll(database.mainDAO().getAll());
        } else {
            // Query the database for filtered results based on title or description
            List<Notes> filteredList = database.mainDAO().searchNotes(query);
            notesList.clear();
            notesList.addAll(filteredList);
        }
        notifyDataSetChanged(); // Notify adapter of the changes
    }

    // Method to remove an item from the list
    public void removeItem(int position) {
        notesList.remove(position);
        originalNotesList.remove(position); // Also remove from the original list
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, notesList.size());
    }
}

class NotesViewHolder extends RecyclerView.ViewHolder {

    CardView cardView;
    TextView titleTxt, descriptionTxt, dateTxt;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.note_container);
        titleTxt = itemView.findViewById(R.id.titleTxt);
        descriptionTxt = itemView.findViewById(R.id.descriptionTxt);
        dateTxt = itemView.findViewById(R.id.dateTxt);
    }
}