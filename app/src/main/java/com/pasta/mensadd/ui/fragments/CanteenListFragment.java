package com.pasta.mensadd.ui.fragments;


import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.pasta.mensadd.R;
import com.pasta.mensadd.controller.FragmentController;
import com.pasta.mensadd.ui.adapter.CanteenListAdapter;
import com.pasta.mensadd.controller.DatabaseController;
import com.pasta.mensadd.controller.ParseController;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.ui.viewmodel.CanteensViewModel;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.networking.callbacks.LoadCanteensCallback;
import com.pasta.mensadd.networking.NetworkController;

import java.util.ArrayList;
import java.util.List;

public class CanteenListFragment extends Fragment implements LoadCanteensCallback, View.OnClickListener, CanteenListAdapter.OnFavoriteClickListener, CanteenListAdapter.OnCanteenClickListener {

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

    private CanteensViewModel mCanteensViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCanteensViewModel = new ViewModelProvider(getActivity()).get(CanteensViewModel.class);
        mCanteensViewModel.getAllCanteens().observe(this, new Observer<List<Canteen>>() {
            @Override
            public void onChanged(List<Canteen> canteens) {
                if (!canteens.isEmpty()) {
                    mProgressLayout.setVisibility(View.GONE);
                }
                mCanteenListAdapter.setCanteens(canteens);
            }
        });
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
        mCanteenListAdapter = new CanteenListAdapter(new ArrayList<Canteen>(), this);
        mCanteenListAdapter.setOnFavoriteClickListener(this);
        mCanteenListAdapter.setOnCanteenClickListener(this);
        mCanteenList.setAdapter(mCanteenListAdapter);
        mCanteenList.setLayoutManager(layoutParams);

        ProgressBar progressBar = view.findViewById(R.id.canteenListProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#CCCCCC"), PorterDuff.Mode.MULTIPLY);

        //NetworkController.getInstance(getActivity()).fetchCanteens(this);

        /*
        if (mSharedPrefs.getLong(KEY_LAST_CANTEENS_UPDATE, 0) == 0 || new Date().getTime() - mSharedPrefs.getLong(KEY_LAST_CANTEENS_UPDATE, 0) > 86400000) {
            NetworkController.getInstance(getActivity()).fetchCanteens(this);
            Log.i("Parsing canteens", "Doing request");
        } else if (DataHolder.getInstance().getCanteenList().isEmpty()) {
            readCanteensFromDb();
        } else {
            mProgressLayout.setVisibility(View.GONE);
            mCanteenList.setVisibility(View.VISIBLE);
        }*/

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showTutorial();
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
        mCanteenList.setVisibility(View.VISIBLE);
    }

    public void showTutorial(){
        try {
            PackageInfo info = this.getContext().getPackageManager().getPackageInfo(this.getContext().getPackageName(), 0);
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
