package com.pasta.mensadd.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CanteensViewModel extends AndroidViewModel {

    private CanteenRepository canteenRepository;
    private LiveData<List<Canteen>> canteens;

    public CanteensViewModel(@NonNull Application application) {
        super(application);
        canteenRepository = new CanteenRepository(application);
        canteens = canteenRepository.getAllCanteens();
    }

    public void insertCanteen(Canteen canteen) {
        canteenRepository.insert(canteen);
    }

    public void deleteCanteen(Canteen canteen) {
        canteenRepository.delete(canteen);
    }

    public void updateCanteen(Canteen canteen) {
        canteenRepository.update(canteen);
    }

    public void deleteAllCanteens() {
        canteenRepository.deleteAllCanteens();
    }

    public LiveData<List<Canteen>> getAllCanteens() {
        return canteens;
    }


}
