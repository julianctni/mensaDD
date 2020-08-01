package com.pasta.mensadd.ui.fragments;


import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonPrimitive;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.pasta.mensadd.R;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.ui.FragmentController;
import com.pasta.mensadd.ui.viewmodel.CanteensViewModel;

import java.util.ArrayList;
import java.util.List;


public class CanteenMapFragment extends Fragment {
    private MapView mMapView;
    private MapboxMap mMap;

    private TextView mCanteenName;
    private TextView mCanteenAddress;
    private TextView mCanteenHours;
    private LinearLayout mInfoCard;

    private PermissionsManager mPermissionManager;
    private LocationComponent mLocationComponent;

    private CanteensViewModel mCanteensViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mCanteensViewModel = new ViewModelProvider(getActivity()).get(CanteensViewModel.class);

        setHasOptionsMenu(true);
        mMapView = view.findViewById(R.id.mapview);
        mCanteenAddress = view.findViewById(R.id.mapInfoCardCanteenAddress);
        mCanteenHours = view.findViewById(R.id.mapInfoCardCanteenHours);
        mCanteenName = view.findViewById(R.id.mapInfoCardCanteenName);
        mInfoCard = view.findViewById(R.id.mapInfoCard);
        MaterialButton buttonClose = view.findViewById(R.id.mapViewCloseButton);
        buttonClose.setOnClickListener(button -> mInfoCard.setVisibility(View.GONE));
        MaterialButton buttonMeals = view.findViewById(R.id.mapViewToMealsButton);
        buttonMeals.setOnClickListener(button -> FragmentController.showMealWeekFragment(getFragmentManager()));
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(mapboxMap -> {
            mMap = mapboxMap;
            mCanteensViewModel.getAllCanteens().observe(requireActivity(), canteens -> {
                initMap(mapboxMap, canteens);
            });
        });
        return view;
    }


    private void initMap(MapboxMap map, List<Canteen> canteens) {
        String styleUrl = getResources().getString(R.string.mapbox_style_url);
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            styleUrl = getResources().getString(R.string.mapbox_style_url_dark);
        }
        map.setStyle(styleUrl, style -> {
            if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
                enableLocationComponent(style);
            }
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.map_marker);
            map.getStyle().addImage("mensa-marker", bm);
            SymbolManager symbolManager = new SymbolManager(mMapView, map, style);
            List<SymbolOptions> canteenSymbols = new ArrayList<>();
            symbolManager.setIconAllowOverlap(true);
            symbolManager.addClickListener(symbol -> {
                mCanteensViewModel.getCanteenById(symbol.getData().getAsString()).observe(getActivity(), canteen -> {
                    mCanteenName.setText(canteen.getName());
                    mCanteenAddress.setText(canteen.getAddress());
                    mCanteenHours.setText(canteen.getHours());
                    if (mInfoCard.getVisibility() == View.GONE) {
                        mInfoCard.setVisibility(View.VISIBLE);
                        mCanteensViewModel.setSelectedCanteen(canteen);
                    }
                });
            });
            LatLngBounds.Builder bounds = new LatLngBounds.Builder();
            for (Canteen c : canteens) {

                JsonPrimitive jsonId = new JsonPrimitive(c.getId());
                LatLng position = new LatLng(c.getPosLat(), c.getPosLong());
                bounds.include(position);
                canteenSymbols.add(new SymbolOptions().withLatLng(position).withIconImage("mensa-marker").withData(jsonId));
            }
            mMap.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50), 1500);
            symbolManager.create(canteenSymbols);
        });
    }

    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
            LocationComponentOptions locationComponentOptions = LocationComponentOptions.builder(getContext()).build();

            LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                    .builder(getContext(), loadedMapStyle)
                    .locationComponentOptions(locationComponentOptions)
                    .useDefaultLocationEngine(true)
                    .build();

            mLocationComponent = mMap.getLocationComponent();
            mLocationComponent.activateLocationComponent(locationComponentActivationOptions);
            mLocationComponent.setLocationComponentEnabled(true);
            mLocationComponent.setCameraMode(CameraMode.TRACKING);
            mLocationComponent.setRenderMode(RenderMode.COMPASS);

        } else {
            mPermissionManager = new PermissionsManager(new PermissionsListener() {
                @Override
                public void onExplanationNeeded(List<String> permissionsToExplain) {
                    Toast.makeText(getContext(), getString(R.string.toast_enable_location), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionResult(boolean granted) {
                    if (granted) {
                        mMap.getStyle(style -> Log.i("i", "i"));
                    } else {
                        Toast.makeText(getContext(), "Location services not allowed", Toast.LENGTH_LONG).show();
                    }
                }
            });
            mPermissionManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        if (mLocationComponent != null)
            mLocationComponent.onStop();
    }


    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        // mMapZoom = (int) mMap.getCameraPosition().zoom;
        // mMapCenter = mMap.getCameraPosition().target;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
        if (mLocationComponent != null)
            mLocationComponent.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fragment_map_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_location:
                if (mMap != null) {
                    toggleGps(mMap.getStyle());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @UiThread
    public void toggleGps(Style style) {
        enableLocationComponent(style);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
