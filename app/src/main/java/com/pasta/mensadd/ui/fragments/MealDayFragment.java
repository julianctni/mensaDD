package com.pasta.mensadd.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pasta.mensadd.R;
import com.pasta.mensadd.Utils;
import com.pasta.mensadd.database.entity.Meal;
import com.pasta.mensadd.ui.adapter.MealListAdapter;
import com.pasta.mensadd.ui.viewmodel.CanteensViewModel;
import com.pasta.mensadd.ui.viewmodel.MealsViewModel;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MealDayFragment extends Fragment {

    private static final String TAG_PAGER_POSITION = "pagerPosition";
    private ProgressBar mProgressBar;
    private MealListAdapter mMealListAdapter;
    private int mPagerPositon = 10;
    private RecyclerView mRecyclerView;
    private CardView noFoodToday;

    private MealsViewModel mMealsViewModel;

    static MealDayFragment newInstance(int position) {
        MealDayFragment fragment = new MealDayFragment();
        Bundle args = new Bundle();
        args.putInt(TAG_PAGER_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPagerPositon = getArguments().getInt(TAG_PAGER_POSITION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        View view = inflater.inflate(R.layout.fragment_meal_day, container, false);
        mMealsViewModel = new ViewModelProvider(this).get(MealsViewModel.class);
        CanteensViewModel mCanteensViewModel = new ViewModelProvider(getActivity()).get(CanteensViewModel.class);
        mRecyclerView = view.findViewById(R.id.mealList);
        mProgressBar = view.findViewById(R.id.mealListProgressBar);
        mProgressBar.setVisibility(mMealsViewModel.isRefreshing() ? View.VISIBLE : View.GONE);
        noFoodToday = view.findViewById(R.id.noFoodToday);
        mMealListAdapter = new MealListAdapter(this.getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mMealListAdapter);
        mRecyclerView.setNestedScrollingEnabled(true);

        Date date = new Date();
        date.setTime(date.getTime() + mPagerPositon * 86400000);
        String day = MealsViewModel.DATE_FORMAT.format(date);
        mMealsViewModel.getMealsByCanteenByDay(mCanteensViewModel.getSelectedCanteen(), day).observe(this, meals -> {
            updateMeals(meals);
            long lastUpdate = mCanteensViewModel.getSelectedCanteen().getLastMealScraping();
            if (lastUpdate != 0) {
                mMealListAdapter.setLastMealUpdate(lastUpdate);
            }
        });

        return view;
    }

    private void updateMeals(List<Meal> meals) {
        if (meals == null) return;
        if (meals.isEmpty() && !mMealsViewModel.isRefreshing()) {
            mRecyclerView.setVisibility(View.GONE);
            noFoodToday.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            noFoodToday.setVisibility(View.GONE);
        }
        mProgressBar.setVisibility(mMealsViewModel.isRefreshing() ? View.VISIBLE : View.GONE);
        mMealListAdapter.submitList(meals);
    }
}
