package com.pasta.mensadd.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pasta.mensadd.database.repository.BalanceEntryRepository;
import com.pasta.mensadd.database.repository.CanteenRepository;

public class BalanceHistoryViewModelFactory implements ViewModelProvider.Factory {

    private BalanceEntryRepository mBalanceEntryRepository;

    public BalanceHistoryViewModelFactory(BalanceEntryRepository balanceEntryRepository) {
        mBalanceEntryRepository = balanceEntryRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BalanceHistoryViewModel(mBalanceEntryRepository);
    }
}
