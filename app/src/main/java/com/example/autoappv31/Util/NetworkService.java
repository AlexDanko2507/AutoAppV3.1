package com.example.autoappv31.Util;

import android.content.Context;

import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NetworkService {
    private static NetworkService mInstance;
    private Retrofit mRetrofit;

    private NetworkService(Context ctx) {
        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);


        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .addInterceptor(interceptor);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(ctx.getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE).getString(Constants.BASE_URL, Constants.DEFAULT_BASE_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
    }

    public static NetworkService getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new NetworkService(ctx);
        }
        return mInstance;

    }

    public JSONPlaceHolderApi getJSONApi() {
        return mRetrofit.create(JSONPlaceHolderApi.class);
    }

}