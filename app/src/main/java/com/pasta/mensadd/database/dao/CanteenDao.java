package com.pasta.mensadd.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.pasta.mensadd.database.entity.Canteen;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CanteenDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertCanteen(Canteen canteen);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertCanteens(List<Canteen> canteens);

    @Update
    void updateCanteen(Canteen canteen);

    @Update
    void updateCanteens(List<Canteen> canteens);

    @Transaction
    default void insertOrUpdateCanteens(List<Canteen> canteens) {
        List<Long> insertResults = insertCanteens(canteens);
        List<Canteen> updateList = new ArrayList<>();
        for (int i = 0; i < insertResults.size(); i++) {
            if (insertResults.get(i) == -1l) {
                updateList.add(canteens.get(i));
            }
        }
        if (!updateList.isEmpty()) {
            updateCanteens(updateList);
        }
    }

    @Query("DELETE FROM table_canteens")
    void deleteAllCanteens();

    @Query("SELECT * FROM table_canteens ORDER BY priority DESC")
    LiveData<List<Canteen>> getCanteens();

    @Query("SELECT * FROM table_canteens WHERE id = :canteenId")
    Canteen getCanteenByIdSync(String canteenId);

    @Query("SELECT * FROM table_canteens WHERE id = :canteenId")
    LiveData<Canteen> getCanteenById(String canteenId);

    @Query("SELECT lastMealUpdate FROM table_canteens WHERE id = :canteenId")
    long getLastMealUpdate(String canteenId);

}
