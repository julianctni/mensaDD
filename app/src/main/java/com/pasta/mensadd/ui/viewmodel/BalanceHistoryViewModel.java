package com.pasta.mensadd.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.database.entity.BalanceEntry;
import com.pasta.mensadd.database.repository.BalanceEntryRepository;

import java.util.List;

public class BalanceHistoryViewModel extends ViewModel {

    private BalanceEntryRepository balanceEntryRepository;
    private LiveData<List<BalanceEntry>> balanceEntries;

    public BalanceHistoryViewModel(BalanceEntryRepository balanceEntryRepository) {
        this.balanceEntryRepository = balanceEntryRepository;
        balanceEntries = balanceEntryRepository.getAllBalanceEntries();
    }

    public LiveData<List<BalanceEntry>> getAllBalanceEntries() {
        return balanceEntries;
    }
}
