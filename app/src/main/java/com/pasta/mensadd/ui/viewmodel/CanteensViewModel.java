package com.pasta.mensadd.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.database.repository.CanteenRepository;

import java.util.List;

public class CanteensViewModel extends ViewModel {

    private CanteenRepository mCanteenRepository;
    private LiveData<List<Canteen>> mCanteens;
    private Canteen mSelectedCanteen;

    public CanteensViewModel(CanteenRepository canteenRepository) {
        mCanteenRepository = canteenRepository;
        mCanteens = canteenRepository.getCanteens();
    }

    public void updateCanteen(Canteen canteen) {
        mCanteenRepository.updateCanteen(canteen);
    }

    public LiveData<List<Canteen>> getCanteens() {
        return mCanteens;
    }

    public Canteen getSelectedCanteen() {
        return mSelectedCanteen;
    }

    public void setSelectedCanteen(Canteen selectedCanteen) {
        mSelectedCanteen = selectedCanteen;
    }

    public LiveData<Canteen> getCanteenById(String id) {
        return mCanteenRepository.getCanteenById(id);
    }

    public LiveData<Integer> getFetchState() {
        return mCanteenRepository.getFetchState();
    }

    public void triggerCanteenFetching(boolean forceFetching) {
        mCanteenRepository.fetchCanteens(forceFetching);
    }

}
