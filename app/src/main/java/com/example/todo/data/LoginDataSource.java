package com.example.todo.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.todo.model.ApiInterface;
import com.example.todo.model.User;
import com.example.todo.util.ApiHandler;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private ApiInterface apiInterface = new ApiHandler().getClient();
    public Result res;
    public MutableLiveData<Result> responseMutableLiveData = new MutableLiveData<>();
    public void login(String username, String password) {
        User user = new User();
        user.setEmail(username);
        user.setPwd(password);

        Call<Boolean> create = apiInterface.login(user);
        try {
            create.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                    responseMutableLiveData.setValue(new Result.Success(user));

                }

                @Override
                public void onFailure(@NonNull Call<Boolean> call, Throwable t) {

                    responseMutableLiveData.setValue( new Result.Error(new IOException("Error logging in", t)));
                    res =  new Result.Error(new IOException("Error logging in", t));
                    call.cancel();
                }
            });
        } catch (Exception e) {
            res =  new Result.Error(new IOException("Error logging in", e));
        }

    };

    public void logout() {
        // TODO: revoke authentication
    }
}