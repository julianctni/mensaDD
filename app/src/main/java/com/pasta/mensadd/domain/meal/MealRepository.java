package com.pasta.mensadd.domain.meal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.pasta.mensadd.AppDatabase;
import com.pasta.mensadd.domain.ApiService;
import com.pasta.mensadd.domain.ApiRepository;
import com.pasta.mensadd.domain.canteen.Canteen;
import com.pasta.mensadd.domain.canteen.CanteenDao;
import com.pasta.mensadd.network.ApiResponse;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealRepository extends ApiRepository {

    public static final int FIFTEEN_MINUTES_MILLIS = 15 * 60 * 1000;
    private MealDao mMealDao;
    private CanteenDao mCanteenDao;

    public MealRepository(AppDatabase appDatabase, ApiService apiService, String canteenId) {
        super(appDatabase, apiService);
        mMealDao = mAppDatabase.mealDao();
        mCanteenDao = mAppDatabase.canteenDao();
        mAppDatabase.getQueryExecutor().execute(() -> {
            if (mCanteenDao.getLastMealUpdate(canteenId) < Calendar.getInstance().getTimeInMillis() - FIFTEEN_MINUTES_MILLIS) {
                fetchMeals(canteenId);
            }
        });

    }


    public void insertOrUpdateMeals(List<Meal> meals, String canteenId, long scrapedAt) {
        mAppDatabase.getTransactionExecutor().execute(() -> {
            mMealDao.insertOrUpdateMeals(meals);
            Canteen canteen = mCanteenDao.getCanteenByIdSync(canteenId);
            canteen.setLastMealScraping(scrapedAt);
            canteen.setLastMealUpdate(Calendar.getInstance().getTimeInMillis());
            mCanteenDao.updateCanteen(canteen);
        });
    }

    public LiveData<List<Meal>> getMealsByCanteenByDay(String canteenId, String day) {
        return Transformations.switchMap(mFetchState, (fetchState) -> mMealDao.getMealsByCanteenByDay(canteenId, day));
    }

    public void fetchMeals(String canteenId) {
        mFetchState.postValue(IS_FETCHING);
        mApiService.getMeals(canteenId).enqueue(new Callback<ApiResponse<Meal>>() {
            @Override
            public void onResponse(Call<ApiResponse<Meal>> call, Response<ApiResponse<Meal>> response) {
                insertOrUpdateMeals(response.body().getData(), canteenId, response.body().getScrapedAt());
                mFetchState.postValue(FETCH_SUCCESS);
            }

            @Override
            public void onFailure(Call<ApiResponse<Meal>> call, Throwable t) {
                mFetchState.postValue(FETCH_ERROR);
            }
        });
    }

    public LiveData<Integer> getFetchState() {
        return mFetchState;
    }

}
