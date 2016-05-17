package com.pasta.mensadd.controller;

import android.app.FragmentManager;
import android.util.Log;

import com.pasta.mensadd.R;
import com.pasta.mensadd.fragments.CardCheckFragment;
import com.pasta.mensadd.fragments.ImprintFragment;
import com.pasta.mensadd.fragments.MealListFragment;
import com.pasta.mensadd.fragments.MensaListFragment;
import com.pasta.mensadd.fragments.MensaMapFragment;
import com.pasta.mensadd.fragments.SettingsFragment;

public class FragmentController {

    public static void showMensaListFragment(FragmentManager fm){
        fm.beginTransaction().replace(R.id.mainContainer, new MensaListFragment()).commit();
    }

    public static void showMapFragment(FragmentManager fm){
        MensaMapFragment fragment = (MensaMapFragment)fm.findFragmentByTag("MAP_FRAGMENT");
        if (fragment != null) {
            fm.beginTransaction().replace(R.id.mainContainer, fragment, "MAP_FRAGMENT").commit();
            Log.i("FRAGMENT-CONTROLLER", "NOT NULL");
        } else {
            fm.beginTransaction().replace(R.id.mainContainer, new MensaMapFragment(), "MAP_FRAGMENT").commit();
            Log.i("FRAGMENT-CONTROLLER", "NULL");
        }
    }

    public static void showCardCheckFragment(FragmentManager fm, String current, String lastTransaction){
        CardCheckFragment fragment = CardCheckFragment.newInstance(current, lastTransaction);
        fm.beginTransaction().replace(R.id.cardCheckContainer, fragment, "CARD_CHECK_FRAGMENT").commit();
    }

    public static void updateCardCheckFragment(FragmentManager fm, String current, String lastTransaction){
        CardCheckFragment fragment = (CardCheckFragment) fm.findFragmentByTag("CARD_CHECK_FRAGMENT");
        if (fragment != null){
            fragment.updateContent(current, lastTransaction);
        }
    }
    
    public static void showSettingsFragment(FragmentManager fm){
        fm.beginTransaction().replace(R.id.mainContainer, new SettingsFragment()).commit();
    }

    public static void showMealListFragment(FragmentManager fm, int mensaId){
        fm.beginTransaction().replace(R.id.mainContainer, MealListFragment.newInstance(mensaId)).commit();
    }

    public static void showImprintFragment(FragmentManager fm){
        fm.beginTransaction().replace(R.id.mainContainer, ImprintFragment.newInstance()).commit();
    }
}
