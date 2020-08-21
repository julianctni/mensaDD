package com.pasta.mensadd.domain;

import androidx.lifecycle.MutableLiveData;

import com.pasta.mensadd.AppDatabase;

public abstract class ApiRepository {

    public final static int NOT_FETCHING = -1;
    public final static int IS_FETCHING = 1;
    public final static int FETCH_ERROR = 2;
    public final static int FETCH_SUCCESS = 3;

    protected AppDatabase mAppDatabase;
    protected ApiService mApiService;
    protected MutableLiveData<Integer> mFetchState;

    protected ApiRepository(AppDatabase appDatabase, ApiService apiService) {
        mApiService = apiService;
        mAppDatabase = appDatabase;
        mFetchState = new MutableLiveData<>(NOT_FETCHING);
    }
}
