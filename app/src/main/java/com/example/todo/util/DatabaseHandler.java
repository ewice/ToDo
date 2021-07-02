package com.example.todo.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todo.model.Todo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 6;
    private static final String DATABASE = "toDoDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DONE = "done";
    private static final String DESCRIPTION = "description";
    private static final String EXPIRY = "expiry";
    private static final String FAVOURITE = "favourite";
    private static final String CONTACTS = "contacts";
    private static final String CREATE_TODO_TABLE =
            "CREATE TABLE " + TODO_TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY, " +
                    NAME + " TEXT NOT NULL, " +
                    DONE + " INTEGER NOT NULL, " +
                    DESCRIPTION + " TEXT, " +
                    EXPIRY + " TEXT, " +
                    FAVOURITE + " INTEGER, " +
                    CONTACTS + " TEXT)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public long createTodo(Todo todo){
        ContentValues cv = new ContentValues();
        if (todo.getId() > 0) {
            cv.put(ID, todo.getId());
        }
        cv.put(NAME, todo.getName());
        cv.put(DESCRIPTION, todo.getDescription());
        cv.put(DONE, todo.isDone() ? 1 : 0);
        cv.put(FAVOURITE, todo.isFavourite() ? 1 : 0);
        return db.insert(TODO_TABLE, null, cv);
    }

    public List<Todo> getAllTodos(){
        List<Todo> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        Todo todo = new Todo();
                        todo.setId(cur.getInt(cur.getColumnIndex(ID)));
                        todo.setName(cur.getString(cur.getColumnIndex(NAME)));
                        todo.setDescription(cur.getString(cur.getColumnIndex(DESCRIPTION)));
                        todo.setDone(cur.getInt(cur.getColumnIndex(DONE)) == 1);
                        todo.setExpiry(cur.getString(cur.getColumnIndex(EXPIRY)));
                        todo.setFavourite(cur.getInt(cur.getColumnIndex(FAVOURITE)) == 1);
                        todo.setContacts(Collections.singletonList(String.valueOf(cur.getColumnIndex(CONTACTS))));
                        taskList.add(todo);
                    }
                    while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return taskList;
    }

    public void updateStatus(long id, int status){
        ContentValues cv = new ContentValues();
        cv.put(DONE, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateFavourite(long id, int favourite){
        ContentValues cv = new ContentValues();
        cv.put(FAVOURITE, favourite);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTodo(long id, Todo todo) {
        ContentValues cv = new ContentValues();
        cv.put(NAME, todo.getName());
        cv.put(DESCRIPTION, todo.getDescription());
        cv.put(DONE, todo.isDone() ? 1 : 0);
        cv.put(EXPIRY, todo.getExpiry() + "" );
        cv.put(FAVOURITE, todo.isFavourite() ? 1 : 0);
        cv.put(CONTACTS, new Gson().toJson(todo.getContacts()));
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteTodo(long id){
        db.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }
}