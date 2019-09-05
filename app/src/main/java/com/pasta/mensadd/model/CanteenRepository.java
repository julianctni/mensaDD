package com.pasta.mensadd.model;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.pasta.mensadd.Utils;
import com.pasta.mensadd.networking.NetworkController;
import com.pasta.mensadd.networking.callbacks.LoadCanteensCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CanteenRepository {

    private CanteenDao canteenDao;
    private LiveData<List<Canteen>> allCanteens;
    private NetworkController network;

    public CanteenRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        canteenDao = appDatabase.canteenDao();
        allCanteens = canteenDao.getAllCanteens();
        network = NetworkController.getInstance(application);
    }

    public void insert(Canteen canteen) {
        new InsertCanteenAsyncTask(canteenDao).execute(canteen);
    }

    public void update(Canteen canteen) {
        new UpadteCanteenAsyncTask(canteenDao).execute(canteen);
    }

    public void delete(Canteen canteen) {
        new DeleteCanteenAsyncTask(canteenDao).execute(canteen);
    }

    public void deleteAllCanteens() {
        new DeleteAllCanteensAsyncTask(canteenDao).execute();
    }

    public LiveData<List<Canteen>> getAllCanteens() {
        refreshCanteens();
        return allCanteens;
    }

    public void refreshCanteens() {
        network.fetchCanteens(new LoadCanteensCallback() {
            @Override
            public void onResponseMessage(int responseType, String message) {
                try {
                    JSONArray json = new JSONObject(message).getJSONArray("canteens");

                    for (int i = 0; i < json.length(); i++) {
                        JSONObject canteen = json.getJSONObject(i);
                        String name = canteen.getString("name");
                        String code = canteen.getString("code");
                        String address = canteen.getString("address");
                        JSONArray gpsArray = canteen.getJSONArray("coordinates");
                        Log.i("Parsing canteens", name);
                        JSONArray hourArray = canteen.getJSONArray("hours");
                        StringBuilder hours = new StringBuilder();
                        for (int j = 0; j < hourArray.length(); j++) {
                            hours.append(hourArray.get(j));
                            if (j < hourArray.length() - 1)
                                hours.append("\n");
                        }
                        double posLat = Double.parseDouble(gpsArray.get(0).toString());
                        double posLong = Double.parseDouble(gpsArray.get(1).toString());
                        int priority = 0;
                        Canteen m = new Canteen(code, name, hours.toString(), address, posLat, posLong, Utils.calculateCanteenPriority(code, priority));
                        insert(m);
                    }
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

    private static class UpadteCanteenAsyncTask extends AsyncTask<Canteen, Void, Void> {
        private CanteenDao canteenDao;
        private UpadteCanteenAsyncTask(CanteenDao canteenDao) {
            this.canteenDao = canteenDao;
        }

        @Override
        protected Void doInBackground(Canteen... canteens) {
            canteenDao.update(canteens[0]);
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
