package com.pasta.mensadd.domain.balanceentry;

import androidx.lifecycle.LiveData;

import com.pasta.mensadd.AppDatabase;

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
