package com.pasta.mensadd.fragments;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
        PagerTabStrip mTabStrip = (PagerTabStrip) view.findViewById(R.id.pager_tab_strip);
        mTabStrip.setTabIndicatorColorResource(R.color.colorPrimaryDark);


        TextView header = (TextView)getActivity().findViewById(R.id.heading_toolbar);
        header.setText(mCanteen.getName());
        header.setVisibility(View.VISIBLE);
        ImageView appLogo = (ImageView)getActivity().findViewById(R.id.home_button);
        appLogo.setVisibility(View.GONE);
        mProgressLayout = (LinearLayout) view.findViewById(R.id.mealListProgressLayout);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.mealListrogressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#CCCCCC"), PorterDuff.Mode.MULTIPLY);
        NetworkController.getInstance(getActivity().getApplicationContext()).getMealsForCanteen(mCanteen.getCode(), this);
    }

    @Override
    public void onResponseMessage(int responseType, String message) {
        if (getActivity() == null) {
            return;
        }
        DatabaseController dbController = new DatabaseController(getActivity().getApplicationContext());
        if (responseType == NetworkController.SUCCESS) {
            ParseController p = new ParseController();
            p.parseMealsForCanteen(mCanteen.getCode(), message, new DatabaseController(this.getActivity().getApplicationContext()));
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
