package com.pasta.mensadd.cardcheck;

import com.pasta.mensadd.cardcheck.cardreader.ValueData;
import com.pasta.mensadd.database.entity.BalanceEntry;

public interface OnCardLoadedCallback {
    public void onCardLoadSuccess(BalanceEntry balanceEntry);
    public void onCardLoadError(boolean cardNotSupported);
}
