package com.pasta.mensadd.ui.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pasta.mensadd.R;

public class BalanceCheckFragment extends Fragment {

    private String mCardBalance;
    private String mLastTransaction;
    private TextView mViewCardBalance;
    private TextView mViewLastTransaction;


    public BalanceCheckFragment() {
    }

    public static BalanceCheckFragment newInstance(String cardBalance, String lastTransaction) {
        BalanceCheckFragment fragment = new BalanceCheckFragment();
        Bundle args = new Bundle();
        args.putString("mCardBalance", cardBalance);
        args.putString("mLastTransaction", lastTransaction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCardBalance = getArguments().getString("mCardBalance");
            mLastTransaction = getArguments().getString("mLastTransaction");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balance_check, container, false);
        mViewCardBalance = view.findViewById(R.id.balanceContent);
        mViewLastTransaction = view.findViewById(R.id.lastTransactionContent);
        updateContent(mCardBalance, mLastTransaction);
        return view;
    }

    public void updateContent(String balance, String lastTransaction) {
        mViewCardBalance.setText(balance);
        mViewLastTransaction.setText(lastTransaction);
    }
}
