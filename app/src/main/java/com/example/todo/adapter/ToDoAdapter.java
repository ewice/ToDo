package com.example.todo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.DetailActivity;
import com.example.todo.MainActivity;
import com.example.todo.model.Todo;
import com.example.todo.R;
import com.example.todo.util.ApiHandler;
import com.example.todo.util.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private final MainActivity activity;
    private final DatabaseHandler db;
    private final ApiHandler apiHandler;
    private List<Todo> todoList;

    public ToDoAdapter(DatabaseHandler db, MainActivity activity, ApiHandler apiHandler) {
        this.db = db;
        this.activity = activity;
        this.apiHandler = apiHandler;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        db.openDatabase();
        Todo todo = todoList.get(position);

        holder.todoCheckbox.setChecked(todo.isDone());
        holder.todoName.setText(todo.getName());
        holder.todoFavorite.setChecked(todo.isFavourite());

        if (todo.getExpiry() != null) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date date = new Date(Long.parseLong(todo.getExpiry()));
            holder.todoDate.setText(sf.format(date));
        } else {
            holder.todoDate.setVisibility(View.GONE);
        }

        holder.todoCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            todo.setDone(isChecked);
            db.updateStatus(todo.getId(), isChecked ? 1 : 0);
            apiHandler.updateTodo(todo.getId(), todo);
        });
        holder.todoFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            todo.setFavourite(isChecked);
            db.updateFavourite(todo.getId(), isChecked ? 1 : 0);
            apiHandler.updateTodo(todo.getId(), todo);
        });
    }

    public int getItemCount() {
        return todoList.size();
    }

    public void setTasks(List<Todo> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return activity;
    }

    public void deleteTodo(int position) {
        Todo item = todoList.get(position);
        db.deleteTodo(item.getId());
        apiHandler.deleteTodo(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editTodo(int position) {
        Todo item = todoList.get(position);
        Intent detailIntent = new Intent(activity, DetailActivity.class);
        detailIntent.putExtra(DetailActivity.ARG_ITEM, item);
        activity.startActivity(detailIntent);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox todoCheckbox;
        CheckBox todoFavorite;
        TextView todoName;
        TextView todoDate;

        ViewHolder(View view) {
            super(view);
            todoCheckbox = view.findViewById(R.id.todo_checkbox);
            todoFavorite = view.findViewById(R.id.todo_favorite);
            todoName = view.findViewById(R.id.todo_name);
            todoDate = view.findViewById(R.id.todo_date);
        }
    }
}
