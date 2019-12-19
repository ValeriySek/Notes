package com.hfad.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    private NotesDBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewHolder = findViewById(R.id.recyclerViewHolder);

        dbHelper = new NotesDBHelper(this);
        sqLiteDatabase = dbHelper.getWritableDatabase();

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
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
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
        int id = noteArrayList.get(position).getId();
        String where = NotesContract.NotesEntry._ID + " = ?";
        String[] strings = new String[]{Integer.toString(id)};
        sqLiteDatabase.delete(NotesContract.NotesEntry.TABLE_NAME, where, strings);
        getData();
        noteAdapter.notifyDataSetChanged();
    }

    public void onClickAddNote(View view) {
        Intent intent = new Intent(getApplicationContext(), AddNoteActivity.class);
        startActivity(intent); finish();
    }

    private void getData(){
        noteArrayList.clear();
        Cursor cursor = sqLiteDatabase.query(NotesContract.NotesEntry.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            int id  = cursor.getInt(cursor.getColumnIndex(NotesContract.NotesEntry._ID));
            String title = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_DESCRIPTION));
            int dayOfWeek = cursor.getInt(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_DAY_OF_WEEK));
            int priority = cursor.getInt(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_PRIORITY));
            Note note = new Note(id, title, description, dayOfWeek, priority);
            noteArrayList.add(note);
        }
        cursor.close();
    }
}
