package com.pasta.mensadd.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.HashMap;

@Entity(tableName = "table_canteens")
public class Canteen {

    @NonNull
    @PrimaryKey
    private String id;
    private String name;
    private String hours;
    private String address;
    private double posLat;
    private double posLong;
    private long lastMealUpdate;
    private int listPriority;

    @Ignore
    private HashMap<String, ArrayList<Meal>> mealMap = new HashMap<>();
    @Ignore
    private static final int FAVORITE_PRIORITY_EXTRA = 9999999;

    public Canteen(String id, String name, String hours, String address, double posLat, double posLong, int listPriority) {
        this.id = id;
        this.name = name;
        this.hours = hours;
        this.address = address;
        this.posLat = posLat;
        this.posLong = posLong;
        this.listPriority = listPriority;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastMealUpdate() {
        return lastMealUpdate;
    }

    public void setLastMealUpdate(long lastUpdate) {
        lastMealUpdate = lastUpdate;
    }

    public HashMap<String, ArrayList<Meal>> getMealMap() {
        return mealMap;
    }

    public int getListPriority() {
        return listPriority;
    }

    public void setListPriority(int listPriority) {
        this.listPriority = listPriority;
    }

    public String getHours() {
        return hours;
    }

    public void increasePriority() {
        listPriority += 1;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public double getPosLat() {
        return posLat;
    }

    public double getPosLong() {
        return posLong;
    }

    public void setAsFavorite(boolean favorite) {
        this.listPriority = favorite ? this.listPriority + FAVORITE_PRIORITY_EXTRA : this.listPriority - FAVORITE_PRIORITY_EXTRA;
    }

    public boolean isFavorite() {
        return this.listPriority >= FAVORITE_PRIORITY_EXTRA;
    }
}
