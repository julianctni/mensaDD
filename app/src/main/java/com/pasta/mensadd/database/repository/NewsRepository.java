package com.pasta.mensadd.database.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.NewsDao;
import com.pasta.mensadd.database.entity.News;
import com.pasta.mensadd.networking.NetworkController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NewsRepository {
    private NetworkController mNetworkController;
    private NewsDao mNewsDao;
    private AppDatabase mAppDatabase;
    private MutableLiveData<Boolean> mIsRefreshing;

    public NewsRepository(AppDatabase appDatabase, NetworkController networkController) {
        mAppDatabase = appDatabase;
        mNewsDao = appDatabase.newsDao();
        mNetworkController = networkController;
        mIsRefreshing = new MutableLiveData<>();
        refreshNews();
    }

    public void insertNews(News news) {
        mAppDatabase.getTransactionExecutor().execute(() -> mNewsDao.insertNews(news));
    }

    public LiveData<List<News>> getNews() {
        return mNewsDao.getNews();
    }

    public LiveData<Boolean> isRefreshing() {
        return mIsRefreshing;
    }

    public void refreshNews() {
        mIsRefreshing.setValue(true);
        mNetworkController.fetchNews(((responseType, message) -> {
            try {
                JSONArray json = new JSONObject(message).getJSONArray("news");
                for (int i = 0; i < json.length(); i++) {
                    JSONObject news = json.getJSONObject(i);
                    String id = news.getString("id");
                    String heading = news.getString("heading");
                    String date = news.getString("date");
                    String category = news.getString("category");
                    String textShort = news.getString("content");
                    String newsLink = news.getString("link");

                    News n = new News(id, category, date, heading, textShort, newsLink);
                    insertNews(n);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mIsRefreshing.setValue(false);
        }));
    }
}
