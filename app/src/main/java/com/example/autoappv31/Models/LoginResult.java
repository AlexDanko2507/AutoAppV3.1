package com.example.autoappv31.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResult {
    @SerializedName("userId")
    @Expose
    private String userId;

    public String getUserId(){
        return userId;
    }
}
