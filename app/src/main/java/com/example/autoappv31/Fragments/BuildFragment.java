package com.example.autoappv31.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoappv31.ActiveAutoManager;
import com.example.autoappv31.Activity.AddBuildActivity;
import com.example.autoappv31.Adapters.BuildListAdapter;
import com.example.autoappv31.Adapters.MoneyListAdapter;
import com.example.autoappv31.Models.TechnicalWork;
import com.example.autoappv31.R;
import com.example.autoappv31.Util.NetworkService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuildFragment extends Fragment {

    NetworkService networkService;
    ActiveAutoManager activeAutoManager;

    RecyclerView buildRecyclerView;
    BuildListAdapter buildListAdapter;

    List<TechnicalWork> builds;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_build, container, false);
        setHasOptionsMenu(true);

        init(view);

        return view;
    }

    private void init(View view)
    {
        activeAutoManager = new ActiveAutoManager(getActivity());
        networkService = NetworkService.getInstance(getActivity());

        loadTechnicalWork(activeAutoManager.getActiveAutoId());

        buildRecyclerView = view.findViewById(R.id.recyclerViewBuild);
        buildRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void loadTechnicalWork(String autoId)
    {
        networkService.getJSONApi()
                .getAllTechnicalWorkAuto(autoId)
                .enqueue(new Callback<List<TechnicalWork>>() {
                    @Override
                    public void onResponse(Call<List<TechnicalWork>> call, Response<List<TechnicalWork>> response) {
                        builds = response.body();
                        if(isAdded())
                        buildListAdapter = new BuildListAdapter(getActivity(), builds, BuildFragment.this);
                        buildRecyclerView.setAdapter(buildListAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<TechnicalWork>> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.build_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.addBuild:
                Intent i = new Intent(getActivity(), AddBuildActivity.class);
                startActivity(i);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}