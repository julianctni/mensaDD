package com.pasta.mensadd.features.balancecheck;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.savedstate.SavedStateRegistryOwner;

import com.pasta.mensadd.domain.balanceentry.BalanceEntryRepository;

public class BalanceCheckViewModelFactory extends AbstractSavedStateViewModelFactory {

    private final BalanceEntryRepository mBalanceEntryRepository;

    public BalanceCheckViewModelFactory(SavedStateRegistryOwner savedStateRegistryOwner, Bundle bundle,  BalanceEntryRepository balanceEntryRepository) {
        super(savedStateRegistryOwner, bundle);
        mBalanceEntryRepository = balanceEntryRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull String key, @NonNull Class<T> modelClass, @NonNull SavedStateHandle handle) {
        return (T) new BalanceCheckViewModel(mBalanceEntryRepository, handle);
    }
}
