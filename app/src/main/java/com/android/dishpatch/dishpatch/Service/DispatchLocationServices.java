package com.android.dishpatch.dishpatch.Service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.dishpatch.dishpatch.Controller.SharedPreferences.DishpatchPreferences;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Lenovo on 9/1/2016.
 */
public class DispatchLocationServices extends IntentService {
    private static final String TAG = DispatchLocationServices.class.getSimpleName();
    private static final Object LOCATION_PERMISSION = 1 ;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private int mID;

    public static Intent newIntent(Context context)
    {
        Intent i = new Intent(context,DispatchLocationServices.class);
        return i;
    }

    public DispatchLocationServices()
    {
        super(TAG);
    }

    public DispatchLocationServices(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getLocation();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }).build();
        mGoogleApiClient.connect();

        mID = DishpatchPreferences.getUserId(this);

        setToOnline();

    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }



    private void getLocation() {
        LocationRequest request = new LocationRequest().setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.i(TAG,"location:" + location);
                    mLocation = location;

                    updateFirebase(mLocation);
                }


            });
        }catch(SecurityException e){

        }


    }

    private void updateFirebase(Location location)
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("dispatchers");
        GeoFire geoFire = new GeoFire(reference);

        geoFire.setLocation(mID+"",new GeoLocation(location.getLatitude(),location.getLongitude()));
    }

    private void setToOnline()
    {
        DatabaseReference dispatchReference = FirebaseDatabase.getInstance().getReference("dispatchers_status");
        dispatchReference.child(mID+"").child("status").setValue("online");
    }

    public static void setServiceAlarm(Context context)
    {
        Intent i = DispatchLocationServices.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context,0,i,0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),1000*60,pi);



    }
}
