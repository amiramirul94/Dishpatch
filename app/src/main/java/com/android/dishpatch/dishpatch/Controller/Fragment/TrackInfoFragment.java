package com.android.dishpatch.dishpatch.Controller.Fragment;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.dishpatch.dishpatch.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


public class TrackInfoFragment extends SupportMapFragment {

    private static final String TAG = "TrackInfoFragment";

    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private Location mCurrentLocation;
    private static final int LOCATION_PERMISSION = 1;

    public static TrackInfoFragment newInstance()
    {
        return new TrackInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        //findLocation();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);


                            } else if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                                findLocation();
                            }
                        } else {
                            findLocation();
                        }


                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }).build();

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;


            }
        });

    }




    @Override
    public void onStart()
    {
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mClient.disconnect();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode){
            case LOCATION_PERMISSION:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG,"Permission granted");
                    findLocation();
                }
                return;

        }

    }

    private void findLocation()
    {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(5000);

        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.i(TAG, "Got a location fix " + location);
                    mCurrentLocation = location;
                    updateUI();

                }

            });
        }catch (SecurityException e){

        }


    }

    private void updateUI()
    {

        if(mMap==null||mCurrentLocation==null)
        {
          return;
        }


        LatLng myPoint = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());

        LatLng myHouse = new LatLng(1.532881, 103.577296);
        MarkerOptions myLocationMarker = new MarkerOptions().position(myPoint);
        MarkerOptions houseMarker = new MarkerOptions().position(myHouse);

        mMap.clear();
        mMap.addMarker(myLocationMarker);
        mMap.addMarker(houseMarker);

       LatLngBounds bounds = new LatLngBounds.Builder()
                .include(myPoint)
                .include(myHouse)
                .build();
       int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
       mMap.animateCamera(update);



    }
}
