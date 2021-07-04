package com.example.todo.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.todo.data.LoginRepository;
import com.example.todo.data.Result;
import com.example.todo.model.ApiInterface;
import com.example.todo.model.User;
import com.example.todo.R;
import com.example.todo.util.ApiHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final ApiInterface apiInterface = new ApiHandler().getClient();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        MutableLiveData<Result<User>> result = new MutableLiveData<>();
        User user = new User();
        user.setEmail(username);
        user.setPwd(password);

        Call<Boolean> create = apiInterface.login(user);
        try {
            create.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                    if (response.body() != null) {
                        User data = ((Result.Success<User>) new Result.Success(user)).getData();
                        loginResult.setValue(new LoginResult(new LoggedInUserView(data.getEmail() + "asdf")));
                    } else {
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                    loginResult.setValue(new LoginResult(R.string.login_failed));
                    call.cancel();
                }
            });
        } catch (Exception e) {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        }
        return false;
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() == 6;
    }
}