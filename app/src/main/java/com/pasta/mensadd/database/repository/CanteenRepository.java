package com.pasta.mensadd.database.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pasta.mensadd.PreferenceService;
import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.CanteenDao;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.networking.ApiResponse;
import com.pasta.mensadd.networking.ApiServiceClient;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CanteenRepository {

    private static final int CANTEEN_UPDATE_INTERVAL = 10 * 60 * 60 * 1000;
    private CanteenDao mCanteenDao;
    private LiveData<List<Canteen>> mCanteens;
    private MutableLiveData<Boolean> mIsRefreshing;
    private ApiServiceClient mApiServiceClient;
    private PreferenceService mPreferenceService;
    private AppDatabase mAppDatabase;

    public CanteenRepository(AppDatabase appDatabase, PreferenceService preferenceService, ApiServiceClient apiServiceClient) {
        mAppDatabase = appDatabase;
        mCanteenDao = appDatabase.canteenDao();
        mCanteens = mCanteenDao.getCanteens();
        mPreferenceService = preferenceService;
        mApiServiceClient = apiServiceClient;
        mIsRefreshing = new MutableLiveData<>();
        fetchCanteens();
    }

    public void insertOrUpdateCanteens(List<Canteen> serverCanteens) {
        mAppDatabase.getTransactionExecutor().execute(() -> {
            for (Canteen serverCanteen : serverCanteens) {
                Canteen localCanteen = mCanteenDao.getCanteenByIdSync(serverCanteen.getId());
                if (localCanteen != null) {
                    serverCanteen.setAsFavorite(localCanteen.isFavorite());
                    serverCanteen.setLastMealUpdate(localCanteen.getLastMealUpdate());
                    serverCanteen.setPriority(localCanteen.getPriority());
                }
            }
            mCanteenDao.insertOrUpdateCanteens(serverCanteens);
        });
    }

    public void updateCanteen(Canteen canteen) {
        mAppDatabase.getTransactionExecutor().execute(() -> mCanteenDao.updateCanteen(canteen));
    }

    public LiveData<Canteen> getCanteenById(String id) {
        return mCanteenDao.getCanteenById(id);
    }

    public LiveData<List<Canteen>> getCanteens() {
        long lastUpdate = mPreferenceService.getLastCanteenUpdate();
        if (lastUpdate == 0 || Calendar.getInstance().getTimeInMillis() - lastUpdate > CANTEEN_UPDATE_INTERVAL) {
            fetchCanteens();
        }
        return mCanteens;
    }

    public LiveData<Boolean> isRefreshing() {
        return mIsRefreshing;
    }

    public void fetchCanteens() {
        mIsRefreshing.setValue(true);
        mApiServiceClient.fetchCanteens().enqueue(new Callback<ApiResponse<Canteen>>() {
            @Override
            public void onResponse(Call<ApiResponse<Canteen>> call, Response<ApiResponse<Canteen>> response) {
                insertOrUpdateCanteens(response.body().getData());
                mPreferenceService.setLastCanteenUpdate(Calendar.getInstance().getTimeInMillis());
                mIsRefreshing.setValue(false);
            }

            @Override
            public void onFailure(Call<ApiResponse<Canteen>> call, Throwable t) {
                // TODO: Add error handling
            }
        });
    }
}
