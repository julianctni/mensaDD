package com.pasta.mensadd.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pasta.mensadd.R;
import com.pasta.mensadd.adapter.MealListAdapter;
import com.pasta.mensadd.controller.DatabaseController;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.model.Meal;
import com.pasta.mensadd.networking.LoadMealsCallback;
import com.pasta.mensadd.networking.NetworkController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MealDayFragment extends Fragment implements LoadMealsCallback {

    private static final String TAG_MENSA_ID = "mensaId";
    private static final String TAG_PAGER_POSITION = "pagerPosition";
    private String mMensaId;
    private LinearLayoutManager layoutParams;
    private Calendar cal = Calendar.getInstance();
    public MealListAdapter mMealListAdapter;
    private RecyclerView mRecyclerView;
    private int mPagerPositon = 10;
    private SwipeRefreshLayout mMealRefresher;
    private DateFormat mDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private Calendar mCalendar = Calendar.getInstance();



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
        layoutParams = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mealList);
        mMealRefresher = (SwipeRefreshLayout) view.findViewById(R.id.mealListRefresher);
        mMealRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NetworkController.getInstance(getActivity().getApplicationContext()).getMealsForCanteen("http://www.julianctni.xyz/mensadd/meals/"+ mMensaId+".json", MealDayFragment.this);
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

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY);
        if (DataHolder.getInstance().getMensa(mMensaId).getMealMap().get(sdf.format(date)) == null) {
            DataHolder.getInstance().getMensa(mMensaId).getMealMap().put(sdf.format(date), new ArrayList<Meal>());
        }
        return DataHolder.getInstance().getMensa(mMensaId).getMealMap().get(sdf.format(date));
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
        DatabaseController dbController = new DatabaseController(getActivity().getApplicationContext());
        if (responseType == NetworkController.SUCCESS) {
            try {
                mCalendar.setTime(new Date());
                JSONArray mealDays = new JSONArray(message);
                dbController.deleteMealsOfCanteen(mMensaId);
                for (ArrayList<Meal> list : DataHolder.getInstance().getMensa(mMensaId).getMealMap().values()){
                    list.clear();
                }
                for (int i=0; i<mealDays.length();i++){
                    JSONObject mealDay = mealDays.getJSONObject(i);
                    JSONArray meals = mealDay.getJSONArray(mDateFormat.format(mCalendar.getTime()));
                    if (!DataHolder.getInstance().getMensa(mMensaId).getMealMap().containsKey(String.valueOf(mealDay.keys().next())))
                        DataHolder.getInstance().getMensa(mMensaId).getMealMap().put(String.valueOf(mealDay.keys().next()), new ArrayList<Meal>());
                    for (int j=0; j<meals.length(); j++){
                        JSONObject jsonMeal = meals.getJSONObject(j);
                        int vegan = jsonMeal.getInt("vegan");
                        int vegetarian = jsonMeal.getInt("vegetarian");
                        int beef = jsonMeal.getInt("beef");
                        int pork = jsonMeal.getInt("porc");
                        int garlic = jsonMeal.getInt("garlic");
                        int alcohol = jsonMeal.getInt("alcohol");
                        String imgLink = jsonMeal.getString("imgLink");
                        String details = jsonMeal.getString("mealDetails");
                        String name = jsonMeal.getString("name");
                        String price = jsonMeal.getString("price");
                        Meal meal = new Meal(name, imgLink, details, price, mMensaId, String.valueOf(mealDay.keys().next()), vegan==1, vegetarian==1, pork==1, beef==1, garlic==1, alcohol==1);
                        DataHolder.getInstance().getMensa(mMensaId).getMealMap().get(String.valueOf(mealDay.keys().next())).add(meal);
                        dbController.updateMealTable(meal);
                    }
                    mCalendar.add(Calendar.DATE, 1);
                }
                this.updateMealList();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Speisepläne konnten aus technischen Gründen nicht geladen werden.", Toast.LENGTH_SHORT).show();
        }
        mMealRefresher.setRefreshing(false);

    }
}
