package com.pasta.mensadd.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pasta.mensadd.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MealWeekFragment extends Fragment {

    private static final String TAG_MENSA_ID = "mensaId";
    private String mMensaId;
    private FragmentStatePagerAdapter mPagerAdapter;
    private ImageView mArrowLeft;
    private ImageView mArrowRight;
    private TextView mMensaName;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Calendar mCalendar = Calendar.getInstance();


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_week, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mPagerAdapter = new MealDayPagerAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.mealViewPager);
        mViewPager.setAdapter(mPagerAdapter);
        mArrowLeft = (ImageView) view.findViewById(R.id.arrow_left);
        mArrowRight = (ImageView) view.findViewById(R.id.arrow_right);
        //mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        //mTabLayout.setupWithViewPager(mViewPager);
        PagerTabStrip tabStrip = (PagerTabStrip)view.findViewById(R.id.pager_tab_strip);
        tabStrip.setTabIndicatorColorResource(R.color.colorPrimaryDark);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

                if (0.0 != positionOffset && positionOffset <= 0.25f) {
                    if (position == 5)
                        mArrowRight.setVisibility(View.VISIBLE);
                    if (position == 0)
                        mArrowLeft.setVisibility(View.INVISIBLE);
                    mArrowLeft.setAlpha(1 - 4 * positionOffset);
                    mArrowRight.setAlpha(1 - 4 * positionOffset);
                } else if (0.0 != positionOffset && positionOffset >= 0.75f) {
                    if (position == 0)
                        mArrowLeft.setVisibility(View.VISIBLE);
                    if (position == 5)
                        mArrowRight.setVisibility(View.INVISIBLE);
                    mArrowRight.setAlpha((-0.75f + positionOffset) * 4);
                    mArrowLeft.setAlpha((-0.75f + positionOffset) * 4);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }
        });
        int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1)
            dayOfWeek = 6;
        else
            dayOfWeek -= 2;
        mViewPager.setCurrentItem(dayOfWeek, true);

        if (dayOfWeek == 0)
            mArrowLeft.setVisibility(View.INVISIBLE);
        if (dayOfWeek == 6)
            mArrowRight.setVisibility(View.INVISIBLE);
        mMensaName = (TextView) view.findViewById(R.id.mensaName);
        mMensaName.setText(mMensaId);
    }

    class MealDayPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public MealDayPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentList.add(new MealDayFragment());
            mFragmentList.add(new MealDayFragment());
            mFragmentList.add(new MealDayFragment());
            mFragmentList.add(new MealDayFragment());
            mFragmentList.add(new MealDayFragment());
            mFragmentList.add(new MealDayFragment());
            mFragmentList.add(new MealDayFragment());
            mFragmentTitleList.add("Montag, 1. April 2016");
            mFragmentTitleList.add("Dienstag, 2. April 2016");
            mFragmentTitleList.add("Mittwoch, 3. April 2016");
            mFragmentTitleList.add("Donnerstag, 4. April 2016");
            mFragmentTitleList.add("Freitag, 5. April 2016");
            mFragmentTitleList.add("Samstag, 6. April 2016");
            mFragmentTitleList.add("Sonntag, 7. April 2016");

        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public Fragment getItem(int position) {
            return new MealDayFragment();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
