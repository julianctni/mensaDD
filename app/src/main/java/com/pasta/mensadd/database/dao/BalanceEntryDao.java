package com.pasta.mensadd.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pasta.mensadd.database.entity.BalanceEntry;

import java.util.List;

@Dao
public interface BalanceEntryDao {

    @Insert
    void insert(BalanceEntry balanceEntry);

    @Update
    void update(BalanceEntry balanceEntry);

    @Delete
    void delete(BalanceEntry balanceEntry);

    @Query("SELECT * FROM table_balance_entries ORDER BY timestamp ASC")
    LiveData<List<BalanceEntry>> getAll();

    @Query("DELETE FROM table_balance_entries WHERE timestamp NOT IN (SELECT timestamp FROM table_balance_entries ORDER BY timestamp DESC LIMIT 14)")
    void deleteDeprecatedBalanceEntries();

    @Query("SELECT * FROM table_balance_entries WHERE timestamp = (SELECT MAX(timestamp) FROM table_balance_entries)")
    LiveData<BalanceEntry> getLatestBalanceEntry();
}
