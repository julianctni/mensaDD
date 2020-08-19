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

import static com.pasta.mensadd.networking.ApiServiceClient.FETCH_ERROR;
import static com.pasta.mensadd.networking.ApiServiceClient.FETCH_SUCCESS;
import static com.pasta.mensadd.networking.ApiServiceClient.NOT_FETCHING;
import static com.pasta.mensadd.networking.ApiServiceClient.IS_FETCHING;

public class NewsRepository {
    private ApiServiceClient mApiServiceClient;
    private NewsDao mNewsDao;
    private AppDatabase mAppDatabase;
    private MutableLiveData<Integer> mFetchState;

    public NewsRepository(AppDatabase appDatabase, ApiServiceClient apiServiceClient) {
        mAppDatabase = appDatabase;
        mNewsDao = appDatabase.newsDao();
        mApiServiceClient = apiServiceClient;
        mFetchState = new MutableLiveData<>();
    }

    public void insertNews(List<News> news) {
        mAppDatabase.getTransactionExecutor().execute(() -> mNewsDao.insertNews(news));
    }

    public LiveData<List<News>> getNews() {
        fetchNews();
        return mNewsDao.getNews();
    }

    public LiveData<Integer> getFetchState() {
        return mFetchState;
    }

    public void fetchNews() {
        mFetchState.setValue(IS_FETCHING);
        mApiServiceClient.fetchNews().enqueue(new Callback<ApiResponse<News>>() {
            @Override
            public void onResponse(Call<ApiResponse<News>> call, Response<ApiResponse<News>> response) {
                insertNews(response.body().getData());
                mFetchState.setValue(FETCH_SUCCESS);
                mFetchState.setValue(NOT_FETCHING);
            }

            @Override
            public void onFailure(Call<ApiResponse<News>> call, Throwable t) {
                mFetchState.setValue(FETCH_ERROR);
                mFetchState.setValue(NOT_FETCHING);
            }
        });
    }
}
