package com.example.autoappv31;

import android.content.Context;
import android.content.SharedPreferences;

public class ActiveAutoManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String ACTIVE_AUTO_ID = "ACTIVE_AUTO_ID";

    public ActiveAutoManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(ACTIVE_AUTO_ID, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void setActiveAutoId(String id)
    {
        editor.putString(ACTIVE_AUTO_ID, id);
        editor.apply();
    }

    public String getActiveAutoId(){
        return sharedPreferences.getString(ACTIVE_AUTO_ID, null);
    }
}
