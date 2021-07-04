package com.example.todo;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.adapter.ToDoAdapter;
import com.example.todo.helper.AddNewTask;
import com.example.todo.helper.DialogCloseListener;
import com.example.todo.helper.RecyclerItemTouchHelper;
import com.example.todo.model.Todo;
import com.example.todo.util.ApiHandler;
import com.example.todo.util.Connection;
import com.example.todo.util.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private DatabaseHandler db;
    private ToDoAdapter toDoAdapter;
    private List<Todo> todoList;
    private Context context;
    private ApiHandler apiHandler;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Tasks");
        context = getApplicationContext();

        AsyncTask<String, Boolean, Boolean> isConnected = new Connection().execute();
        try {
            if (!isConnected.get()){
                TextView errorMessage = findViewById(R.id.errorMessage);
                errorMessage.setVisibility(View.VISIBLE);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        db = new DatabaseHandler(this);
        db.openDatabase();

        apiHandler = new ApiHandler();

        recyclerView = findViewById(R.id.tasksRecyclerView);
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
        favouriteExpirySort();
        toDoAdapter.setTasks(todoList);

        fab.setOnClickListener(v -> AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG));
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        todoList = db.getAllTodos();
        favouriteExpirySort();
        resetRecyclerViewAdapterAndList();
    }

    // Reset Adapter

    private void resetRecyclerViewAdapterAndList() {
        toDoAdapter = new ToDoAdapter(db,MainActivity.this, apiHandler);
        recyclerView.setAdapter(toDoAdapter);
        toDoAdapter.setTasks(todoList);
        recyclerView.invalidate();
    }

    // Sort Todos

    private void favouriteExpirySort() {
        todoList.sort(Comparator
                .comparing(Todo::isDone)
                .thenComparing(Todo::isFavourite)
                .thenComparingLong(Todo::getExpiry)
                .reversed());
        resetRecyclerViewAdapterAndList();
    }

    private void expiryFavouriteSort() {
        todoList.sort(Comparator
                .comparing(Todo::isDone)
                .thenComparing(Todo::getExpiry)
                .thenComparing(Todo::isFavourite)
                .reversed());
        resetRecyclerViewAdapterAndList();
    }

    // Options Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu)  {
        MenuInflater inflater = new MenuInflater(context);
        inflater.inflate(R.menu.menu_context, menu);
        menu.add("Sortieren nach Wichtigkeit und Datum").setOnMenuItemClickListener(item -> {
            favouriteExpirySort();
            return false;
        });
        menu.add("Sortieren nach Datum und Wichtigkeit").setOnMenuItemClickListener(item -> {
            expiryFavouriteSort();
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