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

@Database(entities = {Canteen.class, Meal.class, News.class, BalanceEntry.class}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract CanteenDao canteenDao();
    public abstract MealDao mealDao();
    public abstract NewsDao newsDao();
    public abstract BalanceEntryDao balanceEntryDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "mensadd_database").fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
