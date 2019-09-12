package com.pasta.mensadd.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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

public class MealsViewModel extends AndroidViewModel {

    private MealRepository mealRepository;
    private CanteenRepository canteenRepository;
    private Map<String, LiveData<List<Meal>>> meals = new HashMap<>();
    private static boolean isRefreshing;
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);


    public MealsViewModel(@NonNull Application application) {
        super(application);
        mealRepository = new MealRepository(application);
        canteenRepository = new CanteenRepository(application);
    }

    public LiveData<List<Meal>> getMealsByCanteenByDay(Canteen canteen, String day) {
        if (!meals.containsKey(day)) {
            meals.put(day, mealRepository.getMealsByCanteenByDay(canteen, day));
        }
        return meals.get(day);
    }

    public LiveData<Canteen> getCanteenById(String id) {
        return canteenRepository.getCanteenById(id);
    }

    public void refreshMeals(final Canteen canteen) {
        if (canteen.getLastMealUpdate() < Calendar.getInstance().getTimeInMillis() - 600000) {
            isRefreshing = true;
            mealRepository.refreshMeals(canteen, (responseType, message) -> {
                try {
                    JSONObject data = new JSONObject(message);
                    JSONArray mealDays = data.getJSONArray("meals");
                    long lastScraping = data.getLong("timestamp");
                    Date day = new Date();
                    List<Meal> mealList = new ArrayList<>();
                    for (int i = 0; i < mealDays.length(); i++) {
                        Log.i("Loading meals in repo", "Parsing day " + DATE_FORMAT.format(day));
                        JSONObject mealDay = mealDays.getJSONObject(i);
                        JSONArray meals = mealDay.getJSONArray(DATE_FORMAT.format(day));
                        if (meals.length() == 0)
                            Log.i("Loading meals", "No meals");
                        else
                            Log.i("Loading meals", meals.length() + " meals");
                        for (int j = 0; j < meals.length(); j++) {
                            JSONObject jsonMeal = meals.getJSONObject(j);
                            boolean vegan = jsonMeal.getInt("vegan") == 1;
                            boolean vegetarian = jsonMeal.getInt("vegetarian") == 1;
                            boolean beef = jsonMeal.getInt("beef") == 1;
                            boolean pork = jsonMeal.getInt("pork") == 1;
                            boolean garlic = jsonMeal.getInt("garlic") == 1;
                            boolean alc = jsonMeal.getInt("alcohol") == 1;
                            String id = jsonMeal.getString("id");
                            String imgLink = jsonMeal.getString("imgLink");
                            String details = jsonMeal.getString("details");
                            String location = jsonMeal.getString("location");
                            String name = jsonMeal.getString("name");
                            String price = jsonMeal.getString("price");
                            String date = String.valueOf(mealDay.keys().next());
                            String canteenId = canteen.getId();
                            Meal meal = new Meal(id, name, price, details, imgLink, canteenId, date, location, vegetarian, vegan, pork, beef, garlic, alc);
                            mealList.add(meal);
                            mealRepository.insertOrUpdateMeal(meal);
                        }
                        day.setTime(day.getTime() + 86400000);
                    }

                    canteen.setLastMealUpdate(Calendar.getInstance().getTimeInMillis());
                    canteen.setLastMealScraping(lastScraping);
                    canteenRepository.update(canteen);
                    isRefreshing = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

}
