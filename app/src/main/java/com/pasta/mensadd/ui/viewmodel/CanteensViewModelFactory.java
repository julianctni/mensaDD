package com.pasta.mensadd.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pasta.mensadd.database.repository.CanteenRepository;

public class CanteensViewModelFactory implements ViewModelProvider.Factory {

    private CanteenRepository mCanteenRepository;

    public CanteensViewModelFactory(CanteenRepository canteenRepository) {
        mCanteenRepository = canteenRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CanteensViewModel(mCanteenRepository);
    }
}
