package com.pasta.mensadd.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.repository.CanteenRepository;

import java.util.List;

public class CanteensViewModel extends ViewModel {

    private CanteenRepository mCanteenRepository;
    private LiveData<List<Canteen>> canteens;
    private Canteen selectedCanteen;

    public CanteensViewModel(CanteenRepository canteenRepository) {
        mCanteenRepository = canteenRepository;
        canteens = canteenRepository.getCanteens();
    }

    public void updateCanteen(Canteen canteen) {
        mCanteenRepository.updateCanteen(canteen);
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
        return mCanteenRepository.getCanteenById(id);
    }

    public LiveData<Boolean> isRefreshing() {
        return mCanteenRepository.isRefreshing();
    }

}
