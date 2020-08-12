package com.pasta.mensadd.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.pasta.mensadd.database.entity.Meal;

import java.util.List;

@Dao
public interface MealDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertMeal(Meal meal);

    @Update
    void updateMeal(Meal meal);

    @Transaction
    default void insertOrUpdateMeal(Meal meal) {
        long id = insertMeal(meal);
        if (id == -1l) {
            updateMeal(meal);
        }
    }

    @Query("SELECT * FROM table_meals WHERE canteenId = :canteenId and date = :day")
    LiveData<List<Meal>> getMealsByCanteenByDay(String canteenId, String day);

}
