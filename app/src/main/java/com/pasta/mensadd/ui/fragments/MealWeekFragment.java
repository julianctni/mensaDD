package com.pasta.mensadd.ui.fragments;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.pasta.mensadd.database.repository.CanteenRepository;
import com.pasta.mensadd.database.repository.MealRepository;
import com.pasta.mensadd.networking.ApiServiceClient;
import com.pasta.mensadd.ui.MainActivity;
import com.pasta.mensadd.ui.viewmodel.MealsViewModel;
import com.pasta.mensadd.ui.viewmodel.MealsViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.pasta.mensadd.networking.ApiServiceClient.FETCH_ERROR;
import static com.pasta.mensadd.ui.viewmodel.MealsViewModel.ARGS_KEY_CANTEEN_ID;


public class MealWeekFragment extends Fragment {
    private static final int PAGE_COUNT = 5;
    private Toolbar mToolbar;
    private MealsViewModel mMealsViewModel;
    private boolean mFavoriteClicked = false;
    private MenuItem mFavMenuItem;

    public static MealWeekFragment newInstance(String canteenId) {
        MealWeekFragment mealWeekFragment = new MealWeekFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_KEY_CANTEEN_ID, canteenId);
        mealWeekFragment.setArguments(args);
        return mealWeekFragment;
    }

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
        String canteenId = getArguments() != null ? getArguments().getString(ARGS_KEY_CANTEEN_ID) : "";
        ApiServiceClient apiServiceClient = ApiServiceClient.getInstance(getString(R.string.api_base_url), getString(R.string.api_user), getString(R.string.api_key));
        MealRepository mealRepository = new MealRepository(
                AppDatabase.getInstance(requireContext()),
                apiServiceClient,
                canteenId
        );
        CanteenRepository canteenRepository = new CanteenRepository(
                AppDatabase.getInstance(requireContext()),
                new PreferenceService(requireContext()),
                apiServiceClient
        );
        Bundle bundle = getArguments() == null ? savedInstanceState : getArguments();
        MealsViewModelFactory mealsViewModelFactory = new MealsViewModelFactory(this, bundle, mealRepository, canteenRepository);
        mMealsViewModel = new ViewModelProvider(this, mealsViewModelFactory).get(MealsViewModel.class);
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
        mFavMenuItem = menu.findItem(R.id.set_canteen_favorite);
        startObservingModel();
    }

    public void startObservingModel() {
        mMealsViewModel.getFetchState().observe(getViewLifecycleOwner(), fetchState -> {
            if (fetchState == FETCH_ERROR) {
                int errorMsgId = !Utils.isOnline(requireContext()) ? R.string.error_no_internet : R.string.error_unknown;
                Toast.makeText(requireContext(), getString(R.string.error_fetching_meals, getString(errorMsgId)), Toast.LENGTH_SHORT).show();
            }
        });
        mMealsViewModel.getCanteenAsLiveData().observe(getViewLifecycleOwner(), (canteen) -> {
            ((MainActivity) requireActivity()).setToolbarContent(canteen.getName());
            int iconId = canteen.isFavorite() ? R.drawable.ic_baseline_favorite_24 : R.drawable.ic_baseline_favorite_border_24;
            int colorId = canteen.isFavorite() ? R.color.pink_dark : R.color.white;
            mFavMenuItem.setIcon(iconId);
            mFavMenuItem.getIcon().setColorFilter(ContextCompat.getColor(requireContext(), colorId), PorterDuff.Mode.SRC_IN);
            if (mFavoriteClicked) {
                View favButton = mToolbar.findViewById(R.id.set_canteen_favorite);
                favButton.startAnimation(Utils.getFavoriteScaleOutAnimation(favButton));
                mFavoriteClicked = false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_canteen_favorite:
                mMealsViewModel.toggleCanteenAsFavorite();
                mFavoriteClicked = true;
                return true;
            case R.id.menu_item_meals_refresh:
                mMealsViewModel.triggerMealFetching();
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
