package com.pasta.mensadd.database.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.NewsDao;
import com.pasta.mensadd.database.entity.News;
import com.pasta.mensadd.networking.ApiResponse;
import com.pasta.mensadd.networking.ApiServiceClient;
import com.pasta.mensadd.networking.NetworkController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {
    private ApiServiceClient mApiServiceClient;
    private NewsDao mNewsDao;
    private AppDatabase mAppDatabase;
    private MutableLiveData<Boolean> mIsRefreshing;

    public NewsRepository(AppDatabase appDatabase, ApiServiceClient apiServiceClient) {
        mAppDatabase = appDatabase;
        mNewsDao = appDatabase.newsDao();
        mApiServiceClient = apiServiceClient;
        mIsRefreshing = new MutableLiveData<>();
        fetchNews();
    }

    public void insertNews(List<News> news) {
        mAppDatabase.getTransactionExecutor().execute(() -> mNewsDao.insertNews(news));
    }

    public LiveData<List<News>> getNews() {
        return mNewsDao.getNews();
    }

    public LiveData<Boolean> isRefreshing() {
        return mIsRefreshing;
    }

    public void fetchNews() {
        mIsRefreshing.setValue(true);
        mApiServiceClient.fetchNews().enqueue(new Callback<ApiResponse<News>>() {
            @Override
            public void onResponse(Call<ApiResponse<News>> call, Response<ApiResponse<News>> response) {
                insertNews(response.body().getData());
                mIsRefreshing.setValue(false);
            }

            @Override
            public void onFailure(Call<ApiResponse<News>> call, Throwable t) {
                mIsRefreshing.setValue(false);
                // TODO: Add error handling
            }
        });
    }
}
