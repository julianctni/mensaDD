package com.pasta.mensadd.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.pasta.mensadd.R;
import com.pasta.mensadd.Utils;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.ui.viewmodel.CanteensViewModel;
import com.pasta.mensadd.ui.viewmodel.MealsViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MealWeekFragment extends Fragment {

    private static final int PAGE_COUNT = 5;
    private Toolbar mToolbar;
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
        ViewPager mViewPager = view.findViewById(R.id.mealViewPager);
        if (getActivity() != null) {
            mToolbar = getActivity().findViewById(R.id.toolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            mToolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        }
        MealsViewModel mMealsViewModel = new ViewModelProvider(this).get(MealsViewModel.class);
        mCanteensViewModel = new ViewModelProvider(getActivity()).get(CanteensViewModel.class);
        mMealsViewModel.refreshMeals(mCanteensViewModel.getSelectedCanteen());
        TextView header = view.getRootView().findViewById(R.id.heading_toolbar);
        header.setText(mCanteensViewModel.getSelectedCanteen().getName());
        header.setVisibility(View.VISIBLE);
        view.getRootView().findViewById(R.id.toolbarImage).setVisibility(View.GONE);

        mViewPager.setVisibility(View.VISIBLE);
        if (mViewPager.getAdapter() == null) {
            MealDayPagerAdapter mPagerAdapter = new MealDayPagerAdapter(getChildFragmentManager());
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

    class MealDayPagerAdapter extends FragmentPagerAdapter {
        private final List<MealDayFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        MealDayPagerAdapter(FragmentManager fm) {
            super(fm);
            Locale locale;
            String dateFormat;
            if (Locale.getDefault().getLanguage().equals("de")) {
                locale = Locale.GERMANY;
                dateFormat = "EEE, dd.MM.";
            } else {
                locale = Locale.ENGLISH;
                dateFormat = "EEE, MM-dd";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, locale);
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            for (int d = 0; d <= PAGE_COUNT; d++) {
                mFragmentList.add(MealDayFragment.newInstance(d));
                String title = "";
                if (d == 0) {
                    title = getString(R.string.today);
                } else if (d == 1) {
                    title = getString(R.string.tomorrow);
                } else {
                    title = sdf.format(cal.getTime());
                }
                mFragmentTitleList.add(title);
                cal.add(Calendar.DATE, 1);
            }
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
