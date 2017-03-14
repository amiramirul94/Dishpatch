package com.android.dishpatch.dishpatch.Controller.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dishpatch.dishpatch.Model.DishpatchCentral;
import com.android.dishpatch.dishpatch.ui.Activity.SelectFoodActivity;
import com.android.dishpatch.dishpatch.Model.Restaurant;
import com.android.dishpatch.dishpatch.Model.RestaurantManager;
import com.android.dishpatch.dishpatch.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2/16/2016.
 */
public class RestaurantListFragment extends Fragment {

    private static final String TAG =  RestaurantListFragment.class.getSimpleName();
    private Location mLocation;

    private static final int LOCATION_PERMISSION = 1;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private RestaurantAdapter mAdapter;
    private List<Restaurant> mRestaurantList = new ArrayList<>();
    private List<Integer> keylist = new ArrayList<>();

    private GoogleApiClient mClient;

    public static RestaurantListFragment newInstance()
    {
        return new RestaurantListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);


                            } else if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                                getLocation();
                            }
                        } else {
                            getLocation();
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }).build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_restaurant_list,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.restaurant_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        updateUI();

        return v;

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode){
            case LOCATION_PERMISSION:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG,"Permission granted");

                }
                return;

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        new GetRestaurantsTask().execute();
    }

    private void getLocation(){
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.i(TAG,"location:" + location);
                    mLocation = location;
                    queryNearestRestaurant();
                }


            });
        }catch(SecurityException e){

            Toast.makeText(getActivity(), "Location permission missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI()
    {
        List<Restaurant> restaurants = DishpatchCentral.get(getActivity()).getRestaurantList();

        mAdapter = new RestaurantAdapter(restaurants);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void queryNearestRestaurant()
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("restaurants");
        GeoFire geofire = new GeoFire(databaseReference);
        GeoQuery geoQuery = geofire.queryAtLocation(new GeoLocation(mLocation.getLatitude(),mLocation.getLongitude()),1.0);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.v(TAG,key);
                keylist.add(Integer.parseInt(key));
                new GetRestaurantsTask().execute();

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

    private class GetRestaurantsTask extends AsyncTask<Void,Void,List<Restaurant>>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Restaurant> doInBackground(Void... params) {

            DishpatchCentral.get(getActivity()).createRestaurantList(keylist);
            return null;
        }

        @Override
        protected void onPostExecute(List<Restaurant> restaurants) {
            super.onPostExecute(restaurants);
            updateUI();
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private class RestaurantAdapter extends  RecyclerView.Adapter<RestaurantHolder>
    {
        private List<Restaurant> mRestaurants;

        public RestaurantAdapter(List<Restaurant> restaurants)
        {
            mRestaurants = restaurants;
        }


        @Override
        public RestaurantHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.restaurant_list_layout,parent,false);

            return new RestaurantHolder(v);
        }

        @Override
        public void onBindViewHolder(RestaurantHolder holder, int position) {
            Restaurant restaurant = mRestaurants.get(position);
            holder.bindRestaurant(restaurant);
        }

        @Override
        public int getItemCount() {
            return mRestaurants.size();
        }
    }

    private class RestaurantHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Restaurant mRestaurant;
        private TextView mRestaurantNameTextView;
        private TextView mRestaurantDistanceTextView;
        private TextView mRestaurantRatingTextView;
        private ImageView mRestaurantLogoImageView;

        public RestaurantHolder(View itemView)
        {
            super(itemView);

            mRestaurantNameTextView = (TextView) itemView.findViewById(R.id.restaurant_name_text_view);
            mRestaurantDistanceTextView = (TextView) itemView.findViewById(R.id.restaurant_distance_text_view);
            mRestaurantRatingTextView = (TextView) itemView.findViewById(R.id.restaurant_rating_text_view);
            mRestaurantLogoImageView = (ImageView) itemView.findViewById(R.id.restaurant_logo_image_view);

            itemView.setOnClickListener(this);

        }

        public void bindRestaurant(Restaurant restaurant)
        {
            mRestaurant = restaurant;
            mRestaurantNameTextView.setText(mRestaurant.getName());


            mRestaurantDistanceTextView.setText(mRestaurant.getDistance()+"m");
            mRestaurantRatingTextView.setText(String.format("%.1f", mRestaurant.getRating()));
            mRestaurantLogoImageView.setMinimumHeight(mRestaurantLogoImageView.getWidth());
            Picasso.with(getActivity()).load(restaurant.getPhotoUri()).fit().centerCrop().into(mRestaurantLogoImageView);



        }

        @Override
        public void onClick(View v) {
          Intent i = SelectFoodActivity.newIntent(getActivity(),mRestaurant);
            startActivity(i);
        }
    }
}
