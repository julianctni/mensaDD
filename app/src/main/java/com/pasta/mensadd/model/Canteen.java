package com.pasta.mensadd.model;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by julian on 15.05.16.
 */
public class Canteen {

    private String mName;
    private String mHours;
    private String mAddress;
    private String mCode;
    private LatLng mPosition;
    private int mListPriority;
    private HashMap<String, ArrayList<Meal>> mealMap = new HashMap<>();


    public Canteen(String name, String code, LatLng position, String address, String hour, int priority){
        mName = name;
        mCode = code;
        mHours = hour;
        mAddress = address;
        mPosition = position;
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

    public HashMap<String, ArrayList<Meal>> getMealMap() {
        return mealMap;
    }

    public int getListPriority(){
        return mListPriority;
    }

    public LatLng getPosition () {
        return mPosition;
    }

    public String getHours() {
        return mHours;
    }

    public void increasePriority(){
        mListPriority += 1;
    }

    public String getAddress() {
        return mAddress;
    }


    public String getName() {
        return mName;
    }


}
