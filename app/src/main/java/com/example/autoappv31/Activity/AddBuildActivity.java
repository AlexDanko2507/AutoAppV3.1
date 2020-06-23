package com.example.autoappv31.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.allyants.notifyme.NotifyMe;
import com.example.autoappv31.ActiveAutoManager;
import com.example.autoappv31.Models.Category;
import com.example.autoappv31.Models.CategoryWork;
import com.example.autoappv31.Models.Expense;
import com.example.autoappv31.Models.NotificationAuto;
import com.example.autoappv31.Models.TechnicalWork;
import com.example.autoappv31.R;
import com.example.autoappv31.Util.NetworkService;
import com.loopeer.shadow.ShadowView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBuildActivity extends AppCompatActivity {

    Intent intent;
    Integer mode;
    TechnicalWork editTechnicalWork;
    List<CategoryWork> editListCategoryWorks;
    NotificationAuto editNotificationAuto;
    boolean checkDate;

    ActiveAutoManager activeAutoManager;
    NetworkService networkService;

    TextView toolbarText;
    Toolbar toolbar;

    EditText nameBuild;
    EditText commentsBuild;
    TextView dateBuild;
    TextView saveBuildBtn;
    TextView listWork;
    TextView getDateTimeNotify;
    boolean switchCheck;

    DatePickerDialog.OnDateSetListener mDateSetListener;
    TimePickerDialog.OnTimeSetListener t;
    DatePickerDialog.OnDateSetListener d;
    Calendar now = Calendar.getInstance();
    long DAY_IN_MS = 1000 * 60 * 60 * 24;

    List<CategoryWork> categoryWorkList;
    ArrayList<String> categoryName;
    CharSequence[] categoryWorkListString;
    ArrayList selectedCategory;
    ArrayList<String> selectedCategoryId;
    ArrayList<Boolean> checkedListArrayList;
    boolean[] checkedList;

    AlertDialog listWordkAlertDialog;

    LinearLayout notifyTitleLayout;
    LinearLayout notifyDateLayout;
    ShadowView shadowSaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_build);

        init();
        listClick();
        
    }

    private void listClick() {
        dateBuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                checkDate=false;
                DatePickerDialog dialog = new DatePickerDialog(AddBuildActivity.this,mDateSetListener,year, month, day);
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
                dateBuild.setText(date);
                Date d = null;
                try {
                    d = new SimpleDateFormat("dd.MM.yyyy").parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date newDate = new Date(d.getTime()-(3*DAY_IN_MS));
                now.setTime(newDate);
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                String dateAgo = formatter.format(newDate);
                getDateTimeNotify.setText(dateAgo);
            }
        };


        getDateTimeNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });

        // установка обработчика выбора времени
        t=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                now.set(Calendar.HOUR_OF_DAY, hourOfDay);
                now.set(Calendar.MINUTE, minute);
                getDateTimeNotify.setText(DateUtils.formatDateTime(AddBuildActivity.this,
                        now.getTimeInMillis(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
                checkDate = true;
                //createNotify("test");
            }
        };

        // установка обработчика выбора даты
        d=new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                now.set(Calendar.YEAR, year);
                now.set(Calendar.MONTH, monthOfYear);
                now.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setTime();
            }
        };

        listWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(v.getContext());
//                selectedCategory.clear();
//                selectedCategoryId.clear();
                myBuilder.setTitle("Выберите работы").setMultiChoiceItems(categoryWorkListString, checkedList, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedList[which] = isChecked;
                        if (isChecked)
                        {
                            selectedCategory.add(categoryWorkListString[which]);
                            selectedCategoryId.add(categoryWorkList.get(which).getId());
                        }
                        else
                        {
                            for(Integer i =0;i<selectedCategory.size();i++)
                            {
                                if(categoryWorkListString[which].equals(selectedCategory.get(i)))
                                {
                                    selectedCategory.remove(i.intValue());
                                    selectedCategoryId.remove(i.intValue());
                                }

                            }

                        }
                    }
                });
                myBuilder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder sb = new StringBuilder();
                        for (Object o: selectedCategory){
                            sb.append(o.toString()+"\n");
                        }
                        listWork.setText(sb.toString());
                    }
                });
                listWordkAlertDialog = myBuilder.create();
                listWordkAlertDialog.show();
            }
        });

        saveBuildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameBuild.getText().toString();
                String comments = commentsBuild.getText().toString();
                String dString = dateBuild.getText().toString();
                Date d = null;
                Date dStart = null;
                try {
                    d = new SimpleDateFormat("dd.MM.yyyy").parse(dString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String dStringNotify = getDateTimeNotify.getText().toString();
                try {
                    if (checkDate)
                    {
                        DateFormat dateNow = new SimpleDateFormat("dd.MM.yyyy");
                        String dStartString = dateNow.format(now.getTime());
                        try {
                            dStart = new SimpleDateFormat("dd.MM.yyyy").parse(dStartString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        dStart = new SimpleDateFormat("dd.MM.yyyy").parse(dStringNotify);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(mode == 1)
                {
                    updateTechnicalWork(editTechnicalWork.getId(), name, comments, d, dStart, activeAutoManager.getActiveAutoId(), selectedCategoryId);
                }
                else  {
                    createTechnicalWork(name,comments, d, dStart,true,activeAutoManager.getActiveAutoId(), selectedCategoryId);
                }

            }
        });

    }

    private void init()
    {
        networkService = NetworkService.getInstance(this);
        activeAutoManager = new ActiveAutoManager(this);

        loadCategoryWork();

        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        nameBuild = findViewById(R.id.getTitleNameTO);
        commentsBuild = findViewById(R.id.getTitleCommentsTO);
        dateBuild = findViewById(R.id.getTitleYearTO);
        saveBuildBtn = findViewById(R.id.saveBuildBtn);
        listWork = findViewById(R.id.getListWork);
        getDateTimeNotify = findViewById(R.id.getDateTimeNotify);
        notifyTitleLayout = findViewById(R.id.notifyLayout);
        notifyDateLayout = findViewById(R.id.notifyDateLayout);
        shadowSaveBtn = findViewById(R.id.shadowSaveBtn);
        checkDate = false;

        setCurrentDate();

        categoryName = new ArrayList<>();
        selectedCategory = new ArrayList();
        selectedCategoryId = new ArrayList<>();

        intent = getIntent();
        mode = intent.getIntExtra("mode",0);
        if (mode == 1){
            toolbarText.setText("Редактировать работы");
            loadTechnicalWorkEdit(intent.getStringExtra("build"));
        }
        else if (mode == 2){
            toolbarText.setText("Просмотр работ");
            loadTechnicalWorkEdit(intent.getStringExtra("build"));
            notifyDateLayout.setVisibility(View.INVISIBLE);
            notifyTitleLayout.setVisibility(View.INVISIBLE);
            shadowSaveBtn.setVisibility(View.INVISIBLE);
        }
        else{
            toolbarText.setText("Добавить работы");
        }

    }

    private void updateTechnicalWork(String technicalWorkId ,String name, String comments, Date date, Date dateStart, String autoId, List<String> listWorkId)
    {
        networkService.getJSONApi()
                .updateTechnicalWork(technicalWorkId, name, comments, date,dateStart, autoId, listWorkId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Intent i = new Intent(AddBuildActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
    }

    private void loadNotificationAutoTechnicalWorkId(String technicalWorkId)
    {
        networkService.getJSONApi()
                .getNotificationAutoTechnicalWork(technicalWorkId)
                .enqueue(new Callback<NotificationAuto>() {
                    @Override
                    public void onResponse(Call<NotificationAuto> call, Response<NotificationAuto> response) {
                        editNotificationAuto = response.body();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                        String date = formatter.format(editNotificationAuto.getDateStart());
                        getDateTimeNotify.setText(date);
                    }

                    @Override
                    public void onFailure(Call<NotificationAuto> call, Throwable t) {

                    }
                });
    }

    private void loadCategoryWorkTechnicalWorkId(final String technicalWorkId)
    {
        networkService.getJSONApi()
                .getCategoryWorkTechnicalWork(technicalWorkId)
                .enqueue(new Callback<List<CategoryWork>>() {
                    @Override
                    public void onResponse(Call<List<CategoryWork>> call, Response<List<CategoryWork>> response) {
                        editListCategoryWorks = response.body();
                        for(Integer i =0; i<categoryWorkList.size();i++){
                            for(Integer j =0; j<editListCategoryWorks.size();j++)
                            {
                                if(categoryWorkList.get(i).getId().equals(editListCategoryWorks.get(j).getId()))
                                {
                                    checkedList[i]=true;
                                    selectedCategory.add(categoryWorkList.get(i).getName());
                                    selectedCategoryId.add(categoryWorkList.get(i).getId());
                                }
                            }
                        }
                        StringBuilder sb = new StringBuilder();
                        for (Integer i = 0; i < editListCategoryWorks.size(); i++) {
                            sb.append(editListCategoryWorks.get(i).getName() + "\n");
                        }
                        listWork.setText(sb.toString());
                        loadNotificationAutoTechnicalWorkId(technicalWorkId);
                    }

                    @Override
                    public void onFailure(Call<List<CategoryWork>> call, Throwable t) {

                    }
                });
    }

    private void loadTechnicalWorkEdit(final String technicalWorkId)
    {
        networkService.getJSONApi()
                .getTechnicalWorkbyId(technicalWorkId)
                .enqueue(new Callback<TechnicalWork>() {
                    @Override
                    public void onResponse(Call<TechnicalWork> call, Response<TechnicalWork> response) {
                        editTechnicalWork = response.body();
                        nameBuild.setText(editTechnicalWork.getName());
                        commentsBuild.setText(editTechnicalWork.getComments());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                        String date = formatter.format(editTechnicalWork.getDate());
                        dateBuild.setText(date);
                        loadCategoryWorkTechnicalWorkId(technicalWorkId);
                    }

                    @Override
                    public void onFailure(Call<TechnicalWork> call, Throwable t) {

                    }
                });

    }

    private void createTechnicalWork(String name, String comments, Date date, Date dateStart,boolean status, String autoId, List<String> listWorkId)
    {
        networkService.getJSONApi()
                .createTechnicalWork(name,comments,date,dateStart,status,autoId,listWorkId)
                .enqueue(new Callback<TechnicalWork>() {
                    @Override
                    public void onResponse(Call<TechnicalWork> call, Response<TechnicalWork> response) {
                        TechnicalWork t = response.body();
                        createNotify(t.getId());
                        Intent i = new Intent(AddBuildActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<TechnicalWork> call, Throwable t) {

                    }
                });
    }

    private void createNotify(String id)
    {
        NotifyMe notifyMe = new NotifyMe.Builder(getApplicationContext())
                .title(nameBuild.getText().toString())
                .content(commentsBuild.getText().toString())
                .color(255,0,0,255)
                .led_color(255,255,255,255)
                .time(now)
                .key(id)
                .addAction(new Intent(), "Напомнить позже", true, false)
                .rrule("FREQ=DAILY;INTERVAL=1;COUNT=3")
                .build();
    }

    private void loadCategoryWork()
    {
        networkService.getJSONApi()
                .getAllCategoryWork()
                .enqueue(new Callback<List<CategoryWork>>() {
                    @Override
                    public void onResponse(Call<List<CategoryWork>> call, Response<List<CategoryWork>> response) {
                        categoryWorkList = response.body();
                        checkedList = new boolean[categoryWorkList.size()];
                        for(Integer i =0; i<categoryWorkList.size();i++)
                        {
                            categoryName.add(categoryWorkList.get(i).getName());
                            checkedList[i]=false;
                        }
                        categoryWorkListString = categoryName.toArray(new CharSequence[categoryName.size()]);
                    }

                    @Override
                    public void onFailure(Call<List<CategoryWork>> call, Throwable t) {

                    }
                });
    }

    public void setDate(View v) {
        new DatePickerDialog(AddBuildActivity.this, d,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public void setTime() {
        new TimePickerDialog(AddBuildActivity.this, t,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), true)
                .show();
    }

    private void setCurrentDate()
    {
        Date currentDate = new Date();
        // Форматирование времени как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        dateBuild.setText(dateText);
        Date newDate = new Date(currentDate.getTime()-(3*DAY_IN_MS));
        now.setTime(newDate);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateAgo = formatter.format(newDate);
        getDateTimeNotify.setText(dateAgo);
    }
}
