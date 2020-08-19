package com.pasta.mensadd.networking;

import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.entity.Meal;
import com.pasta.mensadd.database.entity.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("canteens")
    Call<ApiResponse<Canteen>> getCanteens();

    @GET("news")
    Call<ApiResponse<News>> getNews();

    @GET("canteens/{canteenId}/meals")
    Call<ApiResponse<Meal>> getMeals(@Path("canteenId") String canteenId);
}
