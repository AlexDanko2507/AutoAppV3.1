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

public class AllMoneyActivity extends AppCompatActivity {

    ActiveAutoManager activeAutoManager;
    SessionManager sessionManager;
    NetworkService networkService;

    TextView toolbarText;
    Toolbar toolbar;

    Auto currentAuto;

    Double sumCategory;
    Double totalSumInt;

    List<Expense> money;
    List<Category> categories;
    List<Auto> userAutos;
    ArrayList<String> autosName;
    CharSequence[] userAutosString;

    RecyclerView allMoneyRecyclerView;
    AllMoneyListAdapter allMoneyListAdapter;
    DialogInterface.OnClickListener setAutoClickListener;

    AlertDialog setAutoDialog;

    AnyChartView anyChartView;
    List<DataEntry> data;
    Pie pie;

    TextView totalSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_money);

        init();
        listClick();

    }

    private void init()
    {
        networkService = NetworkService.getInstance(this);
        activeAutoManager = new ActiveAutoManager(this);
        sessionManager = new SessionManager(this);
        loadAllAuto(sessionManager.getUser().getId());

        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadAuto(activeAutoManager.getActiveAutoId());

        totalSum = findViewById(R.id.totalSumAllMoney);
        allMoneyRecyclerView = findViewById(R.id.recyclerViewAllMoney);
        allMoneyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        anyChartView = findViewById(R.id.any_chart_viewAll);
        anyChartView.setProgressBar(findViewById(R.id.progress_barMoneyList));
        pie = AnyChart.pie();
        data = new ArrayList<>();
        data.add(new ValueDataEntry("", 0));
        pie.labels().position("outside");
        pie.data(data);
        anyChartView.setChart(pie);
    }

    private void loadCategory()
    {
        networkService.getJSONApi()
                .getAllCategory()
                .enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                        categories = response.body();
                        loadPieChart();
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {

                    }
                });
    }

    private void loadPieChart()
    {
        anyChartView.clear();
        data.clear();
        totalSumInt = .0;
        for(Integer i=0;i<categories.size();i++)
        {
            sumCategory = .0;
            for(Integer j=0;j<money.size();j++)
            {
                if (money.get(j).getCategory().equals(categories.get(i)))
                {
                    sumCategory += money.get(j).getSum();
                }
            }
            totalSumInt+=sumCategory;
            if(sumCategory!=0)
            {
                data.add(new ValueDataEntry(categories.get(i).getName(), sumCategory.intValue()));
            }
        }
        pie.data(data);
        anyChartView.setChart(pie);
        if(totalSumInt != 0) {
            totalSum.setText("Всего потрачено: " + totalSumInt);
        }else {
            totalSum.setText("Нет данных для отображения");
        }
    }

    public void listClick()
    {
        toolbarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView userAutoName = new TextView(v.getContext());
                userAutoName.setText("Выбрать автомобиль");
                userAutoName.setPadding(20, 30, 20, 30);
                userAutoName.setTextSize(20F);
                userAutoName.setBackgroundColor(Color.rgb(00,85,77));
                userAutoName.setTextColor(Color.WHITE);
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(v.getContext());
                myBuilder.setCustomTitle(userAutoName);
                myBuilder.setNegativeButton("Отмена", setAutoClickListener);
                myBuilder.setItems(userAutosString, setAutoClickListener);
                setAutoDialog = myBuilder.create();
                setAutoDialog.show();
            }
        });

        setAutoClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_NEGATIVE)
                {
                    dialog.dismiss();
                }
                else
                {
                    loadAuto(userAutos.get(which).getId());
                }
            }
        };
    }

    private void loadAllAuto(final String userId)
    {
        networkService.getJSONApi()
                .findAutos(userId)
                .enqueue(new Callback<List<Auto>>() {
                    @Override
                    public void onResponse(Call<List<Auto>> call, Response<List<Auto>> response) {
                        userAutos = response.body();
                        autosName = new ArrayList<>();
                        for (Integer i =0;i<userAutos.size();i++)
                        {
                            autosName.add(userAutos.get(i).getMark().getName() + " " + userAutos.get(i).getModel().getName());
                        }
                        userAutosString = autosName.toArray(new CharSequence[autosName.size()]);
                    }

                    @Override
                    public void onFailure(Call<List<Auto>> call, Throwable t) {

                    }
                });
    }

    private void loadAuto(String autoId)
    {
        networkService.getJSONApi()
                .getAutobyId(autoId)
                .enqueue(new Callback<Auto>() {
                    @Override
                    public void onResponse(Call<Auto> call, Response<Auto> response) {
                        currentAuto = response.body();
                        toolbarText.setText(currentAuto.getMark().getName() + " " + currentAuto.getModel().getName());
                        loadAllExpenses();
                    }

                    @Override
                    public void onFailure(Call<Auto> call, Throwable t) {

                    }
                });
    }

    public void loadAllExpenses()
    {
        networkService.getJSONApi()
                .getAllExpenseAuto(currentAuto.getId())
                .enqueue(new Callback<List<Expense>>() {
                    @Override
                    public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                        money = response.body();
                        allMoneyListAdapter = new AllMoneyListAdapter(AllMoneyActivity.this, money,AllMoneyActivity.this);
                        allMoneyRecyclerView.setAdapter(allMoneyListAdapter);
                        loadCategory();
                    }

                    @Override
                    public void onFailure(Call<List<Expense>> call, Throwable t) {

                    }
                });
    }
}
