package com.pasta.mensadd.networking;

import com.pasta.mensadd.database.entity.Canteen;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("canteens")
    Call<ApiResponse<Canteen>> getCanteens();

}
