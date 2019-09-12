package com.pasta.mensadd.ui.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

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

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.pasta.mensadd.R;
import com.pasta.mensadd.ui.FragmentController;
import com.pasta.mensadd.database.entity.Canteen;
import com.pasta.mensadd.DataHolder;


public class CanteenMapFragment extends Fragment {
    private MapView mMapView;
    private MapboxMap mMapboxMap;
    private LocationServices mLocationServices;
    private static final int PERMISSIONS_LOCATION = 0;

    private TextView mCanteenName;
    private TextView mCanteenAddress;
    private TextView mCanteenHours;
    private CardView mInfoCard;
    private Location mLastLocation;

    private String mCurrentCanteen = "";

    private int mMapZoom = 11;
    private LatLng mMapCenter = new LatLng(51.04868491509959, 13.759391673955406);


    public CanteenMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        if (getContext() != null && mLocationServices == null)
            mLocationServices = LocationServices.getLocationServices(getContext());
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
        mInfoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentController.showMealWeekFragment(getFragmentManager());
            }
        });
        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                mMapboxMap = mapboxMap;
                mMapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(mMapCenter)
                        .zoom(mMapZoom)
                        .build());
                drawCanteensOnMap(mapboxMap);
                mMapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
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
                    }
                });
                if (mLastLocation != null)
                    mMapboxMap.setMyLocationEnabled(true);
            }

        });
        return view;
    }

    public void drawCanteensOnMap(MapboxMap map) {
        for (Canteen c : DataHolder.getInstance().getCanteenList()) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(c.getPosLat(), c.getPosLong())).title(c.getId()));
        }
        map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Canteen c = DataHolder.getInstance().getCanteen(marker.getTitle());
                mCurrentCanteen = marker.getTitle();
                mCanteenName.setText(c.getName());
                mCanteenAddress.setText(c.getAddress());
                mCanteenHours.setText(c.getHours());
                if (mInfoCard.getVisibility() == View.GONE) {
                    mInfoCard.setVisibility(View.VISIBLE);
                    ScaleAnimation showAnim = new ScaleAnimation(0, 1, 0, 1, Animation
                            .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    showAnim.setDuration(150);
                    mInfoCard.startAnimation(showAnim);
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
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
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
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
                    toggleGps();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @UiThread
    public void toggleGps() {
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
        }

    }

    private void enableLocation() {
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
        }
    }

    public void moveCamera(Location location){
        final CameraPosition newPosition = new CameraPosition.Builder()
                .target(new LatLng(location))
                .build();
        mMapboxMap.animateCamera(new CameraUpdate() {
            @Override
            public CameraPosition getCameraPosition(@NonNull MapboxMap mapboxMap) {
                return newPosition;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_LOCATION: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableLocation();
                }
            }
        }
    }

}
