package com.example.autoappv31;

import android.content.Context;
import android.content.SharedPreferences;

public class FragmentManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "FRAGMENT";

    public FragmentManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void setFragment(String fragment){
        editor.putString(PREF_NAME, fragment);
        editor.apply();
    }

    public String getFragment(){
        return sharedPreferences.getString(PREF_NAME, null);
    }
}
