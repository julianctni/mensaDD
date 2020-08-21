package com.pasta.mensadd.features.newslist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.domain.news.News;
import com.pasta.mensadd.domain.news.NewsRepository;

import java.util.List;

public class NewsViewModel extends ViewModel {

    private NewsRepository mNewsRepository;
    private LiveData<List<News>> mNews;

    public NewsViewModel(NewsRepository newsRepository) {
        mNewsRepository = newsRepository;
        mNews = mNewsRepository.getNews();
    }

    public LiveData<List<News>> getNews() {
        return mNews;
    }

    public LiveData<Integer> getFetchState() {
        return mNewsRepository.getFetchState();
    }

    public void triggerNewsFetching(boolean forceFetch) {
        mNewsRepository.fetchNews(forceFetch);
    }
}
