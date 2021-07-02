package com.example.todo.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    // TODOS

    @GET("/api/todos/{id}")
    Call<Todo> getTodo(@Path("id") long id);

    @GET("/api/todos")
    Call<List<Todo>> getAllTodos();

    @POST("/api/todos")
    Call<Todo> createTodo(@Body Todo todo);

    @PUT("/api/todos/{id}")
    Call<Todo> updateTodo(@Path("id") long id, @Body Todo todo);

    @DELETE("/api/todos/{id}")
    Call<Boolean> deleteTodo(@Path("id") long id);

    @DELETE("/api/todos")
    Call<Boolean> deleteAllTodos();


    // USERS

    @PUT("/api/users/auth")
    Call<Boolean> login(@Body User user);
}



