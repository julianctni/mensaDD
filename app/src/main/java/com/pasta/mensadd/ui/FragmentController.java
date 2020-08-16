package com.pasta.mensadd.ui;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.pasta.mensadd.R;
import com.pasta.mensadd.database.entity.BalanceEntry;
import com.pasta.mensadd.ui.fragments.BalanceCheckFragment;
import com.pasta.mensadd.ui.fragments.BalanceHistoryFragment;
import com.pasta.mensadd.ui.fragments.CanteenListFragment;
import com.pasta.mensadd.ui.fragments.CanteenMapFragment;
import com.pasta.mensadd.ui.fragments.ImprintFragment;
import com.pasta.mensadd.ui.fragments.MealWeekFragment;
import com.pasta.mensadd.ui.fragments.NewsFragment;
import com.pasta.mensadd.ui.fragments.SettingsFragment;

public class FragmentController {

    public static final String TAG_CANTEEN_LIST = "CANTEEN_LIST_FRAGMENT";
    public static final String TAG_MEAL_WEEK = "MEAL_WEEK_FRAGMENT";
    public static final String TAG_MAP = "MAP_FRAGMENT";
    public static final String TAG_NEWS = "NEWS_FRAGMENT";
    public static final String TAG_BALANCE_CHECK = "BALANCE_CHECK_FRAGMENT";
    public static final String TAG_BALANCE_HISTORY = "BALANCE_HISTORY_FRAGMENT";
    public static final String TAG_SETTINGS = "SETTINGS_FRAGMENT";
    public static final String TAG_IMPRINT = "IMPRINT_FRAGMENT";


    public static void showCanteenListFragment(FragmentManager fm) {
        CanteenListFragment f = (CanteenListFragment) fm.findFragmentByTag(TAG_CANTEEN_LIST);
        if (f == null) f = new CanteenListFragment();
        createAnimatedTransaction(fm).replace(R.id.mainContainer, f, TAG_CANTEEN_LIST).addToBackStack(null).commit();
    }

    public static void showMapFragment(FragmentManager fm) {
        CanteenMapFragment f = (CanteenMapFragment) fm.findFragmentByTag(TAG_MAP);
        if (f == null) f = new CanteenMapFragment();
        createAnimatedTransaction(fm).replace(R.id.mainContainer, f, TAG_MAP).addToBackStack(null).commit();
    }

    public static void showNewsFragment(FragmentManager fm) {
        NewsFragment f = (NewsFragment) fm.findFragmentByTag(TAG_NEWS);
        if (f == null) f = new NewsFragment();
        createAnimatedTransaction(fm).replace(R.id.mainContainer, f, TAG_NEWS).addToBackStack(null).commit();
    }

    public static void showBalanceCheckFragment(FragmentManager fm, BalanceEntry balanceEntry) {
        BalanceCheckFragment f = (BalanceCheckFragment) fm.findFragmentByTag(FragmentController.TAG_BALANCE_CHECK);
        if (f == null) {
            f = new BalanceCheckFragment();
            fm.beginTransaction().replace(R.id.cardCheckContainer, f, TAG_BALANCE_CHECK).commitNow();
        }
        f.setCurrentBalanceData(balanceEntry);
    }

    public static void showSettingsFragment(FragmentManager fm) {
        SettingsFragment f = (SettingsFragment) fm.findFragmentByTag(TAG_SETTINGS);
        if (f == null) f = new SettingsFragment();
        createAnimatedTransaction(fm).addToBackStack("").replace(R.id.mainContainer, f, TAG_SETTINGS).commit();
    }

    public static void showImprintFragment(FragmentManager fm) {
        ImprintFragment f = (ImprintFragment) fm.findFragmentByTag(TAG_IMPRINT);
        if (f == null) f = new ImprintFragment();
        fm.beginTransaction().addToBackStack(null).replace(R.id.mainContainer, f, TAG_IMPRINT).commit();
    }

    public static void showMealWeekFragment(FragmentManager fm) {
        createAnimatedTransaction(fm).addToBackStack("").replace(R.id.mainContainer, new MealWeekFragment(), TAG_MEAL_WEEK).commit();
    }

    public static void showBalanceHistoryFragment(FragmentManager fm) {
        BalanceHistoryFragment f = (BalanceHistoryFragment) fm.findFragmentByTag(TAG_BALANCE_HISTORY);
        if (f == null) f = new BalanceHistoryFragment();
        createAnimatedTransaction(fm).replace(R.id.mainContainer, f, TAG_BALANCE_HISTORY).commit();
    }

    public static FragmentTransaction createAnimatedTransaction(FragmentManager fm) {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        return transaction;
    }


}
