package com.pasta.mensadd.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pasta.mensadd.database.repository.CanteenRepository;
import com.pasta.mensadd.database.repository.NewsRepository;

public class NewsViewModelFactory implements ViewModelProvider.Factory {

    private NewsRepository mNewsRepository;

    public NewsViewModelFactory(NewsRepository newsRepository) {
        mNewsRepository = newsRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NewsViewModel(mNewsRepository);
    }
}
