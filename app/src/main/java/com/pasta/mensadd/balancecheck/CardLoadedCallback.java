package com.pasta.mensadd.balancecheck;

import com.pasta.mensadd.database.entity.BalanceEntry;

public interface CardLoadedCallback {
    void onCardLoadSuccess(BalanceEntry balanceEntry);
    void onCardLoadError(boolean cardNotSupported);
}
