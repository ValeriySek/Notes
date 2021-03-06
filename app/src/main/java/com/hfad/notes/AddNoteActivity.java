package com.hfad.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddNoteActivity extends AppCompatActivity {

    private TextInputEditText titleEditText;
    private TextInputEditText descriptionEditText;
    private AutoCompleteTextView dayOfWeekEditText;
    private MaterialButton buttonAddNote;
    private RadioGroup radioGroup;
    private NotesDatabase notesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        notesDatabase = NotesDatabase.getInstance(this);

        titleEditText = findViewById(R.id.title_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        dayOfWeekEditText = findViewById(R.id.day_of_week_edit_text);
        buttonAddNote = findViewById(R.id.button_add_note);
        radioGroup = findViewById(R.id.radioGroup);


        String[] DAYS = getResources().getStringArray(R.array.days_of_week);
        dayOfWeekEditText.setText(DAYS[0]);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_note,
                        DAYS);
        dayOfWeekEditText.setAdapter(adapter);


        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSaveNote();
            }
        });
    }

    public void onClickSaveNote(){
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String day = dayOfWeekEditText.getText().toString();
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(radioButtonId);
        int priority = Integer.parseInt(radioButton.getText().toString());

        if (isFilled(title,description)){
            Note note = new Note(title, description, day, priority);
            notesDatabase.notesDao().insertNote(note);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);finish();
        } else {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isFilled(String title, String description){
        return !title.isEmpty() && !description.isEmpty();
    }
}
