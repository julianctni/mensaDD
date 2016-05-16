package com.pasta.mensadd.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pasta.mensadd.R;

public class CardCheckFragment extends Fragment{

    private String mCardBalance;
    private String mLastTransaction;
    private TextView mViewCardBalance;
    private TextView mViewLastTransaction;


    public CardCheckFragment() {}

    public static CardCheckFragment newInstance(String cardBalance, String lastTransaction) {
        CardCheckFragment fragment = new CardCheckFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_check, container, false);
        mViewCardBalance = (TextView)view.findViewById(R.id.balanceContent);
        mViewLastTransaction = (TextView)view.findViewById(R.id.lastTransactionContent);
        updateContent(mCardBalance, mLastTransaction);
        return view;
    }

    public void updateContent (String balance, String lastTransaction) {
        mViewCardBalance.setText(balance);
        mViewLastTransaction.setText(lastTransaction);
    }
}
