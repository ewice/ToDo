package com.example.todo;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.adapter.ToDoAdapter;
import com.example.todo.helper.AddNewTask;
import com.example.todo.helper.DialogCloseListener;
import com.example.todo.helper.RecyclerItemTouchHelper;
import com.example.todo.model.Todo;
import com.example.todo.util.ApiHandler;
import com.example.todo.util.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private DatabaseHandler db;
    private ToDoAdapter toDoAdapter;
    private List<Todo> todoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setTitle("Tasks");

        db = new DatabaseHandler(this);
        db.openDatabase();

        ApiHandler apiHandler = new ApiHandler();

        RecyclerView recyclerView = findViewById(R.id.tasksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toDoAdapter = new ToDoAdapter(db,MainActivity.this, apiHandler);
        recyclerView.setAdapter(toDoAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(toDoAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);

        todoList = db.getAllTodos();
        if (!todoList.isEmpty()) {
            apiHandler.deleteAllTodos(todoList);
        } else {
            apiHandler.getAllTodos(toDoAdapter, db);
        }
        toDoAdapter.setTasks(todoList);
        toDoAdapter.FavouriteExpirySort();

        fab.setOnClickListener(v -> AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG));
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        todoList = db.getAllTodos();
        toDoAdapter.FavouriteExpirySort();
        toDoAdapter.setTasks(todoList);
    }
}