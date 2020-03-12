package com.hfad.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHolder;
    private final ArrayList<Note> noteArrayList = new ArrayList<>();
    private NoteAdapter noteAdapter;
    private NotesDatabase notesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notesDatabase = NotesDatabase.getInstance(this);
        recyclerViewHolder = findViewById(R.id.recyclerViewHolder);
        getData();
        noteAdapter = new NoteAdapter(noteArrayList);
        recyclerViewHolder.setLayoutManager(new LinearLayoutManager(getApplication()));
        recyclerViewHolder.setAdapter(noteAdapter);
        noteAdapter.setOnNoteClickListener(new NoteAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(int position) {

            }

            @Override
            public void onNoteLongClick(int position) {
                removeNote(position);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                removeNote(viewHolder.getAdapterPosition());
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewHolder);
    }

    private void removeNote(int position){
        Note note = noteArrayList.get(position);
        notesDatabase.notesDao().deleteNote(note);
    }

    public void onClickAddNote(View view) {
        Intent intent = new Intent(getApplicationContext(), AddNoteActivity.class);
        startActivity(intent); finish();
    }

    private void getData(){
        LiveData<List<Note>> notesFromDB = notesDatabase.notesDao().getAllNotes();
        notesFromDB.observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                noteArrayList.clear();
                noteArrayList.addAll(notes);
                noteAdapter.notifyDataSetChanged();
            }
        });

    }
}
