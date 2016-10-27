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

import com.pasta.mensadd.MainActivity;
import com.pasta.mensadd.R;
import com.pasta.mensadd.controller.DatabaseController;
import com.pasta.mensadd.controller.ParseController;
import com.pasta.mensadd.model.Canteen;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.networking.LoadMealsCallback;
import com.pasta.mensadd.networking.NetworkController;

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
    private LinearLayout mProgressLayout;
    private DatabaseController mDatabaseController;


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
        MainActivity.hideToolbarShadow(true);
        mDatabaseController = new DatabaseController(getActivity().getApplicationContext());
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
        PagerTabStrip mTabStrip = (PagerTabStrip) view.findViewById(R.id.pager_tab_strip);
        mTabStrip.setTabIndicatorColorResource(R.color.colorPrimaryDark);


        TextView header = (TextView)getActivity().findViewById(R.id.heading_toolbar);
        header.setText(mCanteen.getName());
        header.setVisibility(View.VISIBLE);
        ImageView appLogo = (ImageView)getActivity().findViewById(R.id.toolbarImage);
        appLogo.setVisibility(View.GONE);
        mProgressLayout = (LinearLayout) view.findViewById(R.id.mealListProgressLayout);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.canteenListProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#CCCCCC"), PorterDuff.Mode.MULTIPLY);
        if (mCanteen.getMealMap().isEmpty()) {
            NetworkController.getInstance(getActivity().getApplicationContext()).getMealsForCanteen(mCanteen.getCode(), this);
            Log.i("Loading meals", "Loading meals from server...");
        } else {
            if (mCanteen.getLastMealUpdate() < new Date().getTime() - 240000) {
                NetworkController.getInstance(getActivity().getApplicationContext()).getMealsForCanteen(mCanteen.getCode(), this);
                Log.i("LOADING MEALS", "List not empty, getting meals from server");
            } else {
                Log.i("LOADING MEALS", "MealMap not empty, no refresh needed");
                mProgressLayout.setVisibility(View.GONE);
                mViewPager.setVisibility(View.VISIBLE);
                if (mViewPager.getAdapter() == null) {
                    mPagerAdapter = new MealDayPagerAdapter(getChildFragmentManager());
                    mViewPager.setAdapter(mPagerAdapter);
                }
            }
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        //NetworkController.getInstance(getActivity().getApplicationContext()).getMealsForCanteen(mCanteen.getCode(), this);
    }

    @Override
    public void onResponseMessage(int responseType, String message) {
        if (getActivity() == null) {
            return;
        }

        if (mViewPager.getAdapter() == null) {
            mPagerAdapter = new MealDayPagerAdapter(getChildFragmentManager());
            mViewPager.setAdapter(mPagerAdapter);
        }

        if (responseType == NetworkController.SUCCESS) {
            Log.i("Loading meals", "Received data, start parsing");
            ParseController p = new ParseController();
            p.parseMealsForCanteen(mCanteen.getCode(), message, mDatabaseController, this);
        } else if (responseType == ParseController.PARSE_SUCCESS) {
            Log.i("Loading meals", "Finished parsing, showing content");
            mProgressLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
            mCanteen.setLastMealUpdate(new Date().getTime());
        } else {
            mDatabaseController.readMealsFromDb(mCanteen.getCode());
        }
    }

    class MealDayPagerAdapter extends FragmentPagerAdapter {
        private final List<MealDayFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public MealDayPagerAdapter(FragmentManager fm) {
            super(fm);
            Locale locale;
            String dateFormat;
            if (Locale.getDefault().getLanguage().equals("de")) {
                locale = Locale.GERMANY;
                dateFormat = "EEEE, dd.MM.yyyy";
            } else {
                locale = Locale.ENGLISH;
                dateFormat = "EEEE, MM-dd-yyyy";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, locale);
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
