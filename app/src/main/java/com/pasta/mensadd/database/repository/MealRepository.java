package com.pasta.mensadd.database.repository;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.MealDao;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.entity.Meal;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.networking.NetworkController;
import com.pasta.mensadd.networking.callbacks.LoadMealsCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.pasta.mensadd.controller.ParseController.DATE_FORMAT;

public class MealRepository {

    private MealDao mealDao;
    private NetworkController network;

    public MealRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        mealDao = appDatabase.mealDao();
        network = NetworkController.getInstance(application);
    }

    public void insertMeals(List<Meal> meals) {
        new InsertMealAsyncTask(mealDao).execute(meals);
    }

    public void update(Meal meal) {
        new UpdateMealsAsyncTask(mealDao).execute(meal);
    }

    public void delete(Meal meal) {
        new DeleteMealAsyncTask(mealDao).execute(meal);
    }

    public void deleteMealsByCanteen(Canteen canteen) {
        new DeleteMealsOfCanteenAsyncTask(mealDao).execute(canteen);
    }


    public LiveData<List<Meal>> getMealsByCanteen(Canteen canteen) {
        return mealDao.getMealsByCanteen(canteen.getId());
    }

    public LiveData<List<Meal>> getAllMeals() {
        return mealDao.getAllMeals();
    }

    public void refreshMeals(final Canteen canteen) {
        if (canteen.getLastMealUpdate() < Calendar.getInstance().getTimeInMillis() - 600000) {
            network.fetchMeals(canteen.getId(), new LoadMealsCallback() {
                @Override
                public void onResponseMessage(int responseType, String message) {
                    try {
                        JSONArray mealDays = new JSONObject(message).getJSONArray("meals");
                        deleteMealsByCanteen(canteen);
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
                                int vegan = jsonMeal.getInt("vegan");
                                int vegetarian = jsonMeal.getInt("vegetarian");
                                int beef = jsonMeal.getInt("beef");
                                int pork = jsonMeal.getInt("porc");
                                int garlic = jsonMeal.getInt("garlic");
                                int alcohol = jsonMeal.getInt("alcohol");
                                String id = jsonMeal.getString("mealId");
                                String imgLink = jsonMeal.getString("imgLink");
                                String details = jsonMeal.getString("mealDetails");
                                String location = jsonMeal.getString("mealLocation");
                                String name = jsonMeal.getString("name");
                                String price = jsonMeal.getString("price");
                                Meal meal = new Meal(id, name, price, details, imgLink, canteen.getId(), String
                                        .valueOf(mealDay.keys().next()), location, vegetarian == 1, vegan == 1, pork == 1, beef == 1, garlic == 1, alcohol == 1);
                                mealList.add(meal);
                            }
                            insertMeals(mealList);
                            /*
                            if (DataHolder.getInstance().getCanteen(canteen.getId()).getMealMap().get(String.valueOf
                                    (mealDay.keys().next())) != null) {
                                DataHolder.getInstance().getCanteen(canteen.getId()).getMealMap().get(String.valueOf
                                        (mealDay.keys().next())).clear();
                                DataHolder.getInstance().getCanteen(canteen.getId()).getMealMap().get(String.valueOf
                                        (mealDay.keys().next())).addAll(mealList);
                            } else {
                                DataHolder.getInstance().getCanteen(canteen.getId()).getMealMap().put(String.valueOf
                                        (mealDay.keys().next()), mealList);
                            }*/
                            day.setTime(day.getTime() + 86400000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private static class InsertMealAsyncTask extends AsyncTask<List<Meal>, Void, Void> {
        private MealDao mealDao;
        private InsertMealAsyncTask(MealDao mealDao) {
            this.mealDao = mealDao;
        }

        @Override
        protected Void doInBackground(List<Meal>... meals) {
            mealDao.insertMeals(meals[0]);
            return null;
        }
    }

    private static class UpdateMealsAsyncTask extends AsyncTask<Meal, Void, Void> {
        private MealDao mealDao;
        private UpdateMealsAsyncTask(MealDao mealDao) {
            this.mealDao = mealDao;
        }

        @Override
        protected Void doInBackground(Meal... meals) {
            mealDao.update(meals[0]);
            return null;
        }
    }

    private static class DeleteMealAsyncTask extends AsyncTask<Meal, Void, Void> {
        private MealDao mealDao;
        private DeleteMealAsyncTask(MealDao mealDao) {
            this.mealDao = mealDao;
        }

        @Override
        protected Void doInBackground(Meal... meals) {
            mealDao.delete(meals[0]);
            return null;
        }
    }

    private static class DeleteMealsOfCanteenAsyncTask extends AsyncTask<Canteen, Void, Void> {
        private MealDao mealDao;
        private DeleteMealsOfCanteenAsyncTask(MealDao mealDao) {
            this.mealDao = mealDao;
        }

        @Override
        protected Void doInBackground(Canteen... canteens) {
            mealDao.deleteMealsByCanteen(canteens[0].getId());
            return null;
        }
    }
}
