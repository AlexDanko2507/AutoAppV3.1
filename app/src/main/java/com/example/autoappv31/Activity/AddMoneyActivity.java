package com.example.autoappv31.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autoappv31.ActiveAutoManager;
import com.example.autoappv31.Models.Auto;
import com.example.autoappv31.Models.Category;
import com.example.autoappv31.Models.Expense;
import com.example.autoappv31.R;
import com.example.autoappv31.Util.NetworkService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMoneyActivity extends AppCompatActivity {

    NetworkService networkService;
    ActiveAutoManager activeAutoManager;

    Intent intent;
    Integer mode;
    Expense editExpense;

    List<Category> categories;
    Category currentCategory;
    SpinnerDialog spinnerDialogCategory;
    ArrayList<String> stringCategory;
    Auto auto;


    static final int CAMERA_REQUEST = 1;

    String QRString;

    TextView toolbarText;
    Toolbar toolbar;

    TextView titleCategory;
    EditText titlePrice;
    EditText titleRunMoney;
    TextView titleYearMoney;
    EditText titleCommentsMoney;
    TextView saveMoneyBtn;

    DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        init();
        listClick();
    }

    private void init()
    {
        networkService = NetworkService.getInstance(this);
        activeAutoManager = new ActiveAutoManager(this);
        loadCategory();

        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarText.setText("Добавить расходы");

        titleCategory = findViewById(R.id.getTitleCategory);
        titlePrice = findViewById(R.id.getTitlePrice);
        titleRunMoney = findViewById(R.id.getTitleRunMoney);
        titleYearMoney = findViewById(R.id.getTitleYearMoney);
        titleCommentsMoney = findViewById(R.id.getTitleCommentsMoney);
        saveMoneyBtn = findViewById(R.id.saveMoneyBtn);

        setCurrentDate();

        stringCategory=new ArrayList<>();

        spinnerDialogCategory=new SpinnerDialog(AddMoneyActivity.this,stringCategory,"Выберите категорию","Отмена");
        spinnerDialogCategory.setCancellable(true); // for cancellable
        spinnerDialogCategory.setShowKeyboard(false);// for open keyboard by default

        intent = getIntent();
        QRString = intent.getStringExtra("QRString");
        if (QRString != null) convertQRString(QRString);
        mode = intent.getIntExtra("mode",0);
        if (mode == 1){
            toolbarText.setText("Редактировать расходы");
            loadExpenseEdit(intent.getStringExtra("expense"));
        }
        else
        {
            toolbarText.setText("Добавить расходы");
            loadActiveAuto();
            if (QRString == null) {
                Date currentDate = new Date();
                // Форматирование времени как "день.месяц.год"
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String dateText = dateFormat.format(currentDate);
                titleYearMoney.setText(dateText);
            }
        }
    }

    private void listClick()
    {
        saveMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double sum = Double.parseDouble(titlePrice.getText().toString());
                Integer run = Integer.parseInt(titleRunMoney.getText().toString());
                String comments = titleCommentsMoney.getText().toString();
                String dString = titleYearMoney.getText().toString();
                Date date = null;
                try {
                    date = new SimpleDateFormat("dd.MM.yyyy").parse(dString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(mode == 1)
                {
                    updateExpense(editExpense.getId(),sum,run,comments,date,activeAutoManager.getActiveAutoId(),currentCategory.getId());
                }else
                {
                    createExpense(sum,run,comments,date,activeAutoManager.getActiveAutoId(),currentCategory.getId());
                }

            }
        });

        titleYearMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(AddMoneyActivity.this,mDateSetListener,year, month, day);
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = "";
                month = month+1;
                if (month < 10)
                    date = dayOfMonth+ ".0" + month + "." +year;
                else
                    date = dayOfMonth+ "." + month + "." +year;
                titleYearMoney.setText(date);
            }
        };

        spinnerDialogCategory.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                currentCategory = categories.get(position);
                titleCategory.setText(item);
            }
        });

        titleCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialogCategory.showSpinerDialog();
            }
        });
    }

    private void loadActiveAuto()
    {
        networkService.getJSONApi()
                .getAutobyId(activeAutoManager.getActiveAutoId())
                .enqueue(new Callback<Auto>() {
                    @Override
                    public void onResponse(Call<Auto> call, Response<Auto> response) {
                        auto = response.body();
                        titleRunMoney.setText(auto.getRun().toString());
                    }

                    @Override
                    public void onFailure(Call<Auto> call, Throwable t) {

                    }
                });
    }

    private void loadExpenseEdit(String expenseId)
    {
        networkService.getJSONApi()
                .getExpensebyId(expenseId)
                .enqueue(new Callback<Expense>() {
                    @Override
                    public void onResponse(Call<Expense> call, Response<Expense> response) {
                        editExpense = response.body();
                        titleCategory.setText(editExpense.getCategory().getName());
                        titlePrice.setText(editExpense.getSum().toString());
                        titleRunMoney.setText(editExpense.getCurrentRun().toString());
                        titleCommentsMoney.setText(editExpense.getComments());
                        currentCategory = editExpense.getCategory();
                        if (QRString == null) {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                            String date = formatter.format(editExpense.getDate());
                            titleYearMoney.setText(date);
                        }
                    }

                    @Override
                    public void onFailure(Call<Expense> call, Throwable t) {

                    }
                });
    }

    private void updateAutoRun(Integer currentRun)
    {
        NetworkService.getInstance(this).getJSONApi()
                .updateAutoRun(activeAutoManager.getActiveAutoId(), currentRun)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Intent i = new Intent(AddMoneyActivity.this, MainActivity.class);
                        startActivity(i);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
    }

    private void createExpense(Double sum, final Integer currentRun, String comments, Date date, final String autoId, String categoryId)
    {
        networkService.getJSONApi()
                .createExpense(sum, currentRun, comments, date, autoId, categoryId)
                .enqueue(new Callback<Expense>() {
                    @Override
                    public void onResponse(Call<Expense> call, Response<Expense> response) {
                        if(currentRun > auto.getRun())
                        {
                            updateAutoRun(currentRun);
                        }
                        else
                        {
                            Intent i = new Intent(AddMoneyActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                    }

                    @Override
                    public void onFailure(Call<Expense> call, Throwable t) {

                    }
                });
    }

    private void updateExpense(String expenseId, Double sum, final Integer currentRun, String comments, Date date, String autoId, String categoryId)
    {
        networkService.getJSONApi()
                .updateExpennse(expenseId,sum, currentRun, comments, date, autoId, categoryId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(currentRun > editExpense.getAuto().getRun())
                        {
                            updateAutoRun(currentRun);
                        }
                        else
                        {
                            Intent i = new Intent(AddMoneyActivity.this, MainActivity.class);
                            startActivity(i);
                        }
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
                        categories = response.body();
                        for(Integer i =0; i<categories.size();i++)
                        {
                            stringCategory.add(categories.get(i).getName());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {

                    }
                });
    }

    private void setCurrentDate()
    {
        Date currentDate = new Date();
        // Форматирование времени как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        titleYearMoney.setText(dateText);
    }

    private void convertQRString(String qr)
    {
        String[] subStr = qr.split("&");
        String date = subStr[0].substring(2,10);
        Date d = null;
        try {
            d = new SimpleDateFormat("yyyyMMdd").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String newDate = formatter.format(d);
        String price = subStr[1].substring(2);
        titleYearMoney.setText(newDate);
        titlePrice.setText(price);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addmoney_fragment_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.scanCode:
                ActivityCompat.requestPermissions(AddMoneyActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(AddMoneyActivity.this, QRScannerActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(this, "We need next permissions: Camera", Toast.LENGTH_LONG).show();
                }
        }
    }
}
