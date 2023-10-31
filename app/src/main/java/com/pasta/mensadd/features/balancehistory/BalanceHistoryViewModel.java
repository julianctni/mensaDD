package com.pasta.mensadd.features.balancehistory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.domain.balanceentry.BalanceEntry;
import com.pasta.mensadd.domain.balanceentry.BalanceEntryRepository;

import java.util.List;

public class BalanceHistoryViewModel extends ViewModel {

    private final LiveData<List<BalanceEntry>> mBalanceEntries;
    private final LiveData<BalanceEntry> mLatestBalanceEntry;

    public BalanceHistoryViewModel(BalanceEntryRepository balanceEntryRepository) {
        mBalanceEntries = balanceEntryRepository.getBalanceEntries();
        mLatestBalanceEntry = balanceEntryRepository.getLatestBalanceEntry();
    }

    public LiveData<BalanceEntry> getLatestBalanceEntry() {
        return mLatestBalanceEntry;
    }

    public LiveData<List<BalanceEntry>> getBalanceEntries() {
        return mBalanceEntries;
    }
}
