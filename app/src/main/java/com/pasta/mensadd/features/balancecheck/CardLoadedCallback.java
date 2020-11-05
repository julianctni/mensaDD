package com.pasta.mensadd.features.balancecheck;

import com.pasta.mensadd.domain.balanceentry.BalanceEntry;

public interface CardLoadedCallback {
    void onCardLoadSuccess(float balance, float lastTransaction);
    void onCardLoadError(boolean cardNotSupported);
}
