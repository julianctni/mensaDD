package com.pasta.mensadd.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pasta.mensadd.R;
import com.pasta.mensadd.adapter.MealListAdapter;
import com.pasta.mensadd.controller.DatabaseController;
import com.pasta.mensadd.controller.ParseController;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.model.Meal;
import com.pasta.mensadd.networking.LoadMealsCallback;
import com.pasta.mensadd.networking.NetworkController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MealDayFragment extends Fragment implements LoadMealsCallback {

    private static final String TAG_MENSA_ID = "mensaId";
    private static final String TAG_PAGER_POSITION = "pagerPosition";
    private String mMensaId;
    public MealListAdapter mMealListAdapter;
    private int mPagerPositon = 10;
    private SwipeRefreshLayout mMealRefresher;



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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_day, container, false);
        LinearLayoutManager layoutParams = new LinearLayoutManager(getActivity());
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.mealList);
        mMealRefresher = (SwipeRefreshLayout) view.findViewById(R.id.mealListRefresher);
        mMealRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NetworkController.getInstance(getActivity().getApplicationContext()).getMealsForCanteen(mMensaId, MealDayFragment.this);
            }
        });
        CardView noFoodToday = (CardView) view.findViewById(R.id.noFoodToday);
        if (getMeals().isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            noFoodToday.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            noFoodToday.setVisibility(View.GONE);
        }
        mMealListAdapter = new MealListAdapter(getMeals(),this);


        mRecyclerView.setLayoutManager(layoutParams);
        mRecyclerView.setAdapter(mMealListAdapter);

        return view;
    }

    public ArrayList<Meal> getMeals() {
        Date date = new Date();
        date.setTime(date.getTime()+mPagerPositon*86400000);

        if (DataHolder.getInstance().getMensa(mMensaId).getMealMap().get(ParseController.DATE_FORMAT.format(date)) == null) {
            DataHolder.getInstance().getMensa(mMensaId).getMealMap().put(ParseController.DATE_FORMAT.format(date), new ArrayList<Meal>());
        }
        return DataHolder.getInstance().getMensa(mMensaId).getMealMap().get(ParseController.DATE_FORMAT.format(date));
    }

    public void updateMealList(){
        if (mMealListAdapter != null) {
            mMealListAdapter.notifyDataSetChanged();
        }
    }

    public String getCanteenId(){
        return mMensaId;
    }

    @Override
    public void onResponseMessage(int responseType, String message) {
        if (responseType == NetworkController.SUCCESS) {
            ParseController p = new ParseController();
            if (p.parseMealsForCanteen(mMensaId, message, new DatabaseController(getActivity().getApplicationContext()))) {
                this.updateMealList();
            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Speisepläne konnten aus technischen Gründen nicht geladen werden.", Toast.LENGTH_SHORT).show();
        }
        mMealRefresher.setRefreshing(false);
    }
}
