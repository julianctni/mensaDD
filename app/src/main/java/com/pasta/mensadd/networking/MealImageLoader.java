package com.pasta.mensadd.networking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;

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
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    new Handler(Looper.getMainLooper()).post(() -> callback.imageLoaded(true, bitmap));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // TODO: Add error handling
            }
        });
    }

    public interface LoadImageCallback {
        void imageLoaded(boolean success, Bitmap bitmap);
    }

}
