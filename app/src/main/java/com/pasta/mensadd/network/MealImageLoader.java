package com.pasta.mensadd.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MealImageLoader {

    private static final OkHttpClient imageClient = new OkHttpClient();

    public static void fetchImage(String url, LoadImageCallback callback) {
        Request request = new Request.Builder().url(url).build();
        imageClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(Objects.requireNonNull(response.body()).byteStream());
                    new Handler(Looper.getMainLooper()).post(() -> callback.imageLoaded(true, bitmap));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // TODO: Add error handling
            }
        });
    }

    public interface LoadImageCallback {
        void imageLoaded(boolean success, Bitmap bitmap);
    }

}
