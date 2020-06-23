package com.example.autoappv31.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.autoappv31.Adapters.CategoryListAdapter;
import com.example.autoappv31.Adapters.WorkListAdapter;
import com.example.autoappv31.Models.Category;
import com.example.autoappv31.Models.CategoryWork;
import com.example.autoappv31.R;
import com.example.autoappv31.Util.NetworkService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryWorkListActivity extends AppCompatActivity {

    NetworkService networkService;

    TextView toolbarText;
    Toolbar toolbar;

    List<CategoryWork> categoryWorks;
    RecyclerView categoryWorksRecyclerView;
    WorkListAdapter categoryWorksListAdapter;
    DialogInterface.OnClickListener myClickListener;
    String delId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_work_list);

        init();
        listClick();

    }

    private void listClick()
    {
        myClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which)
                {
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteCategoryWork(delId);
                        break;
                }
            }
        };
    }

    private void init()
    {
        networkService = NetworkService.getInstance(this);
        loadCategoryWork();

        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarText.setText("Список технических работ");

        categoryWorksRecyclerView = findViewById(R.id.recyclerViewCategoryWork);
        categoryWorksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void alertDel(String id)
    {
        delId=id;
        final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Удалить выбранное наименование?");
        adb.setMessage("Данные будут также удалены из запланируемых работ");
        adb.setPositiveButton("Удалить",myClickListener );
        adb.setNegativeButton("Отменть",myClickListener );
        adb.show();
    }

    private void deleteCategoryWork(String categoryWorkId)
    {
        networkService.getJSONApi()
                .deleteCategoryWork(categoryWorkId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        loadCategoryWork();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
    }

    private void loadCategoryWork()
    {
        networkService.getJSONApi()
                .getAllCategoryWork()
                .enqueue(new Callback<List<CategoryWork>>() {
                    @Override
                    public void onResponse(Call<List<CategoryWork>> call, Response<List<CategoryWork>> response) {
                        categoryWorks = response.body();
                        categoryWorksListAdapter = new WorkListAdapter(CategoryWorkListActivity.this, categoryWorks, CategoryWorkListActivity.this);
                        categoryWorksRecyclerView.setAdapter(categoryWorksListAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<CategoryWork>> call, Throwable t) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.categorywork_fragment_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.addCategoryWork:
                Intent i = new Intent(CategoryWorkListActivity.this, AddCategoryWorkActivity.class);
                startActivity(i);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
