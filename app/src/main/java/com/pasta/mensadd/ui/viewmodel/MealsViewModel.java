package com.pasta.mensadd.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.entity.Meal;
import com.pasta.mensadd.database.repository.CanteenRepository;
import com.pasta.mensadd.database.repository.MealRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealsViewModel extends ViewModel {

    private MealRepository mMealRepository;
    private CanteenRepository mCanteenRepository;
    private Map<String, LiveData<List<Meal>>> meals = new HashMap<>();
    private Canteen mCanteen;

    public MealsViewModel(MealRepository mealRepository, CanteenRepository canteenRepository, Canteen canteen) {
        mMealRepository = mealRepository;
        mCanteenRepository = canteenRepository;
        mCanteen = canteen;
    }

    public Canteen getCanteen() {
        return mCanteen;
    }

    public LiveData<Canteen> getCanteenAsLiveData() {
        return mCanteenRepository.getCanteenById(mCanteen.getId());
    }

    public void updateCanteen(Canteen canteen) {
        mCanteenRepository.updateCanteen(canteen);
    }

    public LiveData<List<Meal>> getMealsByDay(String day) {
        if (!meals.containsKey(day)) {
            meals.put(day, mMealRepository.getMealsByCanteenByDay(mCanteen, day));
        }
        return meals.get(day);
    }

    public LiveData<Boolean> isRefreshing() {
        return mMealRepository.isRefreshing();
    }

}
