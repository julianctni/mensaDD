package com.pasta.mensadd.controller;

import android.content.SharedPreferences;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.pasta.mensadd.fragments.CanteenListFragment;
import com.pasta.mensadd.model.Canteen;
import com.pasta.mensadd.model.DataHolder;
import com.pasta.mensadd.model.Meal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ParseController {

    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);

    public boolean parseCanteens(String message, DatabaseController dbController,
                                 SharedPreferences prefs) {
        DataHolder.getInstance().getCanteenList().clear();
        try {
            JSONArray json = new JSONArray(message);
            for (int i = 0; i < json.length(); i++) {
                JSONObject canteen = json.getJSONObject(i);
                String name = canteen.getString("name");
                String code = canteen.getString("code");
                String address = canteen.getString("address");
                JSONArray gpsArray = canteen.getJSONArray("coordinates");
                LatLng position = new LatLng(Double.parseDouble(gpsArray.get(0).toString()),
                        Double.parseDouble(gpsArray.get(1).toString()));
                JSONArray hourArray = canteen.getJSONArray("hours");
                String hours = "";
                for (int j = 0; j < hourArray.length(); j++) {
                    hours += hourArray.get(j);
                    if (j < hourArray.length() - 1)
                        hours += "\n";
                }
                int priority = prefs.getInt("priority_" + code, 0);
                Canteen m = new Canteen(name, code, position, address, hours, priority);
                DataHolder.getInstance().getCanteenList().add(m);
            }
            dbController.updateCanteenTable();
            prefs.edit().putLong(CanteenListFragment.KEY_LAST_CANTEENS_UPDATE, new Date().getTime
                    ()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        DataHolder.getInstance().sortCanteenList();
        return true;
    }

    public boolean parseMealsForCanteen(String canteenCode, String message, DatabaseController
            dbController) {
        try {
            JSONArray mealDays = new JSONArray(message);
            dbController.deleteMealsOfCanteen(canteenCode);
            Date day = new Date();
            for (int i = 0; i < mealDays.length(); i++) {
                JSONObject mealDay = mealDays.getJSONObject(i);
                JSONArray meals = mealDay.getJSONArray(DATE_FORMAT.format(day));
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
                    String name = jsonMeal.getString("name");
                    String price = jsonMeal.getString("price");
                    Meal meal = new Meal(name, imgLink, details, price, canteenCode, String
                            .valueOf(mealDay.keys().next()), vegan == 1, vegetarian == 1, pork ==
                            1, beef == 1, garlic == 1, alcohol == 1);
                    mealList.add(meal);
                    dbController.updateMealTable(meal);
                }
                DataHolder.getInstance().getMensa(canteenCode).getMealMap().put(String.valueOf
                        (mealDay.keys().next()), mealList);
                day.setTime(day.getTime() + 86400000);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
