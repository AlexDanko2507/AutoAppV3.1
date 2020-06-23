package com.example.autoappv31.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.autoappv31.FragmentManager;
import com.example.autoappv31.Fragments.AutoFragment;
import com.example.autoappv31.Fragments.BuildFragment;
import com.example.autoappv31.Fragments.MenuFragment;
import com.example.autoappv31.Fragments.MoneyFragment;
import com.example.autoappv31.R;
import com.example.autoappv31.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView toolbarText;
    SessionManager sessionManager;
    FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        fragmentManager = new FragmentManager(this);

        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnNavigationItemSelectedListener(navListener);
        toolbarText.setText("Список работ");

        String check = fragmentManager.getFragment();
        switch (check)
        {
            case "auto":
                bottomNav.setSelectedItemId(R.id.nav_auto);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AutoFragment()).commit();
                break;
            case "menu":
                bottomNav.setSelectedItemId(R.id.nav_menu);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuFragment()).commit();
                break;
            case "expense":
                bottomNav.setSelectedItemId(R.id.nav_money);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MoneyFragment()).commit();
                break;
            case "build":
                bottomNav.setSelectedItemId(R.id.nav_build);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BuildFragment()).commit();
                break;
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId())
            {
//                case R.id.nav_map:
//                    toolbarText.setText("Map");
//                    selectedFragment = new MapFragment();
//                    break;
                case R.id.nav_build:
                    toolbarText.setText("Список работ");
                    fragmentManager.setFragment("build");
                    selectedFragment = new BuildFragment();
                    break;
                case R.id.nav_money:
                    toolbarText.setText("Раходы");
                    fragmentManager.setFragment("expense");
                    selectedFragment = new MoneyFragment();
                    break;
                case R.id.nav_menu:
                    toolbarText.setText("Меню");
                    fragmentManager.setFragment("menu");
                    selectedFragment = new MenuFragment();
                    break;
                case R.id.nav_auto:
                    fragmentManager.setFragment("auto");
                    toolbarText.setText("Данные автомобиля");
                    selectedFragment = new AutoFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };
}
