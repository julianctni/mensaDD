package com.pasta.mensadd.controller;

import android.app.FragmentManager;
import android.util.Log;

import com.pasta.mensadd.R;
import com.pasta.mensadd.fragments.CardCheckFragment;
import com.pasta.mensadd.fragments.MensaListFragment;
import com.pasta.mensadd.fragments.MensaMapFragment;

public class FragmentController {

    public static void showMensaListFragment(FragmentManager fragmentManager){
        fragmentManager.beginTransaction().add(R.id.mainContainer, new MensaListFragment()).commit();
    }

    public static void showMapFragment(FragmentManager fragmentManager){
        MensaMapFragment fragment = (MensaMapFragment)fragmentManager.findFragmentByTag("MAP_FRAGMENT");
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.mainContainer, fragment, "MAP_FRAGMENT").commit();
            Log.i("FRAGMENT-CONTROLLER", "NOT NULL");
        } else {
            fragmentManager.beginTransaction().replace(R.id.mainContainer, new MensaMapFragment(), "MAP_FRAGMENT").commit();
            Log.i("FRAGMENT-CONTROLLER", "NULL");
        }
    }

    public static void showCardCheckFragment(FragmentManager fragmentManager){
        CardCheckFragment fragment = (CardCheckFragment) fragmentManager.findFragmentByTag("CARD_CHECK_FRAGMENT");
        if (fragment == null) {
            fragmentManager.beginTransaction().add(R.id.cardCheckContainer, new CardCheckFragment(), "CARD_CHECK_FRAGMENT").commit();
            Log.i("FRAGMENT-CONTROLLER", "NULL");
        }
    }
}
