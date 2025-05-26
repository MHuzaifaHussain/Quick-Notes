package com.example.quicknotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quicknotes.Model.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TakeNotesActivity extends AppCompatActivity {

    EditText titleEdt, descriptionEdt;
    ImageView saveBtn;
    Notes notes;

    boolean isOldNotes = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_take_notes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        saveBtn = findViewById(R.id.saveBtn);
        titleEdt = findViewById(R.id.titleEdt);
        descriptionEdt = findViewById(R.id.descriptionEdt);

        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("old_notes");
            titleEdt.setText(notes.getTitle());
            descriptionEdt.setText(notes.getDescription());
            isOldNotes = true;
        }catch (Exception e){
            e.printStackTrace();
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isOldNotes){
                    notes = new Notes();
                }

                String title = titleEdt.getText().toString();
                String description = descriptionEdt.getText().toString();

                if (title.isEmpty()){
                    Toast.makeText(TakeNotesActivity.this,"Please Enter the Title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (description.isEmpty()){
                    Toast.makeText(TakeNotesActivity.this,"Please Enter the description", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                Date date = new Date();

                notes.setTitle(title);
                notes.setDescription(description);
                notes.setDate(format.format(date));

                Intent intent = new Intent();
                intent.putExtra("note", notes);
                setResult(TakeNotesActivity.RESULT_OK, intent);
                finish();

            }
        });
    }
}