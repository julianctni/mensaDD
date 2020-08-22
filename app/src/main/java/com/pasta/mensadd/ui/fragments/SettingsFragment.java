package com.pasta.mensadd.ui.fragments;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.ui.MainActivity;
import com.pasta.mensadd.R;
import com.pasta.mensadd.balancecheck.AutostartRegister;
import com.pasta.mensadd.ui.FragmentController;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.preferences);
        findPreference(getString(R.string.pref_reset_key)).setOnPreferenceClickListener(preference -> {
            AppDatabase appDatabase = AppDatabase.getInstance(requireContext());
            appDatabase.getTransactionExecutor().execute(appDatabase::clearAllTables);
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().clear().apply();
            Toast.makeText(getContext(), getResources().getString(R.string.delete_data), Toast.LENGTH_SHORT).show();
            return false;
        });

        findPreference(getString(R.string.pref_imprint)).setOnPreferenceClickListener(preference -> {
            FragmentController.showImprintFragment(getParentFragmentManager());
            return false;
        });

        boolean nfcSupported = NfcAdapter.getDefaultAdapter(requireContext()) != null;
        findPreference(getString(R.string.pref_autostart_key)).setVisible(nfcSupported);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.fragment_background));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDivider(new ColorDrawable(Color.TRANSPARENT));
        setDividerHeight(0);
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null)
            activity.setToolbarContent(getString(R.string.nav_settings));
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
