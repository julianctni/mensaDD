package com.pasta.mensadd.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pasta.mensadd.database.entity.Canteen;

public class MealsViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private Canteen mCanteen;

    public MealsViewModelFactory(Application application, Canteen canteen) {
        mApplication = application;
        mCanteen = canteen;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MealsViewModel(mApplication, mCanteen);
    }
}
