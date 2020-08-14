package com.pasta.mensadd.cardcheck;

import com.pasta.mensadd.cardcheck.cardreader.ValueData;

public interface OnCardLoadedCallback {
    public void onCardLoadSuccess(ValueData valueData);
    public void onCardLoadError(boolean cardNotSupported);
}
