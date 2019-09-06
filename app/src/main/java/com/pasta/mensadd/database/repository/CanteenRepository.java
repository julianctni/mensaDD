package com.pasta.mensadd.database.repository;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.CanteenDao;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.networking.NetworkController;
import com.pasta.mensadd.networking.callbacks.LoadCanteensCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class CanteenRepository {

    private CanteenDao canteenDao;
    private LiveData<List<Canteen>> allCanteens;
    private NetworkController network;
    private SharedPreferences sharedPrefs;
    private static final int CANTEEN_UPDATE_INTERVAL = 86400000;
    private static final String PREF_LAST_CANTEENS_UPDATE = "lastCanteenUpdate";

    public CanteenRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        canteenDao = appDatabase.canteenDao();
        allCanteens = canteenDao.getAllCanteens();
        network = NetworkController.getInstance(application);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(application);
    }

    public void insert(Canteen canteen) {
        new InsertCanteenAsyncTask(canteenDao).execute(canteen);
    }

    public void updateOrInsert(Canteen canteen) { new UpdateOrInsertAsyncTask(canteenDao).execute(canteen); }

    public void update(Canteen canteen) {
        new UpdateCanteensAsyncTask(canteenDao).execute(canteen);
    }

    public void delete(Canteen canteen) {
        new DeleteCanteenAsyncTask(canteenDao).execute(canteen);
    }

    public void deleteAllCanteens() {
        new DeleteAllCanteensAsyncTask(canteenDao).execute();
    }


    public LiveData<List<Canteen>> getAllCanteens() {
        long lastUpdate = sharedPrefs.getLong(PREF_LAST_CANTEENS_UPDATE, 0);
        if (lastUpdate == 0 || Calendar.getInstance().getTimeInMillis() - lastUpdate > CANTEEN_UPDATE_INTERVAL) {
            refreshCanteens();
        }
        return allCanteens;
    }

    public void refreshCanteens() {
        network.fetchCanteens(new LoadCanteensCallback() {
            @Override
            public void onResponseMessage(int responseType, String message) {
                try {
                    JSONArray json = new JSONObject(message).getJSONArray("canteens");

                    for (int i = 0; i < json.length(); i++) {
                        JSONObject jsonCanteen = json.getJSONObject(i);
                        String name = jsonCanteen.getString("name");
                        String code = jsonCanteen.getString("code");
                        String address = jsonCanteen.getString("address");
                        int priority = jsonCanteen.getInt("priority");
                        JSONArray gpsArray = jsonCanteen.getJSONArray("coordinates");
                        Log.i("Parsing canteens", name);
                        JSONArray hourArray = jsonCanteen.getJSONArray("hours");
                        StringBuilder hours = new StringBuilder();
                        for (int j = 0; j < hourArray.length(); j++) {
                            hours.append(hourArray.get(j));
                            if (j < hourArray.length() - 1)
                                hours.append("\n");
                        }
                        double posLat = Double.parseDouble(gpsArray.get(0).toString());
                        double posLong = Double.parseDouble(gpsArray.get(1).toString());
                        Canteen m = new Canteen(code, name, hours.toString(), address, posLat, posLong, priority);
                        updateOrInsert(m);
                    }

                    sharedPrefs.edit().putLong(PREF_LAST_CANTEENS_UPDATE, Calendar.getInstance().getTimeInMillis()).apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static class InsertCanteenAsyncTask extends AsyncTask<Canteen, Void, Void> {
        private CanteenDao canteenDao;
        private InsertCanteenAsyncTask(CanteenDao canteenDao) {
            this.canteenDao = canteenDao;
        }

        @Override
        protected Void doInBackground(Canteen... canteens) {
            canteenDao.insert(canteens[0]);
            return null;
        }
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
                canteenDao.update(canteens[0]);
            }
            return null;
        }
    }

    private static class DeleteCanteenAsyncTask extends AsyncTask<Canteen, Void, Void> {
        private CanteenDao canteenDao;
        private DeleteCanteenAsyncTask(CanteenDao canteenDao) {
            this.canteenDao = canteenDao;
        }

        @Override
        protected Void doInBackground(Canteen... canteens) {
            canteenDao.delete(canteens[0]);
            return null;
        }
    }

    private static class DeleteAllCanteensAsyncTask extends AsyncTask<Void, Void, Void> {
        private CanteenDao canteenDao;
        private DeleteAllCanteensAsyncTask(CanteenDao canteenDao) {
            this.canteenDao = canteenDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            canteenDao.deleteAllCanteens();
            return null;
        }
    }
}
