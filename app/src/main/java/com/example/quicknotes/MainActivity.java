package com.example.quicknotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.quicknotes.Adapter.NoteListAdapter;
import com.example.quicknotes.Database.RoomDb;
import com.example.quicknotes.Interface.NotesClickListner;
import com.example.quicknotes.Model.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    RecyclerView recyclerView;
    NoteListAdapter notesListAdapter;
    RoomDb database;
    FloatingActionButton fabBtn;
    SearchView searchView;
    ImageButton itemSelector;

    List<Notes> notes = new ArrayList<>();
    Notes selectedNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        itemSelector = findViewById(R.id.itemSelector);
        recyclerView = findViewById(R.id.noteRv);
        fabBtn = findViewById(R.id.addBtn);
        searchView = findViewById(R.id.searchView);
        database = RoomDb.getInstance(this);


        itemSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogOut(itemSelector);
            }
        });

        notes = database.mainDAO().getAll();
        updateRecycle(notes);

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TakeNotesActivity.class);
                startActivityForResult(intent, 101);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Pass the search query to the adapter
                notesListAdapter.filterList(newText);
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        notes = database.mainDAO().getAll();
        updateRecycle(notes);
    }

    private void showLogOut(ImageButton itemSelector) {
        PopupMenu popupMenu = new PopupMenu(this, itemSelector);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.logut_menu);
        popupMenu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101){
            if(resultCode == MainActivity.RESULT_OK){
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().insert(new_notes);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }else if (requestCode == 102){
            //Update and Edit the notes on click of notes
            if(resultCode == MainActivity.RESULT_OK){
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().update(new_notes.getID(), new_notes.getTitle(), new_notes.getDescription());
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                notesListAdapter.notifyDataSetChanged();
            }
        }


    }

    private void updateRecycle(List<Notes> notes){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NoteListAdapter(MainActivity.this, notes, notesClickListner);
        recyclerView.setAdapter(notesListAdapter);
    }

    private final NotesClickListner notesClickListner = new NotesClickListner() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this, TakeNotesActivity.class);
            intent.putExtra("old_notes",notes);
            startActivityForResult(intent, 102);
        }

        @Override
        public void onLongPress(Notes notes, CardView cardView, int position) {
            // On Long press we have to pin unpin or delete the note
            selectedNotes = new Notes();
            selectedNotes = notes;
            showPop(cardView, position);
        }
    };

    private void showPop(CardView cardView, int position) {
        // Create a popup menu
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.inflate(R.menu.popup_menu);

        // Handle menu item click
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.delete) {
                // Delete the selected note from the database
                database.mainDAO().delete(selectedNotes);
                notesListAdapter.removeItem(position);
                // Notify adapter that the item is removed

                Toast.makeText(MainActivity.this, "Selected Note Deleted Successfully...", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.logoutMenu){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Optionally, call finish() to explicitly end the current activity
            finish();
            return true;
        }
        return false;
    }
}