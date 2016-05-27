package com.pasta.mensadd.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by julian on 19.05.16.
 */
public class DataHolder {

    private static DataHolder mInstance;
    private HashMap<String,Mensa> mCanteenMap = new HashMap<>();
    private ArrayList<Mensa> mCanteenList = new ArrayList<>();

    private DataHolder(){}

    public static DataHolder getInstance(){
        if (mInstance == null) {
            mInstance = new DataHolder();
        }
        return mInstance;

    }

    public ArrayList<Mensa> getCanteenList() {
        return mCanteenList;
    }

    public Mensa getMensa(String code){
        for (Mensa m : mCanteenList){
            if (m.getCode().equals(code))
                return m;
        }
        return null;
    }
    public HashMap<String,Mensa> getCanteenMap() {
        return mCanteenMap;
    }
}
