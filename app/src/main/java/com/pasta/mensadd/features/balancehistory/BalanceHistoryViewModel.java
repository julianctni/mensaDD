package com.pasta.mensadd.features.balancehistory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.domain.balanceentry.BalanceEntry;
import com.pasta.mensadd.domain.balanceentry.BalanceEntryRepository;

import java.util.List;

public class BalanceHistoryViewModel extends ViewModel {

    private LiveData<List<BalanceEntry>> mBalanceEntries;

    public BalanceHistoryViewModel(BalanceEntryRepository balanceEntryRepository) {
        mBalanceEntries = balanceEntryRepository.getBalanceEntries();
    }

    public LiveData<List<BalanceEntry>> getBalanceEntries() {
        return mBalanceEntries;
    }
}
