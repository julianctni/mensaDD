package com.pasta.mensadd.ui.fragments;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.pasta.mensadd.PreferenceService;
import com.pasta.mensadd.R;
import com.pasta.mensadd.Utils;
import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.repository.CanteenRepository;
import com.pasta.mensadd.database.repository.MealRepository;
import com.pasta.mensadd.networking.ApiServiceClient;
import com.pasta.mensadd.networking.NetworkController;
import com.pasta.mensadd.ui.MainActivity;
import com.pasta.mensadd.ui.viewmodel.CanteensViewModel;
import com.pasta.mensadd.ui.viewmodel.MealsViewModel;
import com.pasta.mensadd.ui.viewmodel.MealsViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MealWeekFragment extends Fragment {

    private static final int PAGE_COUNT = 5;
    private Toolbar mToolbar;
    private MealsViewModel mMealsViewModel;

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
        mToolbar = requireActivity().findViewById(R.id.toolbar_mainActivity);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        ApiServiceClient apiServiceClient = ApiServiceClient.getInstance(getString(R.string.api_base_url), getString(R.string.api_user), getString(R.string.api_key));
        CanteensViewModel canteensViewModel = new ViewModelProvider(requireActivity()).get(CanteensViewModel.class);
        MealRepository mealRepository = new MealRepository(
                AppDatabase.getInstance(requireContext()),
                apiServiceClient,
                canteensViewModel.getSelectedCanteen()
        );
        CanteenRepository canteenRepository = new CanteenRepository(
                AppDatabase.getInstance(requireContext()),
                new PreferenceService(requireContext()),
                apiServiceClient
        );
        MealsViewModelFactory mealsViewModelFactory = new MealsViewModelFactory(mealRepository, canteenRepository, canteensViewModel.getSelectedCanteen());
        mMealsViewModel = new ViewModelProvider(this, mealsViewModelFactory).get(MealsViewModel.class);
        //TextView header = view.getRootView().findViewById(R.id.text_toolbar_mainActivity);
        //header.setText(mMealsViewModel.getCanteen().getName());
        //header.setVisibility(View.VISIBLE);
        //view.getRootView().findViewById(R.id.image_toolbar_mainActivity).setVisibility(View.GONE);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.setToolbarContent(mMealsViewModel.getCanteen().getName());
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
        if (mMealsViewModel.getCanteen().isFavorite()) {
            menu.findItem(R.id.set_canteen_favorite).setIcon(R.drawable.ic_baseline_favorite_24);
            menu.findItem(R.id.set_canteen_favorite).getIcon().setColorFilter(ContextCompat.getColor(this.getContext(), R.color.pink_dark), PorterDuff.Mode.SRC_IN);
        } else {
            menu.findItem(R.id.set_canteen_favorite).setIcon(R.drawable.ic_baseline_favorite_border_24);
            menu.findItem(R.id.set_canteen_favorite).getIcon().setColorFilter(ContextCompat.getColor(this.getContext(), R.color.white), PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_canteen_favorite:
                Canteen canteen = mMealsViewModel.getCanteen();
                int favIconId = canteen.isFavorite() ? R.drawable.ic_baseline_favorite_border_24 : R.drawable.ic_baseline_favorite_24;
                int favIconColor = canteen.isFavorite() ? R.color.white : R.color.pink_dark;
                item.setIcon(favIconId);
                item.getIcon().setColorFilter(ContextCompat.getColor(this.getContext(), favIconColor), PorterDuff.Mode.SRC_IN);
                canteen.setAsFavorite(!canteen.isFavorite());
                mMealsViewModel.updateCanteen(canteen);
                View favButton = mToolbar.findViewById(R.id.set_canteen_favorite);
                favButton.startAnimation(Utils.getFavoriteScaleOutAnimation(favButton));
                return true;
        }
        return super.onOptionsItemSelected(item);
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
