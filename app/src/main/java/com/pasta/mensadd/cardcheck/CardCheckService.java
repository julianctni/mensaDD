package com.pasta.mensadd.cardcheck;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.util.Log;

import com.pasta.mensadd.cardcheck.card.desfire.DesfireException;
import com.pasta.mensadd.cardcheck.card.desfire.DesfireProtocol;
import com.pasta.mensadd.cardcheck.cardreader.Readers;
import com.pasta.mensadd.cardcheck.cardreader.ValueData;
import com.pasta.mensadd.database.entity.BalanceEntry;
import com.pasta.mensadd.ui.MainActivity;

import java.io.IOException;
import java.util.Date;

public class CardCheckService {

    private MainActivity mMainActivity;
    private BalanceEntry mBalanceEntry;

    public CardCheckService(MainActivity mainActivity) {
        mMainActivity = mainActivity;
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
                mBalanceEntry = new BalanceEntry(new Date().getTime(), cardBalance, lastTransaction);
                onCardLoadedCallback.onCardLoadSuccess(mBalanceEntry);
            } else
                onCardLoadedCallback.onCardLoadError(true);
            tech.close();
        } catch (DesfireException ex) {
            onCardLoadedCallback.onCardLoadError(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BalanceEntry getBalanceEntry() {
        return mBalanceEntry;
    }

    public String getLastTransactionAsString() {
        return moneyStr(mBalanceEntry.getLastTransaction());
    }

    public String getCurrentBalanceAsString() {
        return moneyStr(mBalanceEntry.getCardBalance());
    }

    public void setUpCardCheck(NfcAdapter nfcAdapter) {
        PendingIntent mPendingIntent = PendingIntent.getActivity(mMainActivity, 0,
                new Intent(mMainActivity, mMainActivity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tech = new IntentFilter(
                NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] mFilters = new IntentFilter[]{tech,};
        String[][] mTechLists = new String[][]{new String[]{
                IsoDep.class.getName(), NfcA.class.getName()}};
        nfcAdapter.enableForegroundDispatch(mMainActivity, mPendingIntent, mFilters,
                mTechLists);
    }

    public String moneyStr(float f) {
        int euros = (int)f;
        int cents = (int)(f * 100 % 100);
        String centsStr = Integer.toString(cents);
        if (cents < 10)
            centsStr = "0" + centsStr;
        return euros + "." + centsStr;
    }
}
