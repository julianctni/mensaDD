package com.pasta.mensadd.networking.callbacks;

import android.graphics.Bitmap;

public interface LoadImageCallback {
    void onResponseMessage(int responseType, String message, Bitmap bitmap);
}