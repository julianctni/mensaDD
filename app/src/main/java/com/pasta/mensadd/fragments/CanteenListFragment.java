package com.pasta.mensadd.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pasta.mensadd.model.Mensa;
import com.pasta.mensadd.adapter.MensaListAdapter;
import com.pasta.mensadd.R;
import com.pasta.mensadd.networking.LoadCanteensCallback;
import com.pasta.mensadd.networking.NetworkController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CanteenListFragment extends Fragment implements LoadCanteensCallback{


    private ArrayList<Mensa> mMensaList;
    private LinearLayoutManager layoutParams;
    public static MensaListAdapter mMensaListAdapter;
    private RecyclerView mRecyclerView;


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
        layoutParams = new LinearLayoutManager(getActivity());
        mMensaList = new ArrayList();
        mMensaListAdapter = new MensaListAdapter(mMensaList,this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mensaList);
        mRecyclerView.setAdapter(mMensaListAdapter);
        mRecyclerView.setLayoutManager(layoutParams);
        NetworkController network = NetworkController.getInstance(getActivity());
        network.doJSONArrayRequest("http://ctni.sabic.uberspace.de/mensadd/canteen-list.json","",this);
        return view;
    }

    @Override
    public void onResponseMessage(int responseType, String message) {
        if (responseType == 1){
            mMensaList.clear();
            try {
                JSONArray json = new JSONArray(message);
                for(int i = 0 ; i < json.length(); i++){
                    JSONObject canteen = json.getJSONObject(i);
                    String name = canteen.getString("name");
                    String code = canteen.getString("code");
                    String address = canteen.getString("address");
                    JSONArray hourArray = canteen.getJSONArray("hours");
                    String hours = "";
                    for (int j = 0; j < hourArray.length(); j++){
                        hours += hourArray.get(j);
                        if (j < hourArray.length()-1)
                            hours += "\n";
                    }
                    Mensa m = new Mensa(name, code, address, hours);
                    mMensaList.add(m);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mMensaListAdapter.notifyDataSetChanged();
        }
    }
}
