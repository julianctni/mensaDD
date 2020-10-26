package com.pasta.mensadd.features.canteenmap;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
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
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.pasta.mensadd.PreferenceService;
import com.pasta.mensadd.R;
import com.pasta.mensadd.AppDatabase;
import com.pasta.mensadd.domain.ApiService;
import com.pasta.mensadd.domain.canteen.Canteen;
import com.pasta.mensadd.domain.canteen.CanteenRepository;
import com.pasta.mensadd.network.ServiceGenerator;
import com.pasta.mensadd.FragmentController;
import com.pasta.mensadd.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;


public class CanteenMapFragment extends Fragment implements PermissionsListener, MapboxMap.OnMapClickListener {
    private MapView mMapView;
    private MapboxMap mMap;

    private TextView mCanteenName;
    private TextView mCanteenAddress;
    private TextView mCanteenHours;
    private LinearLayout mInfoCard;

    private ValueAnimator markerAnimator;
    private boolean markerSelected = false;

    private LocationComponent mLocationComponent;

    private CanteenMapViewModel mCanteenMapViewModel;

    private static final String SELECTED_CANTEEN_MARKER = "s-marker";
    private static final String SELECTED_CANTEEN_MARKER_LAYER = "s-marker-layer";
    private static final String CANTEEN_MARKER_LAYER = "marker-layer";
    private static final String CANTEEN_MARKER_IMAGE = "marker-image";
    private static final String CANTEEN_MARKER_SOURCE = "marker-source";
    private static final String CANTEEN_ID = "canteen-id";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        PreferenceService preferenceService = new PreferenceService(requireContext());
        CanteenRepository canteenRepository = new CanteenRepository(
                AppDatabase.getInstance(requireContext()),
                preferenceService,
                ServiceGenerator.createService(ApiService.class)
        );
        CanteenMapViewModelFactory canteenMapViewModelFactory = new CanteenMapViewModelFactory(canteenRepository);
        mCanteenMapViewModel = new ViewModelProvider(this, canteenMapViewModelFactory).get(CanteenMapViewModel.class);

        setHasOptionsMenu(true);
        mMapView = view.findViewById(R.id.mapview);
        mCanteenAddress = view.findViewById(R.id.mapInfoCardCanteenAddress);
        mCanteenHours = view.findViewById(R.id.mapInfoCardCanteenHours);
        mCanteenName = view.findViewById(R.id.mapInfoCardCanteenName);
        mInfoCard = view.findViewById(R.id.mapInfoCard);
        MaterialButton buttonClose = view.findViewById(R.id.mapViewCloseButton);
        buttonClose.setOnClickListener(button -> {
            mInfoCard.setVisibility(View.GONE);
            deselectMarker((SymbolLayer) mMap.getStyle().getLayer(SELECTED_CANTEEN_MARKER_LAYER));
        });
        MaterialButton buttonMeals = view.findViewById(R.id.mapViewToMealsButton);
        buttonMeals.setOnClickListener(button -> FragmentController.showMealWeekFragment(getParentFragmentManager(), mCanteenMapViewModel.getSelectedCanteenId()));
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(mapboxMap -> {
            mMap = mapboxMap;
            mCanteenMapViewModel.getCanteens().observe(requireActivity(), canteens ->
                    initMap(mapboxMap, canteens));
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
            List<Feature> canteenCoordinates = new ArrayList<>();
            LatLngBounds.Builder bounds = new LatLngBounds.Builder();

            for (Canteen c : canteens) {
                LatLng position = new LatLng(c.getPosLat(), c.getPosLong());
                bounds.include(position);
                Feature feature = Feature.fromGeometry(
                        Point.fromLngLat(c.getPosLong(), c.getPosLat()));
                feature.addStringProperty(CANTEEN_ID, c.getId());
                canteenCoordinates.add(feature);
            }

            style.addSource(new GeoJsonSource(CANTEEN_MARKER_SOURCE,
                    FeatureCollection.fromFeatures(canteenCoordinates)));

            style.addImage(CANTEEN_MARKER_IMAGE, BitmapFactory.decodeResource(
                    this.getResources(), R.drawable.map_marker));

            style.addLayer(new SymbolLayer(CANTEEN_MARKER_LAYER, CANTEEN_MARKER_SOURCE)
                    .withProperties(PropertyFactory.iconImage(CANTEEN_MARKER_IMAGE),
                            iconAllowOverlap(true),
                            iconOffset(new Float[]{0f, -9f})));

            style.addSource(new GeoJsonSource(SELECTED_CANTEEN_MARKER));

            style.addLayer(new SymbolLayer(SELECTED_CANTEEN_MARKER_LAYER, SELECTED_CANTEEN_MARKER)
                    .withProperties(PropertyFactory.iconImage(CANTEEN_MARKER_IMAGE),
                            iconAllowOverlap(true),
                            iconOffset(new Float[]{0f, -9f})));

            mMap.addOnMapClickListener(this);
            mMap.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50), 1500);

            mCanteenMapViewModel.getSelectedCanteen().observe(getViewLifecycleOwner(), canteen -> {
                if (canteen == null) {
                    return;
                }

                GeoJsonSource source = style.getSourceAs(SELECTED_CANTEEN_MARKER);
                if (source != null) {
                    Feature feature = Feature.fromGeometry(
                            Point.fromLngLat(canteen.getPosLong(), canteen.getPosLat()));
                    feature.addStringProperty(CANTEEN_ID, canteen.getId());
                    source.setGeoJson(FeatureCollection.fromFeatures(
                            new Feature[]{feature}));
                }
                selectMarker((SymbolLayer) mMap.getStyle().getLayer(SELECTED_CANTEEN_MARKER_LAYER));
                CameraPosition cp = new CameraPosition.Builder().zoom(12).padding(0,0,0,400).target(new LatLng(canteen.getPosLat(), canteen.getPosLong())).build();
                mMap.easeCamera(CameraUpdateFactory.newCameraPosition(cp));
                mCanteenName.setText(canteen.getName());
                mCanteenAddress.setText(canteen.getAddress());
                mCanteenHours.setText(canteen.getHours());
                if (mInfoCard.getVisibility() == View.GONE) {
                    mInfoCard.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        Style style = mMap.getStyle();
        if (style != null) {
            final SymbolLayer selectedMarkerSymbolLayer =
                    (SymbolLayer) style.getLayer(SELECTED_CANTEEN_MARKER_LAYER);

            final PointF pixel = mMap.getProjection().toScreenLocation(point);
            List<Feature> features = mMap.queryRenderedFeatures(pixel, CANTEEN_MARKER_LAYER);
            List<Feature> selectedFeature = mMap.queryRenderedFeatures(
                    pixel, SELECTED_CANTEEN_MARKER_LAYER);

            if (selectedFeature.size() > 0 && markerSelected) {
                return false;
            }

            if (features.isEmpty()) {
                if (markerSelected) {
                    deselectMarker(selectedMarkerSymbolLayer);
                    mInfoCard.setVisibility(View.GONE);
                }
                return false;
            }

            GeoJsonSource source = style.getSourceAs(SELECTED_CANTEEN_MARKER);
            if (source != null) {
                source.setGeoJson(FeatureCollection.fromFeatures(
                        new Feature[]{Feature.fromGeometry(features.get(0).geometry())}));
            }

            if (markerSelected) {
                deselectMarker(selectedMarkerSymbolLayer);
            }

            if (features.size() > 0) {
                String canteenId = features.get(0).getStringProperty(CANTEEN_ID);
                mCanteenMapViewModel.setSelectedCanteenId(Objects.requireNonNull(canteenId));
            }
        }
        return true;
    }

    private void selectMarker(final SymbolLayer iconLayer) {
        markerAnimator = new ValueAnimator();
        markerAnimator.setObjectValues(1f, 1.5f);
        markerAnimator.setDuration(200);
        markerAnimator.addUpdateListener(animator -> iconLayer.setProperties(
                PropertyFactory.iconSize((float) animator.getAnimatedValue())
        ));
        markerAnimator.start();
        markerSelected = true;
    }

    private void deselectMarker(final SymbolLayer iconLayer) {
        markerAnimator.setObjectValues(1.5f, 1f);
        markerAnimator.setDuration(200);
        markerAnimator.addUpdateListener(animator -> iconLayer.setProperties(
                PropertyFactory.iconSize((float) animator.getAnimatedValue())
        ));
        markerAnimator.start();
        markerSelected = false;
        mCanteenMapViewModel.setSelectedCanteenId(null);
    }

    @SuppressLint("MissingPermission")
    public void createAndStartLocationComponent() {
        LocationComponentOptions locationComponentOptions = LocationComponentOptions.builder(Objects.requireNonNull(getContext())).build();

        LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                .builder(getContext(), Objects.requireNonNull(mMap.getStyle()))
                .locationComponentOptions(locationComponentOptions)
                .useDefaultLocationEngine(true)
                .build();

        mLocationComponent = mMap.getLocationComponent();
        mLocationComponent.activateLocationComponent(locationComponentActivationOptions);
        mLocationComponent.setLocationComponentEnabled(true);
        mLocationComponent.setCameraMode(CameraMode.TRACKING);
        mLocationComponent.setRenderMode(RenderMode.COMPASS);
    }

    public void requestLocationPermission() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.requestLocationPermission(this);
    }

    private void requestLocationComponent() {
        if (!PermissionsManager.areLocationPermissionsGranted(Objects.requireNonNull(getContext()))) {
            requestLocationPermission();
        } else {
            createAndStartLocationComponent();
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
        if (mMap != null) {
            mMap.removeOnMapClickListener(this);
        }
        if (markerAnimator != null) {
            markerAnimator.cancel();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fragment_map_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_location) {
            toggleGps();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isLocationEnabled() {
        LocationManager lm = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @UiThread
    public void toggleGps() {
        if (!isLocationEnabled()) {
            Snackbar snackbar = Snackbar
                    .make(mMapView, getString(R.string.toast_enable_location), Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.nav_settings), v -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            });
            snackbar.show();
        } else {
            requestLocationComponent();
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            createAndStartLocationComponent();
        } else {
            showMissingLocationPermissionSnackbar();
        }
    }

    private void showMissingLocationPermissionSnackbar() {
        Snackbar snackbar = Snackbar
                .make(mMapView, R.string.location_permission_missing, Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.location_permission_missing_check), v -> requestLocationPermission());
        snackbar.show();
    }


}
