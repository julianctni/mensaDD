package com.pasta.mensadd.network;

import com.pasta.mensadd.domain.canteen.Canteen;
import com.pasta.mensadd.domain.meal.Meal;
import com.pasta.mensadd.domain.news.News;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceClient {

    public final static int NOT_FETCHING = -1;
    public final static int IS_FETCHING = 1;
    public final static int FETCH_ERROR = 2;
    public final static int FETCH_SUCCESS = 3;

    private static ApiServiceClient mInstance;
    private ApiService apiService;

    private ApiServiceClient(String baseUrl, String user, String apiKey) {
        Retrofit.Builder retrofitBuilder = new Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create());

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        String authToken = Credentials.basic(user, apiKey);
        httpClient.interceptors().clear();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Authorization", authToken)
                    .build();
            return chain.proceed(request);
        });
        retrofitBuilder.client(httpClient.build());
        Retrofit retrofit = retrofitBuilder.build();
        apiService = retrofit.create(ApiService.class);
    }

    public static ApiServiceClient getInstance(String baseUrl, String user, String apiKey) {
        if (mInstance == null) {
            mInstance = new ApiServiceClient(baseUrl, user, apiKey);
        }
        return mInstance;
    }


    public Call<ApiResponse<Canteen>> fetchCanteens() {
        return apiService.getCanteens();
    }

    public Call<ApiResponse<News>> fetchNews() {
        return apiService.getNews();
    }

    public Call<ApiResponse<Meal>> fetchMeals(String canteenId) {
        return apiService.getMeals(canteenId);
    }


}
