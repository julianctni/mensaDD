package com.pasta.mensadd.features.canteenmap;

import com.pasta.mensadd.features.canteenlist.CanteenListViewModel;
import com.pasta.mensadd.domain.canteen.CanteenRepository;

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
