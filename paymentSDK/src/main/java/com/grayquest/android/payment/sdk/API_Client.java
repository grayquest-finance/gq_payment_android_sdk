package com.grayquest.android.payment.sdk;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API_Client {

//    public static final String BASE_URL = "https://erp-api.graydev.tech/";// Test
//    public static final String WEB_LOAD_URL = "https://erp-sdk.graydev.tech/";// Test
    public static final String BASE_URL = "https://erp-api.grayquest.com/";// Live
    public static final String WEB_LOAD_URL = "https://erp-sdk.grayquest.com/";// Live

    public static Retrofit getRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(new HeaderInterceptor(AuthToken)).build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl(Environment.baseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }
}
