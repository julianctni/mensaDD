package com.pasta.mensadd.networking;

public interface AbstractCallback {
    void onResponseMessage(int responseType, String message);
}