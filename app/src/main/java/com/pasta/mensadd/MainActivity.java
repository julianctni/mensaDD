package com.pasta.mensadd;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pasta.mensadd.cardcheck.card.desfire.DesfireException;
import com.pasta.mensadd.cardcheck.card.desfire.DesfireProtocol;
import com.pasta.mensadd.cardcheck.cardreader.Readers;
import com.pasta.mensadd.cardcheck.cardreader.ValueData;
import com.pasta.mensadd.controller.DatabaseController;
import com.pasta.mensadd.controller.FragmentController;
import com.pasta.mensadd.fragments.CanteenListFragment;

import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static boolean NFC_SUPPORTED = false;

    private static NavigationView mNavigationView;
    private RelativeLayout mCardCheckContainer;
    private DrawerLayout mNavDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mHeadingToolbar;
    private ImageView mAppLogoToolbar;
    private FloatingActionButton mSaveBalanceButton;

    private NfcAdapter mNfcAdapter;
    private ValueData mCurrentValueData;

    private float mCardCheckHeight;
    private boolean mCardCheckVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(null);
            }
        }
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mSaveBalanceButton = (FloatingActionButton) findViewById(R.id.saveBalanceButton);
        mSaveBalanceButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff4b66")));
        mSaveBalanceButton.setOnClickListener(this);
        mHeadingToolbar = (TextView) findViewById(R.id.heading_toolbar);
        mAppLogoToolbar = (ImageView) findViewById(R.id.home_button);
        mCardCheckContainer = (RelativeLayout) findViewById(R.id.cardCheckContainer);
        mCardCheckContainer.setOnClickListener(this);
        mCardCheckHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        mNavDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mNavDrawer, toolbar, 0, 0);
        if (mNavDrawer != null) mNavDrawer.addDrawerListener(mDrawerToggle);
        if (mNavigationView != null) mNavigationView.setNavigationItemSelectedListener(this);

        if (getSupportFragmentManager().findFragmentById(R.id.mainContainer) == null) {
            CanteenListFragment fragment = new CanteenListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.mainContainer, fragment, "MensaList").commit();
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this.getApplicationContext());

        NFC_SUPPORTED = (mNfcAdapter != null);
        mNavigationView.getMenu().findItem(R.id.nav_card_history).setVisible(NFC_SUPPORTED);

        if (NFC_SUPPORTED && NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            onNewIntent(getIntent());
        }
    }

    public static void updateNavDrawer(int id){
        mNavigationView.getMenu().findItem(id).setChecked(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mNavDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null)
            setUpCardCheck();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onClick(View v) {
        Animation animation = new ViewHeightAnimation(mCardCheckContainer, (int) mCardCheckHeight, 0);
        if (v.getId() == R.id.cardCheckContainer) {
            mCardCheckContainer.setAnimation(animation);
            mCardCheckContainer.startAnimation(animation);
            mCardCheckVisible = false;

        } else if (v.getId() == R.id.saveBalanceButton) {
            storeCardData();
        }
        if (v.getId() == R.id.saveBalanceButton || v.getId() == R.id.cardCheckContainer) {
            mCardCheckContainer.setAnimation(animation);
            mCardCheckContainer.startAnimation(animation);
            mCardCheckVisible = false;
            ScaleAnimation hideAnim = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            hideAnim.setDuration(200);
            hideAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    mSaveBalanceButton.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            mSaveBalanceButton.startAnimation(hideAnim);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_map:
                FragmentController.showMapFragment(getSupportFragmentManager());
                mAppLogoToolbar.setVisibility(View.GONE);
                mHeadingToolbar.setText(getString(R.string.nav_drawer_map));
                mHeadingToolbar.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_mensa:
                FragmentController.showMensaListFragment(getSupportFragmentManager());
                mAppLogoToolbar.setVisibility(View.VISIBLE);
                mHeadingToolbar.setVisibility(View.GONE);
                break;
            case R.id.nav_card_history:
                FragmentController.showBalanceHistoryFragment(getSupportFragmentManager());
                mAppLogoToolbar.setVisibility(View.GONE);
                mHeadingToolbar.setText(getString(R.string.nav_drawer_card_history));
                mHeadingToolbar.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_settings:
                FragmentController.showSettingsFragment(getSupportFragmentManager());
                mAppLogoToolbar.setVisibility(View.GONE);
                mHeadingToolbar.setText(getString(R.string.nav_drawer_settings));
                mHeadingToolbar.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_imprint:
                FragmentController.showImprintFragment(getSupportFragmentManager());
                mAppLogoToolbar.setVisibility(View.GONE);
                mHeadingToolbar.setText(getString(R.string.nav_drawer_imprint));
                mHeadingToolbar.setVisibility(View.VISIBLE);
                break;
        }

        mNavDrawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            loadCard(tag);
        }
    }


    private String moneyStr(int i) {
        int euros = i / 1000;
        int cents = i/10 % 100;
        String centsStr = Integer.toString(cents);
        if (cents < 10)
            centsStr = "0" + centsStr;
        return euros + "," + centsStr + "\u20AC";
    }


    private void storeCardData(){
        float cardBalance = (float)mCurrentValueData.value/1000;
        float lastTransaction = (float)mCurrentValueData.lastTransaction/1000;
        DatabaseController dbController = new DatabaseController(this.getApplicationContext());
        if (cardBalance != dbController.getLastInsertedBalance()) {
            dbController.updateBalanceTable(new Date().getTime(),cardBalance,lastTransaction);
            Toast.makeText(this.getApplicationContext(), "Guthabenstand wurde gespeichert.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.getApplicationContext(), "Guthabenstand bereits gespeichert.", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateCardCheckFragment(ValueData value) {
        mCurrentValueData = value;
        if (!mCardCheckVisible) {
            FragmentController.showBalanceCheckFragment(getSupportFragmentManager(), moneyStr(value.value), moneyStr(value.lastTransaction));
            ScaleAnimation showAnim = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            showAnim.setDuration(400);
            mSaveBalanceButton.setVisibility(View.VISIBLE);
            mSaveBalanceButton.startAnimation(showAnim);
            Animation animation = new ViewHeightAnimation(mCardCheckContainer, 0, (int) mCardCheckHeight);
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
                Toast.makeText(this, "card_not_supported", Toast.LENGTH_LONG).show();
            tech.close();
        } catch (DesfireException ex) {
            ex.printStackTrace();
            Toast.makeText(this, "communication_fail", Toast.LENGTH_LONG).show();
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
}
