package com.pasta.mensadd.ui.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.pasta.mensadd.R;
import com.pasta.mensadd.ui.adapter.NewsListAdapter;
import com.pasta.mensadd.ui.FragmentController;
import com.pasta.mensadd.ui.viewmodel.NewsViewModel;

public class NewsFragment extends Fragment {

    private NewsListAdapter mNewsListAdapter;

    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        LinearLayoutManager layoutParams = new LinearLayoutManager(getActivity());
        RecyclerView mNewsList = view.findViewById(R.id.newsList);
        mNewsListAdapter = new NewsListAdapter(this.getContext());
        mNewsList.setLayoutManager(layoutParams);
        mNewsList.setAdapter(mNewsListAdapter);
        NewsViewModel newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        ProgressBar progressBar = view.findViewById(R.id.newsListProgressBar);
        progressBar.setVisibility(newsViewModel.isRefreshing() ? View.VISIBLE : View.GONE);
        newsViewModel.getAllNews().observe(getViewLifecycleOwner(), news -> {
            mNewsListAdapter.submitList(news);
            progressBar.setVisibility(newsViewModel.isRefreshing() ? View.VISIBLE : View.GONE);
        });
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
}
