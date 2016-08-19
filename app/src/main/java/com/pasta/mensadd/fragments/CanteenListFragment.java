package com.pasta.mensadd.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pasta.mensadd.MainActivity;
import com.pasta.mensadd.R;
import com.pasta.mensadd.adapter.CanteenListAdapter;
import com.pasta.mensadd.controller.DatabaseController;
import com.pasta.mensadd.controller.ParseController;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.networking.LoadCanteensCallback;
import com.pasta.mensadd.networking.NetworkController;

import java.util.Date;

public class CanteenListFragment extends Fragment implements LoadCanteensCallback{

    private CanteenListAdapter mCanteenListAdapter;
    private SharedPreferences mSharedPrefs;

    public static String KEY_LAST_CANTEENS_UPDATE = "lastCanteenUpdate";


    public CanteenListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canteen_list, container, false);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        LinearLayoutManager layoutParams = new LinearLayoutManager(getActivity());
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.mensaList);
        DataHolder.getInstance().sortCanteenList();
        mCanteenListAdapter = new CanteenListAdapter(DataHolder.getInstance().getCanteenList(),this);
        mRecyclerView.setAdapter(mCanteenListAdapter);
        mRecyclerView.setLayoutManager(layoutParams);

        TextView header = (TextView)getActivity().findViewById(R.id.heading_toolbar);
        header.setVisibility(View.GONE);
        ImageView appLogo = (ImageView)getActivity().findViewById(R.id.home_button);
        appLogo.setVisibility(View.VISIBLE);

        if (mSharedPrefs.getLong(KEY_LAST_CANTEENS_UPDATE,0) == 0 || new Date().getTime() - mSharedPrefs.getLong(KEY_LAST_CANTEENS_UPDATE,0) > 86400000) {
            NetworkController.getInstance(getActivity()).getCanteenList(this);
        } else if (DataHolder.getInstance().getCanteenList().isEmpty()){
            readCanteensFromDb();
        }

        MainActivity.updateNavDrawer(R.id.nav_mensa);
        return view;
    }

    @Override
    public void onResponseMessage(int responseType, String message) {
        if (responseType == 1){
            ParseController p = new ParseController();
            p.parseCanteens(message, new DatabaseController(this.getActivity().getApplicationContext()), mSharedPrefs);
            mCanteenListAdapter.notifyDataSetChanged();
        } else {
            readCanteensFromDb();
        }
    }

    public void readCanteensFromDb(){
        DatabaseController dbController = new DatabaseController(getActivity().getApplicationContext());
        dbController.readCanteensFromDb();
    }
}
