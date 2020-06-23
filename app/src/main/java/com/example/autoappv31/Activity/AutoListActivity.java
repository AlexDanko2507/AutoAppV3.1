package com.example.autoappv31.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.autoappv31.Adapters.AutoListAdapter;
import com.example.autoappv31.Models.Auto;
import com.example.autoappv31.R;
import com.example.autoappv31.SessionManager;
import com.example.autoappv31.Util.NetworkService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutoListActivity extends AppCompatActivity {

    SessionManager sessionManager;
    NetworkService networkService;

    TextView toolbarText;
    Toolbar toolbar;
    List<Auto> autos;

    RecyclerView autoRecyclerView;
    AutoListAdapter autoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_list);
        init();
    }

    private void init()
    {
        sessionManager = new SessionManager(this);
        networkService = NetworkService.getInstance(this);


        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarText.setText("Список автомобилей");

        autoRecyclerView = findViewById(R.id.recyclerViewAuto);
        autoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        findAutos(sessionManager.getUser().getId());

    }

    public void findAutos(String userId)
    {
        networkService.getJSONApi()
                .findAutos(userId)
                .enqueue(new Callback<List<Auto>>() {
                    @Override
                    public void onResponse(Call<List<Auto>> call, Response<List<Auto>> response) {
                        autos = response.body();
                        autoListAdapter = new AutoListAdapter(AutoListActivity.this, autos, AutoListActivity.this);
                        autoRecyclerView.setAdapter(autoListAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<Auto>> call, Throwable t) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listauto_fragment_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.addAuto:
                Intent auto = new Intent(AutoListActivity.this, AddAutoActivity.class);
                startActivity(auto);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}

