package com.pasta.mensadd.ui.viewmodel;

import com.pasta.mensadd.database.repository.CanteenRepository;

public class CanteenMapViewModel extends CanteenListViewModel {

    private String mSelectedCanteenId;

    public CanteenMapViewModel(CanteenRepository canteenRepository) {
        super(canteenRepository);
    }

    public String getSelectedCanteenId() {
        return mSelectedCanteenId;
    }

    public void setSelectedCanteenId(String selectedCanteen) {
        this.mSelectedCanteenId = selectedCanteen;
    }
}
