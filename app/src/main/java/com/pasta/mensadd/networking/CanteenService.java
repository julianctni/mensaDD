package com.pasta.mensadd.networking;

import com.pasta.mensadd.database.entity.Canteen;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface CanteenService {

    @GET("canteens")
    Call<ApiResponse<Canteen>> getCanteens(@Header("Authorization") String basicAuth);
}
