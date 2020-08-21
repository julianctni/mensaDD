package com.pasta.mensadd.domain.news;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pasta.mensadd.AppDatabase;
import com.pasta.mensadd.domain.ApiService;
import com.pasta.mensadd.network.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pasta.mensadd.network.ServiceGenerator.FETCH_ERROR;
import static com.pasta.mensadd.network.ServiceGenerator.FETCH_SUCCESS;
import static com.pasta.mensadd.network.ServiceGenerator.IS_FETCHING;
import static com.pasta.mensadd.network.ServiceGenerator.NOT_FETCHING;

public class NewsRepository {
    private ApiService mApiService;
    private NewsDao mNewsDao;
    private AppDatabase mAppDatabase;
    private MutableLiveData<Integer> mFetchState;

    public NewsRepository(AppDatabase appDatabase, ApiService apiService) {
        mAppDatabase = appDatabase;
        mNewsDao = appDatabase.newsDao();
        mApiService = apiService;
        mFetchState = new MutableLiveData<>(NOT_FETCHING);
    }

    public void insertNews(List<News> news) {
        mAppDatabase.getTransactionExecutor().execute(() -> mNewsDao.insertNews(news));
    }

    public LiveData<List<News>> getNews() {
        return mNewsDao.getNews();
    }

    public LiveData<Integer> getFetchState() {
        return mFetchState;
    }

    public void fetchNews(boolean forceFetch) {
        mFetchState.setValue(IS_FETCHING);
        mApiService.getNews().enqueue(new Callback<ApiResponse<News>>() {
            @Override
            public void onResponse(Call<ApiResponse<News>> call, Response<ApiResponse<News>> response) {
                insertNews(response.body().getData());
                mFetchState.setValue(FETCH_SUCCESS);
            }

            @Override
            public void onFailure(Call<ApiResponse<News>> call, Throwable t) {
                mFetchState.setValue(FETCH_ERROR);
            }
        });
    }
}
