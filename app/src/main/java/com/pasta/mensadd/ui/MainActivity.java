package com.pasta.mensadd.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;

import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.pasta.mensadd.R;
import com.pasta.mensadd.Utils;
import com.pasta.mensadd.cardcheck.AutostartRegister;
import com.pasta.mensadd.cardcheck.card.desfire.DesfireException;
import com.pasta.mensadd.cardcheck.card.desfire.DesfireProtocol;
import com.pasta.mensadd.cardcheck.cardreader.Readers;
import com.pasta.mensadd.cardcheck.cardreader.ValueData;
import com.pasta.mensadd.database.entity.BalanceEntry;
import com.pasta.mensadd.database.repository.BalanceEntryRepository;
import com.pasta.mensadd.ui.fragments.BalanceHistoryFragment;
import com.pasta.mensadd.ui.viewmodel.CanteensViewModel;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class MainActivity extends AppCompatActivity
        implements BottomNavigation.OnMenuItemSelectionListener, View.OnClickListener {

    public static boolean NFC_SUPPORTED = false;
    private BottomNavigation mBottomNav;
    private RelativeLayout mCardCheckContainer;
    private FloatingActionButton mSaveBalanceButton;
    private FloatingActionButton mHideBalanceButton;
    private TextView mHeadingToolbar;
    private ImageView mAppLogoToolbar;
    private NfcAdapter mNfcAdapter;
    private ValueData mCurrentValueData;
    private Toolbar mToolbar;

    private float mCardCheckHeight;
    private boolean mCardCheckVisible;

    private PermissionsManager mPermissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String darkMode = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.pref_dark_mode_key), getString(R.string.pref_dark_mode_auto));

        if (darkMode.equals(getString(R.string.pref_dark_mode_yes))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (darkMode.equals(getString(R.string.pref_dark_mode_no))){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (darkMode.equals(getString(R.string.pref_dark_mode_auto))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);

        new ViewModelProvider(this).get(CanteensViewModel.class);

        mBottomNav = findViewById(R.id.bottomNavigation);
        mBottomNav.setMenuItemSelectionListener(this);
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(null);
            }
        }

        mHeadingToolbar = findViewById(R.id.heading_toolbar);
        mAppLogoToolbar = findViewById(R.id.toolbarImage);
        mSaveBalanceButton = findViewById(R.id.saveBalanceButton);
        //mSaveBalanceButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cyan_dark)));
        mSaveBalanceButton.setOnClickListener(this);
        mHideBalanceButton = findViewById(R.id.hideBalanceButton);
        //mHideBalanceButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CC3C51")));
        mHideBalanceButton.setOnClickListener(this);

        mCardCheckContainer = findViewById(R.id.cardCheckContainer);
        mCardCheckContainer.setOnClickListener(this);
        mCardCheckHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());


        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.MONTH) == 11) {
            mAppLogoToolbar.setImageDrawable(getResources().getDrawable(R.drawable.banner_christmas));
        } else {
            mAppLogoToolbar.setImageDrawable(getResources().getDrawable(R.drawable.banner));
        }

        if (getSupportFragmentManager().findFragmentById(R.id.mainContainer) == null) {
            FragmentController.showCanteenListFragment(getSupportFragmentManager());
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        sharedPref.edit().remove("first_start").apply();
        sharedPref.edit().remove("pref_show_tut").apply();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this.getApplicationContext());

        NFC_SUPPORTED = (mNfcAdapter != null);

        if (NFC_SUPPORTED && !sharedPref.getBoolean(getString(R.string.pref_key_autostart_set), false)) {
            AutostartRegister.register(this.getPackageManager(), true);
            sharedPref.edit().putBoolean(getString(R.string.pref_key_autostart_set), true).apply();
            sharedPref.edit().putBoolean(getString(R.string.pref_autostart_key), true).apply();
        }
        if (!NFC_SUPPORTED) {
            mBottomNav.inflateMenu(R.menu.bottom_menu);
        } else {
            mBottomNav.inflateMenu(R.menu.bottom_menu);
        }

        if (NFC_SUPPORTED && NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            onNewIntent(getIntent());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null)
            setUpCardCheck();
    }

    @Override
    public void onMenuItemSelect(int id, int position, boolean b) {

        switch (id) {
            case R.id.nav_mensa:
                FragmentController.showCanteenListFragment(getSupportFragmentManager());
                updateToolbar(id, "");
                break;
            case R.id.nav_news:
                FragmentController.showNewsFragment(getSupportFragmentManager());
                updateToolbar(id, getString(R.string.nav_news));
                break;
            case R.id.nav_map:
                FragmentController.showMapFragment(getSupportFragmentManager());
                updateToolbar(id, getString(R.string.nav_map));
                break;
            case R.id.nav_card_history:
                FragmentController.showBalanceHistoryFragment(getSupportFragmentManager());
                updateToolbar(id, getString(R.string.nav_card_history));
                break;
            case R.id.show_preferences:
                FragmentController.showSettingsFragment(getSupportFragmentManager());
                updateToolbar(id, getString(R.string.nav_settings));
                break;
        }
        mToolbar.setNavigationIcon(null);
    }

    @Override
    public void onMenuItemReselect(int id, int position, boolean b) {
    }

    public void updateToolbar(int id, String title) {
        if (id == R.id.nav_mensa) {
            mAppLogoToolbar.setVisibility(View.VISIBLE);
            mHeadingToolbar.setVisibility(View.GONE);
        } else {
            mAppLogoToolbar.setVisibility(View.GONE);
            mHeadingToolbar.setText(title);
            mHeadingToolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        String currentFragmentTag = getSupportFragmentManager().findFragmentById(R.id.mainContainer).getTag();
        if (mBottomNav.getSelectedIndex() == 0) {
            if (currentFragmentTag.equals(FragmentController.TAG_MEAL_WEEK)) {
                updateToolbar(R.id.nav_mensa, "");
                super.onBackPressed();
            } else {
                this.finishAffinity();
            }
        } else if (mBottomNav.getSelectedIndex() == 1 && currentFragmentTag.equals(FragmentController.TAG_MEAL_WEEK)) {
            updateToolbar(R.id.nav_map, getString(R.string.nav_map));
            super.onBackPressed();
        } else if (currentFragmentTag.equals(FragmentController.TAG_IMPRINT)) {
            updateToolbar(R.id.show_preferences, getString(R.string.nav_settings));
            super.onBackPressed();
        } else {
            FragmentController.showCanteenListFragment(getSupportFragmentManager());
            updateToolbar(R.id.nav_mensa, "");
            mBottomNav.setSelectedIndex(0, true);
        }
        mToolbar.setNavigationIcon(null);
        mBottomNav.setExpanded(true, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onClick(View v) {

        Animation animation = Utils.getViewHeightAnimation(mCardCheckContainer, (int) mCardCheckHeight, 0, 150);
        if (v.getId() == R.id.saveBalanceButton) {
            storeCardData();
        }
        if (v.getId() == R.id.saveBalanceButton || v.getId() == R.id.hideBalanceButton) {
            mCardCheckContainer.setAnimation(animation);
            mCardCheckContainer.startAnimation(animation);
            mCardCheckVisible = false;
            ScaleAnimation hideAnim = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            hideAnim.setDuration(150);
            hideAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mSaveBalanceButton.hide();
                    mHideBalanceButton.hide();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mSaveBalanceButton.startAnimation(hideAnim);
            mHideBalanceButton.startAnimation(hideAnim);

        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            loadCard(tag);
        }
    }


    private String moneyStr(int i) {
        int euros = i / 1000;
        int cents = i / 10 % 100;
        String centsStr = Integer.toString(cents);
        if (cents < 10)
            centsStr = "0" + centsStr;
        return euros + "," + centsStr + "\u20AC";
    }

    private void storeCardData() {
        float cardBalance = (float) mCurrentValueData.value / 1000;
        float lastTransaction = (float) mCurrentValueData.lastTransaction / 1000;
        BalanceEntryRepository balanceEntryRepository = new BalanceEntryRepository(getApplication());
        balanceEntryRepository.getLatestBalanceEntry().observe(this, balanceEntry -> {
            if (balanceEntry == null || balanceEntry.getCardBalance() != cardBalance || balanceEntry.getLastTransaction() != lastTransaction) {
                balanceEntryRepository.insert(new BalanceEntry(new Date().getTime(), cardBalance, lastTransaction));
                Toast.makeText(this.getApplicationContext(), getString(R.string.balance_saved), Toast.LENGTH_SHORT).show();
                BalanceHistoryFragment fragment = (BalanceHistoryFragment) getSupportFragmentManager().findFragmentByTag(FragmentController.TAG_BALANCE_HISTORY);
            } else {
                Toast.makeText(this.getApplicationContext(), getString(R.string.balance_already_saved), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateCardCheckFragment(ValueData value) {

        mCurrentValueData = value;
        if (!mCardCheckVisible) {
            FragmentController.showBalanceCheckFragment(getSupportFragmentManager(), moneyStr(value.value), moneyStr(value.lastTransaction));
            ScaleAnimation showAnim = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            showAnim.setDuration(250);
            showAnim.setStartOffset(100);
            mSaveBalanceButton.show();
            mSaveBalanceButton.startAnimation(showAnim);
            mHideBalanceButton.show();
            mHideBalanceButton.startAnimation(showAnim);
            Animation animation = Utils.getViewHeightAnimation(mCardCheckContainer, 0, (int) mCardCheckHeight, 200);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mCardCheckContainer.setAnimation(animation);
            mCardCheckContainer.startAnimation(animation);
            mCardCheckVisible = true;
        } else {
            FragmentController.updateBalanceCheckFragment(getSupportFragmentManager(), moneyStr(value.value), moneyStr(value.lastTransaction));
        }
    }

    private void loadCard(Tag tag) {
        IsoDep tech = IsoDep.get(tag);
        try {
            tech.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            DesfireProtocol desfireTag = new DesfireProtocol(tech);
            ValueData value = Readers.getInstance().readCard(desfireTag);
            if (value != null)
                updateCardCheckFragment(value);
            else
                Toast.makeText(this, getString(R.string.balance_check_card_not_supported), Toast.LENGTH_LONG).show();
            tech.close();
        } catch (DesfireException ex) {
            ex.printStackTrace();
            Toast.makeText(this, getString(R.string.balance_check_fail), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUpCardCheck() {
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tech = new IntentFilter(
                NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] mFilters = new IntentFilter[]{tech,};
        String[][] mTechLists = new String[][]{new String[]{
                IsoDep.class.getName(), NfcA.class.getName()}};
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
    }

    public void requestLocationPermission(PermissionsListener permissionsListener){
        mPermissionManager = new PermissionsManager(permissionsListener);
        mPermissionManager.requestLocationPermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
