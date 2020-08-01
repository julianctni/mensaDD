package com.pasta.mensadd.database.entity;

import android.text.Html;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_meals")
public class Meal {

    @NonNull
    @PrimaryKey
    private String id;
    private String name;
    private String price;
    private String details;
    private String imgLink;
    private String canteenId;
    private String date;
    private String location;
    private boolean vegetarian;
    private boolean vegan;
    private boolean pork;
    private boolean beef;
    private boolean garlic;
    private boolean alcohol;

    public Meal(String id, String name, String price, String details, String imgLink, String canteenId, String date, String location, boolean vegetarian, boolean vegan, boolean pork, boolean beef, boolean garlic, boolean alcohol) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.details = details;
        this.imgLink = imgLink;
        this.canteenId = canteenId;
        this.date = date;
        this.location = location;
        this.vegetarian = vegetarian;
        this.vegan = vegan;
        this.pork = pork;
        this.beef = beef;
        this.garlic = garlic;
        this.alcohol = alcohol;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getCanteenId() {
        return canteenId;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public Boolean isVegetarian() {
        return vegetarian;
    }

    public Boolean isVegan() {
        return vegan;
    }

    public Boolean isPork() {
        return pork;
    }

    public Boolean isBeef() {
        return beef;
    }

    public Boolean isGarlic() {
        return garlic;
    }

    public Boolean isAlcohol() {
        return alcohol;
    }

    public String getDate() {
        return date;
    }

    public String getDetails() {
        return details;
    }

    public String getImgLink() {
        return imgLink;
    }

    public String getLocation() {
        return location;
    }

    public String formatDetails(String content) {
        return content.isEmpty() ? content : Html.fromHtml("&#149;").toString() + " " + content.replace(", ", "\n" + Html.fromHtml("&#149;").toString() + " ");
    }
}
