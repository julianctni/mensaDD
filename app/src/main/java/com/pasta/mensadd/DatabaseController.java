package com.pasta.mensadd;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.pasta.mensadd.database.entity.Canteen;

public class DatabaseController extends SQLiteOpenHelper {

    public static final String BALANCES_TABLE_NAME = "table_balances";
    public static final String MEALS_TABLE_NAME = "table_meals";
    public static final String CANTEENS_TABLE_NAME = "table_canteens";

    public static final String BALANCE_ID = "balanceId";
    public static final String CARD_BALANCE = "cardBalance";
    public static final String LAST_TRANSACTION = "lastTransaction";

    private static final String DATABASE_NAME = "mensadd.db";
    private static final int DATABASE_VERSION = 3;
    SharedPreferences prefs;

    public DatabaseController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DATABASE", "creating database");
        createBalanceTable(db);
    }


    public void deleteOldTables(SQLiteDatabase db) {
        String canteenTable = "DROP TABLE IF EXISTS mensen;";
        String mealTable = "DROP TABLE IF EXISTS meals;";
        db.execSQL(canteenTable);
        db.execSQL(mealTable);
    }

    public void deleteAllData() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + MEALS_TABLE_NAME + ";");
        db.execSQL("DELETE FROM " + CANTEENS_TABLE_NAME + ";");
        db.execSQL("DELETE FROM " + BALANCES_TABLE_NAME + ";");
        db.close();
        prefs.edit().remove("lastCanteenUpdate").apply();
        prefs.edit().remove("pref_bacon").apply();
        for (Canteen c : DataHolder.getInstance().getCanteenList()) {
            prefs.edit().remove("priority_" + c.getId()).apply();
        }
    }


    public void updateBalanceTable(long timestamp, float cardBalance, float lastTransaction) {
        SQLiteDatabase db = getWritableDatabase();
        String updateDb = "DELETE FROM " + BALANCES_TABLE_NAME + " WHERE " + BALANCE_ID + " NOT IN (" +
                "SELECT " + BALANCE_ID + " FROM " + BALANCES_TABLE_NAME + " ORDER BY " + BALANCE_ID + " DESC LIMIT 14);";
        db.execSQL(updateDb);
        ContentValues values = new ContentValues();
        values.put(DatabaseController.BALANCE_ID, timestamp);
        values.put(DatabaseController.CARD_BALANCE, cardBalance);
        values.put(DatabaseController.LAST_TRANSACTION, lastTransaction);
        db.insert(BALANCES_TABLE_NAME, null, values);
        db.close();
    }

    public float getLastInsertedBalance() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                DatabaseController.BALANCE_ID,
                DatabaseController.CARD_BALANCE,
                DatabaseController.LAST_TRANSACTION};

        String sortOrder =
                DatabaseController.BALANCE_ID + " DESC";

        Cursor c = db.query(
                DatabaseController.BALANCES_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        while (c.moveToNext()) {
            return c.getFloat(c.getColumnIndex(DatabaseController.CARD_BALANCE));
        }

        return 0f;
    }


    public void createBalanceTable(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + BALANCES_TABLE_NAME + " (" + BALANCE_ID +
                " INTEGER PRIMARY KEY, " + CARD_BALANCE + " REAL, " + LAST_TRANSACTION + " REAL);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
