package com.pasta.mensadd.database.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pasta.mensadd.PreferenceService;
import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.CanteenDao;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.networking.ApiResponse;
import com.pasta.mensadd.networking.CanteenService;
import com.pasta.mensadd.networking.NetworkController;

import java.util.Calendar;
import java.util.List;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CanteenRepository {

    private static final int CANTEEN_UPDATE_INTERVAL = 10 * 60 * 60 * 1000;
    private CanteenDao mCanteenDao;
    private LiveData<List<Canteen>> mCanteens;
    private NetworkController mNetworkController;
    private MutableLiveData<Boolean> mIsRefreshing;
    private PreferenceService mPreferenceService;
    private AppDatabase mAppDatabase;

    public CanteenRepository(AppDatabase appDatabase, NetworkController networkController, PreferenceService preferenceService) {
        mAppDatabase = appDatabase;
        mCanteenDao = appDatabase.canteenDao();
        mCanteens = mCanteenDao.getCanteens();
        mNetworkController = networkController;
        mPreferenceService = preferenceService;
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
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://jvlian.uber.space/mensadd/api/v2/").addConverterFactory(GsonConverterFactory.create()).build();

        String authToken = Credentials.basic("mensadd-app", "621df5bb-3947-4527-8bc0-b39b8736abf4");
        CanteenService canteenService = retrofit.create(CanteenService.class);

        Call<ApiResponse<Canteen>> fetchCanteensCall = canteenService.getCanteens(authToken);
        fetchCanteensCall.enqueue(new Callback<ApiResponse<Canteen>>() {
            @Override
            public void onResponse(Call<ApiResponse<Canteen>> call, Response<ApiResponse<Canteen>> response) {
                insertOrUpdateCanteens(response.body().getData());
                mPreferenceService.setLastCanteenUpdate(Calendar.getInstance().getTimeInMillis());
                mIsRefreshing.setValue(false);
            }

            @Override
            public void onFailure(Call<ApiResponse<Canteen>> call, Throwable t) {

            }
        });
    }
}
