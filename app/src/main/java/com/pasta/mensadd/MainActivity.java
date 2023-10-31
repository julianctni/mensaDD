package com.pasta.mensadd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.telemetry.TelemetryEnabler;
import com.mapbox.mapboxsdk.Mapbox;
import com.pasta.mensadd.features.balancecheck.BalanceCheckService;
import com.pasta.mensadd.features.balancecheck.CardLoadedCallback;
import com.pasta.mensadd.network.ServiceGenerator;

import java.util.Calendar;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class MainActivity extends AppCompatActivity
        implements BottomNavigation.OnMenuItemSelectionListener {

    private boolean isNfcSupported;
    private BottomNavigation mBottomNav;
    private Toolbar mToolbar;
    private BalanceCheckService mBalanceCheckService;
    private PermissionsManager mPermissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Delete old non-room database
        getApplicationContext().deleteDatabase("mensadd.db");
        setContentView(R.layout.activity_main);
        isNfcSupported = NfcAdapter.getDefaultAdapter(this.getApplicationContext()) != null;
        ServiceGenerator.init(getString(R.string.api_base_url), getString(R.string.api_user), getString(R.string.api_key));
        PreferenceService mPreferenceService = new PreferenceService(this);
        String darkMode = mPreferenceService.getDarkModeSetting();

        if (darkMode.equals(getString(R.string.pref_dark_mode_yes))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (darkMode.equals(getString(R.string.pref_dark_mode_no))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (darkMode.equals(getString(R.string.pref_dark_mode_auto))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
        TelemetryEnabler.updateTelemetryState(TelemetryEnabler.State.DISABLED);
        if (Mapbox.getTelemetry() != null) {
            Mapbox.getTelemetry().setUserTelemetryRequestState(false);
        }
        mBottomNav = findViewById(R.id.bottomNav_mainActivity);
        mBottomNav.setMenuItemSelectionListener(this);
        mBottomNav.inflateMenu(isNfcSupported ? R.menu.bottom_menu : R.menu.bottom_menu_no_nfc);

        mToolbar = findViewById(R.id.toolbar_mainActivity);
        setSupportActionBar(mToolbar);
        setToolbarContent("");

        mPreferenceService.removePreference("first_start");
        mPreferenceService.removePreference("pref_show_tut");

        if (getSupportFragmentManager().findFragmentById(R.id.layout_mainActivity_main) == null) {
            FragmentController.showCanteenListFragment(getSupportFragmentManager());
        }
        if (isNfcSupported) {
            mBalanceCheckService = new BalanceCheckService();
            mBalanceCheckService.registerNfcAutostart(this, mPreferenceService);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNfcSupported) {
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
                onNewIntent(getIntent());
            }
            mBalanceCheckService.setUpCardCheck(this);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onMenuItemSelect(int id, int position, boolean b) {

        switch (id) {
            case R.id.nav_mensa -> {
                FragmentController.showCanteenListFragment(getSupportFragmentManager());
                setToolbarContent("");
            }
            case R.id.nav_news -> {
                FragmentController.showNewsFragment(getSupportFragmentManager());
                setToolbarContent(getString(R.string.nav_news));
            }
            case R.id.nav_map -> {
                FragmentController.showMapFragment(getSupportFragmentManager());
                setToolbarContent(getString(R.string.nav_map));
            }
            case R.id.nav_card_history -> {
                FragmentController.showBalanceHistoryFragment(getSupportFragmentManager());
                setToolbarContent(getString(R.string.nav_card_history));
            }
            case R.id.show_preferences -> {
                FragmentController.showSettingsFragment(getSupportFragmentManager());
                setToolbarContent(getString(R.string.nav_settings));
            }
        }
        mToolbar.setNavigationIcon(null);
    }

    @Override
    public void onMenuItemReselect(int id, int position, boolean b) {
    }

    public void setToolbarContent(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            boolean isDecember = Calendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER;
            Drawable logo = AppCompatResources.getDrawable(getApplicationContext(), isDecember ? R.drawable.banner_christmas : R.drawable.banner);
            actionBar.setTitle(title.isEmpty() ? null : title);
            actionBar.setLogo(title.isEmpty() ? logo : null);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.layout_mainActivity_main);
        if (currentFragment == null || currentFragment.getTag() == null) {
            return;
        }
        String currentFragmentTag = currentFragment.getTag();
        if (mBottomNav.getSelectedIndex() == 0) {
            if (currentFragmentTag.equals(FragmentController.TAG_MEAL_WEEK)) {
                setToolbarContent("");
                super.onBackPressed();
            } else {
                this.finishAffinity();
            }
        } else if (mBottomNav.getSelectedIndex() == 1 && currentFragmentTag.equals(FragmentController.TAG_MEAL_WEEK)) {
            setToolbarContent(getString(R.string.nav_map));
            super.onBackPressed();
        } else if (currentFragmentTag.equals(FragmentController.TAG_IMPRINT)) {
            setToolbarContent(getString(R.string.nav_settings));
            super.onBackPressed();
        } else {
            FragmentController.showCanteenListFragment(getSupportFragmentManager());
            setToolbarContent("");
            mBottomNav.setSelectedIndex(0, true);
        }
        mToolbar.setNavigationIcon(null);
        mBottomNav.setExpanded(true, true);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            mBalanceCheckService.loadCard(tag, new CardLoadedCallback() {
                @Override
                public void onCardLoadSuccess(float balance, float lastTransaction) {
                    FragmentController.showBalanceCheckFragment(getSupportFragmentManager(), balance, lastTransaction);
                }

                @Override
                public void onCardLoadError(boolean cardNotSupported) {
                    int messageId = cardNotSupported ? R.string.balance_check_card_not_supported : R.string.balance_check_fail;
                    Toast.makeText(MainActivity.this, getString(messageId), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void requestLocationPermission(PermissionsListener permissionsListener) {
        mPermissionManager = new PermissionsManager(permissionsListener);
        mPermissionManager.requestLocationPermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
