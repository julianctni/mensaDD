package com.pasta.mensadd.features.canteenlist;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pasta.mensadd.PreferenceService;
import com.pasta.mensadd.R;
import com.pasta.mensadd.Utils;
import com.pasta.mensadd.AppDatabase;
import com.pasta.mensadd.domain.ApiService;
import com.pasta.mensadd.domain.canteen.Canteen;
import com.pasta.mensadd.domain.canteen.CanteenRepository;
import com.pasta.mensadd.network.ServiceGenerator;
import com.pasta.mensadd.FragmentController;

import static com.pasta.mensadd.domain.ApiRepository.FETCH_ERROR;
import static com.pasta.mensadd.domain.ApiRepository.IS_FETCHING;


public class CanteenListFragment extends Fragment implements View.OnClickListener, CanteenListAdapter.OnFavoriteClickListener, CanteenListAdapter.OnCanteenClickListener {

    private CardView mLatestUpdatesCard;
    private CanteenListViewModel mCanteenListViewModel;
    private PackageInfo mPackageInfo;
    private PreferenceService mPreferenceService;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canteen_list, container, false);
        try {
            mPackageInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mPreferenceService = new PreferenceService(requireContext());
        CanteenRepository canteenRepository = new CanteenRepository(
                AppDatabase.getInstance(requireContext()),
                mPreferenceService,
                ServiceGenerator.createService(ApiService.class)
        );
        CanteenListViewModelFactory canteenListViewModelFactory = new CanteenListViewModelFactory(canteenRepository);
        mCanteenListViewModel = new ViewModelProvider(this, canteenListViewModelFactory).get(CanteenListViewModel.class);
        mLatestUpdatesCard = view.findViewById(R.id.latestUpdatesCard);
        RecyclerView canteenListRecyclerView = view.findViewById(R.id.canteenList);
        view.findViewById(R.id.latestUpdatesCloseButton).setOnClickListener(this);
        LinearLayoutManager layoutParams = new LinearLayoutManager(requireActivity());
        CanteenListAdapter canteenListAdapter = new CanteenListAdapter(requireContext());
        canteenListAdapter.setOnFavoriteClickListener(this);
        canteenListAdapter.setOnCanteenClickListener(this);
        canteenListRecyclerView.setAdapter(canteenListAdapter);
        canteenListRecyclerView.setLayoutManager(layoutParams);
        mCanteenListViewModel.triggerCanteenFetching(false);
        mCanteenListViewModel.getCanteens().observe(getViewLifecycleOwner(), canteenListAdapter::submitList);
        mCanteenListViewModel.getFetchState().observe(getViewLifecycleOwner(), fetchState -> {
            ProgressBar progressBar = view.findViewById(R.id.canteenListProgressBar);
            if (progressBar.getVisibility() == View.VISIBLE && fetchState != IS_FETCHING) {
                Handler handler = new Handler();
                handler.postDelayed(() -> progressBar.setVisibility(View.GONE), 2000);
            } else {
                progressBar.setVisibility(fetchState == IS_FETCHING ? View.VISIBLE : View.GONE);
            }
            if (fetchState == FETCH_ERROR) {
                int errorMsgId = !Utils.isOnline(requireContext()) ? R.string.error_no_internet : R.string.error_unknown;
                Toast.makeText(requireContext(), getString(R.string.error_fetching_canteens, getString(errorMsgId)), Toast.LENGTH_SHORT).show();
            }
        });
        showLatestUpdatesCard();
        return view;
    }

    void showLatestUpdatesCard() {
        if (mPackageInfo != null && mPreferenceService.getBooleanPreference(PreferenceService.SHOW_LATEST_UPDATES + mPackageInfo.versionName, true)) {
            mLatestUpdatesCard.setVisibility(View.VISIBLE);
            //mPreferenceService.removePreference("pref_show_tut_" + (mPackageInfo.versionCode - 1));
            //mPreferenceService.removePreference(PreferenceService.SHOW_LATEST_UPDATES + (mPackageInfo.versionCode - 1));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.latestUpdatesCloseButton) {
            mLatestUpdatesCard.setVisibility(View.GONE);
            if (mPackageInfo != null) {
                mPreferenceService.setBooleanPreference(PreferenceService.SHOW_LATEST_UPDATES + mPackageInfo.versionName, false);
            }
        }
    }

    @Override
    public void onFavoriteClick(Canteen canteen) {
        canteen.setAsFavorite(!canteen.isFavorite());
        mCanteenListViewModel.updateCanteen(canteen);
    }

    @Override
    public void onCanteenClick(Canteen canteen) {
        canteen.increasePriority();
        mCanteenListViewModel.updateCanteen(canteen);
        FragmentController.showMealWeekFragment(getParentFragmentManager(), canteen.getId());
    }
}
