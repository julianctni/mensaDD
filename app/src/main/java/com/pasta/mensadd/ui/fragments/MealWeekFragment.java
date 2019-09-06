package com.pasta.mensadd.ui.fragments;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import com.pasta.mensadd.R;
import com.pasta.mensadd.Utils;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.entity.Meal;
import com.pasta.mensadd.networking.callbacks.LoadMealsCallback;
import com.pasta.mensadd.ui.viewmodel.CanteensViewModel;
import com.pasta.mensadd.ui.viewmodel.MealsViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class MealWeekFragment extends Fragment implements LoadMealsCallback {

    private static final int PAGE_COUNT = 5;
    private MealDayPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private Calendar mCalendar = Calendar.getInstance();
    private LinearLayout mProgressLayout;
    private Toolbar mToolbar;

    private MealsViewModel mMealsViewModel;
    private CanteensViewModel mCanteensViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_meal_week, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        //mDatabaseController = new DatabaseController(getContext());
        mViewPager = view.findViewById(R.id.mealViewPager);
        mToolbar = getActivity().findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        mMealsViewModel = new ViewModelProvider(this).get(MealsViewModel.class);
        mCanteensViewModel = new ViewModelProvider(getActivity()).get(CanteensViewModel.class);
        mMealsViewModel.refreshMeals(mCanteensViewModel.getSelectedCanteen());
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
        PagerTabStrip mTabStrip = view.findViewById(R.id.pager_tab_strip);
        mTabStrip.setTabIndicatorColorResource(R.color.colorPrimary);
        TextView header = view.getRootView().findViewById(R.id.heading_toolbar);
        header.setText(mCanteensViewModel.getSelectedCanteen().getName());
        header.setVisibility(View.VISIBLE);
        view.getRootView().findViewById(R.id.toolbarImage).setVisibility(View.GONE);
        mProgressLayout = view.findViewById(R.id.mealListProgressLayout);
        ProgressBar progressBar = view.findViewById(R.id.canteenListProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#CCCCCC"), PorterDuff.Mode.MULTIPLY);

        /*
        if (mCanteen.getMealMap().isEmpty()) {
            NetworkController.getInstance(getContext()).fetchMeals(mCanteen.getId(), this);
            Log.i("Loading meals", "Loading meals from server...");
        } else {
            if (mCanteen.getLastMealUpdate() < new Date().getTime() - 240000) {
                NetworkController.getInstance(getContext()).fetchMeals(mCanteen.getId(), this);
                Log.i("LOADING MEALS", "List not empty, getting meals from server");
            } else {
                Log.i("LOADING MEALS", "MealMap not empty, no refresh needed");

            }
        }*/
        mProgressLayout.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        if (mViewPager.getAdapter() == null) {
            mPagerAdapter = new MealDayPagerAdapter(getChildFragmentManager());
            mViewPager.setAdapter(mPagerAdapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_meals_menu, menu);
        if (mCanteensViewModel.getSelectedCanteen().isFavorite()) {
            menu.findItem(R.id.set_canteen_favorite).setIcon(R.drawable.ic_favorite_pink_24dp);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_canteen_favorite:
                Canteen canteen = mCanteensViewModel.getSelectedCanteen();
                int iconId = canteen.isFavorite() ? R.drawable.ic_favorite_border_white_24dp : R.drawable.ic_favorite_pink_24dp;
                item.setIcon(iconId);
                canteen.setAsFavorite(!canteen.isFavorite());
                mCanteensViewModel.updateCanteen(canteen);
                View favButton = mToolbar.findViewById(R.id.set_canteen_favorite);
                favButton.startAnimation(Utils.getFavoriteScaleOutAnimation(favButton));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onResponseMessage(int responseType, String message) {
        /*
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
            p.parseMealsForCanteen(mCanteen.getId(), message, mDatabaseController, this);
        } else if (responseType == ParseController.PARSE_SUCCESS) {
            Log.i("Loading meals", "Finished parsing, showing content");
            mProgressLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
            mCanteen.setLastMealUpdate(new Date().getTime());
        } else {
            mDatabaseController.readMealsFromDb(mCanteen.getId());
        }*/
    }

    class MealDayPagerAdapter extends FragmentPagerAdapter {
        private final List<MealDayFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        MealDayPagerAdapter(FragmentManager fm) {
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
            for (int d = 0; d <= PAGE_COUNT; d++) {
                mFragmentList.add(MealDayFragment.newInstance(mCanteensViewModel.getSelectedCanteen().getId(), d));
                mFragmentTitleList.add(sdf.format(mCalendar.getTime()));
                mCalendar.add(Calendar.DATE, 1);
            }
        }

        void updateList(int position) {
            mFragmentList.get(position).updateMealList();
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
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
