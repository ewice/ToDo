package com.example.todo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.adapter.ToDoAdapter;
import com.example.todo.helper.AddNewTask;
import com.example.todo.helper.DialogCloseListener;
import com.example.todo.helper.RecyclerItemTouchHelper;
import com.example.todo.model.ApiInterface;
import com.example.todo.model.Todo;
import com.example.todo.util.ApiHandler;
import com.example.todo.util.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private DatabaseHandler db;
    private ToDoAdapter toDoAdapter;
    private List<Todo> todoList;
    private List<Todo> remoteTaskList;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setTitle("Tasks");

        db = new DatabaseHandler(this);
        db.openDatabase();

        ApiHandler apiHandler = new ApiHandler();

        RecyclerView tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        toDoAdapter = new ToDoAdapter(db,MainActivity.this, apiHandler);
        tasksRecyclerView.setAdapter(toDoAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(toDoAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);

        todoList = db.getAllTodos();
        if (!todoList.isEmpty()) {
            apiHandler.deleteAllTodos(todoList);
        } else {
            apiHandler.getAllTodos(toDoAdapter, db);
        }
        toDoAdapter.setTasks(todoList);

        fab.setOnClickListener(v -> AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG));
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        todoList = db.getAllTodos();
        Collections.reverse(todoList);
        toDoAdapter.setTasks(todoList);
        toDoAdapter.notifyDataSetChanged();
    }


    // Sort Todos

    private void defaultSort() {
        todoList.sort(Comparator
                .comparing(Todo::isDone)
                .thenComparing(Todo::isFavourite).reversed());
        toDoAdapter.notifyDataSetChanged();
    }

    private void reverseSort() {
        sortDate();
        sortFavourite();
        sortDone();
        toDoAdapter.notifyDataSetChanged();
    }

    private void sortDate() {
        todoList.sort((o1, o2) -> {
            int x = Integer.parseInt(o1.getExpiry());
            int y = Integer.parseInt(o2.getExpiry());
            return x - y;
        });
        toDoAdapter.setTasks(todoList);
    }

    private void sortFavourite() {
        todoList.sort((o1, o2) -> {
            boolean b1 = o1.isFavourite();
            boolean b2 = o2.isFavourite();
            return (b1 == b2) ? 0 : b1 ? -1 : 1;
        });
        toDoAdapter.setTasks(todoList);
    }

    private void sortDone() {
        todoList.sort((o1, o2) -> {
            boolean b2 = o1.isDone();
            boolean b1 = o2.isDone();
            return (b1 == b2) ? 0 : b1 ? -1 : 1;
        });
        toDoAdapter.setTasks(todoList);
    }
}