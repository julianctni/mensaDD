package com.pasta.mensadd.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pasta.mensadd.R;
import com.pasta.mensadd.adapter.NewsListAdapter;
import com.pasta.mensadd.controller.FragmentController;
import com.pasta.mensadd.controller.ParseController;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.networking.LoadNewsCallback;
import com.pasta.mensadd.networking.NetworkController;

public class NewsFragment extends Fragment implements LoadNewsCallback {

    private NewsListAdapter mNewsListAdapter;
    private RecyclerView mNewsList;

    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        LinearLayoutManager layoutParams = new LinearLayoutManager(getActivity());
        mNewsList = view.findViewById(R.id.newsList);
        mNewsListAdapter = new NewsListAdapter(DataHolder.getInstance().getNewsList(),this);
        mNewsList.setAdapter(mNewsListAdapter);
        mNewsList.setLayoutManager(layoutParams);
        NetworkController.getInstance(getActivity()).getNews(this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fragment_canteens_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_preferences:
                FragmentController.showSettingsFragment(getFragmentManager());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResponseMessage(int responseType, String message) {
        if (responseType == NetworkController.SUCCESS) {
            ParseController p = new ParseController();
            p.parseNews(message, this);
        } else if (responseType == ParseController.PARSE_SUCCESS) {
            DataHolder.getInstance().sortNewsList();
            mNewsListAdapter.notifyDataSetChanged();
            mNewsList.setVisibility(View.VISIBLE);
        }
    }
}
