package com.pasta.mensadd.database.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.pasta.mensadd.database.AppDatabase;
import com.pasta.mensadd.database.dao.CanteenDao;
import com.pasta.mensadd.database.dao.MealDao;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.entity.Meal;
import com.pasta.mensadd.networking.ApiResponse;
import com.pasta.mensadd.networking.ApiServiceClient;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pasta.mensadd.networking.ApiServiceClient.FETCH_ERROR;
import static com.pasta.mensadd.networking.ApiServiceClient.FETCH_SUCCESS;
import static com.pasta.mensadd.networking.ApiServiceClient.IS_FETCHING;
import static com.pasta.mensadd.networking.ApiServiceClient.NOT_FETCHING;

public class MealRepository {

    public static final int FIFTEEN_MINUTES_MILLIS = 0 * 60 * 1000;
    private MealDao mMealDao;
    private CanteenDao mCanteenDao;
    private ApiServiceClient mApiServiceClient;
    private MutableLiveData<Integer> mFetchState;
    private AppDatabase mAppDatabase;

    public MealRepository(AppDatabase appDatabase, ApiServiceClient apiServiceClient, Canteen canteen) {
        mAppDatabase = appDatabase;
        mMealDao = appDatabase.mealDao();
        mCanteenDao = appDatabase.canteenDao();
        mApiServiceClient = apiServiceClient;
        mFetchState = new MutableLiveData<>(NOT_FETCHING);
        if (canteen.getLastMealUpdate() < Calendar.getInstance().getTimeInMillis() - FIFTEEN_MINUTES_MILLIS) {
            fetchMeals(canteen);
        }
    }

    public void insertOrUpdateMeals(List<Meal> meals) {
        mAppDatabase.getTransactionExecutor().execute(() -> mMealDao.insertOrUpdateMeals(meals));
    }

    public LiveData<List<Meal>> getMealsByCanteenByDay(Canteen canteen, String day) {
        return Transformations.switchMap(mFetchState, (fetchState) -> mMealDao.getMealsByCanteenByDay(canteen.getId(), day));
    }

    public void fetchMeals(Canteen canteen) {
        mFetchState.setValue(IS_FETCHING);
        mApiServiceClient.fetchMeals(canteen.getId()).enqueue(new Callback<ApiResponse<Meal>>() {
            @Override
            public void onResponse(Call<ApiResponse<Meal>> call, Response<ApiResponse<Meal>> response) {
                insertOrUpdateMeals(response.body().getData());
                canteen.setLastMealUpdate(Calendar.getInstance().getTimeInMillis());
                canteen.setLastMealScraping(response.body().getScrapedAt());
                mAppDatabase.getTransactionExecutor().execute(() -> mCanteenDao.updateCanteen(canteen));
                mFetchState.setValue(FETCH_SUCCESS);
            }

            @Override
            public void onFailure(Call<ApiResponse<Meal>> call, Throwable t) {
                mFetchState.setValue(FETCH_ERROR);
            }
        });
    }

    public LiveData<Integer> getFetchState() {
        return mFetchState;
    }

}
