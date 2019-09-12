package com.pasta.mensadd.ui.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.repository.CanteenRepository;
import com.pasta.mensadd.networking.NetworkController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class CanteensViewModel extends AndroidViewModel {

    private CanteenRepository canteenRepository;
    private LiveData<List<Canteen>> canteens;
    private NetworkController network;
    private Canteen selectedCanteen;
    private SharedPreferences prefs;
    private boolean isRefreshing;
    private static final int CANTEEN_UPDATE_INTERVAL = 86400000;
    private static final String PREF_LAST_CANTEENS_UPDATE = "lastCanteenUpdate";

    public CanteensViewModel(@NonNull Application application) {
        super(application);
        canteenRepository = new CanteenRepository(application);
        canteens = canteenRepository.getAllCanteens();
        prefs = PreferenceManager.getDefaultSharedPreferences(application);
        network = NetworkController.getInstance(application);
    }

    public void updateCanteen(Canteen canteen) {
        canteenRepository.update(canteen);
    }

    public void updateOrInsertCanteen(Canteen canteen) { canteenRepository.updateOrInsert(canteen); }

    public LiveData<List<Canteen>> getAllCanteens() {
        long lastUpdate = prefs.getLong(PREF_LAST_CANTEENS_UPDATE, 0);
        if (lastUpdate == 0 || Calendar.getInstance().getTimeInMillis() - lastUpdate > CANTEEN_UPDATE_INTERVAL) {
            refreshCanteens();
        }
        return canteens;
    }

    public Canteen getSelectedCanteen() {
        return selectedCanteen;
    }

    public void setSelectedCanteen(Canteen selectedCanteen) {
        this.selectedCanteen = selectedCanteen;
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void refreshCanteens() {
        network.fetchCanteens((responseType, message) -> {
            isRefreshing = true;
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
                    updateOrInsertCanteen(m);
                }

                prefs.edit().putLong(PREF_LAST_CANTEENS_UPDATE, Calendar.getInstance().getTimeInMillis()).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isRefreshing = false;
        });
    }
}
