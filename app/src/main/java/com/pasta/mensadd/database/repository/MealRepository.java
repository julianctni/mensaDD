package com.pasta.mensadd.database.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.MealDao;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.entity.Meal;
import com.pasta.mensadd.networking.NetworkController;
import com.pasta.mensadd.networking.callbacks.LoadMealsCallback;

import java.util.List;

public class MealRepository {

    private MealDao mealDao;
    private NetworkController network;

    public MealRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        mealDao = appDatabase.mealDao();
        network = NetworkController.getInstance(application);
    }

    public void insertOrUpdateMeal(Meal meal) {
        new InsertOrUpdateMealTask(mealDao).execute(meal);
    }

    public LiveData<List<Meal>> getMealsByCanteenByDay(Canteen canteen, String day) {
        return mealDao.getMealsByCanteenByDay(canteen.getId(), day);
    }

    public void refreshMeals(final Canteen canteen, LoadMealsCallback callback) {
        network.fetchMeals(canteen.getId(), callback);
    }

    private static class InsertOrUpdateMealTask extends AsyncTask<Meal, Void, Void> {
        private MealDao mealDao;
        private InsertOrUpdateMealTask(MealDao mealDao) {
            this.mealDao = mealDao;
        }

        @Override
        protected Void doInBackground(Meal... meals) {
            Meal meal = mealDao.getMealById(meals[0].getId());
            if (meal == null) {
                mealDao.insert(meals[0]);
            } else {
                mealDao.update(meals[0]);
            }
            return null;
        }
    }
}
