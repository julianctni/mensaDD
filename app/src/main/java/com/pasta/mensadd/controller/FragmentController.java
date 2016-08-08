package com.pasta.mensadd.controller;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.pasta.mensadd.MainActivity;
import com.pasta.mensadd.R;
import com.pasta.mensadd.fragments.CardCheckFragment;
import com.pasta.mensadd.fragments.ImprintFragment;
import com.pasta.mensadd.fragments.CanteenListFragment;
import com.pasta.mensadd.fragments.MealWeekFragment;
import com.pasta.mensadd.fragments.CanteenMapFragment;
import com.pasta.mensadd.fragments.SettingsFragment;

public class FragmentController {

    public static void showMensaListFragment(FragmentManager fm){
        MainActivity.setToolbarShadow(true);
        fm.beginTransaction().replace(R.id.mainContainer, new CanteenListFragment()).commit();
    }

    public static void showMapFragment(FragmentManager fm){
        MainActivity.setToolbarShadow(true);
        CanteenMapFragment fragment = (CanteenMapFragment)fm.findFragmentByTag("MAP_FRAGMENT");
        if (fragment != null) {
            fm.beginTransaction().replace(R.id.mainContainer, fragment, "MAP_FRAGMENT").commit();
        } else {
            fm.beginTransaction().replace(R.id.mainContainer, new CanteenMapFragment(), "MAP_FRAGMENT").commit();
        }
    }

    public static void showCardCheckFragment(FragmentManager fm, String current, String lastTransaction){
        MainActivity.setToolbarShadow(false);
        CardCheckFragment fragment = CardCheckFragment.newInstance(current, lastTransaction);
        fm.beginTransaction().replace(R.id.cardCheckContainer, fragment, "CARD_CHECK_FRAGMENT").commitAllowingStateLoss();
    }

    public static void updateCardCheckFragment(FragmentManager fm, String current, String lastTransaction){
        CardCheckFragment fragment = (CardCheckFragment) fm.findFragmentByTag("CARD_CHECK_FRAGMENT");
        if (fragment != null){
            fragment.updateContent(current, lastTransaction);
        }
    }
    
    public static void showSettingsFragment(FragmentManager fm){
        MainActivity.setToolbarShadow(true);
        fm.beginTransaction().addToBackStack("").replace(R.id.mainContainer, new SettingsFragment()).commit();
    }

    public static void showMealWeekFragment(FragmentManager fm, String mensaId){
        MainActivity.setToolbarShadow(false);
        fm.beginTransaction().addToBackStack("").replace(R.id.mainContainer, MealWeekFragment.newInstance(mensaId)).commit();
    }

    public static void showImprintFragment(FragmentManager fm){
        MainActivity.setToolbarShadow(true);
        fm.beginTransaction().addToBackStack("").replace(R.id.mainContainer, ImprintFragment.newInstance()).commit();
    }
}
