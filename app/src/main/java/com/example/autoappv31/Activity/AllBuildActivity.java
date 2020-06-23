package com.example.autoappv31.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.autoappv31.ActiveAutoManager;
import com.example.autoappv31.Adapters.AllBuildListAdapter;
import com.example.autoappv31.Adapters.BuildListAdapter;
import com.example.autoappv31.Fragments.BuildFragment;
import com.example.autoappv31.Models.TechnicalWork;
import com.example.autoappv31.R;
import com.example.autoappv31.SessionManager;
import com.example.autoappv31.Util.NetworkService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllBuildActivity extends AppCompatActivity {

    ActiveAutoManager activeAutoManager;
    NetworkService networkService;

    TextView toolbarText;
    Toolbar toolbar;

    RecyclerView buildRecyclerView;
    AllBuildListAdapter buildListAdapter;

    List<TechnicalWork> builds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_build);
        init();

    }

    private void init()
    {
        networkService = NetworkService.getInstance(this);
        activeAutoManager = new ActiveAutoManager(this);

        loadTechnicalWorkAll(activeAutoManager.getActiveAutoId());

        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarText.setText("Технические работы");

        buildRecyclerView = findViewById(R.id.recyclerViewBuildAll);
        buildRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void loadTechnicalWorkAll(String autoId)
    {
        networkService.getJSONApi()
                .getAllTechnicalWorkAutoAll(autoId)
                .enqueue(new Callback<List<TechnicalWork>>() {
                    @Override
                    public void onResponse(Call<List<TechnicalWork>> call, Response<List<TechnicalWork>> response) {
                        builds = response.body();
                        buildListAdapter = new AllBuildListAdapter(AllBuildActivity.this, builds, AllBuildActivity.this);
                        buildRecyclerView.setAdapter(buildListAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<TechnicalWork>> call, Throwable t) {

                    }
                });
    }
}
