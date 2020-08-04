package com.pasta.mensadd.database.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.NewsDao;
import com.pasta.mensadd.database.entity.News;
import com.pasta.mensadd.networking.NetworkController;
import com.pasta.mensadd.networking.callbacks.LoadNewsCallback;

import java.util.List;

public class NewsRepository {
    private NetworkController network;
    private NewsDao newsDao;

    public NewsRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        newsDao = appDatabase.newsDao();
        network = NetworkController.getInstance(application);
    }

    public void insertNews(News news) {
        AppDatabase.dbExecutor.execute(() -> newsDao.insert(news));
    }

    public LiveData<List<News>> getAllNews() {
        return newsDao.getAllNews();
    }

    public void refreshNews(LoadNewsCallback callback) {
        network.fetchNews(callback);
    }
}
