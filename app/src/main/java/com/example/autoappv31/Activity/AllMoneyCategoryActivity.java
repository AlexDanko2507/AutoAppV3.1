package com.example.autoappv31.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.autoappv31.ActiveAutoManager;
import com.example.autoappv31.Adapters.AllMoneyCategoryListAdapter;
import com.example.autoappv31.Adapters.AllMoneyListAdapter;
import com.example.autoappv31.Models.Auto;
import com.example.autoappv31.Models.Category;
import com.example.autoappv31.Models.Expense;
import com.example.autoappv31.R;
import com.example.autoappv31.SessionManager;
import com.example.autoappv31.Util.NetworkService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllMoneyCategoryActivity extends AppCompatActivity {

    ActiveAutoManager activeAutoManager;
    SessionManager sessionManager;
    NetworkService networkService;

    TextView toolbarText;
    Toolbar toolbar;

    Double sumCategory;
    Double totalSumInt;
    Double totalSumText;


    List<Expense> money;
    List<Category> categories;
    List<Auto> autos;
    Category currentCategory;
    ArrayList<String> categoriesName;
    CharSequence[] categoriesString;

    ArrayList<Double> autoSum;

    AlertDialog setCategoryDialog;

    RecyclerView allMoneyRecyclerView;
    AllMoneyCategoryListAdapter allMoneyListAdapter;
    DialogInterface.OnClickListener setCategoryClickListener;

    AnyChartView anyChartView;
    List<DataEntry> data;
    Pie pie;

    TextView totalSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_money_category);

        init();
        listClick();

    }

    private void init()
    {
        networkService = NetworkService.getInstance(this);
        activeAutoManager = new ActiveAutoManager(this);
        sessionManager = new SessionManager(this);

        loadAllCategory();

        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        totalSum = findViewById(R.id.totalSumAllMoneyCategory);
        allMoneyRecyclerView = findViewById(R.id.recyclerViewAllMoneyCategory);
        allMoneyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        anyChartView = findViewById(R.id.any_chart_viewAllMoneyCategory);
        anyChartView.setProgressBar(findViewById(R.id.progress_barMoneyListMoneyCategory));
        pie = AnyChart.pie();
        data = new ArrayList<>();
        data.add(new ValueDataEntry("", 0));
        pie.labels().position("outside");
        pie.data(data);
        anyChartView.setChart(pie);
    }

    private void listClick()
    {
        toolbarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView categoriesName = new TextView(v.getContext());
                categoriesName.setText("Выбрать категорию");
                categoriesName.setPadding(20, 30, 20, 30);
                categoriesName.setTextSize(20F);
                categoriesName.setBackgroundColor(Color.rgb(00,85,77));
                categoriesName.setTextColor(Color.WHITE);
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(v.getContext());
                myBuilder.setCustomTitle(categoriesName);
                myBuilder.setNegativeButton("Отмена", setCategoryClickListener);
                myBuilder.setItems(categoriesString, setCategoryClickListener);
                setCategoryDialog = myBuilder.create();
                setCategoryDialog.show();
            }
        });
        setCategoryClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_NEGATIVE)
                {
                    dialog.dismiss();
                }
                else
                {
                    loadCurrentCategory(categories.get(which).getId());
                }
            }
        };
    }


    public void loadExpense()
    {
        networkService.getJSONApi()
                .getAllExpenseCategory(currentCategory.getId(), sessionManager.getUser().getId())
                .enqueue(new Callback<List<Expense>>() {
                    @Override
                    public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                        money = response.body();
                        allMoneyListAdapter = new AllMoneyCategoryListAdapter(AllMoneyCategoryActivity.this, money,AllMoneyCategoryActivity.this);
                        allMoneyRecyclerView.setAdapter(allMoneyListAdapter);
                        loadAllAuto();
                    }

                    @Override
                    public void onFailure(Call<List<Expense>> call, Throwable t) {

                    }
                });
    }

    private void loadPieChart()
    {

        anyChartView.clear();
        data.clear();
        totalSumText = .0;
        for(Integer i=0;i<autos.size();i++)
        {
            if(autoSum.get(i)!=0)
            {
                data.add(new ValueDataEntry(autos.get(i).getMark().getName()+" "+autos.get(i).getModel().getName(), autoSum.get(i).intValue()));
            }
            totalSumText+=autoSum.get(i);
        }
        pie.data(data);
        anyChartView.setChart(pie);
        if(totalSumText != 0) {
            totalSum.setText("Всего потрачено: " + totalSumText);
        }else {
            totalSum.setText("Нет данных для отображения");
        }
    }

    private void loadAllAuto()
    {
        networkService.getJSONApi()
                .findAutos(sessionManager.getUser().getId())
                .enqueue(new Callback<List<Auto>>() {
                    @Override
                    public void onResponse(Call<List<Auto>> call, Response<List<Auto>> response) {
                        autos = response.body();
                        autoSum = new ArrayList<>();
                        for(Integer i=0;i<autos.size();i++)
                        {
                            totalSumInt = .0;
                            for(Integer j=0;j<money.size();j++)
                            {
                                if (money.get(j).getAuto().getId().equals(autos.get(i).getId()))
                                {
                                    totalSumInt+=money.get(j).getSum();
                                }
                            }
                            autoSum.add(totalSumInt);
                        }
                        loadPieChart();
                    }

                    @Override
                    public void onFailure(Call<List<Auto>> call, Throwable t) {

                    }
                });
    }

    private void loadAllCategory()
    {
        networkService.getJSONApi()
                .getAllCategory()
                .enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                        categories = response.body();
                        categoriesName = new ArrayList<>();
                        for (Integer i =0;i<categories.size();i++)
                        {
                            categoriesName.add(categories.get(i).getName());
                        }
                        categoriesString = categoriesName.toArray(new CharSequence[categoriesName.size()]);
                        loadCurrentCategory(categories.get(0).getId());
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {

                    }
                });
    }

    private void loadCurrentCategory(String categoryId)
    {
        networkService.getJSONApi()
                .getCategorybyId(categoryId)
                .enqueue(new Callback<Category>() {
                    @Override
                    public void onResponse(Call<Category> call, Response<Category> response) {
                        currentCategory = response.body();
                        toolbarText.setText(currentCategory.getName());
                        loadExpense();
                    }

                    @Override
                    public void onFailure(Call<Category> call, Throwable t) {

                    }
                });
    }
}
