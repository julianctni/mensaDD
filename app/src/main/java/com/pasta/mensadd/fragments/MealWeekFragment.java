package com.pasta.mensadd.fragments;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.pasta.mensadd.controller.DatabaseController;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.model.Meal;
import com.pasta.mensadd.model.Canteen;
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
    private Canteen mCanteen;
    private MealDayPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private Calendar mCalendar = Calendar.getInstance();
    private PagerTabStrip mTabStrip;
    private DateFormat mDateFormat = new SimpleDateFormat("dd-MM-yyyy");
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
        mCanteen = DataHolder.getInstance().getMensa(mMensaId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_week, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.mealViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPagerAdapter.updateList(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabStrip = (PagerTabStrip)view.findViewById(R.id.pager_tab_strip);
        mTabStrip.setTabIndicatorColorResource(R.color.colorPrimaryDark);


        TextView header = (TextView)getActivity().findViewById(R.id.heading_toolbar);
        header.setText(mCanteen.getName());
        header.setVisibility(View.VISIBLE);
        ImageView appLogo = (ImageView)getActivity().findViewById(R.id.home_button);
        appLogo.setVisibility(View.GONE);
        mProgressLayout = (LinearLayout) view.findViewById(R.id.mealListProgressLayout);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.mealListrogressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#CCCCCC"), PorterDuff.Mode.MULTIPLY);
        NetworkController.getInstance(getActivity().getApplicationContext()).getMealsForCanteen("http://www.julianctni.xyz/mensadd/meals/"+ mCanteen.getCode()+".json", this);
    }

    @Override
    public void onResponseMessage(int responseType, String message) {
        DatabaseController dbController = new DatabaseController(getActivity().getApplicationContext());
        if (responseType == NetworkController.SUCCESS) {
            try {
                JSONArray mealDays = new JSONArray(message);
                dbController.deleteMealsOfCanteen(mCanteen.getCode());
                for (int i=0; i<mealDays.length();i++){
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
                        Meal meal = new Meal(name, imgLink, details, price, mCanteen.getCode(), String.valueOf(mealDay.keys().next()), vegan==1, vegetarian==1, pork==1, beef==1, garlic==1, alcohol==1);
                        mealList.add(meal);
                        dbController.updateMealTable(meal);
                    }
                    mCanteen.getMealMap().put(String.valueOf(mealDay.keys().next()), mealList);
                    mCalendar.add(Calendar.DATE, 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            dbController.readMealsFromDb(mCanteen.getCode());
        }
        mProgressLayout.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        if (mViewPager.getAdapter() == null) {
            mPagerAdapter = new MealDayPagerAdapter(getChildFragmentManager());
            mViewPager.setAdapter(mPagerAdapter);
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
            for (int d = 0; d<8; d++) {
                mFragmentList.add(MealDayFragment.newInstance(mMensaId,d));
                mFragmentTitleList.add(sdf.format(mCalendar.getTime()));
                mCalendar.add(Calendar.DATE,1);
            }
        }

        public void updateList(int position){
            mFragmentList.get(position).updateMealList();

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
