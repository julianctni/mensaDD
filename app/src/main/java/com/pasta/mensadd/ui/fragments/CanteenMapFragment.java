package com.pasta.mensadd.ui.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.JsonPrimitive;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
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
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.pasta.mensadd.DataHolder;
import com.pasta.mensadd.R;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.ui.FragmentController;
import com.pasta.mensadd.ui.viewmodel.CanteensViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;


public class CanteenMapFragment extends Fragment {
    private MapView mMapView;
    private MapboxMap mMapboxMap;
    //private LocationServices mLocationServices;
    private static final int PERMISSIONS_LOCATION = 0;

    private TextView mCanteenName;
    private TextView mCanteenAddress;
    private TextView mCanteenHours;
    private CardView mInfoCard;
    private Location mLastLocation;

    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;

    private String mCurrentCanteen = "";

    private CanteensViewModel mCanteensViewModel;
    private int mMapZoom = 11;
    private LatLng mMapCenter = new LatLng(51.04868491509959, 13.759391673955406);

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
        if (!mCurrentCanteen.isEmpty()) {
            Canteen c = DataHolder.getInstance().getCanteen(mCurrentCanteen);
            mCanteenName.setText(c.getName());
            mCanteenAddress.setText(c.getAddress());
            mCanteenHours.setText(c.getHours());
            mInfoCard.setVisibility(View.VISIBLE);
        }
        mInfoCard.setOnClickListener(view1 -> FragmentController.showMealWeekFragment(getFragmentManager()));
        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;
            mMapboxMap.setCameraPosition(new CameraPosition.Builder()
                    .target(mMapCenter)
                    .zoom(mMapZoom)
                    .build());
            mCanteensViewModel.getAllCanteens().observe(CanteenMapFragment.this, canteens -> {

                initMap(mapboxMap, canteens);
            });
        });
        return view;
    }

    private MapboxMap.OnMapClickListener onMapClickListener = point -> {
        if (mInfoCard.getVisibility() == View.VISIBLE) {
            ScaleAnimation hideAnim = new ScaleAnimation(1, 0, 1, 0, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            hideAnim.setDuration(150);
            mInfoCard.startAnimation(hideAnim);
            hideAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mInfoCard.setVisibility(View.GONE);
                    mCurrentCanteen = "";
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        return false;
    };


    private void initMap(MapboxMap map, List<Canteen> canteens) {
        map.setStyle(Style.LIGHT, style -> {
            if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
                enableLocationComponent(style);
            }
            map.addOnMapClickListener(onMapClickListener);
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.garlic);
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
                        ScaleAnimation showAnim = new ScaleAnimation(0, 1, 0, 1, Animation
                                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        showAnim.setDuration(150);
                        mInfoCard.startAnimation(showAnim);
                    }
                });
            });
            for (Canteen c : canteens) {
                JsonPrimitive jsonId = new JsonPrimitive(c.getId());
                canteenSymbols.add(new SymbolOptions().withLatLng(new LatLng(c.getPosLat(), c.getPosLong())).withIconImage("mensa-marker").withData(jsonId));
            }
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

            locationComponent = mMapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(locationComponentActivationOptions);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

        } else {
            permissionsManager = new PermissionsManager(new PermissionsListener() {
                @Override
                public void onExplanationNeeded(List<String> permissionsToExplain) {
                    Toast.makeText(getContext(), "location not enabled", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onPermissionResult(boolean granted) {
                    if (granted) {
                        mMapboxMap.getStyle(style -> Log.i("i", "i"));
                    } else {
                        Toast.makeText(getContext(), "Location services not allowed", Toast.LENGTH_LONG).show();
                    }
                }
            });
            permissionsManager.requestLocationPermissions(getActivity());
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
        locationComponent.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        mMapZoom = (int) mMapboxMap.getCameraPosition().zoom;
        mMapCenter = mMapboxMap.getCameraPosition().target;
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
        locationComponent.onDestroy();
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
                if (mMapboxMap != null) {
                    toggleGps(mMapboxMap.getStyle());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @UiThread
    public void toggleGps(Style style) {
        enableLocationComponent(style);
        /*
        LocationManager lm = (LocationManager) getContext().getSystemService( Context.LOCATION_SERVICE );
        // Check if user has granted location permission
        if (!mLocationServices.areLocationPermissionsGranted() && getActivity() != null) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
        } else if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            enableLocation();
        } else {
            Toast.makeText(getContext(), getString(R.string.toast_enable_location), Toast.LENGTH_SHORT).show();
        }*/

    }

    private void enableLocation() {
        /*
        if (mLastLocation != null) {
            moveCamera(mLastLocation);
        } else {
            mLocationServices.addLocationListener(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        moveCamera(location);
                        mLastLocation = location;
                    }
                }
            });

            mMapboxMap.setMyLocationEnabled(true);
        }*/
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
