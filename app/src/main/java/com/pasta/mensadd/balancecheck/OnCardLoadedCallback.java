package com.pasta.mensadd.balancecheck;

import com.pasta.mensadd.database.entity.BalanceEntry;

public interface OnCardLoadedCallback {
    public void onCardLoadSuccess(BalanceEntry balanceEntry);
    public void onCardLoadError(boolean cardNotSupported);
}
