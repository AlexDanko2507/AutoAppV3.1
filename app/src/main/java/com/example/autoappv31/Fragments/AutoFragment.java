package com.example.autoappv31.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.autoappv31.ActiveAutoManager;
import com.example.autoappv31.Activity.AddAutoActivity;
import com.example.autoappv31.Activity.AllMoneyActivity;
import com.example.autoappv31.Adapters.AllMoneyListAdapter;
import com.example.autoappv31.Models.Auto;
import com.example.autoappv31.Models.CategoryWork;
import com.example.autoappv31.Models.Expense;
import com.example.autoappv31.Models.TechnicalWork;
import com.example.autoappv31.R;
import com.example.autoappv31.SessionManager;
import com.example.autoappv31.Util.NetworkService;
import com.loopeer.shadow.ShadowView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutoFragment extends Fragment {

    NetworkService networkService;
    TechnicalWork technicalWork;

    ActiveAutoManager activeAutoManager;
    SessionManager sessionManager;
    List <Auto> autos;
    ArrayList<String> autosName;
    AlertDialog addAutoDialog;
    AlertDialog setAutoDialog;
    Auto auto;

    TextView textMark;
    TextView textModel;
    ImageView imageAuto;
    TextView allExpense;
    TextView allAutoRun;
    TextView lastWork;
    TextView titleWork;
    TextView endTechnicalWork;
    ShadowView showEndTechnicalWork;

    CharSequence[] userAutosString;
    DialogInterface.OnClickListener setAutoClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_auto, container, false);
        setHasOptionsMenu(true);

        init(view);
        checkAuto(sessionManager.getCountAuto(),view);
        listClick();

        return view;
    }

    private void listClick()
    {
        setAutoClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_NEGATIVE)
                {
                    dialog.dismiss();
                }
                else
                {
                    activeAutoManager.setActiveAutoId(autos.get(which).getId());
                    setActiveAutoInfo(activeAutoManager.getActiveAutoId());
                }
            }
        };

        endTechnicalWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setEndndTechnicalWork(technicalWork.getId());
                showEndDialog();
            }
        });
    }

    private void init(View view)
    {
        networkService = NetworkService.getInstance(getContext());
        sessionManager = new SessionManager(getActivity());
        activeAutoManager = new ActiveAutoManager(getActivity());
//        Toast.makeText(getActivity(), activeAutoManager.getActiveAutoId(),Toast.LENGTH_SHORT).show();
        findAllAutoUsers(sessionManager.getUser().getId());

        textMark = view.findViewById(R.id.textViewMark);
        textModel = view.findViewById(R.id.textViewModel);
        imageAuto = view.findViewById(R.id.imageAuto);
        allExpense = view.findViewById(R.id.allExpense);
        allAutoRun = view.findViewById(R.id.allAutoRun);
        lastWork = view.findViewById(R.id.lastWork);
        titleWork = view.findViewById(R.id.titleWork);
        endTechnicalWork = view.findViewById(R.id.endTechnicalWork);
        showEndTechnicalWork = view.findViewById(R.id.showEndTechnicalWork);

    }

    private void showEndDialog()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Заполните данные о расходах");

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View add_window = inflater.inflate(R.layout.end_technicalwork_window, null);
        dialog.setView(add_window);

        final EditText priceEnd = add_window.findViewById(R.id.getTitlePriceEnd);
        final EditText runEnd = add_window.findViewById(R.id.getTitleRunMoneyEnd);

        runEnd.setText(auto.getRun().toString());

        dialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.setPositiveButton("Завершить событие", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createExpenseEnd(Double.parseDouble(priceEnd.getText().toString()),Integer.parseInt(runEnd.getText().toString()),technicalWork.getComments(),technicalWork.getDate(),activeAutoManager.getActiveAutoId(),technicalWork.getId());
            }
        });

        dialog.show();
    }

    private void checkAuto(String count, View view)
    {
        if(count.equals("0"))
        {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(view.getContext());
            myBuilder.setTitle("Добро пожаловать!");
            myBuilder.setMessage("Для начала работы, пожалуйста, добавьте Ваш первый автомобиль");
            myBuilder.setNeutralButton("Добавить автомобиль", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(getActivity(), AddAutoActivity.class);
                    startActivity(i);
                }
            });
            myBuilder.setCancelable(false);
            addAutoDialog = myBuilder.create();
            addAutoDialog.show();
        }
        else
        {
            //activeAutoManager.setActiveAutoId(autos.get(0).getId());
            setActiveAutoInfo(activeAutoManager.getActiveAutoId());
        }
    }

    private void createExpenseEnd(Double sum, Integer currentRun, String comments, Date date, String autoId, final String technicalWorkId)
    {
        networkService.getJSONApi()
                .createExpenseEnd(sum, currentRun, comments, date, autoId, technicalWorkId)
                .enqueue(new Callback<Expense>() {
                    @Override
                    public void onResponse(Call<Expense> call, Response<Expense> response) {
                        setEndTechnicalWork(technicalWorkId);
                    }

                    @Override
                    public void onFailure(Call<Expense> call, Throwable t) {

                    }
                });
    }

    private void setEndTechnicalWork(String technicalWorkId)
    {
        networkService.getJSONApi()
                .endTechnicalWorkNow(technicalWorkId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        setActiveAutoInfo(activeAutoManager.getActiveAutoId());
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
    }

    private void loadCategoryWorkNow(String technicalWorkId)
    {
        networkService.getJSONApi()
                .getCategoryWorklWorkAutoNow(technicalWorkId)
                .enqueue(new Callback<List<CategoryWork>>() {
                    @Override
                    public void onResponse(Call<List<CategoryWork>> call, Response<List<CategoryWork>> response) {
                        List<CategoryWork> categoryWorks = response.body();
                        StringBuilder sb = new StringBuilder();
                        for (Integer i = 0; i < categoryWorks.size(); i++) {
                            sb.append(categoryWorks.get(i).getName()+"\n");
                        }
                        lastWork.setText(sb.toString());
                    }

                    @Override
                    public void onFailure(Call<List<CategoryWork>> call, Throwable t) {

                    }
                });
    }

    private void loadTechnicalWorkNow(String autoId)
    {
        networkService.getJSONApi()
                .getAllTechnicalWorkAutoNow(autoId)
                .enqueue(new Callback<TechnicalWork>() {
                    @Override
                    public void onResponse(Call<TechnicalWork> call, Response<TechnicalWork> response) {
                        if (response.code() == 200) {
                            Calendar now = Calendar.getInstance();
                            technicalWork = response.body();
                            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                            String dateNow = formatter.format(now.getTime());
                            String date = formatter.format(technicalWork.getDate());
                            if (dateNow.compareTo(date) > 0 || dateNow.compareTo(date) == 0)
                            {
                                showEndTechnicalWork.setVisibility(View.VISIBLE);
                            }
                            titleWork.setText("Ближайшее событие" + "\n" + date);
                            loadCategoryWorkNow(technicalWork.getId());
                        }
                        else{
                            titleWork.setText("");
                            lastWork.setText("Нет запланированных событий");
                        }
                    }

                    @Override
                    public void onFailure(Call<TechnicalWork> call, Throwable t) {

                    }
                });
    }

    private void findAllAutoUsers(String userId)
    {
        networkService.getJSONApi()
                .findAutos(userId)
                .enqueue(new Callback<List<Auto>>() {
                    @Override
                    public void onResponse(Call<List<Auto>> call, Response<List<Auto>> response) {
                        autos = response.body();
                        autosName = new ArrayList<>();
                        for (Integer i =0;i<autos.size();i++)
                        {
                            autosName.add(autos.get(i).getMark().getName() + " " + autos.get(i).getModel().getName());
                        }
                        userAutosString = autosName.toArray(new CharSequence[autosName.size()]);
                    }

                    @Override
                    public void onFailure(Call<List<Auto>> call, Throwable t) {

                    }
                });
    }

    public void loadAllExpenses(final String autoId)
    {
        networkService.getJSONApi()
                .getAllExpenseAuto(autoId)
                .enqueue(new Callback<List<Expense>>() {
                    @Override
                    public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                        List<Expense> money = response.body();
                        Double sum = .0;
                        for (Integer i =0; i<money.size();i++)
                        {
                            sum+=money.get(i).getSum();
                        }
                        allExpense.setText(sum.toString());
                        loadTechnicalWorkNow(autoId);
                    }

                    @Override
                    public void onFailure(Call<List<Expense>> call, Throwable t) {

                    }
                });
    }

    private void setActiveAutoInfo(final String autoId)
    {
        networkService.getJSONApi()
                .getAutobyId(autoId)
                .enqueue(new Callback<Auto>() {
                    @Override
                    public void onResponse(Call<Auto> call, Response<Auto> response) {
                        auto = response.body();
                        showEndTechnicalWork.setVisibility(View.GONE);
                        textMark.setText(auto.getMark().getName());
                        textModel.setText(auto.getModel().getName());
                        allAutoRun.setText(auto.getRun().toString());
                        if(isAdded())
                        Glide.with(getContext())
                                .load(auto.getImageUrl())
                                .into(imageAuto);
                        loadAllExpenses(autoId);
                    }

                    @Override
                    public void onFailure(Call<Auto> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.auto_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.setAuto:
                TextView userAutoName = new TextView(getActivity());
                userAutoName.setText("Выбрать основной автомобиль");
                userAutoName.setPadding(20, 30, 20, 30);
                userAutoName.setTextSize(20F);
                userAutoName.setBackgroundColor(Color.rgb(00,85,77));
                userAutoName.setTextColor(Color.WHITE);
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
                myBuilder.setCustomTitle(userAutoName);
                myBuilder.setNegativeButton("Отмена", setAutoClickListener);
                myBuilder.setItems(userAutosString, setAutoClickListener);
                setAutoDialog = myBuilder.create();
                setAutoDialog.show();

                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}