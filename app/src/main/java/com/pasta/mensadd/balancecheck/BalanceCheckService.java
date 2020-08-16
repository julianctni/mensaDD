package com.pasta.mensadd.balancecheck;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;

import com.pasta.mensadd.PreferenceService;
import com.pasta.mensadd.balancecheck.card.desfire.DesfireException;
import com.pasta.mensadd.balancecheck.card.desfire.DesfireProtocol;
import com.pasta.mensadd.balancecheck.cardreader.Readers;
import com.pasta.mensadd.balancecheck.cardreader.ValueData;
import com.pasta.mensadd.database.entity.BalanceEntry;
import com.pasta.mensadd.ui.MainActivity;

import java.io.IOException;
import java.util.Date;

public class BalanceCheckService {

    public static String formatAsString(float f) {
        int euros = (int) f;
        int cents = (int) (f * 100 % 100);
        String centsStr = Integer.toString(cents);
        if (cents < 10)
            centsStr = "0" + centsStr;
        return euros + "." + centsStr;
    }

    public void loadCard(Tag tag, OnCardLoadedCallback onCardLoadedCallback) {
        IsoDep tech = IsoDep.get(tag);
        try {
            tech.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            DesfireProtocol desfireTag = new DesfireProtocol(tech);
            ValueData valueData = Readers.getInstance().readCard(desfireTag);
            if (valueData != null) {
                float cardBalance = (float) valueData.value / 1000;
                float lastTransaction = (float) valueData.lastTransaction / 1000;
                BalanceEntry balanceEntry = new BalanceEntry(new Date().getTime(), cardBalance, lastTransaction);
                onCardLoadedCallback.onCardLoadSuccess(balanceEntry);
            } else
                onCardLoadedCallback.onCardLoadError(true);
            tech.close();
        } catch (DesfireException ex) {
            onCardLoadedCallback.onCardLoadError(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUpCardCheck(MainActivity mainActivity) {
        PendingIntent mPendingIntent = PendingIntent.getActivity(mainActivity, 0,
                new Intent(mainActivity, mainActivity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tech = new IntentFilter(
                NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] mFilters = new IntentFilter[]{tech,};
        String[][] mTechLists = new String[][]{new String[]{
                IsoDep.class.getName(), NfcA.class.getName()}};
        NfcAdapter.getDefaultAdapter(mainActivity.getApplicationContext()).enableForegroundDispatch(mainActivity, mPendingIntent, mFilters,
                mTechLists);
    }

    public void registerNfcAutostart(Context context, PreferenceService preferenceService) {
        if (!preferenceService.isNfcAutostartRegistered()) {
            AutostartRegister.register(context.getPackageManager(), true);
            preferenceService.setNfcAutostartRegistered(true);
            preferenceService.setNfcAutostartSetting(true);
        }
    }
}
