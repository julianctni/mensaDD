package com.pasta.mensadd.features.meallist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.savedstate.SavedStateRegistryOwner;

import com.pasta.mensadd.domain.canteen.CanteenRepository;
import com.pasta.mensadd.domain.meal.MealRepository;

public class MealsViewModelFactory extends AbstractSavedStateViewModelFactory {

    private final MealRepository mMealRepository;
    private final CanteenRepository mCanteenRepository;

    public MealsViewModelFactory(SavedStateRegistryOwner savedStateRegistryOwner, Bundle bundle, MealRepository mealRepository, CanteenRepository canteenRepository) {
        super(savedStateRegistryOwner, bundle);
        mMealRepository = mealRepository;
        mCanteenRepository = canteenRepository;
    }


    @NonNull
    @Override
    protected <T extends ViewModel> T create(@NonNull String key, @NonNull Class<T> modelClass, @NonNull SavedStateHandle handle) {
        return (T) new MealsViewModel(mMealRepository, mCanteenRepository, handle);
    }
}
