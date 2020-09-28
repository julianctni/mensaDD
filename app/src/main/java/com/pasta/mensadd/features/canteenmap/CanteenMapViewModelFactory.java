package com.pasta.mensadd.features.canteenmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.pasta.mensadd.features.canteenlist.CanteenListViewModelFactory;
import com.pasta.mensadd.domain.canteen.CanteenRepository;

public class CanteenMapViewModelFactory extends CanteenListViewModelFactory {

    public CanteenMapViewModelFactory(CanteenRepository canteenRepository) {
        super(canteenRepository);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CanteenMapViewModel(mCanteenRepository);
    }
}
