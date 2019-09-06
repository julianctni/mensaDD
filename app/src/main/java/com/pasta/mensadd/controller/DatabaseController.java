package com.pasta.mensadd.controller;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.database.entity.Meal;

import java.util.ArrayList;

public class DatabaseController extends SQLiteOpenHelper {

    public static final String BALANCES_TABLE_NAME = "table_balances";
    public static final String MEALS_TABLE_NAME = "table_meals";
    public static final String CANTEENS_TABLE_NAME = "table_canteens";

    public static final String BALANCE_ID = "balanceId";
    public static final String CARD_BALANCE = "cardBalance";
    public static final String LAST_TRANSACTION = "lastTransaction";

    public static final String CANTEEN_ID = "canteenId";
    public static final String CANTEEN_NAME = "canteenName";
    public static final String CANTEEN_ADDRESS = "canteenAddress";
    public static final String CANTEEN_HOURS = "canteenHours";
    public static final String CANTEEN_POS_LAT = "canteenPosLat";
    public static final String CANTEEN_POS_LONG = "canteenPosLong";

    public static final String MEAL_ID = "mealId";
    public static final String MEAL_NAME = "mealName";
    public static final String MEAL_LOCATION = "mealLocation";
    public static final String MEAL_PRICE = "mealPrice";
    public static final String MEAL_DETAILS = "mealDetails";
    public static final String MEAL_IMG_LINK = "mealImgLink";
    public static final String MEAL_VEGAN = "mealVegan";
    public static final String MEAL_VEGETARIAN = "mealVegetarian";
    public static final String MEAL_PORC = "mealPorc";
    public static final String MEAL_BEEF = "mealBeef";
    public static final String MEAL_GARLIC = "mealGarlic";
    public static final String MEAL_ALCOHOL = "mealAlcohol";
    public static final String MEAL_CANTEEN_CODE = "mealCanteenCode";
    public static final String MEAL_DATE = "mealDate";

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
        createMealTable(db);
        createCanteenTable(db);
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


    public void readCanteensFromDb() {
        SQLiteDatabase db = getReadableDatabase();
        Log.i("SQLite", "importing canteens from db");
        String[] projection = {CANTEEN_ID, CANTEEN_NAME, CANTEEN_ADDRESS, CANTEEN_HOURS, CANTEEN_POS_LAT, CANTEEN_POS_LONG};
        Cursor c = db.query(CANTEENS_TABLE_NAME, projection, null, null, null, null, null);
        while (c.moveToNext()) {
            String code = c.getString(c.getColumnIndex(CANTEEN_ID));
            String name = c.getString(c.getColumnIndex(CANTEEN_NAME));
            String address = c.getString(c.getColumnIndex(CANTEEN_ADDRESS));
            String hours = c.getString(c.getColumnIndex(CANTEEN_HOURS));
            double posLat = c.getDouble(c.getColumnIndex(CANTEEN_POS_LAT));
            double posLong = c.getDouble(c.getColumnIndex(CANTEEN_POS_LONG));
            int priority = prefs.getInt("priority_" + code, 0);
            Log.i("CANTEENPRIO", code + ": " + priority);
            Canteen canteen = new Canteen(code, name, hours, address, posLat, posLong, 0);
            DataHolder.getInstance().getCanteenList().add(canteen);
        }
        DataHolder.getInstance().sortCanteenList();
        c.close();
        db.close();
    }

    public void readMealsFromDb(String canteenCode) {
        SQLiteDatabase db = getReadableDatabase();
        Log.i("SQLite", "importing meals from db");
        String[] projection = {MEAL_NAME, MEAL_LOCATION, MEAL_PRICE, MEAL_DETAILS, MEAL_IMG_LINK, MEAL_DATE, MEAL_ALCOHOL, MEAL_GARLIC, MEAL_PORC, MEAL_BEEF, MEAL_VEGAN, MEAL_VEGETARIAN};
        String selection = MEAL_CANTEEN_CODE + " = '" + canteenCode + "'";
        Cursor c = db.query(MEALS_TABLE_NAME, projection, selection, null, null, null, null);
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(MEAL_NAME));
            String location = c.getString(c.getColumnIndex(MEAL_LOCATION));
            String price = c.getString(c.getColumnIndex(MEAL_PRICE));
            String details = c.getString(c.getColumnIndex(MEAL_DETAILS));
            String imgLink = c.getString(c.getColumnIndex(MEAL_IMG_LINK));
            String date = c.getString(c.getColumnIndex(MEAL_DATE));
            boolean alcohol = (c.getInt(c.getColumnIndex(MEAL_ALCOHOL)) == 1);
            boolean porc = (c.getInt(c.getColumnIndex(MEAL_PORC)) == 1);
            boolean beef = (c.getInt(c.getColumnIndex(MEAL_BEEF)) == 1);
            boolean garlic = (c.getInt(c.getColumnIndex(MEAL_GARLIC)) == 1);
            boolean vegan = (c.getInt(c.getColumnIndex(MEAL_VEGAN)) == 1);
            boolean vegetarian = (c.getInt(c.getColumnIndex(MEAL_VEGETARIAN)) == 1);
            Meal m = new Meal("test", name, location, imgLink, details, price, canteenCode, date, vegan, vegetarian, porc, beef, garlic, alcohol);
            if (DataHolder.getInstance().getCanteen(canteenCode).getMealMap().get(date) == null) {
                ArrayList<Meal> meals = new ArrayList<>();
                meals.add(m);
                DataHolder.getInstance().getCanteen(canteenCode).getMealMap().put(date, meals);
            } else {
                DataHolder.getInstance().getCanteen(canteenCode).getMealMap().get(date).add(m);
            }
        }
        c.close();
        db.close();
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

    public void updateCanteenTable() {
        SQLiteDatabase db = getWritableDatabase();
        createCanteenTable(db);
        db.execSQL("DELETE FROM " + CANTEENS_TABLE_NAME + ";");
        for (Canteen c : DataHolder.getInstance().getCanteenList()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseController.CANTEEN_ID, c.getId());
            values.put(DatabaseController.CANTEEN_NAME, c.getName());
            values.put(DatabaseController.CANTEEN_ADDRESS, c.getAddress());
            values.put(DatabaseController.CANTEEN_HOURS, c.getHours());
            values.put(DatabaseController.CANTEEN_POS_LAT, c.getPosLat());
            values.put(DatabaseController.CANTEEN_POS_LONG, c.getPosLong());
            db.insert(CANTEENS_TABLE_NAME, null, values);
        }
        db.close();
    }

    public void deleteMealsOfCanteen(String canteenCode) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + MEALS_TABLE_NAME + " WHERE " + MEAL_CANTEEN_CODE + " = '" + canteenCode + "';");
        db.close();
    }

    public void updateMealTable(Meal m) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseController.MEAL_NAME, m.getName());
        values.put(DatabaseController.MEAL_LOCATION, m.getLocation());
        values.put(DatabaseController.MEAL_PRICE, m.getPrice());
        values.put(DatabaseController.MEAL_DETAILS, m.getDetails());
        values.put(DatabaseController.MEAL_IMG_LINK, m.getImgLink());
        values.put(DatabaseController.MEAL_VEGAN, m.isVegan() ? 1 : 0);
        values.put(DatabaseController.MEAL_VEGETARIAN, m.isVegetarian() ? 1 : 0);
        values.put(DatabaseController.MEAL_PORC, m.isPork() ? 1 : 0);
        values.put(DatabaseController.MEAL_BEEF, m.isBeef() ? 1 : 0);
        values.put(DatabaseController.MEAL_ALCOHOL, m.isAlcohol() ? 1 : 0);
        values.put(DatabaseController.MEAL_GARLIC, m.isGarlic() ? 1 : 0);
        values.put(DatabaseController.MEAL_CANTEEN_CODE, m.getCanteenId());
        values.put(DatabaseController.MEAL_DATE, m.getDate());
        db.insert(MEALS_TABLE_NAME, null, values);
        db.close();
    }

    public void createMealTable(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + MEALS_TABLE_NAME + " (" + MEAL_ID + " " +
                "INTEGER PRIMARY KEY, " + MEAL_NAME + " TEXT, " + MEAL_LOCATION + " TEXT, " + MEAL_PRICE + " " + "TEXT, " +
                MEAL_DETAILS + " TEXT," + MEAL_IMG_LINK + " TEXT, " + MEAL_DATE + " TEXT, " + MEAL_CANTEEN_CODE + " TEXT," + MEAL_VEGETARIAN + " " +
                "INTEGER, " + MEAL_VEGAN + " " + "INTEGER, " + MEAL_GARLIC + " INTEGER, " +
                MEAL_PORC + " INTEGER, " + MEAL_BEEF + " INTEGER," + MEAL_ALCOHOL + " INTEGER);";
        db.execSQL(query);
    }

    public void createCanteenTable(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + CANTEENS_TABLE_NAME + " (" + CANTEEN_ID +
                " TEXT PRIMARY KEY, " + CANTEEN_NAME + " TEXT, " + CANTEEN_ADDRESS + " TEXT, "
                + CANTEEN_HOURS + " TEXT," + CANTEEN_POS_LAT + " REAL," +
                " " + CANTEEN_POS_LONG + " " + "REAL);";
        db.execSQL(query);
    }

    public void addColumnToTable(SQLiteDatabase db, String table, String column, String columnType) {
        String query = "ALTER TABLE " + table + " ADD COLUMN " + column + " " + columnType;
        Log.i("DATABASE", query);
        db.execSQL(query);
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
        Log.i("DATABASE", "Upgrading database");
        if (oldVersion == 1) {
            deleteOldTables(db);
            createMealTable(db);
            createCanteenTable(db);
            createBalanceTable(db);
        } else if (oldVersion == 2) {
            Log.i("DATABASE", "Adding location column to meal table.");
            addColumnToTable(db, MEALS_TABLE_NAME, MEAL_LOCATION, "TEXT");
        }
    }
}
