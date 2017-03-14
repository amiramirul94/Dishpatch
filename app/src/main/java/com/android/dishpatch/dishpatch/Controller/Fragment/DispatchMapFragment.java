package com.android.dishpatch.dishpatch.Controller.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.dishpatch.dishpatch.Model.Dispatch;
import com.android.dishpatch.dishpatch.Model.Order;
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

/**
 * Created by Lenovo on 4/19/2016.
 */
public class DispatchMapFragment extends SupportMapFragment {

    private static final String BUNDLE_DISPATCH = "BUNDLE_DISPATCH";
    private static final String EXTRA_DISPATCH_RESULT = "com.andrid.dispatch.dispatch.DispatchFragment.Dispatch";

    public static Dispatch getDispatchResult(Intent result)
    {
        return result.getParcelableExtra(EXTRA_DISPATCH_RESULT);
    }

    public static DispatchMapFragment newInstance(Order order)
    {
        Bundle args = new Bundle();

        args.putParcelable(BUNDLE_DISPATCH,order);

        DispatchMapFragment fragment = new DispatchMapFragment();

        fragment.setArguments(args);
        return fragment;
    }


    private static final String TAG = "DispatchMapFragment";
    private static final int LOCATION_PERMISSION = 1;

    private GoogleApiClient mClient;
    private GoogleMap mGoogleMap;
    private Dispatch mDispatch;
    private Location mCurrentLocation;
    private Location mPickupLocation;
    private Location mDestinationLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        mDispatch = getArguments().getParcelable(BUNDLE_DISPATCH);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
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
                })
                .build();

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
            }
        });

    }

    private void findLocation()
    {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(5000);

        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, "Got a location fix " + location);
                mCurrentLocation = location;
                updateUI();

            }


        });
    }

    private void updateUI()
    {
        if(mGoogleMap==null)
        {
            return;
        }

        LatLng myLoc = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(myLoc)
                .build();

        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);

        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds,margin);
        mGoogleMap.animateCamera(update);
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

    @Override
    public void onStart() {
        super.onStart();

        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        mClient.disconnect();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Intent data = new Intent();
        data.putExtra(EXTRA_DISPATCH_RESULT,mDispatch);
        getActivity().setResult(Activity.RESULT_OK,data);

    }
}
