package com.pasta.mensadd.network;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static Retrofit retrofit;

    public static void init(String baseUrl, String user, String apiKey) {
        if (retrofit == null) {
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
            retrofit = retrofitBuilder.build();
        }
    }

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }

}
