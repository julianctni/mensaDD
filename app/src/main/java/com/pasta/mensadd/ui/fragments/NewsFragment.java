package com.pasta.mensadd.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pasta.mensadd.R;
import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.repository.NewsRepository;
import com.pasta.mensadd.networking.NetworkController;
import com.pasta.mensadd.ui.FragmentController;
import com.pasta.mensadd.ui.adapter.NewsListAdapter;
import com.pasta.mensadd.ui.viewmodel.NewsViewModel;
import com.pasta.mensadd.ui.viewmodel.NewsViewModelFactory;

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
        NewsViewModelFactory newsViewModelFactory = new NewsViewModelFactory(
                new NewsRepository(AppDatabase.getInstance(requireContext()),
                        NetworkController.getInstance(requireContext())));
        NewsViewModel newsViewModel = new ViewModelProvider(this, newsViewModelFactory).get(NewsViewModel.class);
        newsViewModel.isRefreshing().observe(getViewLifecycleOwner(), isRefreshing -> {
            ProgressBar progressBar = view.findViewById(R.id.newsListProgressBar);
            progressBar.setVisibility(isRefreshing ? View.VISIBLE : View.GONE);
        });
        newsViewModel.getNews().observe(getViewLifecycleOwner(), news -> mNewsListAdapter.submitList(news));
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
