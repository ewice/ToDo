package com.example.todo;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private DatabaseHandler db;
    private ToDoAdapter toDoAdapter;
    private List<Todo> todoList;
    private Context context;
    private ApiHandler apiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setTitle("Tasks");
        context = getApplicationContext();

        db = new DatabaseHandler(this);
        db.openDatabase();

        apiHandler = new ApiHandler();

        RecyclerView recyclerView = findViewById(R.id.tasksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toDoAdapter = new ToDoAdapter(db,MainActivity.this, apiHandler);
        recyclerView.setAdapter(toDoAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(toDoAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);

        todoList = db.getAllTodos();
        if (!todoList.isEmpty()) {
            apiHandler.deleteAllTodosAndCreateNewAfter(todoList);
        } else {
            apiHandler.getAllTodos(toDoAdapter, db);
        }
        toDoAdapter.setTasks(todoList);
        FavouriteExpirySort();

        fab.setOnClickListener(v -> AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG));
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        todoList = db.getAllTodos();
        FavouriteExpirySort();
        toDoAdapter.setTasks(todoList);
    }

    // Sort Todos

    private void FavouriteExpirySort() {
        todoList.sort(Comparator
                .comparing(Todo::isDone)
                .thenComparing(Todo::isFavourite)
                .thenComparingLong(Todo::getExpiry)
                .reversed());
    }

    private void ExpiryFavouriteSort() {
        todoList.sort(Comparator
                .comparing(Todo::isDone)
                .thenComparing(Todo::getExpiry)
                .thenComparing(Todo::isFavourite)
                .reversed());
    }

    // Options Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu)  {
        MenuInflater inflater = new MenuInflater(context);
        inflater.inflate(R.menu.menu_context, menu);
        menu.add("Sortieren nach Wichtigkeit und Datum").setOnMenuItemClickListener(item -> {
            FavouriteExpirySort();
            toDoAdapter.notifyDataSetChanged();
            return false;
        });
        menu.add("Sortieren nach Datum und Wichtigkeit").setOnMenuItemClickListener(item -> {
            ExpiryFavouriteSort();
            toDoAdapter.notifyDataSetChanged();
            return false;
        });

        menu.add("Lokale Todos löschen").setOnMenuItemClickListener(item -> {
            for (Todo todo : todoList) {
                db.deleteTodo(todo.getId());
            }
            List<Todo> todos = new ArrayList<Todo>();
            toDoAdapter.setTasks(todos);
            toDoAdapter.notifyDataSetChanged();
            return false;
        });

        menu.add("Remote Todos löschen").setOnMenuItemClickListener(item -> {
            apiHandler.deleteAllTodos();
            return false;
        });

        menu.add("Sync").setOnMenuItemClickListener(item -> {
            List<Todo> todos = db.getAllTodos();
            if (!todos.isEmpty()) {
                apiHandler.deleteAllTodosAndCreateNewAfter(todos);
            } else {
                apiHandler.getAllTodos(toDoAdapter, db);
            }
            return false;
        });
        return true;
    }
}