package com.pasta.mensadd.features.balancecheck;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.domain.balanceentry.BalanceEntry;
import com.pasta.mensadd.domain.balanceentry.BalanceEntryRepository;

import java.util.Calendar;

public class BalanceCheckViewModel extends ViewModel {

    public static final String ARGS_KEY_CURRENT_BALANCE = "arg_current_balance";
    public static final String ARGS_KEY_LAST_TRANSACTION = "arg_last_transaction";
    private final BalanceEntryRepository mBalanceEntryRepository;
    private final LiveData<BalanceEntry> mLatestBalanceEntryLive;
    private BalanceEntry mLatestBalanceEntry;
    //private BalanceEntry mCurrentBalanceEntry;
    private float mCurrentBalance;
    private float mLastTransaction;
    private final SavedStateHandle mSavedStateHandle;

    public BalanceCheckViewModel(BalanceEntryRepository balanceEntryRepository, SavedStateHandle savedStateHandle) {
        mBalanceEntryRepository = balanceEntryRepository;
        mLatestBalanceEntryLive = mBalanceEntryRepository.getLatestBalanceEntry();
        mCurrentBalance = savedStateHandle.get(ARGS_KEY_CURRENT_BALANCE);
        mLastTransaction = savedStateHandle.get(ARGS_KEY_LAST_TRANSACTION);
        mSavedStateHandle = savedStateHandle;
    }

    public boolean insertNewBalanceEntry() {
        BalanceEntry balanceEntry = new BalanceEntry(Calendar.getInstance().getTimeInMillis(), mCurrentBalance, mLastTransaction);
        if (mLatestBalanceEntry != null && mLatestBalanceEntry.getCardBalance() == mCurrentBalance
                && mLatestBalanceEntry.getLastTransaction() == mLastTransaction) {
            return false;
        } else {
            mBalanceEntryRepository.insertBalanceEntry(balanceEntry);
            return true;
        }
    }

    public LiveData<BalanceEntry> getLastBalanceEntryLive() {
        return mLatestBalanceEntryLive;
    }

    public void setLatestBalanceEntry(BalanceEntry balanceEntry) {
        this.mLatestBalanceEntry = balanceEntry;
    }

    public void setCurrentBalanceData(float balance, float lastTransaction){
        this.mCurrentBalance = balance;
        this.mLastTransaction = lastTransaction;
    }

    public void saveCurrentBalanceData() {
        mSavedStateHandle.set(ARGS_KEY_CURRENT_BALANCE, mCurrentBalance);
        mSavedStateHandle.set(ARGS_KEY_LAST_TRANSACTION, mLastTransaction);
    }

    public float getCurrentBalance() {
        return mCurrentBalance;
    }

    public float getLastTransaction() {
        return mLastTransaction;
    }

}
