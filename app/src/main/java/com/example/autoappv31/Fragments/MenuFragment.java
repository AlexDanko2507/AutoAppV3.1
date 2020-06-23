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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.autoappv31.Activity.AllBuildActivity;
import com.example.autoappv31.Activity.AllMoneyActivity;
import com.example.autoappv31.Activity.AllMoneyCategoryActivity;
import com.example.autoappv31.Activity.AutoListActivity;
import com.example.autoappv31.Activity.CategoryListActivity;
import com.example.autoappv31.Activity.CategoryWorkListActivity;
import com.example.autoappv31.Activity.EditUserActivity;
import com.example.autoappv31.Activity.UserControlActivity;
import com.example.autoappv31.Models.AllExpenseSum;
import com.example.autoappv31.Models.User;
import com.example.autoappv31.R;
import com.example.autoappv31.Activity.RecognizeAutoActivity;
import com.example.autoappv31.SessionManager;
import com.example.autoappv31.Util.NetworkService;
import com.loopeer.shadow.ShadowView;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuFragment extends Fragment {

    SessionManager sessionManager;

    CircleImageView userImageMenu;
    TextView userLoginMenu;
    TextView userEmailMenu;
    TextView userEditBtn;
    TextView countAuto;
    TextView autoListMenu;
    TextView categoryListMenu;
    TextView categoryWorkListMenu;
    TextView allMoneyListMenu;
    TextView setSum;
    TextView recognizeBtn;
    TextView allBuildMenu;
    TextView allListMoneyCategory;
    TextView userControlbtn;
    ShadowView titleUserControl;
    ShadowView shadowCategory;
    ShadowView shadowCategoryWork;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_menu, container, false);
        setHasOptionsMenu(true);

        init(view);
        listClick(view);

        return view;
    }

    private void listClick(View view) {
        autoListMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AutoListActivity.class);
                startActivity(i);
            }
        });

        userEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditUserActivity.class);
                startActivity(i);
            }
        });
        categoryListMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CategoryListActivity.class);
                startActivity(i);
            }
        });
        categoryWorkListMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CategoryWorkListActivity.class);
                startActivity(i);
            }
        });
        allMoneyListMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AllMoneyActivity.class);
                startActivity(i);
            }
        });
        recognizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), RecognizeAutoActivity.class);
                startActivity(i);
            }
        });
        allBuildMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AllBuildActivity.class);
                startActivity(i);
            }
        });
        allListMoneyCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AllMoneyCategoryActivity.class);
                startActivity(i);
            }
        });
        userControlbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UserControlActivity.class);
                startActivity(i);
            }
        });
    }

    private void init(View view)
    {
        sessionManager = new SessionManager(getActivity());
        getAllExpenseUser(sessionManager.getUser().getId());

        userImageMenu = view.findViewById(R.id.userImageMenu);
        userLoginMenu = view.findViewById(R.id.userLoginMenu);
        userEmailMenu = view.findViewById(R.id.userEmailMenu);
        userEditBtn = view.findViewById(R.id.editUserBtn);
        autoListMenu = view.findViewById(R.id.autoListMenu);
        categoryListMenu = view.findViewById(R.id.categoryListMenu);
        categoryWorkListMenu = view.findViewById(R.id.categoryWorkListMenu);
        allMoneyListMenu = view.findViewById(R.id.allExpenseMenu);
        allListMoneyCategory = view.findViewById(R.id.allExpenseMenuCategory);
        setSum = view.findViewById(R.id.allExpense);
        countAuto = view.findViewById(R.id.countAuto);
        recognizeBtn = view.findViewById(R.id.recognizeAuto);
        allBuildMenu = view.findViewById(R.id.allBuildMenu);
        userControlbtn = view.findViewById(R.id.userControl);
        titleUserControl = view.findViewById(R.id.titleUserControl);
        shadowCategory = view.findViewById(R.id.shadowCategoryList);
        shadowCategoryWork = view.findViewById(R.id.shadowCategoryWorkList);

        userEmailMenu.setText(sessionManager.getUser().getEmail());
        userLoginMenu.setText(sessionManager.getUser().getUsername());
        if (sessionManager.getUser().getImageUrl()!=null)
        Glide.with(view)
                .load(sessionManager.getUser().getImageUrl())
                .into(userImageMenu);
        countAuto.setText(sessionManager.getCountAuto());
        if(sessionManager.getRole().getName().equals("admin"))
        {
            titleUserControl.setVisibility(View.VISIBLE);
            shadowCategory.setVisibility(View.VISIBLE);
            shadowCategoryWork.setVisibility(View.VISIBLE);
        }
    }

    private void getAllExpenseUser(String userId)
    {
        NetworkService.getInstance(getActivity())
                .getJSONApi()
                .findAutosExpense(userId)
                .enqueue(new Callback<AllExpenseSum>() {
                    @Override
                    public void onResponse(Call<AllExpenseSum> call, Response<AllExpenseSum> response) {
                        AllExpenseSum allExpenseSum = response.body();
                        setSum.setText(allExpenseSum.getSum().toString());
                    }

                    @Override
                    public void onFailure(Call<AllExpenseSum> call, Throwable t) {

                    }
                });
    }

    private void getUserInformation(String userId)
    {
        NetworkService.getInstance(getActivity())
                .getJSONApi()
                .getUserbyId(userId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();
                        if (response.code() == 200) {
                            userLoginMenu.setText(user.getUsername());
                            userEmailMenu.setText(user.getEmail());
                        } else if (response.code() == 400) {
                            Toast.makeText(getActivity(),
                                    "ERROE 400", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logOut:
                sessionManager.logout();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}