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
        new UpdateOrInsertAsyncTask(canteenDao).execute(canteen);
    }

    public void update(Canteen canteen) {
        new UpdateCanteensAsyncTask(canteenDao).execute(canteen);
    }

    public LiveData<Canteen> getCanteenById(String id) {
        return canteenDao.getCanteenByIdAsync(id);
    }

    public LiveData<List<Canteen>> getAllCanteens() {
        return allCanteens;
    }

    private static class UpdateCanteensAsyncTask extends AsyncTask<Canteen, Void, Void> {
        private CanteenDao canteenDao;

        private UpdateCanteensAsyncTask(CanteenDao canteenDao) {
            this.canteenDao = canteenDao;
        }

        @Override
        protected Void doInBackground(Canteen... canteens) {
            canteenDao.update(canteens[0]);
            return null;
        }
    }

    private static class UpdateOrInsertAsyncTask extends AsyncTask<Canteen, Void, Void> {
        private CanteenDao canteenDao;

        private UpdateOrInsertAsyncTask(CanteenDao canteenDao) {
            this.canteenDao = canteenDao;
        }

        @Override
        protected Void doInBackground(Canteen... canteens) {
            Canteen canteen = canteenDao.getCanteenById(canteens[0].getId());
            if (canteen == null) {
                canteenDao.insert(canteens[0]);
            } else {
                canteens[0].setListPriority(canteen.getListPriority());
                canteens[0].setLastMealUpdate(canteen.getLastMealUpdate());
                canteens[0].setLastMealScraping((canteen.getLastMealScraping()));
                canteenDao.update(canteens[0]);
            }
            return null;
        }
    }
}
