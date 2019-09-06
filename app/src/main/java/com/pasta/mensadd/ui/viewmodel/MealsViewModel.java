package com.pasta.mensadd.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.entity.Meal;
import com.pasta.mensadd.database.repository.MealRepository;

import java.util.List;

public class MealsViewModel extends AndroidViewModel {

    private MealRepository mealRepository;
    private LiveData<List<Meal>> meals;

    public MealsViewModel(@NonNull Application application) {
        super(application);
        mealRepository = new MealRepository(application);
    }

    public LiveData<List<Meal>> getAllMeals() {
        return mealRepository.getAllMeals();
    }

    public LiveData<List<Meal>> getMealsByCanteen(Canteen canteen) {
        if (meals == null) {
            Log.i("MEALS", "meals is null");
            meals = mealRepository.getMealsByCanteen(canteen);
        }
        return meals;
    }

    public void refreshMeals(Canteen canteen) {
        mealRepository.refreshMeals(canteen);
    }

}
