package com.pasta.mensadd.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pasta.mensadd.MainActivity;
import com.pasta.mensadd.R;
import com.pasta.mensadd.adapter.CanteenListAdapter;
import com.pasta.mensadd.controller.DatabaseController;
import com.pasta.mensadd.controller.ParseController;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.networking.LoadCanteensCallback;
import com.pasta.mensadd.networking.NetworkController;

import java.util.Date;

public class CanteenListFragment extends Fragment implements LoadCanteensCallback, View.OnClickListener{

    private CanteenListAdapter mCanteenListAdapter;
    private SharedPreferences mSharedPrefs;
    private LinearLayout mTutorialPage1;
    private LinearLayout mTutorialPage2;
    private LinearLayout mTutorialPage3;
    private Button mTutorialContinueBtn;
    private Button mTutorialBackBtn;
    private CardView mTutorialCard;

    public static String KEY_LAST_CANTEENS_UPDATE = "lastCanteenUpdate";


    public CanteenListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canteen_list, container, false);
        MainActivity.hideToolbarShadow(false);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        mTutorialPage1 = (LinearLayout) view.findViewById(R.id.tutorialPage1);
        mTutorialPage2 = (LinearLayout) view.findViewById(R.id.tutorialPage2);
        mTutorialPage3 = (LinearLayout) view.findViewById(R.id.tutorialPage3);
        mTutorialContinueBtn = (Button) view.findViewById(R.id.tutorialContinueButton);
        mTutorialBackBtn = (Button) view.findViewById(R.id.tutorialBackButton);
        mTutorialCard = (CardView) view.findViewById(R.id.tutorialCard);
        mTutorialBackBtn.setOnClickListener(this);
        mTutorialContinueBtn.setOnClickListener(this);
        if (mSharedPrefs.getBoolean("pref_show_tut", true))
            mTutorialCard.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutParams = new LinearLayoutManager(getActivity());
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.mensaList);
        DataHolder.getInstance().sortCanteenList();
        mCanteenListAdapter = new CanteenListAdapter(DataHolder.getInstance().getCanteenList(),this);
        mRecyclerView.setAdapter(mCanteenListAdapter);
        mRecyclerView.setLayoutManager(layoutParams);

        TextView header = (TextView)getActivity().findViewById(R.id.heading_toolbar);
        header.setVisibility(View.GONE);
        ImageView appLogo = (ImageView)getActivity().findViewById(R.id.toolbarImage);
        appLogo.setVisibility(View.VISIBLE);

        if (mSharedPrefs.getLong(KEY_LAST_CANTEENS_UPDATE, 0) == 0 || new Date().getTime() - mSharedPrefs.getLong(KEY_LAST_CANTEENS_UPDATE,0) > 86400000) {
            NetworkController.getInstance(getActivity()).getCanteenList(this);
            Log.i("Parsing canteens", "Doing request");
        } else if (DataHolder.getInstance().getCanteenList().isEmpty()){
            readCanteensFromDb();
        }

        MainActivity.updateNavDrawer(R.id.nav_mensa);
        return view;
    }

    @Override
    public void onResponseMessage(int responseType, String message) {
        if (responseType == NetworkController.SUCCESS) {
            ParseController p = new ParseController();
            p.parseCanteens(message, new DatabaseController(this.getActivity().getApplicationContext()), mSharedPrefs);
            mCanteenListAdapter.notifyDataSetChanged();

        } else {
            readCanteensFromDb();
        }
        Log.i("Parsing canteens", "test");
        //Log.i("Parsing canteens", "Calling onResponseMessage");
    }

    public void readCanteensFromDb(){
        DatabaseController dbController = new DatabaseController(getActivity().getApplicationContext());
        dbController.readCanteensFromDb();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tutorialContinueButton){
            if (mTutorialPage1.getVisibility() == View.VISIBLE){
                mTutorialPage1.setVisibility(View.GONE);
                mTutorialPage2.setVisibility(View.VISIBLE);
                mTutorialBackBtn.setVisibility(View.VISIBLE);
            } else if (mTutorialPage2.getVisibility() == View.VISIBLE){
                mTutorialPage2.setVisibility(View.GONE);
                mTutorialPage3.setVisibility(View.VISIBLE);
                mTutorialContinueBtn.setText(getResources().getString(R.string.tutorial_button_close));
            } else if (mTutorialPage3.getVisibility() == View.VISIBLE){
                mTutorialCard.setVisibility(View.GONE);
                mSharedPrefs.edit().putBoolean("pref_show_tut", false).apply();
            }

        } else if (view.getId() == R.id.tutorialBackButton) {
            if (mTutorialPage2.getVisibility() == View.VISIBLE){
                mTutorialPage2.setVisibility(View.GONE);
                mTutorialPage1.setVisibility(View.VISIBLE);
                mTutorialBackBtn.setVisibility(View.GONE);
            } else if (mTutorialPage3.getVisibility() == View.VISIBLE){
                mTutorialPage3.setVisibility(View.GONE);
                mTutorialPage2.setVisibility(View.VISIBLE);
                mTutorialContinueBtn.setText(getResources().getString(R.string.tutorial_button_continue));
            }
        }
    }
}
