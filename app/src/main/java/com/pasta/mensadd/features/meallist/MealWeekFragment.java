package com.pasta.mensadd.features.meallist;


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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pasta.mensadd.AppDatabase;
import com.pasta.mensadd.MainActivity;
import com.pasta.mensadd.PreferenceService;
import com.pasta.mensadd.R;
import com.pasta.mensadd.Utils;
import com.pasta.mensadd.domain.ApiService;
import com.pasta.mensadd.domain.canteen.CanteenRepository;
import com.pasta.mensadd.domain.meal.MealRepository;
import com.pasta.mensadd.network.ServiceGenerator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.pasta.mensadd.domain.ApiRepository.FETCH_ERROR;
import static com.pasta.mensadd.features.meallist.MealsViewModel.ARGS_KEY_CANTEEN_ID;


public class MealWeekFragment extends Fragment {
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
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        ViewPager2 mViewPager = view.findViewById(R.id.mealViewPager);
        mToolbar = requireActivity().findViewById(R.id.toolbar_mainActivity);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        String canteenId = getArguments() != null ? getArguments().getString(ARGS_KEY_CANTEEN_ID) : "";
        MealRepository mealRepository = new MealRepository(
                AppDatabase.getInstance(requireContext()),
                ServiceGenerator.createService(ApiService.class),
                canteenId
        );
        CanteenRepository canteenRepository = new CanteenRepository(
                AppDatabase.getInstance(requireContext()),
                new PreferenceService(requireContext()),
                ServiceGenerator.createService(ApiService.class)
        );
        Bundle bundle = getArguments() == null ? savedInstanceState : getArguments();
        MealsViewModelFactory mealsViewModelFactory = new MealsViewModelFactory(this, bundle, mealRepository, canteenRepository);
        mMealsViewModel = new ViewModelProvider(this, mealsViewModelFactory).get(MealsViewModel.class);
        mViewPager.setOffscreenPageLimit(2);
        MealDayPagerAdapter mPagerAdapter = new MealDayPagerAdapter(getChildFragmentManager(), getLifecycle());
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout mTabLayout = view.findViewById(R.id.pager_tab_strip);
        new TabLayoutMediator(mTabLayout, mViewPager,
                (tab, position) -> tab.setText(getTabText(position))
        ).attach();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_meals_menu, menu);
        mFavMenuItem = menu.findItem(R.id.set_canteen_favorite);
        startObservingModel();
    }

    private void startObservingModel() {
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
            Objects.requireNonNull(mFavMenuItem.getIcon()).setColorFilter(ContextCompat.getColor(requireContext(), colorId), PorterDuff.Mode.SRC_IN);
            if (mFavoriteClicked) {
                View favButton = mToolbar.findViewById(R.id.set_canteen_favorite);
                favButton.startAnimation(Utils.getFavoriteScaleOutAnimation(favButton));
                mFavoriteClicked = false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.set_canteen_favorite) {
            mMealsViewModel.toggleCanteenAsFavorite();
            mFavoriteClicked = true;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getTabText(int position) {
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
        cal.add(Calendar.DATE, position);
        String title;
        if (position == 0) {
            title = getString(R.string.today);
        } else if (position == 1) {
            title = getString(R.string.tomorrow);
        } else {
            title = sdf.format(cal.getTime());
        }
        return title;
    }

    static class MealDayPagerAdapter extends FragmentStateAdapter {
        private static final int PAGE_COUNT = 5;

        public MealDayPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return MealDayFragment.newInstance(position);
        }

        @Override
        public int getItemCount() {
            return PAGE_COUNT;
        }
    }

}
