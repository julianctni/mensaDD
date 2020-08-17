package com.pasta.mensadd.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pasta.mensadd.database.repository.BalanceEntryRepository;

public class BalanceCheckViewModelFactory implements ViewModelProvider.Factory {

    private BalanceEntryRepository mBalanceEntryRepository;

    public BalanceCheckViewModelFactory(BalanceEntryRepository balanceEntryRepository) {
        mBalanceEntryRepository = balanceEntryRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BalanceCheckViewModel(mBalanceEntryRepository);
    }
}
