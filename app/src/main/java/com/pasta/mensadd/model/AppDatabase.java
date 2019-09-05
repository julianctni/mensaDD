package com.pasta.mensadd.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Canteen.class, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract CanteenDao canteenDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "mensadd_database").fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
