package com.pasta.mensadd.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by julian on 15.05.16.
 */
public class Canteen {

    private int mId;
    private String mName;
    private String mHours;
    private String mAddress;
    private String mCode;
    private String mInfo;
    private LatLng mPosition;
    private String mContactData;
    private int mListPriority;
    private HashMap<Integer, ArrayList<Meal>> mealMap = new HashMap<Integer, ArrayList<Meal>>();

    public Canteen(int id, String name, String hours, String address,
                   String contact, LatLng position, String info) {
        mId = id;
        mAddress = address;
        mHours = hours;
        mName = name;
        mInfo = info;
        mContactData = contact;
        mPosition = position;
        if (mCode.contains("zeltschloesschen") || mCode.contains("alte-mensa"))
            mListPriority = 3;
        else if (mCode.contains("siedepunkt") || mCode.contains("mensa-reichenbachstrasse"))
            mListPriority = 2;
        else
            mListPriority = 0;
    }

    public Canteen(String name, String code, String address, String hour, int priority){
        mName = name;
        mCode = code;
        mHours = hour;
        mAddress = address;
        if (mCode.contains("zeltschloesschen") || mCode.contains("alte-mensa"))
            if (priority < 2)
                mListPriority = 2;
            else
                mListPriority = priority;
        else if (mCode.contains("siedepunkt") || mCode.contains("mensa-reichenbachstrasse"))
            if (priority < 1)
                mListPriority = 1;
            else
                mListPriority = priority;
        else
            mListPriority = priority;
    }

    public String getCode(){
        return mCode;
    }

    public HashMap<Integer, ArrayList<Meal>> getmealMap() {
        return mealMap;
    }

    public int getListPriority(){
        return mListPriority;
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
