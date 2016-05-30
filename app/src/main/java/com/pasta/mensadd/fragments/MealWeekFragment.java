package com.pasta.mensadd.fragments;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pasta.mensadd.R;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.model.Meal;
import com.pasta.mensadd.model.Mensa;
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
import java.util.List;
import java.util.Locale;


public class MealWeekFragment extends Fragment implements LoadMealsCallback{

    private static final String TAG_MENSA_ID = "mensaId";
    private String mMensaId;
    private Mensa mMensa;
    private MealDayPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private Calendar mCalendar = Calendar.getInstance();
    private PagerTabStrip mTabStrip;
    private DateFormat mDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private Date mFirstDayOfWeekDate;
    private int mFirstDayOfWeekInt;
    private LinearLayout mProgressLayout;


    public MealWeekFragment() {
    }

    public static MealWeekFragment newInstance(String mensaId) {
        MealWeekFragment fragment = new MealWeekFragment();
        Bundle args = new Bundle();
        args.putString(TAG_MENSA_ID, mensaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMensaId = getArguments().getString(TAG_MENSA_ID);
        }
        mMensa = DataHolder.getInstance().getMensa(mMensaId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_week, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mCalendar.set(Calendar.DAY_OF_WEEK, 2);
        mFirstDayOfWeekDate = mCalendar.getTime();
        mFirstDayOfWeekInt = mCalendar.get(Calendar.DAY_OF_YEAR);
        mViewPager = (ViewPager) view.findViewById(R.id.mealViewPager);
        mTabStrip = (PagerTabStrip)view.findViewById(R.id.pager_tab_strip);
        mTabStrip.setTabIndicatorColorResource(R.color.colorPrimaryDark);

        TextView header = (TextView)getActivity().findViewById(R.id.heading_toolbar);
        header.setText(mMensa.getName());
        header.setVisibility(View.VISIBLE);
        ImageView appLogo = (ImageView)getActivity().findViewById(R.id.home_button);
        appLogo.setVisibility(View.GONE);
        mProgressLayout = (LinearLayout) view.findViewById(R.id.mealListProgressLayout);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.mealListrogressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#CCCCCC"), PorterDuff.Mode.MULTIPLY);
        NetworkController.getInstance(getActivity().getApplicationContext()).getMealsForCanteen("http://www.julianctni.xyz/mensadd/meals/"+mMensa.getCode()+".json", this);
    }

    @Override
    public void onResponseMessage(int responseType, String message) {
        if (responseType == NetworkController.SUCCESS) {
            try {
                JSONArray mealDays = new JSONArray(message);
                mCalendar.set(Calendar.DAY_OF_WEEK, 2);
                for (int i=0; i<mealDays.length();i++){
                    Log.i("MEAL-PARSING", "Parsing "+mDateFormat.format(mCalendar.getTime()));
                    Log.i("MEAL-PARSING", "Parsing "+mCalendar.get(Calendar.DAY_OF_YEAR));
                    JSONObject mealDay = mealDays.getJSONObject(i);
                    JSONArray meals = mealDay.getJSONArray(mDateFormat.format(mCalendar.getTime()));
                    ArrayList<Meal> mealList = new ArrayList<>();
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
                        Meal meal = new Meal(name, imgLink, details, price, vegan==1, vegetarian==1, pork==1, beef==1, garlic==1, alcohol==1);
                        mealList.add(meal);
                    }
                    mMensa.getmealMap().put(mCalendar.get(Calendar.DAY_OF_YEAR), mealList);
                    mCalendar.add(Calendar.DATE, 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mProgressLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
            mPagerAdapter = new MealDayPagerAdapter(getChildFragmentManager());
            mViewPager.setAdapter(mPagerAdapter);
            mCalendar.setTime(new Date());
            int today = mCalendar.get(Calendar.DAY_OF_WEEK);
            if (today == 1)
                today = 8;
            Log.i("CALENDAR", mCalendar.get(Calendar.DAY_OF_WEEK)+"");
            mViewPager.setCurrentItem(today-2, true);
        } else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }

    class MealDayPagerAdapter extends FragmentPagerAdapter {
        private final List<MealDayFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public MealDayPagerAdapter(FragmentManager fm) {
            super(fm);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd.MM.yyyy",
                    Locale.GERMANY);
            mCalendar.setTime(new Date());
            mCalendar.set(Calendar.DAY_OF_WEEK, 2);
            for (int d = 0; d<7; d++) {
                mFragmentList.add(MealDayFragment.newInstance(mMensaId,mFirstDayOfWeekInt+d));
                mFragmentTitleList.add(sdf.format(mCalendar.getTime()));
                mCalendar.add(Calendar.DATE,1);
            }
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
