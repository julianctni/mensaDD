package com.pasta.mensadd.database.repository;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.CanteenDao;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.networking.NetworkController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class CanteenRepository {

    private CanteenDao canteenDao;
    private LiveData<List<Canteen>> allCanteens;
    private NetworkController network;
    private MutableLiveData<Boolean> isRefreshing;
    private SharedPreferences prefs;

    private static final String PREF_LAST_CANTEENS_UPDATE = "lastCanteenUpdate";
    private static final int CANTEEN_UPDATE_INTERVAL = 5000;//86400000;

    public CanteenRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        canteenDao = appDatabase.canteenDao();
        allCanteens = canteenDao.getAllCanteens();
        network = NetworkController.getInstance(application);
        prefs = PreferenceManager.getDefaultSharedPreferences(application);
        isRefreshing = new MutableLiveData<>();
    }

    public void insertOrUpdateCanteen(Canteen canteen) {
        AppDatabase.dbExecutor.execute(() -> canteenDao.insertOrUpdate(canteen));
    }

    public void update(Canteen canteen) {
        AppDatabase.dbExecutor.execute(() -> canteenDao.update(canteen));
    }

    public LiveData<Canteen> getCanteenById(String id) {
        return canteenDao.getCanteenByIdAsync(id);
    }

    public LiveData<List<Canteen>> getAllCanteens() {
        long lastUpdate = prefs.getLong(PREF_LAST_CANTEENS_UPDATE, 0);
        if (lastUpdate == 0 || Calendar.getInstance().getTimeInMillis() - lastUpdate > CANTEEN_UPDATE_INTERVAL) {
            refreshCanteens();
        }
        return allCanteens;
    }

    public LiveData<Boolean> isRefreshing() {
        return isRefreshing;
    }

    public void refreshCanteens() {
        isRefreshing.setValue(true);
        network.fetchCanteens((responseType, message) -> {
            try {
                JSONArray json = new JSONObject(message).getJSONArray("canteens");

                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonCanteen = json.getJSONObject(i);
                    String name = jsonCanteen.getString("name");
                    String code = jsonCanteen.getString("code");
                    String address = jsonCanteen.getString("address");
                    int priority = jsonCanteen.getInt("priority");
                    JSONArray gpsArray = jsonCanteen.getJSONArray("coordinates");
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
                    insertOrUpdateCanteen(m);
                }

                prefs.edit().putLong(PREF_LAST_CANTEENS_UPDATE, Calendar.getInstance().getTimeInMillis()).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isRefreshing.setValue(false);
        });
    }
}
