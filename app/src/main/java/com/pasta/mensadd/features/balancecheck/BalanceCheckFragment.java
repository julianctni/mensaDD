package com.pasta.mensadd.features.balancecheck;


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

import com.pasta.mensadd.AppDatabase;
import com.pasta.mensadd.R;
import com.pasta.mensadd.domain.balanceentry.BalanceEntry;
import com.pasta.mensadd.domain.balanceentry.BalanceEntryRepository;

import static com.pasta.mensadd.features.balancecheck.BalanceCheckViewModel.ARGS_KEY_CURRENT_BALANCE;
import static com.pasta.mensadd.features.balancecheck.BalanceCheckViewModel.ARGS_KEY_LAST_TRANSACTION;

public class BalanceCheckFragment extends Fragment {

    private TextView mViewCardBalance;
    private TextView mViewLastTransaction;
    private boolean mIsVisible;
    private BalanceCheckViewModel mBalanceCheckViewModel;


    public static BalanceCheckFragment getInstance(float balance, float lastTransaction) {
        BalanceCheckFragment balanceCheckFragment = new BalanceCheckFragment();
        Bundle args = new Bundle();
        args.putFloat(ARGS_KEY_CURRENT_BALANCE, balance);
        args.putFloat(ARGS_KEY_LAST_TRANSACTION, lastTransaction);
        balanceCheckFragment.setArguments(args);
        return balanceCheckFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balance_check, container, false);
        Bundle bundle = getArguments() == null ? savedInstanceState : getArguments();
        BalanceCheckViewModelFactory balanceCheckViewModelFactory = new BalanceCheckViewModelFactory(this, bundle, new BalanceEntryRepository(AppDatabase.getInstance(requireContext())));
        mBalanceCheckViewModel = new ViewModelProvider(this, balanceCheckViewModelFactory).get(BalanceCheckViewModel.class);
        mViewCardBalance = view.findViewById(R.id.text_balanceCheck_balance);
        mViewLastTransaction = view.findViewById(R.id.text_balanceCheck_lastTransaction);
        Button closeBalanceCheckButton = view.findViewById(R.id.btn_balanceCheck_close);
        closeBalanceCheckButton.setOnClickListener((v) -> animateView(false));
        Button saveBalanceCheckButton = view.findViewById(R.id.btn_balanceCheck_save);
        Observer<BalanceEntry> latestBalanceObserver = new Observer<BalanceEntry>() {
            @Override
            public void onChanged(BalanceEntry balanceEntry) {
                mBalanceCheckViewModel.setLatestBalanceEntry(balanceEntry);
                saveBalanceCheckButton.setEnabled(true);
                saveBalanceCheckButton.setAlpha(1.0f);
            }
        };
        mBalanceCheckViewModel.getLastBalanceEntryLive().observe(getViewLifecycleOwner(), latestBalanceObserver);
        saveBalanceCheckButton.setOnClickListener((v) -> {
            boolean insertResult = mBalanceCheckViewModel.insertNewBalanceEntry();
            if (insertResult) {
                Toast.makeText(requireContext(), getString(R.string.balance_saved), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), getString(R.string.balance_already_saved), Toast.LENGTH_SHORT).show();
            }
            animateView(false);
        });
        mViewCardBalance.setText(BalanceCheckService.formatAsString(mBalanceCheckViewModel.getCurrentBalance()));
        mViewLastTransaction.setText(getString(R.string.balance_check_last_transaction, BalanceCheckService.formatAsString(mBalanceCheckViewModel.getLastTransaction())));
        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!mIsVisible) {
            animateView(true);
        }
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

    public void setCurrentBalanceData(float balance, float lastTransaction) {
        mBalanceCheckViewModel.setCurrentBalanceData(balance, lastTransaction);
        mViewCardBalance.setText(BalanceCheckService.formatAsString(balance));
        mViewLastTransaction.setText(getString(R.string.balance_check_last_transaction, BalanceCheckService.formatAsString(lastTransaction)));
        if (!mIsVisible) {
            animateView(true);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mBalanceCheckViewModel.saveCurrentBalanceData();
    }
}
