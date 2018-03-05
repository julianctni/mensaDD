package com.pasta.mensadd.networking.callbacks;

public interface AbstractCallback {
    void onResponseMessage(int responseType, String message);
}