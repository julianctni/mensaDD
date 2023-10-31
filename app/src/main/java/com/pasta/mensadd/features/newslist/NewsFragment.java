package com.pasta.mensadd.features.newslist;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.pasta.mensadd.AppDatabase;
import com.pasta.mensadd.PullToRefreshFragment;
import com.pasta.mensadd.R;
import com.pasta.mensadd.Utils;
import com.pasta.mensadd.domain.ApiService;
import com.pasta.mensadd.domain.news.NewsRepository;
import com.pasta.mensadd.network.ServiceGenerator;

import static com.pasta.mensadd.domain.ApiRepository.FETCH_ERROR;
import static com.pasta.mensadd.domain.ApiRepository.IS_FETCHING;

public class NewsFragment extends PullToRefreshFragment {

    private NewsViewModel mNewsViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        mRecyclerView = view.findViewById(R.id.newsList);
        NewsListAdapter newsListAdapter = new NewsListAdapter(this.requireContext());
        mRecyclerView.setAdapter(newsListAdapter);
        mRefreshText = view.findViewById(R.id.newsListRefreshText);
        super.setUpPullToRefresh(R.string.news_wanna_refresh, R.string.news_release_to_refresh);
        NewsViewModelFactory newsViewModelFactory = new NewsViewModelFactory(
                new NewsRepository(AppDatabase.getInstance(requireContext()),
                        ServiceGenerator.createService(ApiService.class)
                )
        );
        mNewsViewModel = new ViewModelProvider(this, newsViewModelFactory).get(NewsViewModel.class);
        mNewsViewModel.triggerNewsFetching(false);
        mNewsViewModel.getFetchState().observe(getViewLifecycleOwner(), fetchState -> {
            ProgressBar progressBar = view.findViewById(R.id.newsListProgressBar);
            if (progressBar.getVisibility() == View.VISIBLE && fetchState != IS_FETCHING) {
                Handler handler = new Handler();
                handler.postDelayed(() -> progressBar.setVisibility(View.GONE), 2000);
            } else {
                progressBar.setVisibility(fetchState == IS_FETCHING ? View.VISIBLE : View.GONE);
            }
            if (fetchState == FETCH_ERROR) {
                int errorMsgId = !Utils.isOnline(requireContext()) ? R.string.error_no_internet : R.string.error_unknown;
                Toast.makeText(requireContext(), getString(R.string.error_fetching_news, getString(errorMsgId)), Toast.LENGTH_SHORT).show();
            }
        });
        mNewsViewModel.getNews().observe(getViewLifecycleOwner(), newsListAdapter::submitList);
        return view;
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        mNewsViewModel.triggerNewsFetching(true);
    }
}
