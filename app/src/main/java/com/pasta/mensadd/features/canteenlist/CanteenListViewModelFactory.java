package com.pasta.mensadd.features.canteenlist;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pasta.mensadd.domain.canteen.CanteenRepository;

public class CanteenListViewModelFactory implements ViewModelProvider.Factory {

    protected CanteenRepository mCanteenRepository;

    public CanteenListViewModelFactory(CanteenRepository canteenRepository) {
        mCanteenRepository = canteenRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CanteenListViewModel(mCanteenRepository);
    }
}
