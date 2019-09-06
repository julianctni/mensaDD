package com.pasta.mensadd.controller;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.database.entity.Meal;
import com.pasta.mensadd.model.News;
import com.pasta.mensadd.networking.callbacks.LoadCanteensCallback;
import com.pasta.mensadd.networking.callbacks.LoadMealsCallback;
import com.pasta.mensadd.networking.callbacks.LoadNewsCallback;

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

    public void parseCanteens(String message, DatabaseController dbController,
                                 SharedPreferences prefs, LoadCanteensCallback callback) {
        CanteenParserTask canteenParserTask = new CanteenParserTask(dbController, message, prefs, callback);
        canteenParserTask.execute();
    }

    public void parseNews(String message, LoadNewsCallback callback) {
        NewsParserTask newsParserTask = new NewsParserTask(message, callback);
        newsParserTask.execute();
    }

    public void parseMealsForCanteen(String canteenCode, String message, DatabaseController
            dbController, LoadMealsCallback callback) {
        MealParserTask mealParserTask = new MealParserTask(dbController, canteenCode, message, callback);
        mealParserTask.execute();
    }


    private static class MealParserTask extends AsyncTask<Void, Void, Boolean> {
        DatabaseController mDbController;
        String mCanteenCode;
        String mMessage;
        LoadMealsCallback mMealsCallback;


        MealParserTask(DatabaseController dbController, String canteenCode, String message, LoadMealsCallback callback){
            mDbController = dbController;
            mCanteenCode = canteenCode;
            mMessage = message;
            mMealsCallback = callback;
        }


        protected Boolean doInBackground(Void... canteens) {
            try {
                JSONArray mealDays = new JSONObject(mMessage).getJSONArray("meals");
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
                        Meal meal = new Meal("test", name, location, imgLink, details, price, mCanteenCode, String
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

    private static class CanteenParserTask extends AsyncTask<Void, Void, Boolean> {
        DatabaseController mDbController;
        String mMessage;
        LoadCanteensCallback mCanteensCallback;
        SharedPreferences mPrefs;


        CanteenParserTask(DatabaseController dbController, String message, SharedPreferences prefs, LoadCanteensCallback callback){
            mDbController = dbController;
            mMessage = message;
            mCanteensCallback = callback;
            mPrefs = prefs;
        }


        protected Boolean doInBackground(Void... canteens) {
            DataHolder.getInstance().getCanteenList().clear();
            try {
                JSONArray json = new JSONObject(mMessage).getJSONArray("canteens");

                for (int i = 0; i < json.length(); i++) {
                    JSONObject canteen = json.getJSONObject(i);
                    String name = canteen.getString("name");
                    String code = canteen.getString("code");
                    String address = canteen.getString("address");
                    JSONArray gpsArray = canteen.getJSONArray("coordinates");
                    Log.i("Parsing canteens", name);
                    JSONArray hourArray = canteen.getJSONArray("hours");
                    StringBuilder hours = new StringBuilder();
                    for (int j = 0; j < hourArray.length(); j++) {
                        hours.append(hourArray.get(j));
                        if (j < hourArray.length() - 1)
                            hours.append("\n");
                    }
                    int priority = mPrefs.getInt("priority_" + code, 0);
                    Canteen m = new Canteen(code, name, hours.toString(), address, Double.parseDouble(gpsArray.get(0).toString()), Double.parseDouble(gpsArray.get(1).toString()), 0);
                    DataHolder.getInstance().getCanteenList().add(m);
                }
                mDbController.updateCanteenTable();
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

    private static class NewsParserTask extends AsyncTask<Void, Void, Boolean> {
        String mMessage;
        LoadNewsCallback mLoadNewsCallback;

        NewsParserTask(String message, LoadNewsCallback callback){
            mMessage = message;
            mLoadNewsCallback = callback;
        }

        protected Boolean doInBackground(Void... canteens) {
            DataHolder.getInstance().getNewsList().clear();
            try {
                JSONArray json = new JSONObject(mMessage).getJSONArray("news");
                for (int i = 0; i < json.length(); i++) {
                    JSONObject news = json.getJSONObject(i);
                    String heading = news.getString("newsHeading");
                    String date = news.getString("newsDate");
                    String category = news.getString("newsCategory");
                    String textShort = news.getString("newsContentShort");
                    String newsLink = news.getString("newsLink");

                    News n = new News(category, date, heading, textShort, newsLink);
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


