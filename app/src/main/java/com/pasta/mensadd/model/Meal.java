package com.pasta.mensadd.model;

import android.text.Html;

public class Meal {
    private String mName;
    private String mPrice;
    private String mDetails;
    private String mImgLink;
    private String mCanteenCode;
    private String mDate;
    private String mLocation;
    private boolean mVegetarian;
    private boolean mVegan;
    private boolean mPork;
    private boolean mBeef;
    private boolean mGarlic;
    private boolean mAlcohol;

    public Meal(String name, String location, String imgLink, String details, String price, String canteen, String date, boolean vegan, boolean vegetarian, boolean pork, boolean beef, boolean garlic, boolean alcohol) {
        mName = name;
        mLocation = location;
        mPrice = price;
        mImgLink = imgLink;
        mDetails = formatDetails(details);
        mVegan = vegan;
        mVegetarian = vegetarian;
        mPork = pork;
        mBeef = beef;
        mGarlic = garlic;
        mAlcohol = alcohol;
        mCanteenCode = canteen;
        mDate = date;
    }

    public String getCanteenCode() {
        return mCanteenCode;
    }

    public String getName() {
        return mName;
    }

    public String getPrice() {
        return mPrice;
    }

    public Boolean isVegetarian() {
        return mVegetarian;
    }

    public Boolean isVegan() {
        return mVegan;
    }

    public Boolean containsPork() {
        return mPork;
    }

    public Boolean containsBeef() {
        return mBeef;
    }

    public Boolean containsGarlic() {
        return mGarlic;
    }

    public Boolean containsAlcohol() {
        return mAlcohol;
    }

    public String getDate() {
        return mDate;
    }

    public String getDetails() {
        return mDetails;
    }

    public String getImgLink() {
        return mImgLink;
    }

    public String getLocation() {
        return mLocation;
    }

    private String formatDetails(String content) {
        return Html.fromHtml("&#149;").toString() + " " + content.replace(", ", "\n" + Html.fromHtml("&#149;").toString() + " ");
    }
}
