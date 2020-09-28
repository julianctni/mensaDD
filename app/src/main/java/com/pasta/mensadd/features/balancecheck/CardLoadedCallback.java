package com.pasta.mensadd.features.balancecheck;

import com.pasta.mensadd.domain.balanceentry.BalanceEntry;

public interface CardLoadedCallback {
    void onCardLoadSuccess(BalanceEntry balanceEntry);
    void onCardLoadError(boolean cardNotSupported);
}
