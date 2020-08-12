package com.pasta.mensadd.ui.fragments;


import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.pasta.mensadd.R;
import com.pasta.mensadd.ui.FragmentController;
import com.pasta.mensadd.ui.adapter.CanteenListAdapter;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.ui.viewmodel.CanteensViewModel;

public class CanteenListFragment extends Fragment implements View.OnClickListener, CanteenListAdapter.OnFavoriteClickListener, CanteenListAdapter.OnCanteenClickListener {

    private CanteenListAdapter mCanteenListAdapter;
    private SharedPreferences mSharedPrefs;
    private LinearLayout mTutorialPage1;
    private LinearLayout mTutorialPage2;
    private LinearLayout mTutorialPage3;
    private Button mTutorialContinueBtn;
    private Button mTutorialBackBtn;
    private CardView mTutorialCard;
    private CanteensViewModel mCanteensViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canteen_list, container, false);
        if (getContext() != null)
            mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mCanteensViewModel = new ViewModelProvider(getActivity()).get(CanteensViewModel.class);
        mTutorialPage1 = view.findViewById(R.id.tutorialPage1);
        mTutorialPage2 = view.findViewById(R.id.tutorialPage2);
        mTutorialPage3 = view.findViewById(R.id.tutorialPage3);
        mTutorialContinueBtn = view.findViewById(R.id.tutorialContinueButton);
        mTutorialBackBtn = view.findViewById(R.id.tutorialBackButton);
        ProgressBar progressBar = view.findViewById(R.id.canteenListProgressBar);
        mTutorialCard = view.findViewById(R.id.tutorialCard);
        RecyclerView mCanteenList = view.findViewById(R.id.canteenList);
        mTutorialBackBtn.setOnClickListener(this);
        mTutorialContinueBtn.setOnClickListener(this);
        LinearLayoutManager layoutParams = new LinearLayoutManager(getActivity());
        mCanteenListAdapter = new CanteenListAdapter(this.getContext());
        mCanteenListAdapter.setOnFavoriteClickListener(this);
        mCanteenListAdapter.setOnCanteenClickListener(this);
        mCanteenList.setAdapter(mCanteenListAdapter);
        mCanteenList.setLayoutManager(layoutParams);
        mCanteensViewModel.getCanteens().observe(getViewLifecycleOwner(), canteens -> {
            mCanteenListAdapter.submitList(canteens);
        });
        mCanteensViewModel.isRefreshing().observe(getViewLifecycleOwner(), refreshing -> progressBar.setVisibility(refreshing ? View.VISIBLE : View.GONE));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showTutorial();
    }

    void showTutorial() {
        try {
            PackageInfo info = getContext().getPackageManager().getPackageInfo(this.getContext().getPackageName(), 0);
            if (mSharedPrefs.getBoolean("pref_show_tut_" + info.versionCode, true) && info.versionCode == 20) {
                mTutorialCard.setVisibility(View.VISIBLE);
                mSharedPrefs.edit().remove("pref_show_tut_" + (info.versionCode - 1)).apply();
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
                    PackageInfo info = getContext().getPackageManager().getPackageInfo(this.getContext().getPackageName(), 0);
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

    @Override
    public void onFavoriteClick(Canteen canteen) {
        canteen.setAsFavorite(!canteen.isFavorite());
        mCanteensViewModel.updateCanteen(canteen);
    }

    @Override
    public void onCanteenClick(Canteen canteen) {
        canteen.increasePriority();
        mCanteensViewModel.updateCanteen(canteen);
        mCanteensViewModel.setSelectedCanteen(canteen);
        FragmentController.showMealWeekFragment(getFragmentManager());
    }
}
