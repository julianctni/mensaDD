package com.pasta.mensadd.database.repository;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    private CanteenDao mCanteenDao;
    private LiveData<List<Canteen>> mCanteens;
    private NetworkController mNetworkController;
    private MutableLiveData<Boolean> mIsRefreshing;
    private SharedPreferences mSharedPreferences;
    private AppDatabase mAppDatabase;

    private static final String PREF_LAST_CANTEENS_UPDATE = "lastCanteenUpdate";
    private static final int CANTEEN_UPDATE_INTERVAL = 10 * 60 * 60 * 1000;

    public CanteenRepository(AppDatabase appDatabase, NetworkController networkController, SharedPreferences sharedPreferences) {
        mAppDatabase = appDatabase;
        mCanteenDao = appDatabase.canteenDao();
        mCanteens = mCanteenDao.getAllCanteens();
        mNetworkController = networkController;
        mSharedPreferences = sharedPreferences;
        mIsRefreshing = new MutableLiveData<>();
    }

    public void insertOrUpdateCanteen(Canteen canteen) {
        mAppDatabase.getTransactionExecutor().execute(() -> mCanteenDao.insertOrUpdate(canteen));
    }

    public void updateCanteen(Canteen canteen) {
        mAppDatabase.getTransactionExecutor().execute(() -> mCanteenDao.update(canteen));
    }

    public LiveData<Canteen> getCanteenById(String id) {
        return mCanteenDao.getCanteenByIdAsync(id);
    }

    public LiveData<List<Canteen>> getCanteens() {
        long lastUpdate = mSharedPreferences.getLong(PREF_LAST_CANTEENS_UPDATE, 0);
        if (lastUpdate == 0 || Calendar.getInstance().getTimeInMillis() - lastUpdate > CANTEEN_UPDATE_INTERVAL) {
            refreshCanteens();
        }
        return mCanteens;
    }

    public LiveData<Boolean> isRefreshing() {
        return mIsRefreshing;
    }

    public void refreshCanteens() {
        mIsRefreshing.setValue(true);
        mNetworkController.fetchCanteens((responseType, message) -> {
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

                mSharedPreferences.edit().putLong(PREF_LAST_CANTEENS_UPDATE, Calendar.getInstance().getTimeInMillis()).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mIsRefreshing.setValue(false);
        });
    }
}
