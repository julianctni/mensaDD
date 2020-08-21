package com.pasta.mensadd.network;

import com.pasta.mensadd.domain.canteen.Canteen;
import com.pasta.mensadd.domain.meal.Meal;
import com.pasta.mensadd.domain.news.News;

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
