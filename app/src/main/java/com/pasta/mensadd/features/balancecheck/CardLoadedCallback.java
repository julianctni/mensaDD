package com.pasta.mensadd.features.balancecheck;

public interface CardLoadedCallback {
    void onCardLoadSuccess(float balance, float lastTransaction);
    void onCardLoadError(boolean cardNotSupported);
}
