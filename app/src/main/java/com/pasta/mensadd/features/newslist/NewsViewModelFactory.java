package com.pasta.mensadd.features.newslist;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pasta.mensadd.domain.news.NewsRepository;

public class NewsViewModelFactory implements ViewModelProvider.Factory {

    private final NewsRepository mNewsRepository;

    public NewsViewModelFactory(NewsRepository newsRepository) {
        mNewsRepository = newsRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NewsViewModel(mNewsRepository);
    }
}
