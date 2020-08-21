package com.pasta.mensadd.domain.news;

import androidx.lifecycle.LiveData;

import com.pasta.mensadd.AppDatabase;
import com.pasta.mensadd.domain.ApiService;
import com.pasta.mensadd.domain.ApiRepository;
import com.pasta.mensadd.network.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository extends ApiRepository {
    private NewsDao mNewsDao;

    public NewsRepository(AppDatabase appDatabase, ApiService apiService) {
        super(appDatabase, apiService);
        mNewsDao = appDatabase.newsDao();
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
