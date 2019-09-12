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

    @Update
    void update(Meal meal);

    @Query("SELECT * FROM table_meals WHERE canteenId = :canteenId and date = :day")
    LiveData<List<Meal>> getMealsByCanteenByDay(String canteenId, String day);

    @Query("SELECT * FROM table_meals WHERE id = :id")
    Meal getMealById(String id);

}
