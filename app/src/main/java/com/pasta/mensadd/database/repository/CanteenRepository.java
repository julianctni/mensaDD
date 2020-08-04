package com.pasta.mensadd.database.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.CanteenDao;
import com.pasta.mensadd.database.entity.Canteen;

import java.util.List;

public class CanteenRepository {

    private CanteenDao canteenDao;
    private LiveData<List<Canteen>> allCanteens;

    public CanteenRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        canteenDao = appDatabase.canteenDao();
        allCanteens = canteenDao.getAllCanteens();
    }

    public void updateOrInsert(Canteen canteen) {
        AppDatabase.dbExecutor.execute(() -> canteenDao.insertOrUpdate(canteen));
    }

    public void update(Canteen canteen) {
        AppDatabase.dbExecutor.execute(() -> canteenDao.update(canteen));
    }

    public LiveData<Canteen> getCanteenById(String id) {
        return canteenDao.getCanteenByIdAsync(id);
    }

    public LiveData<List<Canteen>> getAllCanteens() {
        return allCanteens;
    }

}
