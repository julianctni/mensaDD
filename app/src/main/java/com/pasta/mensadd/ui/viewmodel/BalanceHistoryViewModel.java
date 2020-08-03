package com.pasta.mensadd.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pasta.mensadd.database.entity.BalanceEntry;
import com.pasta.mensadd.database.repository.BalanceEntryRepository;

import java.util.List;

public class BalanceHistoryViewModel extends AndroidViewModel {

    private BalanceEntryRepository balanceEntryRepository;
    private LiveData<List<BalanceEntry>> balanceEntries;

    public BalanceHistoryViewModel(@NonNull Application application) {
        super(application);
        balanceEntryRepository = new BalanceEntryRepository(application);
        balanceEntries = balanceEntryRepository.getAll();
    }

    public LiveData<List<BalanceEntry>> getAllBalanceEntries() {
        return balanceEntries;
    }
}
