package com.pasta.mensadd.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.repository.CanteenRepository;

import java.util.List;

public class CanteensViewModel extends AndroidViewModel {

    private CanteenRepository canteenRepository;
    private LiveData<List<Canteen>> canteens;
    private Canteen selectedCanteen;

    public CanteensViewModel(@NonNull Application application) {
        super(application);
        canteenRepository = new CanteenRepository(application);
        canteens = canteenRepository.getAllCanteens();
    }

    public void updateCanteen(Canteen canteen) {
        canteenRepository.update(canteen);
    }

    public LiveData<List<Canteen>> getAllCanteens() {
        return canteens;
    }

    public Canteen getSelectedCanteen() {
        return selectedCanteen;
    }

    public void setSelectedCanteen(Canteen selectedCanteen) {
        this.selectedCanteen = selectedCanteen;
    }

    public LiveData<Canteen> getCanteenById(String id) {
        return canteenRepository.getCanteenById(id);
    }

    public LiveData<Boolean> isRefreshing() {
        return canteenRepository.isRefreshing();
    }

}
