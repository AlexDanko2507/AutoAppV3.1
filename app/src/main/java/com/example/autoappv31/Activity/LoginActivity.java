package com.example.autoappv31.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.autoappv31.ActiveAutoManager;
import com.example.autoappv31.FragmentManager;
import com.example.autoappv31.Models.Auto;
import com.example.autoappv31.Models.LoginResult;
import com.example.autoappv31.Models.User;
import com.example.autoappv31.R;
import com.example.autoappv31.SessionManager;
import com.example.autoappv31.Util.NetworkService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    String TAG = "LoginActivity";

    TextView toRegisterPage;
    TextView loginBtn;
    TextView forgotPassword;
    AutoCompleteTextView email;
    EditText password;

    SessionManager sessionManager;
    FragmentManager fragmentManager;
    ActiveAutoManager activeAutoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        listClick();
    }

    private void init()
    {
        activeAutoManager = new ActiveAutoManager(this);
        sessionManager = new SessionManager(this);
        fragmentManager = new FragmentManager(this);
        fragmentManager.setFragment("auto");

        password = findViewById(R.id.passwordLogin);
        email = findViewById(R.id.emailLogin);

        toRegisterPage = findViewById(R.id.toRegisterPage);
        loginBtn = findViewById(R.id.loginBtn);
        forgotPassword = findViewById(R.id.forgotPassword);
    }

    private void listClick()
    {
        toRegisterPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, PasswordActivity.class);
                startActivity(i);
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(email.getText().toString(),password.getText().toString());
            }
        });
    }

    private void signIn(String email, String password) {
        NetworkService.getInstance(LoginActivity.this)
                .getJSONApi()
                .signin(email, password)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        User user = response.body();
                        if (response.code() == 200) {
                            sessionManager.createSession(user);
                            NetworkService.getInstance(LoginActivity.this)
                                    .getJSONApi()
                                    .findAutos(user.getId())
                                    .enqueue(new Callback<List<Auto>>() {
                                        @Override
                                        public void onResponse(Call<List<Auto>> call, Response<List<Auto>> response) {
                                            List<Auto> autos = response.body();
                                            if(autos.size()!=0)
                                            {
                                                activeAutoManager.setActiveAutoId(autos.get(0).getId());
                                            }
                                            sessionManager.setCountAuto(autos.size()+"");
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onFailure(Call<List<Auto>> call, Throwable t) {
                                            Log.w(TAG, "NO SUCCESS!!!!!!!!!!!!!!!!!");
                                            Log.e(TAG, t.getMessage());
                                            t.printStackTrace();
                                        }
                                    });
                        } else if (response.code() == 400) {
                            Toast.makeText(LoginActivity.this,
                                    "No exist", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Log.w(TAG, "NO SUCCESS!!!!!!!!!!!!!!!!!");
                        Log.e(TAG, t.getMessage());
                        t.printStackTrace();
                    }
                });
    }
}
