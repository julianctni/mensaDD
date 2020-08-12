package com.pasta.mensadd.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.pasta.mensadd.database.entity.BalanceEntry;

import java.util.List;

@Dao
public interface BalanceEntryDao {

    @Insert
    void insertBalanceEntry(BalanceEntry balanceEntry);

    @Query("SELECT * FROM table_balance_entries ORDER BY timestamp ASC")
    LiveData<List<BalanceEntry>> getBalanceEntries();

    @Query("DELETE FROM table_balance_entries WHERE timestamp NOT IN (SELECT timestamp FROM table_balance_entries ORDER BY timestamp DESC LIMIT 14)")
    void deleteDeprecatedBalanceEntries();

    @Query("SELECT * FROM table_balance_entries WHERE timestamp = (SELECT MAX(timestamp) FROM table_balance_entries)")
    LiveData<BalanceEntry> getLatestBalanceEntry();
}
