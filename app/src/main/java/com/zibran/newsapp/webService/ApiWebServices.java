package com.zibran.newsapp.webService;

import com.zibran.newsapp.interfaces.ApiInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiWebServices {

    public static final String baseUrl = "https://newsapi.org/v2/";
    static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create());
    static Retrofit retrofit = builder.build();

    public static ApiInterface getApiInterface() {
        return retrofit.create(ApiInterface.class);
    }
}
