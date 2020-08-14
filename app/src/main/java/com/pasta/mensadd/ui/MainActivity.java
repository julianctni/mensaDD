package com.pasta.mensadd.ui;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.pasta.mensadd.cardcheck.CardCheckService;
import com.pasta.mensadd.cardcheck.OnCardLoadedCallback;
import com.pasta.mensadd.PreferenceService;
import com.pasta.mensadd.R;
import com.pasta.mensadd.Utils;
import com.pasta.mensadd.cardcheck.AutostartRegister;
import com.pasta.mensadd.cardcheck.cardreader.ValueData;
import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.repository.CanteenRepository;
import com.pasta.mensadd.networking.NetworkController;
import com.pasta.mensadd.ui.viewmodel.CanteensViewModel;
import com.pasta.mensadd.ui.viewmodel.CanteensViewModelFactory;

import java.util.Calendar;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class MainActivity extends AppCompatActivity
        implements BottomNavigation.OnMenuItemSelectionListener, View.OnClickListener {

    public static boolean NFC_SUPPORTED;
    private BottomNavigation mBottomNav;
    private RelativeLayout mCardCheckContainer;
    private FloatingActionButton mSaveBalanceButton;
    private FloatingActionButton mHideBalanceButton;
    private TextView mHeadingToolbar;
    private ImageView mAppLogoToolbar;
    private NfcAdapter mNfcAdapter;
    private Toolbar mToolbar;
    private CardCheckService mCardCheckService;

    private float mCardCheckHeight;
    private boolean mCardCheckVisible;

    private PermissionsManager mPermissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceService preferenceService = new PreferenceService(this);
        String darkMode = preferenceService.getDarkModeSetting();

        if (darkMode.equals(getString(R.string.pref_dark_mode_yes))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (darkMode.equals(getString(R.string.pref_dark_mode_no))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (darkMode.equals(getString(R.string.pref_dark_mode_auto))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);
        CanteenRepository canteenRepository = new CanteenRepository(
                AppDatabase.getInstance(this),
                NetworkController.getInstance(this),
                preferenceService
        );
        CanteensViewModelFactory canteensViewModelFactory = new CanteensViewModelFactory(canteenRepository);
        new ViewModelProvider(this, canteensViewModelFactory).get(CanteensViewModel.class);

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
        mSaveBalanceButton.setOnClickListener(this);
        mHideBalanceButton = findViewById(R.id.hideBalanceButton);
        mHideBalanceButton.setOnClickListener(this);

        mCardCheckContainer = findViewById(R.id.cardCheckContainer);
        mCardCheckContainer.setOnClickListener(this);
        mCardCheckHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());


        boolean isDecember = Calendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER;
        mAppLogoToolbar.setImageDrawable(getResources().getDrawable(isDecember ? R.drawable.banner_christmas : R.drawable.banner));

        if (getSupportFragmentManager().findFragmentById(R.id.mainContainer) == null) {
            FragmentController.showCanteenListFragment(getSupportFragmentManager());
        }

        preferenceService.removePreference("first_start");
        preferenceService.removePreference("pref_show_tut");

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this.getApplicationContext());
        NFC_SUPPORTED = (mNfcAdapter != null);

        if (NFC_SUPPORTED) {
            mCardCheckService = new CardCheckService(this);
            if (!preferenceService.isNfcAutostartRegistered()) {
                AutostartRegister.register(this.getPackageManager(), true);
                preferenceService.setNfcAutostartRegistered(true);
                preferenceService.setNfcAutostartSetting(true);
            }
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
                onNewIntent(getIntent());
            }
        }

        mBottomNav.inflateMenu(NFC_SUPPORTED ? R.menu.bottom_menu : R.menu.bottom_menu_no_nfc);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NFC_SUPPORTED) {
            mCardCheckService.setUpCardCheck(mNfcAdapter);
        }
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
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        if (currentFragment == null || currentFragment.getTag() == null) {
            return;
        }
        String currentFragmentTag = currentFragment.getTag();
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
    public void onClick(View v) {

        Animation animation = Utils.getViewHeightAnimation(mCardCheckContainer, (int) mCardCheckHeight, 0, 150);
        if (v.getId() == R.id.saveBalanceButton) {
            mCardCheckService.storeCardData((hasSavedCardData) -> {
                int messageId = hasSavedCardData ? R.string.balance_saved : R.string.balance_already_saved;
                Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
            });
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
            mCardCheckService.loadCard(tag, new OnCardLoadedCallback() {
                @Override
                public void onCardLoadSuccess(ValueData valueData) {
                    updateCardCheckFragment(valueData);
                }

                @Override
                public void onCardLoadError(boolean cardNotSupported) {
                    int messageId = cardNotSupported ? R.string.balance_check_card_not_supported : R.string.balance_check_fail;
                    Toast.makeText(MainActivity.this, getString(messageId), Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private void updateCardCheckFragment(ValueData value) {
        if (!mCardCheckVisible) {
            FragmentController.showBalanceCheckFragment(getSupportFragmentManager(), mCardCheckService.moneyStr(value.value), mCardCheckService.moneyStr(value.lastTransaction));
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
            FragmentController.updateBalanceCheckFragment(getSupportFragmentManager(), mCardCheckService.moneyStr(value.value), mCardCheckService.moneyStr(value.lastTransaction));
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
    }

}
