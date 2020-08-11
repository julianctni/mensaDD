package com.pasta.mensadd.database.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.CanteenDao;
import com.pasta.mensadd.database.dao.MealDao;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.entity.Meal;
import com.pasta.mensadd.networking.NetworkController;
import com.pasta.mensadd.networking.callbacks.LoadMealsCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MealRepository {

    private MealDao mealDao;
    private CanteenDao canteenDao;
    private NetworkController network;
    private static MutableLiveData<Boolean> isRefreshing;

    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);
    public static int FIFTEEN_MINUTES_MILLIS = 15 * 60 * 1000;

    public MealRepository(Application application, Canteen canteen) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        mealDao = appDatabase.mealDao();
        canteenDao = appDatabase.canteenDao();
        network = NetworkController.getInstance(application);
        isRefreshing = new MutableLiveData<>();
        if (canteen.getLastMealUpdate() < Calendar.getInstance().getTimeInMillis() - FIFTEEN_MINUTES_MILLIS) {
            refreshMeals(canteen);
        }
    }

    public void updateCanteen(Canteen canteen) {
        AppDatabase.dbExecutor.execute(() -> canteenDao.update(canteen));
    }

    public void insertOrUpdateMeal(Meal meal) {
        AppDatabase.dbExecutor.execute(() -> mealDao.insertOrUpdate(meal));
    }

    public LiveData<List<Meal>> getMealsByCanteenByDay(Canteen canteen, String day) {
        return mealDao.getMealsByCanteenByDay(canteen.getId(), day);
    }

    public void refreshMeals(final Canteen canteen) {

        isRefreshing.setValue(true);
        network.fetchMeals(canteen.getId(), (responseType, message) -> {

            try {
                JSONObject data = new JSONObject(message);
                JSONArray mealDays = data.getJSONArray("meals");
                long lastScraping = data.getLong("timestamp");
                Date day = new Date();
                List<Meal> mealList = new ArrayList<>();
                for (int i = 0; i < mealDays.length(); i++) {
                    JSONObject mealDay = mealDays.getJSONObject(i);
                    JSONArray meals = mealDay.getJSONArray(DATE_FORMAT.format(day));
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
                        insertOrUpdateMeal(meal);
                    }
                    day.setTime(day.getTime() + 86400000);
                }

                canteen.setLastMealUpdate(Calendar.getInstance().getTimeInMillis());
                canteen.setLastMealScraping(lastScraping);
                updateCanteen(canteen);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isRefreshing.setValue(false);
        });

    }

    public LiveData<Boolean> isRefreshing() {
        return isRefreshing;
    }

}
