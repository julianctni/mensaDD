package com.pasta.mensadd.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.pasta.mensadd.R;
import com.pasta.mensadd.controller.FragmentController;
import com.pasta.mensadd.model.Canteen;
import com.pasta.mensadd.model.DataHolder;


public class CanteenMapFragment extends Fragment {
    private MapView mMapView;
    private MapboxMap mMapboxMap;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private static final int PERMISSION_REQUEST_LOCATION = 42;

    private TextView mCanteenName;
    private TextView mCanteenAddress;
    private TextView mCanteenHours;
    private CardView mInfoCard;

    private String mCurrentCanteen = "";

    private int mMapZoom = 12;
    private LatLng mMapCenter = new LatLng(51.053130, 13.744334);

    private MarkerOptions mLocationMarker;

    public CanteenMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Mapbox.getInstance(getActivity().getApplicationContext(), getString(R.string.mapbox_access_token));
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        mLocationMarker = new MarkerOptions().setTitle("Location");
        Bitmap locationIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_pin_circle_black_24dp);
        mLocationMarker.setIcon(IconFactory.getInstance(getContext()).fromBitmap(locationIcon));
        // Define a listener that responds to location updates
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Toast.makeText(getContext(), location.toString(), Toast.LENGTH_LONG).show();
                mMapboxMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(location))
                        .zoom(13)
                        .build());
                if (mLocationMarker.getPosition() != null)
                    mMapboxMap.removeMarker(mLocationMarker.getMarker());
                mLocationMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                mMapboxMap.addMarker(mLocationMarker);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };


        setHasOptionsMenu(true);
        mMapView = view.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);

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
                FragmentController.showMealWeekFragment(getFragmentManager(), mCurrentCanteen);
            }
        });


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
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                });
            }
        });
        return view;
    }

    public void drawCanteensOnMap(MapboxMap map) {
        for (Canteen c : DataHolder.getInstance().getCanteenList()) {
            map.addMarker(new MarkerOptions()
                    .position(c.getPosition()).title(c.getCode()));
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fragment_map_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_location:
                toggleGps();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @UiThread
    public void toggleGps() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationListener, null);
            mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationListener, null);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toggleGps();
                } else {
                    Toast.makeText(getContext(), "No permission, no party!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStart() {
        super.onStart();
        mMapView.onStart();
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
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
