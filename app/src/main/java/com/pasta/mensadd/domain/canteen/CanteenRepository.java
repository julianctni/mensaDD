package com.pasta.mensadd.domain.canteen;

import androidx.lifecycle.LiveData;

import com.pasta.mensadd.AppDatabase;
import com.pasta.mensadd.PreferenceService;
import com.pasta.mensadd.domain.ApiService;
import com.pasta.mensadd.domain.ApiRepository;
import com.pasta.mensadd.network.ApiResponse;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CanteenRepository extends ApiRepository {

    private static final int CANTEEN_UPDATE_INTERVAL = 10 * 60 * 60 * 1000;
    private CanteenDao mCanteenDao;
    private LiveData<List<Canteen>> mCanteens;
    private PreferenceService mPreferenceService;

    public CanteenRepository(AppDatabase appDatabase, PreferenceService preferenceService, ApiService apiService) {
        super(appDatabase, apiService);
        mCanteenDao = mAppDatabase.canteenDao();
        mCanteens = mCanteenDao.getCanteens();
        mPreferenceService = preferenceService;
    }

    public void toggleCanteenFavorite(String canteenId) {
        mAppDatabase.getTransactionExecutor().execute(() -> {
            Canteen canteen = mCanteenDao.getCanteenByIdSync(canteenId);
            canteen.setAsFavorite(!canteen.isFavorite());
            mCanteenDao.updateCanteen(canteen);
        });
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
        return mCanteens;
    }

    public LiveData<Integer> getFetchState() {
        return mFetchState;
    }

    public void fetchCanteens(boolean forceFetching) {
        long lastUpdate = mPreferenceService.getLastCanteenUpdate();
        if (!forceFetching && (Calendar.getInstance().getTimeInMillis() - lastUpdate < CANTEEN_UPDATE_INTERVAL)) {
            return;
        }
        mFetchState.setValue(IS_FETCHING);
        mApiService.getCanteens().enqueue(new Callback<ApiResponse<Canteen>>() {
            @Override
            public void onResponse(Call<ApiResponse<Canteen>> call, Response<ApiResponse<Canteen>> response) {
                insertOrUpdateCanteens(response.body().getData());
                mPreferenceService.setLastCanteenUpdate(Calendar.getInstance().getTimeInMillis());
                mFetchState.setValue(FETCH_SUCCESS);
            }

            @Override
            public void onFailure(Call<ApiResponse<Canteen>> call, Throwable t) {
                mFetchState.setValue(FETCH_ERROR);
            }
        });
    }
}
