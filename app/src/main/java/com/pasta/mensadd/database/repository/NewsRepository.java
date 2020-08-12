package com.pasta.mensadd.database.repository;

import androidx.lifecycle.LiveData;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.NewsDao;
import com.pasta.mensadd.database.entity.News;
import com.pasta.mensadd.networking.NetworkController;
import com.pasta.mensadd.networking.callbacks.LoadNewsCallback;

import java.util.List;

public class NewsRepository {
    private NetworkController networkController;
    private NewsDao newsDao;

    public NewsRepository(AppDatabase appDatabase, NetworkController networkController) {
        newsDao = appDatabase.newsDao();
        this.networkController = networkController;
    }

    public void insertNews(News news) {
        AppDatabase.dbExecutor.execute(() -> newsDao.insert(news));
    }

    public LiveData<List<News>> getAllNews() {
        return newsDao.getAllNews();
    }

    public void refreshNews(LoadNewsCallback callback) {
        networkController.fetchNews(callback);
    }
}
