package com.pasta.mensadd.fragments;


import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.pasta.mensadd.R;
import com.pasta.mensadd.adapter.CanteenListAdapter;
import com.pasta.mensadd.controller.DatabaseController;
import com.pasta.mensadd.controller.ParseController;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.networking.LoadCanteensCallback;
import com.pasta.mensadd.networking.NetworkController;

import java.util.Date;

public class CanteenListFragment extends Fragment implements LoadCanteensCallback, View.OnClickListener {

    private CanteenListAdapter mCanteenListAdapter;
    private SharedPreferences mSharedPrefs;
    private LinearLayout mTutorialPage1;
    private LinearLayout mTutorialPage2;
    private LinearLayout mTutorialPage3;
    private RecyclerView mCanteenList;
    private Button mTutorialContinueBtn;
    private Button mTutorialBackBtn;
    private CardView mTutorialCard;
    private LinearLayout mProgressLayout;

    public static String KEY_LAST_CANTEENS_UPDATE = "lastCanteenUpdate";


    public CanteenListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canteen_list, container, false);
        if (getContext() != null)
            mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mTutorialPage1 = view.findViewById(R.id.tutorialPage1);
        mTutorialPage2 = view.findViewById(R.id.tutorialPage2);
        mTutorialPage3 = view.findViewById(R.id.tutorialPage3);
        mTutorialContinueBtn = view.findViewById(R.id.tutorialContinueButton);
        mTutorialBackBtn = view.findViewById(R.id.tutorialBackButton);
        mProgressLayout = view.findViewById(R.id.canteenListProgressLayout);
        mTutorialCard = view.findViewById(R.id.tutorialCard);
        mCanteenList = view.findViewById(R.id.canteenList);
        mTutorialBackBtn.setOnClickListener(this);
        mTutorialContinueBtn.setOnClickListener(this);
        LinearLayoutManager layoutParams = new LinearLayoutManager(getActivity());
        DataHolder.getInstance().sortCanteenList();
        mCanteenListAdapter = new CanteenListAdapter(DataHolder.getInstance().getCanteenList(), this);
        mCanteenList.setAdapter(mCanteenListAdapter);
        mCanteenList.setLayoutManager(layoutParams);

        ProgressBar progressBar = view.findViewById(R.id.canteenListProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#CCCCCC"), PorterDuff.Mode.MULTIPLY);

        if (mSharedPrefs.getLong(KEY_LAST_CANTEENS_UPDATE, 0) == 0 || new Date().getTime() - mSharedPrefs.getLong(KEY_LAST_CANTEENS_UPDATE, 0) > 86400000) {
            NetworkController.getInstance(getActivity()).getCanteenList(this);
            Log.i("Parsing canteens", "Doing request");
        } else if (DataHolder.getInstance().getCanteenList().isEmpty()) {
            readCanteensFromDb();
        } else {
            mProgressLayout.setVisibility(View.GONE);
            mCanteenList.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCanteenListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResponseMessage(int responseType, String message) {
        if (responseType == NetworkController.SUCCESS) {
            ParseController p = new ParseController();
            p.parseCanteens(message, new DatabaseController(this.getContext()), mSharedPrefs, this);
        } else if (responseType == ParseController.PARSE_SUCCESS) {
            mProgressLayout.setVisibility(View.GONE);
            mCanteenListAdapter.notifyDataSetChanged();
            showTutorial();
            mCanteenList.setVisibility(View.VISIBLE);
        } else {
            readCanteensFromDb();
        }
    }

    public void readCanteensFromDb() {
        DatabaseController dbController = new DatabaseController(getContext());
        dbController.readCanteensFromDb();
        mProgressLayout.setVisibility(View.GONE);
        mCanteenListAdapter.notifyDataSetChanged();
        showTutorial();
        mCanteenList.setVisibility(View.VISIBLE);
    }

    public void showTutorial(){
        try {
            PackageInfo info = this.getContext().getPackageManager().getPackageInfo(this.getContext().getPackageName(), 0);
            if (mSharedPrefs.getBoolean("pref_show_tut_" + info.versionCode, true) && info.versionCode == 20) {
                mTutorialCard.setVisibility(View.VISIBLE);
                mSharedPrefs.edit().remove("pref_show_tut_" + (info.versionCode - 1));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tutorialContinueButton) {
            if (mTutorialPage1.getVisibility() == View.VISIBLE) {
                mTutorialPage1.setVisibility(View.GONE);
                mTutorialPage2.setVisibility(View.VISIBLE);
                mTutorialBackBtn.setVisibility(View.VISIBLE);
            } else if (mTutorialPage2.getVisibility() == View.VISIBLE) {
                mTutorialPage2.setVisibility(View.GONE);
                mTutorialPage3.setVisibility(View.VISIBLE);
                mTutorialContinueBtn.setText(getResources().getString(R.string.tutorial_button_close));
            } else if (mTutorialPage3.getVisibility() == View.VISIBLE) {
                mTutorialCard.setVisibility(View.GONE);
                try {
                    PackageInfo info = this.getContext().getPackageManager().getPackageInfo(this.getContext().getPackageName(), 0);
                    mSharedPrefs.edit().putBoolean("pref_show_tut_" + info.versionCode, false).apply();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }

        } else if (view.getId() == R.id.tutorialBackButton) {
            if (mTutorialPage2.getVisibility() == View.VISIBLE) {
                mTutorialPage2.setVisibility(View.GONE);
                mTutorialPage1.setVisibility(View.VISIBLE);
                mTutorialBackBtn.setVisibility(View.GONE);
            } else if (mTutorialPage3.getVisibility() == View.VISIBLE) {
                mTutorialPage3.setVisibility(View.GONE);
                mTutorialPage2.setVisibility(View.VISIBLE);
                mTutorialContinueBtn.setText(getResources().getString(R.string.tutorial_button_continue));
            }
        }
    }
}
