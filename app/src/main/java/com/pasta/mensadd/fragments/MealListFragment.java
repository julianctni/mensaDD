package com.pasta.mensadd.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pasta.mensadd.R;


public class MealListFragment extends Fragment {

    private static final String TAG_MENSA_ID = "mensaId";
    private String mMensaId;


    public MealListFragment() {}

    public static MealListFragment newInstance(int mensaId) {
        MealListFragment fragment = new MealListFragment();
        Bundle args = new Bundle();
        args.putInt(TAG_MENSA_ID, mensaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMensaId = getArguments().getString(TAG_MENSA_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_list, container, false);
    }

}
