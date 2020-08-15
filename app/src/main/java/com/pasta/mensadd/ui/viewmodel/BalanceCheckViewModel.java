package com.pasta.mensadd.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.database.entity.BalanceEntry;
import com.pasta.mensadd.database.repository.BalanceEntryRepository;

public class BalanceCheckViewModel extends ViewModel {

    private BalanceEntryRepository mBalanceEntryRepository;
    private LiveData<BalanceEntry> mLatestBalanceEntry;

    public BalanceCheckViewModel(BalanceEntryRepository balanceEntryRepository) {
        mBalanceEntryRepository = balanceEntryRepository;
        mLatestBalanceEntry = mBalanceEntryRepository.getLatestBalanceEntry();
    }

    public void insertBalanceEntry(BalanceEntry balanceEntry) {
        mBalanceEntryRepository.insertBalanceEntry(balanceEntry);
    }

    public LiveData<BalanceEntry> getLastBalanceEntry() {
        return mLatestBalanceEntry;
    }
}
