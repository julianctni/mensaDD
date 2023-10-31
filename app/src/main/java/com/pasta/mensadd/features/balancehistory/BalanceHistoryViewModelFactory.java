package com.pasta.mensadd.features.balancehistory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pasta.mensadd.domain.balanceentry.BalanceEntryRepository;

public class BalanceHistoryViewModelFactory implements ViewModelProvider.Factory {

    private final BalanceEntryRepository mBalanceEntryRepository;

    public BalanceHistoryViewModelFactory(BalanceEntryRepository balanceEntryRepository) {
        mBalanceEntryRepository = balanceEntryRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BalanceHistoryViewModel(mBalanceEntryRepository);
    }
}
