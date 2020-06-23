package com.example.autoappv31;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autoappv31.Activity.LoginActivity;
import com.example.autoappv31.Activity.MainActivity;
import com.example.autoappv31.Models.Role;
import com.example.autoappv31.Models.User;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String USERNAME = "USERNAME";
    public static final String EMAIL = "EMAIL";
    public static final String ID = "ID";
    public static final String PASSWORD = "PASSWORD";
    public static final String IMAGEURL = "IMAGEURL";
    public static final String ROLE = "ROLE";
    public static final String COUNT_AUTO = "COUNT_AUTO";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void setCountAuto(String count) {
        editor.putString(COUNT_AUTO, count);
        editor.apply();
    }

    public String getCountAuto()
    {
        return sharedPreferences.getString(COUNT_AUTO, null);
    }

    public void createSession(User user){
        editor.putBoolean(LOGIN, true);
        editor.putString(USERNAME, user.getUsername());
        editor.putString(EMAIL, user.getEmail());
        editor.putString(ID, user.getId());
        editor.putString(PASSWORD, user.getPassword());
        editor.putString(ROLE, user.getRole().getName());
        editor.putString(IMAGEURL, user.getImageUrl());
        editor.apply();
    }

    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(){
        if (!this.isLoggin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((MainActivity) context).finish();
        }
    }

    public User getUser(){
        User user = new User();
        user.setId(sharedPreferences.getString(ID, null));
        user.setEmail(sharedPreferences.getString(EMAIL, null));
        user.setUsername(sharedPreferences.getString(USERNAME, null));
        user.setPassword(sharedPreferences.getString(PASSWORD, null));
        user.setImageUrl(sharedPreferences.getString(IMAGEURL, null));
        return user;
    }

    public Role getRole()
    {
        Role role = new Role();
        role.setName(sharedPreferences.getString(ROLE, null));
        return role;
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((MainActivity) context).finish();
    }

}
