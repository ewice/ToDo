package com.example.todo.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.todo.adapter.ToDoAdapter;
import com.example.todo.model.ApiInterface;
import com.example.todo.model.Todo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHandler {

    private final ApiInterface apiInterface;


    public ApiHandler() {
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = adapter.create(ApiInterface.class);
    }

    public void createTodo(Todo todo) {
        Call<Todo> create = apiInterface.createTodo(todo);
        create.enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(@NonNull Call<Todo> call, @NonNull Response<Todo> response) {
                System.out.println(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Todo> call, @NonNull Throwable t) {
                System.out.println("Fail " + t);
                call.cancel();
            }
        });
    }

    public void getAllTodos(ToDoAdapter toDoAdapter, DatabaseHandler db) {
        Call<List<Todo>> call = apiInterface.getAllTodos();
        call.enqueue(new Callback<List<Todo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Todo>> call, @NonNull Response<List<Todo>> response) {
                Log.d("TAG", response.code() + "");
                System.out.println(response.body());
                List<Todo> todoList = response.body();
                toDoAdapter.setTasks(todoList);
                assert todoList != null;
                for (Todo todo : todoList) {
                    db.createTodo(todo);
                }
                toDoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<List<Todo>> call, @NonNull Throwable t) {
                Log.d("TAG", t.getMessage() + "##############");
                call.cancel();
            }

        });
    }

    public void updateTodo(long id, Todo todo) {
        Call<Todo> update = apiInterface.updateTodo(id, todo);
        update.enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(@NonNull Call<Todo> call, @NonNull Response<Todo> response) {
                System.out.println(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Todo> call, @NonNull Throwable t) {
                System.out.println("Fail " + t);
                call.cancel();
            }
        });
    }

    public void deleteTodo(long id) {
        Call<Boolean> update = apiInterface.deleteTodo(id);
        update.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                System.out.println(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                System.out.println("Fail " + t);
                call.cancel();
            }
        });
    }

    public void deleteAllTodos(List<Todo> todoList) {
        Call<Boolean> deleteAllTodos = apiInterface.deleteAllTodos();
        deleteAllTodos.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                System.out.println("Delete all remote todos");
                for (Todo todo : todoList) {
                    createTodo(todo);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                System.out.println(t);
            }
        });
    }

}
