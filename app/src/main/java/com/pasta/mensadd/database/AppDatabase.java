package com.pasta.mensadd.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.pasta.mensadd.database.dao.BalanceEntryDao;
import com.pasta.mensadd.database.dao.MealDao;
import com.pasta.mensadd.database.dao.NewsDao;
import com.pasta.mensadd.database.entity.BalanceEntry;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.dao.CanteenDao;
import com.pasta.mensadd.database.entity.Meal;
import com.pasta.mensadd.database.entity.News;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Database(entities = {Canteen.class, Meal.class, News.class, BalanceEntry.class}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    public static final Executor dbExecutor = Executors.newSingleThreadExecutor();
    private static final String DB_NAME = "mensadd_database";

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
