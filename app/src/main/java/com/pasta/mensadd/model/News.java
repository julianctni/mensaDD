package com.pasta.mensadd.model;

import android.graphics.Bitmap;

/**
 * Created by julian on 09.02.18.
 */

public class News {

    private String mCategory;
    private String mDate;
    private String mHeading;
    private String mContentShort;
    private String mContentLong;
    private String mImgLink;
    private String mLink;

    public News(String mCategory, String mDate, String mHeading, String mContentShort, String mImgLink, String mLink) {
        this.mCategory = mCategory;
        this.mDate = mDate;
        this.mHeading = mHeading;
        this.mContentShort = mContentShort;
        this.mContentLong = mContentLong;
        this.mImgLink = mImgLink;
        this.mLink = mLink;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getDate() {
        return mDate;
    }

    public String getHeading() {
        return mHeading;
    }

    public String getContentShort() {
        return mContentShort;
    }

    public String getContentLong() {
        return mContentLong;
    }

    public String getImgLink() {
        return mImgLink;
    }


    public String getLink () { return mLink; }


}
