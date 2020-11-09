package com.pasta.mensadd;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class PreferenceService {

    private SharedPreferences mSharedPreferences;
    private Context mContext;

    private static final String LAST_CANTEEN_UPDATE = "last_canteen_update";
    private static final String BACON_FEATURE = "pref_bacon";
    public static final String TRANSFERED_LEGACY_PRIO = "transfered_legacy_prio";
    public static final String SHOW_LATEST_UPDATES = "show_latest_updates_";
    public static final String MAP_SHOW_DIALOG = "map_show_dialog";

    public PreferenceService(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
    }

    public long getLastCanteenUpdate() {
        return mSharedPreferences.getLong(LAST_CANTEEN_UPDATE, 0);
    }

    public void setLastCanteenUpdate(long timestamp) {
        mSharedPreferences.edit().putLong(LAST_CANTEEN_UPDATE, timestamp).apply();
    }

    public boolean isBaconFeatureEnabled() {
        return mSharedPreferences.getBoolean(BACON_FEATURE, false);
    }

    public void setBaconFeatureEnabled(boolean enable) {
        mSharedPreferences.edit().putBoolean(BACON_FEATURE, enable).apply();
    }

    public boolean isGreenVeggieMealsEnabled() {
        return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_veg_meals_key), true);
    }

    public String getDarkModeSetting() {
        return mSharedPreferences.getString(mContext.getString(R.string.pref_dark_mode_key), mContext.getString(R.string.pref_dark_mode_auto));
    }

    public void removePreference(String key) {
        mSharedPreferences.edit().remove(key).apply();
    }

    public boolean isNfcAutostartRegistered() {
        return mSharedPreferences.getBoolean(mContext.getString(R.string.pref_key_autostart_set), false);
    }

    public void setNfcAutostartRegistered(boolean isRegistered) {
        mSharedPreferences.edit().putBoolean(mContext.getString(R.string.pref_key_autostart_set), isRegistered).apply();
    }

    public void setNfcAutostartSetting(boolean enable) {
        mSharedPreferences.edit().putBoolean(mContext.getString(R.string.pref_autostart_key), enable).apply();
    }

    public boolean getBooleanPreference(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    public void setBooleanPreference(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public int getCanteenLegacyPriority(String canteenId) {
        int priority = mSharedPreferences.getInt("priority_" + canteenId, -1);
        mSharedPreferences.edit().remove("priority_" + canteenId).apply();
        return priority;
    }

    public String getMapCenterPref() {
        return mSharedPreferences.getString(mContext.getString(R.string.pref_map_center_key), mContext.getString(R.string.pref_map_center_fav));
    }

    public boolean getMapShowDialogPref() {
        return mSharedPreferences.getBoolean(MAP_SHOW_DIALOG, true);
    }

    public void setMapShowDialogPref(boolean value) {
        mSharedPreferences.edit().putBoolean(MAP_SHOW_DIALOG, value).apply();
    }
}
