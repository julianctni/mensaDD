package com.pasta.mensadd.features.meallist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.domain.canteen.Canteen;
import com.pasta.mensadd.domain.meal.Meal;
import com.pasta.mensadd.domain.canteen.CanteenRepository;
import com.pasta.mensadd.domain.meal.MealRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealsViewModel extends ViewModel {


    public static final String ARGS_KEY_CANTEEN_ID = "arg_canteen_id";
    private final MealRepository mMealRepository;
    private final CanteenRepository mCanteenRepository;
    private final Map<String, LiveData<List<Meal>>> meals = new HashMap<>();
    private final String mCanteenId;

    public MealsViewModel(MealRepository mealRepository, CanteenRepository canteenRepository, SavedStateHandle savedStateHandle) {
        mMealRepository = mealRepository;
        mCanteenRepository = canteenRepository;
        mCanteenId = savedStateHandle.get(ARGS_KEY_CANTEEN_ID);
    }

    public LiveData<Canteen> getCanteenAsLiveData() {
        return mCanteenRepository.getCanteenById(mCanteenId);
    }

    public void toggleCanteenAsFavorite() {
        mCanteenRepository.toggleCanteenFavorite(mCanteenId);
    }

    public LiveData<List<Meal>> getMealsByDay(String day) {
        if (!meals.containsKey(day)) {
            meals.put(day, mMealRepository.getMealsByCanteenByDay(mCanteenId, day));
        }
        return meals.get(day);
    }

    public LiveData<Integer> getFetchState() {
        return mMealRepository.getFetchState();
    }

    public void triggerMealFetching() {
        mMealRepository.fetchMeals(mCanteenId);
    }

}
