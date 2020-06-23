package com.example.autoappv31.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autoappv31.R;
import com.example.autoappv31.SessionManager;
import com.example.autoappv31.Util.NetworkService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordActivity extends AppCompatActivity {

    TextView cancelSetPassword;
    TextView setPassword;
    EditText username;
    EditText password;
    EditText rePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        init();
        listClick();
    }

    private void init()
    {
        cancelSetPassword = findViewById(R.id.cancelSetPassword);
        setPassword = findViewById(R.id.setPasswordBtnReset);
        username = findViewById(R.id.usernameOreEmailReset);
        password = findViewById(R.id.passwordReset);
        rePassword = findViewById(R.id.re_passwordReset);
    }

    private void listClick()
    {
        cancelSetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PasswordActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        setPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals(rePassword.getText().toString()))
                    resetPass(username.getText().toString(), password.getText().toString());
            }
        });
    }

    private void resetPass(String username, String password)
    {
        NetworkService.getInstance(this)
                .getJSONApi()
                .resetPassword(username, password)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(PasswordActivity.this,
                                    "Pass change", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(PasswordActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (response.code() == 400) {
                            Toast.makeText(PasswordActivity.this,
                                    "No change pass", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
    }

}
