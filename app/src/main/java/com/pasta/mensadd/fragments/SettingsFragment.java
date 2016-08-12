package com.pasta.mensadd.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.pasta.mensadd.MainActivity;
import com.pasta.mensadd.R;
import com.pasta.mensadd.cardcheck.AutostartRegister;
import com.pasta.mensadd.controller.DatabaseController;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        findPreference("pref_reset_key").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DatabaseController dbController = new DatabaseController(getActivity().getApplicationContext());
                dbController.deleteAllData();
                return false;
            }
        });
        findPreference("nfc_autostart").setVisible(MainActivity.NFC_SUPPORTED);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_autostart_key))){
            AutostartRegister.register(getActivity().getPackageManager(), sharedPreferences.getBoolean(key, false));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
