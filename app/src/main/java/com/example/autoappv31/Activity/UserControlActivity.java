package com.example.autoappv31.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import com.example.autoappv31.Adapters.CategoryListAdapter;
import com.example.autoappv31.Adapters.UsersListAdapter;
import com.example.autoappv31.Models.Category;
import com.example.autoappv31.Models.User;
import com.example.autoappv31.R;
import com.example.autoappv31.Util.NetworkService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserControlActivity extends AppCompatActivity {

    NetworkService networkService;

    TextView toolbarText;
    Toolbar toolbar;

    List<User> users;
    RecyclerView userRecyclerView;
    UsersListAdapter userListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_control);
        init();

    }

    private void init()
    {
        networkService = NetworkService.getInstance(this);
        loadUsers();

        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarText.setText("Список пользователей");

        userRecyclerView = findViewById(R.id.recyclerViewUserControl);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void loadUsers() {
        networkService.getJSONApi()
                .getAllUsers()
                .enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        users = response.body();
                        userListAdapter = new UsersListAdapter(UserControlActivity.this, users, UserControlActivity.this);
                        userRecyclerView.setAdapter(userListAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {

                    }
                });
    }
}
