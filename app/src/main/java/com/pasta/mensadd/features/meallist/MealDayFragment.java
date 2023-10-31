package com.pasta.mensadd.features.meallist;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.pasta.mensadd.PullToRefreshFragment;
import com.pasta.mensadd.R;
import com.pasta.mensadd.domain.meal.Meal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.pasta.mensadd.domain.ApiRepository.FETCH_SUCCESS;
import static com.pasta.mensadd.domain.ApiRepository.IS_FETCHING;
import static com.pasta.mensadd.domain.ApiRepository.NOT_FETCHING;

public class MealDayFragment extends PullToRefreshFragment {

    private static final String TAG_PAGER_POSITION = "pagerPosition";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);
    private static final int ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
    private MealListAdapter mMealListAdapter;
    private int mPagerPosition = 10;
    //private CardView noFoodToday;
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
            mPagerPosition = getArguments().getInt(TAG_PAGER_POSITION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        View view = inflater.inflate(R.layout.fragment_meal_day, container, false);
        mMealsViewModel = new ViewModelProvider(requireParentFragment()).get(MealsViewModel.class);
        mRefreshText = view.findViewById(R.id.mealListRefreshText);
        mRecyclerView = view.findViewById(R.id.mealList);
        //noFoodToday = view.findViewById(R.id.noFoodToday);
        mMealListAdapter = new MealListAdapter(this.getContext());
        mRecyclerView.setAdapter(mMealListAdapter);
        mRecyclerView.setNestedScrollingEnabled(true);
        super.setUpPullToRefresh(R.string.meals_wanna_refresh, R.string.meals_release_to_refresh);
        Date date = new Date();
        date.setTime(date.getTime() + (long) mPagerPosition * ONE_DAY_IN_MILLIS);
        String day = DATE_FORMAT.format(date);
        mMealsViewModel.getMealsByDay(day).observe(getViewLifecycleOwner(), meals -> {
            //noinspection ConstantConditions
            //showNoFoodToday(meals.isEmpty() && (mMealsViewModel.getFetchState().getValue() == FETCH_SUCCESS || mMealsViewModel.getFetchState().getValue() == NOT_FETCHING));
            if (meals.isEmpty() && (mMealsViewModel.getFetchState().getValue() == FETCH_SUCCESS || mMealsViewModel.getFetchState().getValue() == NOT_FETCHING)) {
                ArrayList<Meal> emptyList = new ArrayList<>();
                emptyList.add(Meal.getEmptyMeal());
                mMealListAdapter.submitList(emptyList);
            } else {
                mMealListAdapter.submitList(meals);
            }
        });

        mMealsViewModel.getFetchState().observe(getViewLifecycleOwner(), fetchState -> {
            ProgressBar progressBar = view.findViewById(R.id.mealListProgressBar);
            if (progressBar.getVisibility() == View.VISIBLE && fetchState != IS_FETCHING) {
                Handler handler = new Handler();
                handler.postDelayed(() -> progressBar.setVisibility(View.GONE), 2000);
            } else {
                progressBar.setVisibility(fetchState == IS_FETCHING ? View.VISIBLE : View.GONE);
            }
        });
        mMealsViewModel.getCanteenAsLiveData().observe(getViewLifecycleOwner(), canteen -> mMealListAdapter.setLastMealUpdate(canteen.getLastMealScraping()));
        return view;
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        mMealsViewModel.triggerMealFetching();
    }
}
