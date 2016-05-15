package com.pasta.mensadd;

import android.app.FragmentManager;
import android.util.Log;

import com.mapbox.mapboxsdk.maps.MapFragment;

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
}
