package com.pasta.mensadd.cardcheck;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;

import com.pasta.mensadd.cardcheck.card.desfire.DesfireException;
import com.pasta.mensadd.cardcheck.card.desfire.DesfireProtocol;
import com.pasta.mensadd.cardcheck.cardreader.Readers;
import com.pasta.mensadd.cardcheck.cardreader.ValueData;
import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.entity.BalanceEntry;
import com.pasta.mensadd.database.repository.BalanceEntryRepository;
import com.pasta.mensadd.ui.MainActivity;

import java.io.IOException;
import java.util.Date;

public class CardCheckService {

    private ValueData mCurrentValueData;
    private MainActivity mMainActivity;

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
            ValueData value = Readers.getInstance().readCard(desfireTag);
            if (value != null) {
                mCurrentValueData = value;
                onCardLoadedCallback.onCardLoadSuccess(value);
            } else
                onCardLoadedCallback.onCardLoadError(true);
            tech.close();
        } catch (DesfireException ex) {
            onCardLoadedCallback.onCardLoadError(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void storeCardData(OnCardDataStoredCallback onCardDataStoredCallback) {
        float cardBalance = (float) mCurrentValueData.value / 1000;
        float lastTransaction = (float) mCurrentValueData.lastTransaction / 1000;
        BalanceEntryRepository balanceEntryRepository = new BalanceEntryRepository(AppDatabase.getInstance(mMainActivity.getApplicationContext()));
        balanceEntryRepository.getLatestBalanceEntry().observe(mMainActivity, balanceEntry -> {
            boolean isNewData = balanceEntry == null || balanceEntry.getCardBalance() != cardBalance || balanceEntry.getLastTransaction() != lastTransaction;
            if (isNewData) {
                balanceEntryRepository.insertBalanceEntry(new BalanceEntry(new Date().getTime(), cardBalance, lastTransaction));
            }
            onCardDataStoredCallback.onCardDataStored(isNewData);
        });
    }

    public String moneyStr(int i) {
        int euros = i / 1000;
        int cents = i / 10 % 100;
        String centsStr = Integer.toString(cents);
        if (cents < 10)
            centsStr = "0" + centsStr;
        return euros + "." + centsStr;
    }
}
