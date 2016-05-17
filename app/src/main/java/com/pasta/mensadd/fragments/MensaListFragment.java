package com.pasta.mensadd.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pasta.mensadd.model.Mensa;
import com.pasta.mensadd.adapter.MensaListAdapter;
import com.pasta.mensadd.R;

import java.util.ArrayList;

public class MensaListFragment extends Fragment {


    private ArrayList<Mensa> mMensaList;
    private LinearLayoutManager layoutParams;
    public static MensaListAdapter mMensaListAdapter;


    public MensaListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mensa_list, container, false);
        layoutParams = new LinearLayoutManager(getActivity());
        mMensaList = new ArrayList();
        for (int i=1; i<20; i++) {
            mMensaList.add(new Mensa("Mensa"+i));
        }
        mMensaListAdapter = new MensaListAdapter(mMensaList,this);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.mensaList);
        rv.setAdapter(mMensaListAdapter);
        rv.setLayoutManager(layoutParams);
        return view;
    }
}
