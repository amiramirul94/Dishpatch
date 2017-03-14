package com.android.dishpatch.dishpatch.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.dishpatch.dishpatch.Controller.SharedPreferences.DishpatchPreferences;
import com.android.dishpatch.dishpatch.Model.Dispatch;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lenovo on 8/30/2016.
 */
public class SubmitOrderService extends IntentService {

    private static final String TAG = SubmitOrderService.class.getSimpleName();
    private static final String ORDER_EXTRA = "ORDERS_EXTRA";
    private static final String SUBMIT_ORDER_URL = "http://insvite.com/php/submit_order.php";
    private static final String ADD_DISPATCHER_QUEUE = "http://insvite.com/php/add_dispatcher_queue.php";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private List<Dispatch> mDispatcherIdList = new ArrayList<>();
    private int userId;
    private int orderId;
    private boolean isOnline;





    public SubmitOrderService()
    {
        super(TAG);
    }
    public SubmitOrderService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
    }

    public static Intent newIntent(Context context, JSONObject ordersObject)
    {
        Intent i = new Intent(context,SubmitOrderService.class);
        i.putExtra(ORDER_EXTRA,ordersObject.toString());

        return i;
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        String orders = intent.getStringExtra(ORDER_EXTRA);
        Log.v(TAG,orders);

        /*1.search dispatchers within the area
          2. get the dispatcher id
          3. add the dispatcher id to the json object
          4. submit the json object to the server
          5. notify the restaurant
          6.
        */
        userId = DishpatchPreferences.getUserId(this);
      submitOrders(orders);

    }

    private void getLocation() {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.i(TAG,"location:" + location);
                    mLocation = location;

                }


            });
        }catch(SecurityException e){

        }


    }

    private void submitOrders(String menuObject)
    {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("order_object",menuObject)
                .build();

        Request request = new Request.Builder().url(SUBMIT_ORDER_URL).post(requestBody).build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    String responseString = response.body().string();
                    Log.v(TAG,responseString);

                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        orderId = Integer.parseInt(jsonObject.getString("id"));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getDispatchers();
                }
            }
        });
    }

    private void getDispatchers()
    {
        DatabaseReference dispatchReference = FirebaseDatabase.getInstance().getReference("dispatchers");

        GeoFire geoFire = new GeoFire(dispatchReference);

        while (mLocation==null)
        {}

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLocation.getLatitude(),mLocation.getLongitude()),1.0);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                int id = Integer.parseInt(key);
                if(id!=userId)
                {

                    addDispatcher(id,location);


                    Log.v(TAG,id+"");
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });



    }

    private void addDispatcher(final int dispatcher, final GeoLocation location)

    {
        DatabaseReference statusReference = FirebaseDatabase.getInstance().getReference("dispatchers_status").child(dispatcher+"").child("status");



        //Add dispatcher if the dispatcher is online
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = (String) dataSnapshot.getValue();

                if(online.equals("online"))
                {
                    Dispatch dispatch = new Dispatch(dispatcher);


                    double distance = calculateDistance(location.latitude,location.longitude);

                    dispatch.setDistance(distance);

                    mDispatcherIdList.add(dispatch);
                    new AddDispatcherAsyncTask().execute();

                    Log.v(TAG,"added "+dispatch.getDispatcherId());



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        statusReference.addValueEventListener(eventListener);

    }

    private double calculateDistance(double latitude, double longitude)
    {
        float[] distance = new float[1];
        Location.distanceBetween(mLocation.getLatitude(),mLocation.getLongitude(),latitude,longitude,distance);

        Log.v(TAG,"Distance = "+distance[0]);
        return distance[0];
    }

    private void addDispatcher(Dispatch dispatch) throws JSONException
    {
        JSONObject dispatchObject = new JSONObject();
        dispatchObject.put("order_id",orderId+"");
        dispatchObject.put("dispatcher_id",dispatch.getDispatcherId()+"");
        dispatchObject.put("dispatcher_distance",dispatch.getDistance()+"");




        Log.v(TAG,dispatchObject.toString());







    }

    class AddDispatcherAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int i=0;

            JSONArray dispatcherArray = new JSONArray();

            for (Dispatch dispatch: mDispatcherIdList) {
                JSONObject dispatchObject = new JSONObject();

                try {
                    dispatchObject.put("order_id",orderId+"");
                    dispatchObject.put("dispatcher_id",dispatch.getDispatcherId()+"");
                    dispatchObject.put("dispatcher_distance",dispatch.getDistance()+"");
                    dispatcherArray.put(i,dispatchObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                i++;

            }


            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder().add("dispatch_object",dispatcherArray.toString()).build();

            Request request = new Request.Builder().post(requestBody).url(ADD_DISPATCHER_QUEUE).build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful())
                    {
                        Log.v(TAG,"Successfully added");
                    }
                }
            });




            Log.v(TAG,dispatcherArray.toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mDispatcherIdList.clear();
        }
    }





}
