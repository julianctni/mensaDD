package com.pasta.mensadd.features.balancecheck;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.domain.balanceentry.BalanceEntry;
import com.pasta.mensadd.domain.balanceentry.BalanceEntryRepository;

public class BalanceCheckViewModel extends ViewModel {

    private BalanceEntryRepository mBalanceEntryRepository;
    private LiveData<BalanceEntry> mLatestBalanceEntryLive;
    private BalanceEntry mLatestBalanceEntry;
    private BalanceEntry mCurrentBalanceEntry;

    public BalanceCheckViewModel(BalanceEntryRepository balanceEntryRepository) {
        mBalanceEntryRepository = balanceEntryRepository;
        mLatestBalanceEntryLive = mBalanceEntryRepository.getLatestBalanceEntry();
    }

    public boolean insertBalanceEntry(BalanceEntry balanceEntry) {
        if (mLatestBalanceEntry != null && mLatestBalanceEntry.getCardBalance() == mCurrentBalanceEntry.getCardBalance()
                && mLatestBalanceEntry.getLastTransaction() == mCurrentBalanceEntry.getLastTransaction()) {
            return false;
        } else {
            mBalanceEntryRepository.insertBalanceEntry(balanceEntry);
            return true;
        }
    }

    public void setCurrentBalanceEntry(BalanceEntry balanceEntry) {
        mCurrentBalanceEntry = balanceEntry;
    }

    public BalanceEntry getCurrentBalanceEntry() {
        return mCurrentBalanceEntry;
    }

    public LiveData<BalanceEntry> getLastBalanceEntryLive() {
        return mLatestBalanceEntryLive;
    }

    public void setLatestBalanceEntry(BalanceEntry balanceEntry) {
        this.mLatestBalanceEntry = balanceEntry;
    }

}
