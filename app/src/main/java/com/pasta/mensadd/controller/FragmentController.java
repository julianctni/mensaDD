package com.pasta.mensadd.controller;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;

import com.pasta.mensadd.MainActivity;
import com.pasta.mensadd.R;
import com.pasta.mensadd.fragments.CanteenListFragment;
import com.pasta.mensadd.fragments.CanteenMapFragment;
import com.pasta.mensadd.fragments.BalanceCheckFragment;
import com.pasta.mensadd.fragments.BalanceHistoryFragment;
import com.pasta.mensadd.fragments.Imprintfragment;
import com.pasta.mensadd.fragments.LargeImageFragment;
import com.pasta.mensadd.fragments.MealWeekFragment;
import com.pasta.mensadd.fragments.SettingsFragment;

public class FragmentController {

    public static final String TAG_CANTEEN_LIST = "CANTEEN_LIST_FRAGMENT";
    public static final String TAG_MEAL_WEEK = "MEAL_WEEK_FRAGMENT";
    public static final String TAG_MAP = "MAP_FRAGMENT";
    public static final String TAG_BALANCE_CHECK = "BALANCE_CHECK_FRAGMENT";
    public static final String TAG_BALANCE_HISTORY= "BALANCE_HISTORY_FRAGMENT";
    public static final String TAG_SETTINGS = "SETTINGS_FRAGMENT";
    public static final String TAG_IMPRINT = "IMPRINT_FRAGMENT";
    public static final String TAG_LARGE_IMAGE = "LARGE_IMAGE_FRAGMENT";


    public static void showCanteenListFragment(FragmentManager fm){
        fm.beginTransaction().replace(R.id.mainContainer, new CanteenListFragment(), TAG_CANTEEN_LIST).commit();
    }

    public static void showMapFragment(FragmentManager fm){
        fm.beginTransaction().addToBackStack("").replace(R.id.mainContainer, new CanteenMapFragment(), TAG_MAP).commit();
    }

    public static void showBalanceCheckFragment(FragmentManager fm, String current, String lastTransaction){
        BalanceCheckFragment fragment = BalanceCheckFragment.newInstance(current, lastTransaction);
        fm.beginTransaction().replace(R.id.cardCheckContainer, fragment, TAG_BALANCE_CHECK).commitAllowingStateLoss();
    }

    public static void updateBalanceCheckFragment(FragmentManager fm, String current, String lastTransaction){
        BalanceCheckFragment fragment = (BalanceCheckFragment) fm.findFragmentByTag(TAG_BALANCE_CHECK);
        if (fragment != null){
            fragment.updateContent(current, lastTransaction);
        }
    }
    
    public static void showSettingsFragment(FragmentManager fm){
        fm.beginTransaction().addToBackStack("").replace(R.id.mainContainer, new SettingsFragment(), TAG_SETTINGS).commit();
    }

    public static void showMealWeekFragment(FragmentManager fm, String mensaId){
        fm.beginTransaction().addToBackStack("").replace(R.id.mainContainer, MealWeekFragment.newInstance(mensaId), TAG_MEAL_WEEK).commit();
    }

    public static void showBalanceHistoryFragment(FragmentManager fm){
        fm.beginTransaction().addToBackStack("").replace(R.id.mainContainer, new BalanceHistoryFragment(), TAG_BALANCE_HISTORY).commit();
    }

    public static void showImprintFragment(FragmentManager fm){
        fm.beginTransaction().addToBackStack("").replace(R.id.mainContainer, new Imprintfragment(), TAG_IMPRINT).commit();
    }

    public static void showLargeImageFragment(FragmentManager fm, Bitmap image){
        fm.beginTransaction().addToBackStack("").add(R.id.mainContainer, LargeImageFragment.newInstance(image), TAG_LARGE_IMAGE).commit();
    }

    public static void hideLargeImageFragment(FragmentManager fm){
        //fm.beginTransaction().remove(fm.findFragmentByTag(TAG_LARGE_IMAGE)).commit();
        fm.popBackStack();
    }
}
