package com.pasta.mensadd.database.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.BalanceEntryDao;
import com.pasta.mensadd.database.entity.BalanceEntry;

import java.util.List;

public class BalanceEntryRepository {

    private BalanceEntryDao balanceEntryDao;
    private LiveData<List<BalanceEntry>> balanceEntries;

    public BalanceEntryRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        balanceEntryDao = appDatabase.balanceEntryDao();
        balanceEntries = balanceEntryDao.getAll();
    }

    public void insert(BalanceEntry balanceEntry) {
        new InsertAsyncTask(balanceEntryDao).execute(balanceEntry);
    }

    public LiveData<BalanceEntry> getLatestBalanceEntry() {
        return balanceEntryDao.getLatestBalanceEntry();
    }

    public LiveData<List<BalanceEntry>> getAll() {
        return balanceEntryDao.getAll();
    }

    private static class InsertAsyncTask extends AsyncTask<BalanceEntry, Void, Void> {
        private BalanceEntryDao balanceEntryDao;

        private InsertAsyncTask(BalanceEntryDao balanceEntryDao) {
            this.balanceEntryDao = balanceEntryDao;
        }

        @Override
        protected Void doInBackground(BalanceEntry... balanceEntries) {
            balanceEntryDao.insert(balanceEntries[0]);
            return null;
        }
    }
}
