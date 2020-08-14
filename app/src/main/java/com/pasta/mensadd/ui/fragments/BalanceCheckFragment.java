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

import com.pasta.mensadd.R;
import com.pasta.mensadd.cardcheck.CardCheckService;

public class BalanceCheckFragment extends Fragment {

    private String mCardBalance;
    private String mLastTransaction;
    private TextView mViewCardBalance;
    private TextView mViewLastTransaction;
    private boolean mIsVisible;
    private CardCheckService mCardCheckService;

    public static BalanceCheckFragment newInstance(String cardBalance, String lastTransaction, CardCheckService cardCheckService) {
        BalanceCheckFragment fragment = new BalanceCheckFragment();
        fragment.setCardCheckService(cardCheckService);
        Bundle args = new Bundle();
        args.putString("mCardBalance", cardBalance);
        args.putString("mLastTransaction", lastTransaction);
        fragment.setArguments(args);
        return fragment;
    }

    public void setCardCheckService(CardCheckService cardCheckService) {
        mCardCheckService = cardCheckService;
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
        Button closeBalanceCheckButton = view.findViewById(R.id.closeBalanceCheckButton);
        closeBalanceCheckButton.setOnClickListener((v) -> animateView(false));
        Button saveBalanceCheckButton = view.findViewById(R.id.saveBalanceButton);
        saveBalanceCheckButton.setOnClickListener((v) -> {
            mCardCheckService.storeCardData((hasSavedCardData) -> {
                int messageId = hasSavedCardData ? R.string.balance_saved : R.string.balance_already_saved;
                Toast.makeText(requireActivity(), getString(messageId), Toast.LENGTH_SHORT).show();
            });
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateContent(mCardBalance, mLastTransaction);
    }

    public void animateView(boolean show) {
        View view = getView();
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

    public void updateContent(String balance, String lastTransaction) {
        if (!mIsVisible) {
            animateView(true);
        }
        mViewCardBalance.setText(getString(R.string.balance_check_balance, balance));
        mViewLastTransaction.setText(getString(R.string.balance_check_last_transaction, lastTransaction));
    }
}
