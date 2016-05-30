package com.pasta.mensadd.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pasta.mensadd.R;
import com.pasta.mensadd.adapter.MealListAdapter;
import com.pasta.mensadd.adapter.MensaListAdapter;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.model.Meal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MealDayFragment extends Fragment {

    private static final String TAG_MENSA_ID = "mensaId";
    private static final String TAG_PAGER_POSITION = "pagerPosition";
    private String mMensaId;
    private LinearLayoutManager layoutParams;
    private Calendar cal = Calendar.getInstance();
    public static MealListAdapter mMealListAdapter;
    private RecyclerView mRecyclerView;
    private int mPagerPositon = 10;



    public MealDayFragment() {}

    public static MealDayFragment newInstance(String mensaId, int position) {
        MealDayFragment fragment = new MealDayFragment();
        Bundle args = new Bundle();
        args.putString(TAG_MENSA_ID, mensaId);
        args.putInt(TAG_PAGER_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMensaId = getArguments().getString(TAG_MENSA_ID);
            mPagerPositon = getArguments().getInt(TAG_PAGER_POSITION);
            Log.i("MEALDAYFRAGMENT", "Creating MealDayFragment Nr. "+mPagerPositon);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_day, container, false);
        layoutParams = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mealList);
        mMealListAdapter = new MealListAdapter(getMeals(),this);
        mRecyclerView.setAdapter(mMealListAdapter);
        mRecyclerView.setLayoutManager(layoutParams);
        Log.i("MEALDAYFRAGMENT", "Creating View of MealDayFragment Nr. "+mPagerPositon);
        return view;
    }

    public ArrayList<Meal> getMeals() {
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        if (DataHolder.getInstance().getMensa(mMensaId).getmealMap().get(mPagerPositon) == null)
            DataHolder.getInstance().getMensa(mMensaId).getmealMap().put(mPagerPositon, new ArrayList<Meal>());
        return DataHolder.getInstance().getMensa(mMensaId).getmealMap().get(mPagerPositon);
    }


    public String getCanteenId(){
        return mMensaId;
    }
}
