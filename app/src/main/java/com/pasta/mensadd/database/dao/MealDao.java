package com.pasta.mensadd.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.entity.Meal;

import java.util.List;
import java.util.Set;

@Dao
public interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Meal meal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMeals(List<Meal> meals);

    @Update
    void update(Meal meal);

    @Delete
    void delete(Meal meal);

    @Query("DELETE FROM table_meals")
    void deleteAllMeals();

    @Query("SELECT * FROM table_meals")
    LiveData<List<Meal>> getAllMeals();

    @Query("SELECT * FROM table_meals WHERE canteenId = :canteenId")
    LiveData<List<Meal>> getMealsByCanteen(String canteenId);

    @Query("DELETE FROM table_meals WHERE canteenId = :canteenId")
    void deleteMealsByCanteen(String canteenId);

}
