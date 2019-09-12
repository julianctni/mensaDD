package com.pasta.mensadd.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pasta.mensadd.database.entity.Canteen;

import java.util.List;

@Dao
public interface CanteenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Canteen canteen);

    @Update
    void update(Canteen canteen);

    @Delete
    void delete(Canteen canteen);

    @Query("DELETE FROM table_canteens")
    void deleteAllCanteens();

    @Query("SELECT * FROM table_canteens ORDER BY listPriority DESC")
    LiveData<List<Canteen>> getAllCanteens();

    @Query("SELECT * FROM table_canteens WHERE id = :canteenId")
    Canteen getCanteenById(String canteenId);

    @Query("SELECT * FROM table_canteens WHERE id = :canteenId")
    LiveData<Canteen> getCanteenByIdAsync(String canteenId);

}
