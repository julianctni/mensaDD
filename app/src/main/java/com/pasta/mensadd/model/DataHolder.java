package com.pasta.mensadd.model;

import com.pasta.mensadd.database.entity.Canteen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class DataHolder {

    private static DataHolder mInstance;
    private ArrayList<Canteen> mCanteenList = new ArrayList<>();
    private ArrayList<News> mNewsList = new ArrayList<>();

    private DataHolder() {
    }

    public static DataHolder getInstance() {
        if (mInstance == null) {
            mInstance = new DataHolder();
        }
        return mInstance;

    }

    public ArrayList<Canteen> getCanteenList() {
        return mCanteenList;
    }

    public ArrayList<News> getNewsList() {
        return mNewsList;
    }

    public Canteen getCanteen(String code) {
        for (Canteen m : mCanteenList) {
            if (m.getId().equals(code))
                return m;
        }
        return null;
    }

    public void sortCanteenList() {
        Collections.sort(mCanteenList, new PriorityComparator());
    }

    public void sortNewsList() {
        Collections.sort(mNewsList, new DateComparator());
    }


    private class PriorityComparator implements Comparator<Canteen> {

        @Override
        public int compare(Canteen c1, Canteen c2) {
            if (c1.getListPriority() > c2.getListPriority())
                return -1;
            else if (c1.getListPriority() < c2.getListPriority())
                return 1;
            else
                return 0;
        }

        @Override
        public boolean equals(Object object) {
            return false;
        }
    }

    private class DateComparator implements Comparator<News> {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);

        @Override
        public int compare(News n1, News n2) {
            Date d1, d2;
            try {
                d1 = dateFormat.parse(n1.getDate());
                d2 = dateFormat.parse(n2.getDate());
                if (d1.after(d2))
                    return -1;
                else if (d2.after(d1))
                    return 1;
                else
                    return 0;
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        public boolean equals(Object object) {
            return false;
        }
    }
}
