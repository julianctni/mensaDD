package com.pasta.mensadd.database.repository;

import androidx.lifecycle.LiveData;

import com.pasta.mensadd.cardcheck.OnCardDataStoredCallback;
import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.BalanceEntryDao;
import com.pasta.mensadd.database.entity.BalanceEntry;

import java.util.List;

public class BalanceEntryRepository {

    private BalanceEntryDao mBalanceEntryDao;
    private LiveData<List<BalanceEntry>> mBalanceEntries;
    private AppDatabase mAppDatabase;

    public BalanceEntryRepository(AppDatabase appDatabase) {
        mAppDatabase = appDatabase;
        mBalanceEntryDao = mAppDatabase.balanceEntryDao();
        mBalanceEntries = mBalanceEntryDao.getBalanceEntries();
    }

    public void insertBalanceEntry(BalanceEntry balanceEntry) {
        mAppDatabase.getTransactionExecutor().execute(() -> {
            mBalanceEntryDao.insertBalanceEntry(balanceEntry);
        });
    }

    public LiveData<BalanceEntry> getLatestBalanceEntry() {
        return mBalanceEntryDao.getLatestBalanceEntry();
    }

    public LiveData<List<BalanceEntry>> getBalanceEntries() {
        return mBalanceEntries;
    }
}
