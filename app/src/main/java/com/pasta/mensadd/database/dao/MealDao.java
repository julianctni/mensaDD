package com.pasta.mensadd.database.dao;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.pasta.mensadd.database.entity.Meal;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface MealDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertMeal(Meal meal);

    @Update
    void updateMeal(Meal meal);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertMeals(List<Meal> meals);

    @Update
    void updateMeals(List<Meal> meals);

    @Transaction
    default void insertOrUpdateMeal(Meal meal) {
        long id = insertMeal(meal);
        if (id == -1l) {
            updateMeal(meal);
        }
    }

    @Query("DELETE FROM table_meals WHERE id IN (:mealIds)")
    void removeDeprecatedMeals(List<String> mealIds);

    @Transaction
    default void insertOrUpdateMeals(List<Meal> meals) {
        List<Long> insertResults = insertMeals(meals);
        List<Meal> updateList = new ArrayList<>();
        for (int i = 0; i < insertResults.size(); i++) {
            if (insertResults.get(i) == -1l) {
                updateList.add(meals.get(i));
            }
        }
        if (!updateList.isEmpty()) {
            updateMeals(updateList);
        }
        List<String> mealIds = getMealIdsByCanteen(meals.get(0).getCanteenId());
        for (Meal meal : meals) {
            mealIds.remove(meal.getId());
        }
        removeDeprecatedMeals(mealIds);
    }

    @Query("SELECT * FROM table_meals WHERE canteenId = :canteenId and day = :day")
    LiveData<List<Meal>> getMealsByCanteenByDay(String canteenId, String day);

    @Query("SELECT id FROM table_meals WHERE canteenId = :canteenId")
    List<String> getMealIdsByCanteen(String canteenId);
}
