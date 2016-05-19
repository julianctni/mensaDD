package com.pasta.mensadd.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pasta.mensadd.R;
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
    Calendar cal = Calendar.getInstance();
    ArrayList<Meal> meals = new ArrayList<Meal>();
    Date dateOfYear;
    int mId;
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd.MM.yyyy",
            Locale.GERMANY);
    Meal currentMeal;
    int mPagerPositon;


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
        getMeals(mPagerPositon,0);
        TextView tv = (TextView) view.findViewById(R.id.dateOfDay);
        this.setDate(tv);
        TextView v = (TextView) view.findViewById(R.id.no_food_today);
        if (meals.isEmpty())
            v.setVisibility(View.VISIBLE);

        return view;
    }

    public ArrayList<Meal> getMeals(int position, int mId) {
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1)
            dayOfWeek = 6;
        else
            dayOfWeek -= 2;

        cal.add(Calendar.DAY_OF_YEAR, (position - dayOfWeek));

        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        /*
        try {
            return MensaRepo.getMensaRepo().getMensaMap().get(mId).getmealMap().get(dayOfYear);
        } catch (Exception e){
            return new ArrayList<Meal>();
        }
        */
        return new ArrayList<Meal>();
    }

    public void setDate(TextView tv) {
        // TextView tv = (TextView)getActivity().findViewById(R.id.dateOfDay);
        tv.setText(sdf.format(cal.getTime()));
    }
}
