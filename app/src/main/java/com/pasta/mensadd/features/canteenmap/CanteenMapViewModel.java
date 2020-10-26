package com.pasta.mensadd.features.canteenmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.pasta.mensadd.domain.canteen.Canteen;
import com.pasta.mensadd.domain.canteen.CanteenRepository;
import com.pasta.mensadd.features.canteenlist.CanteenListViewModel;

public class CanteenMapViewModel extends CanteenListViewModel {

    private MutableLiveData<String> mSelectedCanteenIdLive;
    private String mSelectedCanteenId;

    public CanteenMapViewModel(CanteenRepository canteenRepository) {
        super(canteenRepository);
        mSelectedCanteenIdLive = new MutableLiveData<>();
    }

    public String getSelectedCanteenId() {
        return mSelectedCanteenId;
    }

    public void setSelectedCanteenId(String selectedCanteen) {
        mSelectedCanteenIdLive.setValue(selectedCanteen);
        mSelectedCanteenId = selectedCanteen;
    }

    public LiveData<Canteen> getSelectedCanteen() {
        return Transformations.switchMap(mSelectedCanteenIdLive, (canteenId) -> getCanteenById(canteenId));
    }
}
