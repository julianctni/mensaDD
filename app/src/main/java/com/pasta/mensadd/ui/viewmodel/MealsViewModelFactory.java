package com.pasta.mensadd.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.repository.CanteenRepository;
import com.pasta.mensadd.database.repository.MealRepository;

public class MealsViewModelFactory implements ViewModelProvider.Factory {

    private MealRepository mMealRepository;
    private CanteenRepository mCanteenRepository;
    private Canteen mCanteen;

    public MealsViewModelFactory(MealRepository mealRepository, CanteenRepository canteenRepository, Canteen canteen) {
        mMealRepository = mealRepository;
        mCanteenRepository = canteenRepository;
        mCanteen = canteen;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MealsViewModel(mMealRepository, mCanteenRepository, mCanteen);
    }
}
