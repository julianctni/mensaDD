package com.pasta.mensadd.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.database.entity.News;
import com.pasta.mensadd.database.repository.NewsRepository;

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
}
