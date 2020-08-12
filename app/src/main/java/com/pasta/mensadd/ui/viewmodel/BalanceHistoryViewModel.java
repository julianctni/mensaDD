package com.pasta.mensadd.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.database.entity.BalanceEntry;
import com.pasta.mensadd.database.repository.BalanceEntryRepository;

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
