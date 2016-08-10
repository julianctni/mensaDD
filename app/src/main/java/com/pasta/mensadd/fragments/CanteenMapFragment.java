package com.pasta.mensadd.fragments;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
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

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.pasta.mensadd.MainActivity;
import com.pasta.mensadd.R;
import com.pasta.mensadd.controller.FragmentController;
import com.pasta.mensadd.model.Canteen;
import com.pasta.mensadd.model.DataHolder;


public class CanteenMapFragment extends Fragment {
    private MapView mapView;
    private MapboxMap map;
    private LocationServices locationServices;
    private static final int PERMISSIONS_LOCATION = 0;

    private TextView mCanteenName;
    private TextView mCanteenAddress;
    private TextView mCanteenHours;
    private CardView mInfoCard;

    private String mCurrentCanteen = "";

    private int mMapZoom = 12;
    private LatLng mMapCenter = new LatLng(51.053130, 13.744334);

    public CanteenMapFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        locationServices = LocationServices.getLocationServices(getActivity());
        setHasOptionsMenu(true);
        mapView = (MapView) view.findViewById(R.id.mapview);
        mCanteenAddress = (TextView) view.findViewById(R.id.mapInfoCardCanteenAddress);
        mCanteenHours = (TextView) view.findViewById(R.id.mapInfoCardCanteenHours);
        mCanteenName = (TextView) view.findViewById(R.id.mapInfoCardCanteenName);
        mInfoCard = (CardView) view.findViewById(R.id.mapInfoCard);
        if (!mCurrentCanteen.isEmpty()) {
            Canteen c = DataHolder.getInstance().getMensa(mCurrentCanteen);
            mCanteenName.setText(c.getName());
            mCanteenAddress.setText(c.getAddress());
            mCanteenHours.setText(c.getHours());
            mInfoCard.setVisibility(View.VISIBLE);
        }
        mInfoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentController.showMealWeekFragment(getActivity().getSupportFragmentManager(),mCurrentCanteen);
            }
        });
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                map = mapboxMap;
                map.setCameraPosition(new CameraPosition.Builder()
                        .target(mMapCenter)
                        .zoom(mMapZoom)
                        .build());
                drawCanteensOnMap(mapboxMap);
                map.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        ScaleAnimation hideAnim = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        hideAnim.setDuration(150);
                        mInfoCard.startAnimation(hideAnim);
                        hideAnim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mInfoCard.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                });
            }
        });
        MainActivity.updateNavDrawer(R.id.nav_map);
        return view;
    }

    public void drawCanteensOnMap(MapboxMap map){
        for (Canteen c : DataHolder.getInstance().getCanteenList()) {
            map.addMarker(new MarkerOptions()
                    .position(c.getPosition()).title(c.getCode()));
        }
        map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Canteen c = DataHolder.getInstance().getMensa(marker.getTitle());
                mCurrentCanteen = marker.getTitle();
                mCanteenName.setText(c.getName());
                mCanteenAddress.setText(c.getAddress());
                mCanteenHours.setText(c.getHours());
                if (mInfoCard.getVisibility() == View.GONE) {
                    mInfoCard.setVisibility(View.VISIBLE);
                    ScaleAnimation showAnim = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
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
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        mMapZoom = (int)map.getCameraPosition().zoom;
        mMapCenter = map.getCameraPosition().target;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        outState.putString("title", "Some Text");
        Log.i("MAPFRAGMENT", "SAVING INSTANCE");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
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
                if (map != null) {
                    toggleGps();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @UiThread
    public void toggleGps() {
        // Check if user has granted location permission
        if (!locationServices.areLocationPermissionsGranted()) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
        } else {
            enableLocation(true);
        }

    }

    private void enableLocation(boolean enabled) {
        if (enabled) {
            locationServices.addLocationListener(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        // Move the map camera to where the user location is
                        map.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(location))
                                .zoom(13)
                                .build());
                    }
                }
            });
        }
        map.setMyLocationEnabled(enabled);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_LOCATION: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableLocation(true);
                }
            }
        }
    }

}
