package com.pasta.mensadd.features.balancecheck;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.domain.balanceentry.BalanceEntry;
import com.pasta.mensadd.domain.balanceentry.BalanceEntryRepository;

public class BalanceCheckViewModel extends ViewModel {

    private BalanceEntryRepository mBalanceEntryRepository;
    private LiveData<BalanceEntry> mLatestBalanceEntry;
    private BalanceEntry mCurrentBalanceEntry;

    public BalanceCheckViewModel(BalanceEntryRepository balanceEntryRepository) {
        mBalanceEntryRepository = balanceEntryRepository;
        mLatestBalanceEntry = mBalanceEntryRepository.getLatestBalanceEntry();
    }

    public void insertBalanceEntry(BalanceEntry balanceEntry) {
        mBalanceEntryRepository.insertBalanceEntry(balanceEntry);
    }

    public void setCurrentBalanceEntry(BalanceEntry balanceEntry) {
        mCurrentBalanceEntry = balanceEntry;
    }

    public BalanceEntry getCurrentBalanceEntry() {
        return mCurrentBalanceEntry;
    }

    public LiveData<BalanceEntry> getLastBalanceEntry() {
        return mLatestBalanceEntry;
    }
}
