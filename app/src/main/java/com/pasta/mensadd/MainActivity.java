package com.pasta.mensadd;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pasta.mensadd.cardcheck.AutostartRegister;
import com.pasta.mensadd.cardcheck.card.desfire.DesfireException;
import com.pasta.mensadd.cardcheck.card.desfire.DesfireProtocol;
import com.pasta.mensadd.cardcheck.cardreader.Readers;
import com.pasta.mensadd.cardcheck.cardreader.ValueData;
import com.pasta.mensadd.controller.FragmentController;
import com.pasta.mensadd.fragments.MensaListFragment;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private RelativeLayout mCardCheckContainer;
    private DrawerLayout mNavDrawer;
    private float mCardCheckHeight;
    private boolean mCardCheckVisible;
    private ActionBarDrawerToggle toggle;
    private NfcAdapter mAdapter;
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(null);
        }
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mCardCheckContainer = (RelativeLayout) findViewById(R.id.cardCheckContainer);
        if (mCardCheckContainer != null) mCardCheckContainer.setOnClickListener(this);
        mCardCheckHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        mNavDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, mNavDrawer, toolbar, 0, 0);
        if (mNavDrawer != null) mNavDrawer.addDrawerListener(toggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) navigationView.setNavigationItemSelectedListener(this);

        if (getFragmentManager().findFragmentById(R.id.mainContainer) == null) {
            MensaListFragment fragment = new MensaListFragment();
            getFragmentManager().beginTransaction().add(R.id.mainContainer, fragment, "MensaList").commit();
        }

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter != null) {
            //AutostartRegister.register(getPackageManager(), mPrefs.getBoolean("nfc_autostart", false));
        }

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            onNewIntent(getIntent());
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
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
        setUpCardCheck();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cardCheckContainer) {
            Animation animation = new ViewHeightAnimation(mCardCheckContainer, (int) mCardCheckHeight, 0);
            mCardCheckContainer.setAnimation(animation);
            mCardCheckContainer.startAnimation(animation);
            mCardCheckVisible = false;
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_map:
                FragmentController.showMapFragment(getFragmentManager());
                break;
            case R.id.nav_mensa:
                FragmentController.showMensaListFragment(getFragmentManager());
                break;
            case R.id.nav_settings:
                FragmentController.showSettingsFragment(getFragmentManager());
                break;
        }

        mNavDrawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    public void onNewIntent(Intent intent) {
        Log.i(TAG, "Foreground dispatch");
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Log.i(TAG, "Discovered tag with intent: " + intent);
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            loadCard(tag);
        }
    }

    private String moneyStr(int i) {
        int euros = i / 100;
        int cents = i % 100;

        String centsStr = Integer.toString(cents);
        if (cents < 10)
            centsStr = "0" + centsStr;
        return euros + "," + centsStr + "\u20AC"; // Last one is euro sign
    }

    private void updateCardCheckFragment(ValueData value) {
        if (!mCardCheckVisible) {
            FragmentController.showCardCheckFragment(getFragmentManager(), moneyStr(value.value), moneyStr(value.lastTransaction));
            Animation animation = new ViewHeightAnimation(mCardCheckContainer, 0, (int) mCardCheckHeight);
            mCardCheckContainer.setAnimation(animation);
            mCardCheckContainer.startAnimation(animation);
            mCardCheckVisible = true;
        } else {
            FragmentController.updateCardCheckFragment(getFragmentManager(), moneyStr(value.value), moneyStr(value.lastTransaction));
        }
    }

    private void loadCard(Tag tag) {
        Log.i(TAG, "Loading tag");
        IsoDep tech = IsoDep.get(tag);
        try {
            tech.connect();
        } catch (IOException e) {
            // Tag was removed. We fail silently.
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

        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
    }
}
