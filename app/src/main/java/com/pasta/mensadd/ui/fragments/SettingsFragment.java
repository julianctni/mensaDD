package com.pasta.mensadd.ui.fragments;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.view.View;
import android.widget.Toast;

import com.pasta.mensadd.ui.MainActivity;
import com.pasta.mensadd.R;
import com.pasta.mensadd.cardcheck.AutostartRegister;
import com.pasta.mensadd.ui.FragmentController;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.preferences);
        findPreference(getString(R.string.pref_reset_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //TODO: Implement app reset
                //DatabaseController dbController = new DatabaseController(getContext());
                //dbController.deleteAllData();
                Toast.makeText(getContext(), getResources().getString(R.string.delete_data), Toast.LENGTH_LONG).show();

                return false;
            }
        });
        findPreference(getString(R.string.pref_imprint)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentController.showImprintFragment(getFragmentManager());
                //MainActivity.updateToolbar(-1, getString(R.string.pref_imprint));
                return false;
            }
        });

        findPreference(getString(R.string.pref_autostart_key)).setVisible(MainActivity.NFC_SUPPORTED);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDivider(new ColorDrawable(Color.TRANSPARENT));
        setDividerHeight(0);
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null)
            activity.updateToolbar(-1, getString(R.string.nav_settings));
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_autostart_key)) && getActivity() != null) {
            AutostartRegister.register(getActivity().getPackageManager(), sharedPreferences.getBoolean(key, false));
        } else if (key.equals(getString(R.string.pref_dark_mode_key))) {
            String value = sharedPreferences.getString(key, getString(R.string.pref_dark_mode_auto));

            if (value.equals(getString(R.string.pref_dark_mode_yes))) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (value.equals(getString(R.string.pref_dark_mode_no))){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (value.equals(getString(R.string.pref_dark_mode_auto))) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        }
    }

    @Override
    public void setDividerHeight(int height) {
        super.setDividerHeight(0);
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
