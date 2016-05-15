package com.pasta.mensadd.model;

public class Meal {
    private String mName;
    private String mPrice;
    private String mDetailLink;
    private boolean mVegetarian;
    private boolean mVegan;
    private boolean mPork;
    private boolean mBeef;
    private boolean mGarlic;
    private boolean mAlcohol;

    public Meal(String name, String detailLink, String price, boolean vegan, boolean vegetarian, boolean pork, boolean beef, boolean garlic, boolean alcohol) {
        mName = name;
        mPrice = price;
        mDetailLink = detailLink;
        mVegan = vegan;
        mVegetarian = vegetarian;
        mPork = pork;
        mBeef = beef;
        mGarlic = garlic;
        mAlcohol = alcohol;
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

    public String getDetailLink() {
        return mDetailLink;
    }

    public String getStringNotes() {
        String result = "";
        if (this.isVegan())
            result += "\nMenü ist vegan";
        else if (this.isVegetarian())
            result += "\nMenü ist vegetarisch";
        if (this.containsAlcohol())
            result += "\nMenü enthält Alkohol";
        if (this.containsGarlic())
            result += "\nMenü enthält Knoblauch";
        if (this.containsPork())
            result += "\nMenü enthält Schweinefleisch";
        if (this.containsBeef())
            result += "\nMenü enthält Rindfleisch";
        return result;
    }
}
