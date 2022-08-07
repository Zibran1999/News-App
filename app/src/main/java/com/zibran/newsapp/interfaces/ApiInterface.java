package com.zibran.newsapp.interfaces;

import com.zibran.newsapp.models.NewsApiModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("everything/")
    Call<NewsApiModel> getNewsFromNewsAPI(@Query("q") String q,
                                          @Query("from") String from,
                                          @Query("sortBy") String sortBy,
                                          @Query("apiKey") String apiKey);


}
