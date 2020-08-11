package com.pasta.mensadd.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.entity.Meal;
import com.pasta.mensadd.database.repository.CanteenRepository;
import com.pasta.mensadd.database.repository.MealRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MealsViewModel extends ViewModel {

    private MealRepository mealRepository;
    private CanteenRepository canteenRepository;
    private Map<String, LiveData<List<Meal>>> meals = new HashMap<>();
    private Canteen mCanteen;

    public MealsViewModel(@NonNull Application application, Canteen canteen) {
        mealRepository = new MealRepository(application, canteen);
        canteenRepository = new CanteenRepository(application);
        mCanteen = canteen;
    }

    public Canteen getCanteen() {
        return mCanteen;
    }

    public LiveData<Canteen> getCanteenAsLiveData() {
        return canteenRepository.getCanteenById(mCanteen.getId());
    }

    public void updateCanteen(Canteen canteen) {
        canteenRepository.update(canteen);
    }

    public LiveData<List<Meal>> getMealsByDay(String day) {
        if (!meals.containsKey(day)) {
            meals.put(day, mealRepository.getMealsByCanteenByDay(mCanteen, day));
        }
        return meals.get(day);
    }

    public LiveData<Boolean> isRefreshing() {
        return mealRepository.isRefreshing();
    }

}
