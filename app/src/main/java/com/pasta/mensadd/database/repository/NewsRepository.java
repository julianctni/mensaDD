package com.pasta.mensadd.database.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.NewsDao;
import com.pasta.mensadd.database.entity.News;
import com.pasta.mensadd.networking.ApiResponse;
import com.pasta.mensadd.networking.ApiServiceClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {
    private ApiServiceClient mApiServiceClient;
    private NewsDao mNewsDao;
    private AppDatabase mAppDatabase;
    private MutableLiveData<Boolean> mIsFetching;

    public NewsRepository(AppDatabase appDatabase, ApiServiceClient apiServiceClient) {
        mAppDatabase = appDatabase;
        mNewsDao = appDatabase.newsDao();
        mApiServiceClient = apiServiceClient;
        mIsFetching = new MutableLiveData<>();
        fetchNews();
    }

    public void insertNews(List<News> news) {
        mAppDatabase.getTransactionExecutor().execute(() -> mNewsDao.insertNews(news));
    }

    public LiveData<List<News>> getNews() {
        return mNewsDao.getNews();
    }

    public LiveData<Boolean> isFetching() {
        return mIsFetching;
    }

    public void fetchNews() {
        mIsFetching.setValue(true);
        mApiServiceClient.fetchNews().enqueue(new Callback<ApiResponse<News>>() {
            @Override
            public void onResponse(Call<ApiResponse<News>> call, Response<ApiResponse<News>> response) {
                insertNews(response.body().getData());
                mIsFetching.setValue(false);
            }

            @Override
            public void onFailure(Call<ApiResponse<News>> call, Throwable t) {
                mIsFetching.setValue(false);
                // TODO: Add error handling
            }
        });
    }
}
