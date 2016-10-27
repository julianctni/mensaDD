package com.pasta.mensadd.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.pasta.mensadd.MainActivity;
import com.pasta.mensadd.R;
import com.pasta.mensadd.cardcheck.AutostartRegister;
import com.pasta.mensadd.controller.DatabaseController;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.hideToolbarShadow(false);
        addPreferencesFromResource(R.xml.preferences);
        findPreference(getString(R.string.pref_reset_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DatabaseController dbController = new DatabaseController(getActivity().getApplicationContext());
                dbController.deleteAllData();
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.delete_data), Toast.LENGTH_LONG).show();

                return false;
            }
        });
        findPreference(getString(R.string.pref_autostart_key)).setVisible(MainActivity.NFC_SUPPORTED);
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
