package com.pasta.mensadd.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class DataHolder {

    private static DataHolder mInstance;
    private HashMap<String,Canteen> mCanteenMap = new HashMap<>();
    private ArrayList<Canteen> mCanteenList = new ArrayList<>();

    private DataHolder(){}

    public static DataHolder getInstance(){
        if (mInstance == null) {
            mInstance = new DataHolder();
        }
        return mInstance;

    }

    public ArrayList<Canteen> getCanteenList() {
        return mCanteenList;
    }

    public Canteen getMensa(String code){
        for (Canteen m : mCanteenList){
            if (m.getCode().equals(code))
                return m;
        }
        return null;
    }

    public void sortCanteenList(){
        Collections.sort(mCanteenList, new PriorityComparator());
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
}
