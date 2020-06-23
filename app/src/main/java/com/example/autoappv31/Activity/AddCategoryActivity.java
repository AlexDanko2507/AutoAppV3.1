package com.example.autoappv31.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.autoappv31.R;
import com.example.autoappv31.Util.NetworkService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCategoryActivity extends AppCompatActivity {

    TextView toolbarText;
    Toolbar toolbar;

    AutoCompleteTextView nameCategory;
    TextView saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        init();
        clickList();

    }

    public void init()
    {
        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarText.setText("Добавить категорию");

        nameCategory = findViewById(R.id.addNameCategory);
        saveBtn = findViewById(R.id.saveNameCategory);
    }

    public void clickList()
    {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCategory(nameCategory.getText().toString());
            }
        });

    }

    public void createCategory(String name)
    {
        NetworkService.getInstance(this)
                .getJSONApi()
                .createCategory(name)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == 200){
                            Intent i = new Intent(AddCategoryActivity.this, CategoryListActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
    }

}
