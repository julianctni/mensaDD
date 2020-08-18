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

public class MealRepository {

    public static final int FIFTEEN_MINUTES_MILLIS = 15 * 60 * 1000;
    private MealDao mMealDao;
    private CanteenDao mCanteenDao;
    private ApiServiceClient mApiServiceClient;
    private MutableLiveData<Boolean> mIsRefreshing;
    private AppDatabase mAppDatabase;

    public MealRepository(AppDatabase appDatabase, ApiServiceClient apiServiceClient, Canteen canteen) {
        mAppDatabase = appDatabase;
        mMealDao = appDatabase.mealDao();
        mCanteenDao = appDatabase.canteenDao();
        mApiServiceClient = apiServiceClient;
        mIsRefreshing = new MutableLiveData<>(false);
        if (canteen.getLastMealUpdate() < Calendar.getInstance().getTimeInMillis() - FIFTEEN_MINUTES_MILLIS) {
            fetchMeals(canteen);
        }
    }

    public void insertOrUpdateMeals(List<Meal> meals) {
        mAppDatabase.getTransactionExecutor().execute(() -> mMealDao.insertOrUpdateMeals(meals));
    }

    public LiveData<List<Meal>> getMealsByCanteenByDay(Canteen canteen, String day) {
        return Transformations.switchMap(mIsRefreshing, (refreshState) -> mMealDao.getMealsByCanteenByDay(canteen.getId(), day));
    }

    public void fetchMeals(Canteen canteen) {
        mIsRefreshing.setValue(true);
        mApiServiceClient.fetchMeals(canteen.getId()).enqueue(new Callback<ApiResponse<Meal>>() {
            @Override
            public void onResponse(Call<ApiResponse<Meal>> call, Response<ApiResponse<Meal>> response) {
                insertOrUpdateMeals(response.body().getData());
                canteen.setLastMealUpdate(Calendar.getInstance().getTimeInMillis());
                canteen.setLastMealScraping(response.body().getScrapedAt());
                mAppDatabase.getTransactionExecutor().execute(() -> mCanteenDao.updateCanteen(canteen));
                mIsRefreshing.setValue(false);
            }

            @Override
            public void onFailure(Call<ApiResponse<Meal>> call, Throwable t) {
                mIsRefreshing.setValue(false);
                // TODO: Add error handling
            }
        });
    }

    public LiveData<Boolean> isRefreshing() {
        return mIsRefreshing;
    }

}
