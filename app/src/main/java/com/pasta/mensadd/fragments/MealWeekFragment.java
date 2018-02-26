package com.pasta.mensadd.fragments;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pasta.mensadd.R;
import com.pasta.mensadd.Utils;
import com.pasta.mensadd.controller.DatabaseController;
import com.pasta.mensadd.controller.FragmentController;
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


public class MealWeekFragment extends Fragment implements LoadMealsCallback {

    private static final String TAG_MENSA_ID = "mensaId";
    private static final int PAGE_COUNT = 5;
    private String mMensaId;
    private Canteen mCanteen;
    private MealDayPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private Calendar mCalendar = Calendar.getInstance();
    private LinearLayout mProgressLayout;
    private DatabaseController mDatabaseController;
    private Toolbar mToolbar;

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

        mCanteen = DataHolder.getInstance().getCanteen(mMensaId);
        if (mCanteen == null)
            FragmentController.showCanteenListFragment(this.getFragmentManager());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_meal_week, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mDatabaseController = new DatabaseController(getContext());
        mViewPager = view.findViewById(R.id.mealViewPager);
        mToolbar = getActivity().findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
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
        header.setText(mCanteen.getName());
        header.setVisibility(View.VISIBLE);
        ImageView appLogo = view.getRootView().findViewById(R.id.toolbarImage);
        appLogo.setVisibility(View.GONE);
        mProgressLayout = view.findViewById(R.id.mealListProgressLayout);
        ProgressBar progressBar = view.findViewById(R.id.canteenListProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#CCCCCC"), PorterDuff.Mode.MULTIPLY);
        if (mCanteen.getMealMap().isEmpty()) {
            NetworkController.getInstance(getContext()).getMealsForCanteen(mCanteen.getCode(), this);
            Log.i("Loading meals", "Loading meals from server...");
        } else {
            if (mCanteen.getLastMealUpdate() < new Date().getTime() - 240000) {
                NetworkController.getInstance(getContext()).getMealsForCanteen(mCanteen.getCode(), this);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_meals_menu, menu);
        if (DataHolder.getInstance().getCanteen(mMensaId).isFavorite()) {
            menu.findItem(R.id.set_canteen_favorite).setIcon(R.drawable.ic_favorite_pink_24dp);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_canteen_favorite:
                Canteen canteen = DataHolder.getInstance().getCanteen(mMensaId);
                if (canteen.isFavorite()) {
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    canteen.setAsFavorite(false, getContext());
                    //Toast.makeText(getContext(), getString(R.string.toast_remove_favorite), Toast.LENGTH_SHORT).show();
                } else {
                    item.setIcon(R.drawable.ic_favorite_pink_24dp);
                    canteen.setAsFavorite(true, getContext());
                    //Toast.makeText(getContext(), getString(R.string.toast_add_favorite), Toast.LENGTH_SHORT).show();
                }
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
                mFragmentList.add(MealDayFragment.newInstance(mMensaId, d));
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
