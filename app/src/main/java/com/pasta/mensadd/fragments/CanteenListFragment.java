package com.pasta.mensadd.fragments;


import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.pasta.mensadd.MainActivity;
import com.pasta.mensadd.R;
import com.pasta.mensadd.adapter.CanteenListAdapter;
import com.pasta.mensadd.controller.DatabaseController;
import com.pasta.mensadd.model.Canteen;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.networking.LoadCanteensCallback;
import com.pasta.mensadd.networking.NetworkController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class CanteenListFragment extends Fragment implements LoadCanteensCallback{

    private LinearLayoutManager layoutParams;
    public static CanteenListAdapter mCanteenListAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mCanteenListRefresher;
    private SharedPreferences prefs;
    private final String URL_CANTEEN_LIST = "http://ctni.sabic.uberspace.de/mensadd/canteens.json";

    private static String KEY_LAST_CANTEENS_UPDATE = "lastCanteenUpdate";


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
        MainActivity.setToolbarShadow(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        layoutParams = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mensaList);
        mCanteenListAdapter = new CanteenListAdapter(DataHolder.getInstance().getCanteenList(),this);
        mRecyclerView.setAdapter(mCanteenListAdapter);
        mRecyclerView.setLayoutManager(layoutParams);
        mCanteenListRefresher = (SwipeRefreshLayout) view.findViewById(R.id.canteenListRefresher);
        int colorArray[] = {R.color.tile_cyan1, R.color.tile_orange1, R.color.tile_blue1, R.color.tile_pink1};
        mCanteenListRefresher.setColorSchemeResources(colorArray);
        mCanteenListRefresher.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        NetworkController.getInstance(getActivity()).getCanteenList(URL_CANTEEN_LIST, CanteenListFragment.this);
                    }
                }
        );
        TextView header = (TextView)getActivity().findViewById(R.id.heading_toolbar);
        header.setVisibility(View.GONE);
        ImageView appLogo = (ImageView)getActivity().findViewById(R.id.home_button);
        appLogo.setVisibility(View.VISIBLE);

        if (prefs.getLong(KEY_LAST_CANTEENS_UPDATE,0) == 0 || new Date().getTime() - prefs.getLong(KEY_LAST_CANTEENS_UPDATE,0) > 86400000) {
            NetworkController.getInstance(getActivity()).getCanteenList(URL_CANTEEN_LIST, this);
        } else if (DataHolder.getInstance().getCanteenList().isEmpty()){
            DatabaseController dbController = new DatabaseController(getActivity().getApplicationContext());
            dbController.readCanteensFromDb();
        }

        MainActivity.updateNavDrawer(R.id.nav_mensa);
        return view;
    }

    @Override
    public void onResponseMessage(int responseType, String message) {
        if (responseType == 1){
            DataHolder.getInstance().getCanteenList().clear();
            DatabaseController dbController = new DatabaseController(getActivity().getApplicationContext());
            SQLiteDatabase db = dbController.getWritableDatabase();
            try {
                JSONArray json = new JSONArray(message);
                for(int i = 0 ; i < json.length(); i++){
                    JSONObject canteen = json.getJSONObject(i);
                    String name = canteen.getString("name");
                    String code = canteen.getString("code");
                    String address = canteen.getString("address");
                    JSONArray gpsArray = canteen.getJSONArray("coordinates");
                    LatLng position = new LatLng(Double.parseDouble(gpsArray.get(0).toString()),Double.parseDouble(gpsArray.get(1).toString()));
                    JSONArray hourArray = canteen.getJSONArray("hours");
                    String hours = "";
                    for (int j = 0; j < hourArray.length(); j++){
                        hours += hourArray.get(j);
                        if (j < hourArray.length()-1)
                            hours += "\n";
                    }
                    int priority = prefs.getInt("priority_"+code, 0);
                    Canteen m = new Canteen(name, code, position,address, hours, priority);
                    DataHolder.getInstance().getCanteenList().add(m);
                }
                prefs.edit().putLong(KEY_LAST_CANTEENS_UPDATE,new Date().getTime()).commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dbController.updateCanteenTable();
            DataHolder.getInstance().sortCanteenList();
            mCanteenListRefresher.setRefreshing(false);
            mCanteenListAdapter.notifyDataSetChanged();

        }
    }
}
