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


    ArrayList<Mensa> mMensaList;
    RecyclerView rv;
    LinearLayoutManager layoutParams;
    public static MensaListAdapter spotListAdapter;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MensaListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MensaListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MensaListFragment newInstance(String param1, String param2) {
        MensaListFragment fragment = new MensaListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mensa_list, container, false);
        // Inflate the layout for this fragment
        layoutParams = new LinearLayoutManager(getActivity());
        mMensaList = new ArrayList();
        for (int i=1; i<20; i++) {
            mMensaList.add(new Mensa("Mensa"+i));
        }
        spotListAdapter = new MensaListAdapter(mMensaList,this);
        rv = (RecyclerView) view.findViewById(R.id.mensaList);
        rv.setAdapter(spotListAdapter);
        rv.setLayoutManager(layoutParams);
        return view;
    }
}
