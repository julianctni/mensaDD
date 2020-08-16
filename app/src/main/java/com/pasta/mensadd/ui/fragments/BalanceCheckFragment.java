package com.pasta.mensadd.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.pasta.mensadd.R;
import com.pasta.mensadd.cardcheck.BalanceCheckService;
import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.entity.BalanceEntry;
import com.pasta.mensadd.database.repository.BalanceEntryRepository;
import com.pasta.mensadd.ui.viewmodel.BalanceCheckViewModel;
import com.pasta.mensadd.ui.viewmodel.BalanceCheckViewModelFactory;

public class BalanceCheckFragment extends Fragment {

    private TextView mViewCardBalance;
    private TextView mViewLastTransaction;
    private boolean mIsVisible;
    private BalanceCheckViewModel mBalanceCheckViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balance_check, container, false);
        BalanceCheckViewModelFactory balanceCheckViewModelFactory = new BalanceCheckViewModelFactory(new BalanceEntryRepository(AppDatabase.getInstance(requireContext())));
        mBalanceCheckViewModel = new ViewModelProvider(this, balanceCheckViewModelFactory).get(BalanceCheckViewModel.class);
        mViewCardBalance = view.findViewById(R.id.balanceContent);
        mViewLastTransaction = view.findViewById(R.id.lastTransactionContent);
        Button closeBalanceCheckButton = view.findViewById(R.id.closeBalanceCheckButton);
        closeBalanceCheckButton.setOnClickListener((v) -> animateView(false));
        Button saveBalanceCheckButton = view.findViewById(R.id.saveBalanceButton);
        Observer<BalanceEntry> latestBalanceObserver = new Observer<BalanceEntry>() {
            @Override
            public void onChanged(BalanceEntry balanceEntry) {
                if (balanceEntry.getCardBalance() == mBalanceCheckViewModel.getCurrentBalanceEntry().getCardBalance()) {
                    Toast.makeText(requireContext(), getString(R.string.balance_already_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), getString(R.string.balance_saved), Toast.LENGTH_SHORT).show();
                    mBalanceCheckViewModel.insertBalanceEntry(mBalanceCheckViewModel.getCurrentBalanceEntry());
                }
                mBalanceCheckViewModel.getLastBalanceEntry().removeObserver(this);
                animateView(false);
            }
        };
        saveBalanceCheckButton.setOnClickListener((v) -> mBalanceCheckViewModel.getLastBalanceEntry().observe(getViewLifecycleOwner(), latestBalanceObserver));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void animateView(boolean show) {
        View view = requireView();
        view.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (show) {
            view.setTranslationY(view.getTranslationY() + view.getMeasuredHeight());
            view.animate().translationY(0).setDuration(300);
            mIsVisible = true;
        } else {
            view.animate().translationYBy(view.getTranslationY() + view.getMeasuredHeight()).setDuration(300);
            mIsVisible = false;
        }
    }

    public void setCurrentBalanceData(BalanceEntry balanceEntry) {
        mBalanceCheckViewModel.setCurrentBalanceEntry(balanceEntry);
        if (!mIsVisible) {
            animateView(true);
        }
        mViewCardBalance.setText(getString(R.string.balance_check_balance, BalanceCheckService.formatAsString(balanceEntry.getCardBalance())));
        mViewLastTransaction.setText(getString(R.string.balance_check_last_transaction, BalanceCheckService.formatAsString(balanceEntry.getLastTransaction())));
    }
}
