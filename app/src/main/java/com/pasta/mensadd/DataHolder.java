package com.pasta.mensadd;

import com.pasta.mensadd.database.entity.Canteen;

import java.util.ArrayList;

public class DataHolder {

    private static DataHolder mInstance;
    private ArrayList<Canteen> mCanteenList = new ArrayList<>();

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

    public Canteen getCanteen(String code) {
        for (Canteen m : mCanteenList) {
            if (m.getId().equals(code))
                return m;
        }
        return null;
    }
}
