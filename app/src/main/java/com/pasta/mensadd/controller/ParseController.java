package com.pasta.mensadd.controller;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.pasta.mensadd.fragments.CanteenListFragment;
import com.pasta.mensadd.model.Canteen;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.model.Meal;
import com.pasta.mensadd.model.News;
import com.pasta.mensadd.networking.LoadCanteensCallback;
import com.pasta.mensadd.networking.LoadMealsCallback;
import com.pasta.mensadd.networking.LoadNewsCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ParseController {

    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);
    public static final int PARSE_SUCCESS = 10;

    public boolean parseCanteens(String message, DatabaseController dbController,
                                 SharedPreferences prefs, LoadCanteensCallback callback) {
        CanteenParserTask canteenParserTask = new CanteenParserTask(dbController, message, prefs, callback);
        canteenParserTask.execute();
        return true;
    }

    public boolean parseNews(String message, LoadNewsCallback callback) {
        NewsParserTask newsParserTask = new NewsParserTask(message, callback);
        newsParserTask.execute();
        return true;
    }

    public boolean parseMealsForCanteen(String canteenCode, String message, DatabaseController
            dbController, LoadMealsCallback callback) {
        MealParserTask mealParserTask = new MealParserTask(dbController, canteenCode, message, callback);
        mealParserTask.execute();
        return true;
    }


    private class MealParserTask extends AsyncTask<Void, Void, Boolean> {
        DatabaseController mDbController;
        String mCanteenCode;
        String mMessage;
        LoadMealsCallback mMealsCallback;


        public MealParserTask(DatabaseController dbController, String canteenCode, String message, LoadMealsCallback callback){
            mDbController = dbController;
            mCanteenCode = canteenCode;
            mMessage = message;
            mMealsCallback = callback;
        }


        protected Boolean doInBackground(Void... canteens) {
            try {
                JSONArray mealDays = new JSONArray(mMessage);
                mDbController.deleteMealsOfCanteen(mCanteenCode);
                Date day = new Date();
                for (int i = 0; i < mealDays.length(); i++) {
                    Log.i("Loading meals", "Parsing day "+DATE_FORMAT.format(day));
                    Log.i("Loading meals", "Parsing day "+day.getTime());
                    JSONObject mealDay = mealDays.getJSONObject(i);
                    JSONArray meals = mealDay.getJSONArray(DATE_FORMAT.format(day));
                    if (meals.length() == 0) Log.i("Loading meals", "No meals");
                    ArrayList<Meal> mealList = new ArrayList<>();
                    for (int j = 0; j < meals.length(); j++) {
                        JSONObject jsonMeal = meals.getJSONObject(j);
                        int vegan = jsonMeal.getInt("vegan");
                        int vegetarian = jsonMeal.getInt("vegetarian");
                        int beef = jsonMeal.getInt("beef");
                        int pork = jsonMeal.getInt("porc");
                        int garlic = jsonMeal.getInt("garlic");
                        int alcohol = jsonMeal.getInt("alcohol");
                        String imgLink = jsonMeal.getString("imgLink");
                        String details = jsonMeal.getString("mealDetails");
                        String location = jsonMeal.getString("mealLocation");
                        String name = jsonMeal.getString("name");
                        String price = jsonMeal.getString("price");
                        Meal meal = new Meal(name, location, imgLink, details, price, mCanteenCode, String
                                .valueOf(mealDay.keys().next()), vegan == 1, vegetarian == 1, pork ==
                                1, beef == 1, garlic == 1, alcohol == 1);
                        mealList.add(meal);
                        //Log.i("Loading meals", "Padding "+meal.getName());
                        mDbController.updateMealTable(meal);
                    }
                    if (DataHolder.getInstance().getCanteen(mCanteenCode).getMealMap().get(String.valueOf
                                    (mealDay.keys().next())) != null) {
                        DataHolder.getInstance().getCanteen(mCanteenCode).getMealMap().get(String.valueOf
                                (mealDay.keys().next())).clear();
                        DataHolder.getInstance().getCanteen(mCanteenCode).getMealMap().get(String.valueOf
                                (mealDay.keys().next())).addAll(mealList);
                    } else {
                        DataHolder.getInstance().getCanteen(mCanteenCode).getMealMap().put(String.valueOf
                                (mealDay.keys().next()), mealList);
                    }
                    day.setTime(day.getTime() + 86400000);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            mMealsCallback.onResponseMessage(PARSE_SUCCESS,"");
        }
    }

    private class CanteenParserTask extends AsyncTask<Void, Void, Boolean> {
        DatabaseController mDbController;
        String mMessage;
        LoadCanteensCallback mCanteensCallback;
        SharedPreferences mPrefs;


        public CanteenParserTask(DatabaseController dbController, String message, SharedPreferences prefs, LoadCanteensCallback callback){
            mDbController = dbController;
            mMessage = message;
            mCanteensCallback = callback;
            mPrefs = prefs;
        }


        protected Boolean doInBackground(Void... canteens) {
            DataHolder.getInstance().getCanteenList().clear();
            try {
                JSONArray json = new JSONArray(mMessage);
                for (int i = 0; i < json.length(); i++) {
                    JSONObject canteen = json.getJSONObject(i);
                    String name = canteen.getString("name");
                    String code = canteen.getString("code");
                    String address = canteen.getString("address");
                    JSONArray gpsArray = canteen.getJSONArray("coordinates");
                    Log.i("Parsing canteens", name);
                    LatLng position = new LatLng(Double.parseDouble(gpsArray.get(0).toString()),
                            Double.parseDouble(gpsArray.get(1).toString()));
                    JSONArray hourArray = canteen.getJSONArray("hours");
                    String hours = "";
                    for (int j = 0; j < hourArray.length(); j++) {
                        hours += hourArray.get(j);
                        if (j < hourArray.length() - 1)
                            hours += "\n";
                    }
                    int priority = mPrefs.getInt("priority_" + code, 0);
                    Canteen m = new Canteen(name, code, position, address, hours, priority);
                    DataHolder.getInstance().getCanteenList().add(m);
                }
                mDbController.updateCanteenTable();
                mPrefs.edit().putLong(CanteenListFragment.KEY_LAST_CANTEENS_UPDATE, new Date().getTime
                        ()).apply();
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            DataHolder.getInstance().sortCanteenList();
            return true;
        }

        protected void onPostExecute(Boolean result) {
            mCanteensCallback.onResponseMessage(PARSE_SUCCESS,"");
        }
    }

    private class NewsParserTask extends AsyncTask<Void, Void, Boolean> {
        String mMessage;
        LoadNewsCallback mLoadNewsCallback;

        public NewsParserTask(String message, LoadNewsCallback callback){
            mMessage = message;
            mLoadNewsCallback = callback;
        }

        protected Boolean doInBackground(Void... canteens) {
            DataHolder.getInstance().getNewsList().clear();
            try {
                JSONArray json = new JSONArray(mMessage);
                for (int i = 0; i < json.length(); i++) {
                    JSONObject news = json.getJSONObject(i);
                    String heading = news.getString("newsHeading");
                    String date = news.getString("newsDate");
                    String category = news.getString("newsCategory");
                    String textShort = news.getString("newsContentShort");
                    String textLong = news.getString("newsContentLong");
                    String imgLink = news.getString("newsImgLink");

                    News n = new News(category, date, heading, textShort, textLong, imgLink);
                    DataHolder.getInstance().getNewsList().add(n);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            mLoadNewsCallback.onResponseMessage(PARSE_SUCCESS,"");
        }
    }
}


