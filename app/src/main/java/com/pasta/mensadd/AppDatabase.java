package com.pasta.mensadd;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.pasta.mensadd.domain.balanceentry.BalanceEntryDao;
import com.pasta.mensadd.domain.meal.MealDao;
import com.pasta.mensadd.domain.news.NewsDao;
import com.pasta.mensadd.domain.balanceentry.BalanceEntry;
import com.pasta.mensadd.domain.canteen.Canteen;
import com.pasta.mensadd.domain.canteen.CanteenDao;
import com.pasta.mensadd.domain.meal.Meal;
import com.pasta.mensadd.domain.news.News;

@Database(entities = {Canteen.class, Meal.class, News.class, BalanceEntry.class}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    private static final String DB_NAME = "mensadd_v4.0_database";

    public abstract CanteenDao canteenDao();
    public abstract MealDao mealDao();
    public abstract NewsDao newsDao();
    public abstract BalanceEntryDao balanceEntryDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, AppDatabase.DB_NAME).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
