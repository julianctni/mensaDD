package com.pasta.mensadd.ui.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pasta.mensadd.R;
import com.pasta.mensadd.ui.adapter.MealListAdapter;
import com.pasta.mensadd.controller.DatabaseController;
import com.pasta.mensadd.controller.ParseController;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.database.entity.Meal;
import com.pasta.mensadd.networking.callbacks.LoadMealsCallback;
import com.pasta.mensadd.networking.NetworkController;
import com.pasta.mensadd.ui.viewmodel.CanteensViewModel;
import com.pasta.mensadd.ui.viewmodel.MealsViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MealDayFragment extends Fragment implements LoadMealsCallback {

    private static final String TAG_MENSA_ID = "mensaId";
    private static final String TAG_PAGER_POSITION = "pagerPosition";
    private String mMensaId;
    public MealListAdapter mMealListAdapter;
    private int mPagerPositon = 10;
    private SwipeRefreshLayout mMealRefresher;
    RecyclerView mRecyclerView;
    CardView noFoodToday;

    MealsViewModel mMealsViewModel;
    CanteensViewModel mCanteensViewModel;

    public MealDayFragment() {
    }

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        View view = inflater.inflate(R.layout.fragment_meal_day, container, false);
        mRecyclerView = view.findViewById(R.id.mealList);
        mMealRefresher = view.findViewById(R.id.mealListRefresher);
        mMealRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NetworkController.getInstance(getContext()).fetchMeals(mMensaId, MealDayFragment.this);
            }
        });
        mMealsViewModel = new ViewModelProvider(this).get(MealsViewModel.class);
        mCanteensViewModel = new ViewModelProvider(getActivity()).get(CanteensViewModel.class);
        noFoodToday = view.findViewById(R.id.noFoodToday);
        if (mMealListAdapter == null) {
            mMealListAdapter = new MealListAdapter(new ArrayList<Meal>(), MealDayFragment.this);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(mMealListAdapter);
        }
        updateMeals(mMealsViewModel.getMealsByCanteen(mCanteensViewModel.getSelectedCanteen()).getValue());

        mMealsViewModel.getMealsByCanteen(mCanteensViewModel.getSelectedCanteen()).observe(getParentFragment(), new Observer<List<Meal>>() {
            @Override
            public void onChanged(List<Meal> meals) {
                updateMeals(meals);
                Log.i("CHANGE", "HEY");
            }
        });

        return view;
    }

    public void updateMeals(List<Meal> meals) {
        if (meals == null) return;
        List<Meal> mealsOfDay = new ArrayList<>();
        Date date = new Date();
        date.setTime(date.getTime() + mPagerPositon * 86400000);
        for (Meal m : meals) {
            if (m.getDate().equals(ParseController.DATE_FORMAT.format(date))) {
                mealsOfDay.add(m);
            }
        }
        if (mealsOfDay.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            noFoodToday.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            noFoodToday.setVisibility(View.GONE);
        }
        Log.i("CHANGE", mealsOfDay.size()+ " ");
        mMealListAdapter.setMeals(mealsOfDay);
    }

    public ArrayList<Meal> getMeals() {
        Date date = new Date();
        date.setTime(date.getTime() + mPagerPositon * 86400000);
        if (DataHolder.getInstance().getCanteen(mMensaId).getMealMap().get(ParseController.DATE_FORMAT.format(date)) == null) {
            DataHolder.getInstance().getCanteen(mMensaId).getMealMap().put(ParseController.DATE_FORMAT.format(date), new ArrayList<Meal>());
        }
        return DataHolder.getInstance().getCanteen(mMensaId).getMealMap().get(ParseController.DATE_FORMAT.format(date));
    }

    public void updateMealList() {
        return;
    }

    public String getCanteenId() {
        return mMensaId;
    }

    @Override
    public void onResponseMessage(int responseType, String message) {
        /*
        if (responseType == NetworkController.SUCCESS) {
            ParseController p = new ParseController();
            p.parseMealsForCanteen(mMensaId, message, new DatabaseController(getContext()), this);
        } else if (responseType == NetworkController.ERROR) {
            Toast.makeText(getContext(), getString(R.string.load_meals_technical), Toast.LENGTH_SHORT).show();
        } else if (responseType == ParseController.PARSE_SUCCESS) {
            mMealRefresher.setRefreshing(false);
            this.updateMealList();
        } else {
            DatabaseController dbController = new DatabaseController(getContext());
            dbController.readMealsFromDb(mMensaId);
            mMealRefresher.setRefreshing(false);
        }
*/
    }
}
