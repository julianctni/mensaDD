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

import static com.pasta.mensadd.database.repository.MealRepository.DATE_FORMAT;

public class MealDayFragment extends Fragment {

    private static final String TAG_PAGER_POSITION = "pagerPosition";
    private MealListAdapter mMealListAdapter;
    private int mPagerPositon = 10;
    private RecyclerView mRecyclerView;
    private CardView noFoodToday;
    private static final int ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

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
        mMealsViewModel = new ViewModelProvider(requireParentFragment()).get(MealsViewModel.class);
        mRecyclerView = view.findViewById(R.id.mealList);
        noFoodToday = view.findViewById(R.id.noFoodToday);
        mMealListAdapter = new MealListAdapter(this.getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mMealListAdapter);
        mRecyclerView.setNestedScrollingEnabled(true);

        Date date = new Date();
        date.setTime(date.getTime() + mPagerPositon * ONE_DAY_IN_MILLIS);
        String day = DATE_FORMAT.format(date);
        mMealsViewModel.getMealsByDay(day).observe(getViewLifecycleOwner(), meals -> {
            updateMeals(meals);
        });
        mMealsViewModel.isRefreshing().observe(getViewLifecycleOwner(), isRefreshing -> {
            ProgressBar progressBar = view.findViewById(R.id.mealListProgressBar);
            progressBar.setVisibility(isRefreshing ? View.VISIBLE : View.GONE);
        });
        mMealsViewModel.getCanteenAsLiveData().observe(getViewLifecycleOwner(), canteen -> mMealListAdapter.setLastMealUpdate(canteen.getLastMealUpdate()));
        return view;
    }

    private void updateMeals(List<Meal> meals) {
        if (meals == null) return;
        if (meals.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            noFoodToday.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            noFoodToday.setVisibility(View.GONE);
        }
        mMealListAdapter.submitList(meals);
    }
}
