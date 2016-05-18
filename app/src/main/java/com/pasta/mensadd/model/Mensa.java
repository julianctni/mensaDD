package com.pasta.mensadd.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by julian on 15.05.16.
 */
public class Mensa {

    private int mId;
    private String mName;
    private String mHours;
    private String mAddress;
    private String mCode;
    private String mInfo;
    private LatLng mPosition;
    private String mContactData;
    private HashMap<Integer, ArrayList<Meal>> mealMap = new HashMap<Integer, ArrayList<Meal>>();

    public Mensa(int id, String name, String hours, String address,
                 String contact, LatLng position, String info) {
        mId = id;
        mAddress = address;
        mHours = hours;
        mName = name;
        mInfo = info;
        mContactData = contact;
        mPosition = position;
    }

    public Mensa(String name, String code, String address, String hour){
        mName = name;
        mCode = code;
        mHours = hour;
        mAddress = address;
    }

    public HashMap<Integer, ArrayList<Meal>> getmealMap() {
        return mealMap;
    }

    public void setMealMap(HashMap<Integer, ArrayList<Meal>> meal) {
        this.mealMap = meal;
    }

    public int getId() {
        return mId;
    }

    public String getHours() {
        return mHours;
    }


    public String getAddress() {
        return mAddress;
    }

    public String getInfo() {
        return mInfo;
    }

    public String getName() {
        return mName;
    }

    public String getContactData() {
        return mContactData;
    }

}
