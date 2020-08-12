package com.pasta.mensadd.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.pasta.mensadd.database.entity.Canteen;

import java.util.List;

@Dao
public interface CanteenDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertCanteen(Canteen canteen);

    @Update
    void updateCanteen(Canteen canteen);

    @Transaction
    default void insertOrUpdateCanteen(Canteen canteen) {
        long id = insertCanteen(canteen);
        if (id == -1l) {
            Canteen c = getCanteenById(canteen.getId());
            canteen.setListPriority(c.getListPriority());
            canteen.setLastMealUpdate(c.getLastMealUpdate());
            canteen.setLastMealScraping((c.getLastMealScraping()));
            updateCanteen(canteen);
        }
    }

    @Query("DELETE FROM table_canteens")
    void deleteAllCanteens();

    @Query("SELECT * FROM table_canteens ORDER BY listPriority DESC")
    LiveData<List<Canteen>> getCanteens();

    @Query("SELECT * FROM table_canteens WHERE id = :canteenId")
    Canteen getCanteenById(String canteenId);

    @Query("SELECT * FROM table_canteens WHERE id = :canteenId")
    LiveData<Canteen> getCanteenByIdAsync(String canteenId);

}
