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
import com.example.autoappv31.Models.Category;
import com.example.autoappv31.R;
import com.example.autoappv31.Util.NetworkService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryListActivity extends AppCompatActivity {

    NetworkService networkService;

    TextView toolbarText;
    Toolbar toolbar;

    List<Category> category;
    RecyclerView categoryRecyclerView;
    CategoryListAdapter categoryListAdapter;
    DialogInterface.OnClickListener myClickListener;
    String delId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        init();
        listclick();
    }

    private void init()
    {
        networkService = NetworkService.getInstance(this);
        loadCategory();

        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarText.setText("Список категорий");

        categoryRecyclerView = findViewById(R.id.recyclerViewCategory);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void listclick()
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
                        deleteCategory(delId);
                        break;
                }
            }
        };
    }

    public void alertDel(String id)
    {
        delId=id;
        final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Удалить выбранную категорию?");
        adb.setMessage("Будут также удалены записи по расходам в данной категории");
        adb.setPositiveButton("Удалить",myClickListener );
        adb.setNegativeButton("Отменть",myClickListener );
        adb.show();
    }

    private void deleteCategory(String categoryId)
    {
        networkService.getJSONApi()
                .deleteCategory(categoryId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        loadCategory();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
    }

    private void loadCategory()
    {
        networkService.getJSONApi()
                .getAllCategory()
                .enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                        category = response.body();
                        categoryListAdapter = new CategoryListAdapter(CategoryListActivity.this, category, CategoryListActivity.this);
                        categoryRecyclerView.setAdapter(categoryListAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_fragment_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.addCategory:
                Intent i = new Intent(CategoryListActivity.this, AddCategoryActivity.class);
                startActivity(i);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
