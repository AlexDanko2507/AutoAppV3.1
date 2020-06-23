package com.example.autoappv31.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.autoappv31.ActiveAutoManager;
import com.example.autoappv31.Activity.AddMoneyActivity;
import com.example.autoappv31.Activity.CategoryListActivity;
import com.example.autoappv31.Adapters.CategoryListAdapter;
import com.example.autoappv31.Adapters.MoneyListAdapter;
import com.example.autoappv31.Models.Category;
import com.example.autoappv31.Models.Expense;
import com.example.autoappv31.R;
import com.example.autoappv31.Util.NetworkService;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoneyFragment extends Fragment {

    NetworkService networkService;
    ActiveAutoManager activeAutoManager;

    CharSequence[] monthNames = {"Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"};

    RecyclerView moneyRecyclerView;
    MoneyListAdapter moneyListAdapter;
    List<Expense> money;
    List<Category> categories;
    Double sumCategory;
    Double totalSumInt;
    TextView totalSum;

    TextView toolbarText;
    AnyChartView anyChartView;
    List<DataEntry> data;
    Pie pie;

    Date dStart;
    Date dEnd;

    java.sql.Date dStartSql;
    java.sql.Date dEndSql;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_money, container, false);
        setHasOptionsMenu(true);

        init(view);
        litsClick(view);

        return view;
    }

    private void init(View view)
    {
        activeAutoManager = new ActiveAutoManager(getActivity());
        networkService = NetworkService.getInstance(getActivity());

        totalSum = view.findViewById(R.id.totalSum);
        Calendar cal = Calendar.getInstance();
        int m = cal.get(Calendar.MONTH);
        int y = cal.get(Calendar.YEAR);
        toolbarText = getActivity().findViewById(R.id.toolbarText);
        toolbarText.setText(monthNames[m]+" "+y);
        checkDate(m,y);


        moneyRecyclerView = view.findViewById(R.id.recyclerViewMoney);
        moneyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadExpenses(activeAutoManager.getActiveAutoId());
        anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_barMoney));
        pie = AnyChart.pie();
        data = new ArrayList<>();
        data.add(new ValueDataEntry("", 0));
        pie.labels().position("outside");
        pie.data(data);
        anyChartView.setChart(pie);

    }

    private void litsClick(View view)
    {
        final Calendar today = Calendar.getInstance();
        toolbarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getActivity(), new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        toolbarText.setText(monthNames[selectedMonth]+" "+selectedYear);
                        checkDate(selectedMonth,selectedYear);
                        loadExpenses(activeAutoManager.getActiveAutoId());
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
                builder.setActivatedMonth(Calendar.MONTH)
                        .setMinYear(1990)
                        .setActivatedYear(2020)
                        .setMaxYear(2030)
                        .setMinMonth(Calendar.JANUARY)
                        .setTitle("Выберите месяц и год")
                        .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                        .build()
                        .show();
            }
        });
    }

    private void checkDate(int m, int y)
    {
        int month = m+1;
        String start;
        String end;
        start = "01."+month+"."+y;
        dStart = null;
        dEnd = null;
        try {
            dStart = new SimpleDateFormat("dd.MM.yyyy").parse(start);
            dStartSql = new java.sql.Date(dStart.getTime());
            Calendar c = Calendar.getInstance();
            c.setTime(dStart);
            int endDay = c.getActualMaximum(Calendar.DATE);
            end = endDay+"."+month+"."+y;
            dEnd = new SimpleDateFormat("dd.MM.yyyy").parse(end);
            dEndSql = new java.sql.Date(dEnd.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    public void loadExpenses(String autoId)
    {
        networkService.getJSONApi()
                .getAllExpenseAutoMonth(autoId, dStartSql, dEndSql)
                .enqueue(new Callback<List<Expense>>() {
                    @Override
                    public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                        money = response.body();
                        if(isAdded())
                        moneyListAdapter = new MoneyListAdapter(getActivity(), money, MoneyFragment.this);
                        moneyRecyclerView.setAdapter(moneyListAdapter);
                        loadCategory();
                    }

                    @Override
                    public void onFailure(Call<List<Expense>> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.money_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.addMoney:
                Intent money = new Intent(getActivity(), AddMoneyActivity.class);
                startActivity(money);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}