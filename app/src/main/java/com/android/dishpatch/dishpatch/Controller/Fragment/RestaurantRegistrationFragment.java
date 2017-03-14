package com.android.dishpatch.dishpatch.Controller.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.dishpatch.dishpatch.Controller.Fragment.Dialog.LocationConfirmationDialog;
import com.android.dishpatch.dishpatch.R;
import com.android.dishpatch.dishpatch.Util;
import com.android.dishpatch.dishpatch.ui.Activity.RestaurantLoginActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lenovo on 6/26/2016.
 */
public class RestaurantRegistrationFragment extends Fragment{

    private GoogleApiClient mClient;
    private Validator mValidator;


    private Location mLocation;

    private static final int LOCATION_PERMISSION = 1;
    private static final String LOCATION_CONFIRMATION = "LOCATION_CONFIRMATION";
    private static final String TAG = "RegistrationFragment";
    private String REGISTER_RESTAURANT_URL = "http://insvite.com/php/register_restaurant.php";
    private String ADD_PROFILE_IMAGE_RESTAURANT_URL = "http://insvite.com/php/add_profile_picture_restaurant.php";

    private static final int CHOOSE_PHOTO = 0;




    @NotEmpty
    EditText mRestaurantNameEditText;

    @Email
    EditText mRestaurantEmailEditText;

    @Password(min = 6,scheme= Password.Scheme.ANY)
    EditText mRestaurantPasswordEditText;
    Button mRestaurantLocationButton;
    Button mRestaurantRegisterButton;

    private ImageView mRestaurantProfileImageView;
    private ImageButton mAddImageButton;
    private Uri mProfileImageUri;


    String id="";





    public static RestaurantRegistrationFragment newInstance()
    {
        return new RestaurantRegistrationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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

                            }
                        } else {

                        }


                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }


                }).build();


        mValidator = new Validator(this);

        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                Log.v(TAG,"Validated");
                registerRestaurant();

                }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                Log.v(TAG,"Validation failed");

            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_restaurant_registration,container,false);
        mRestaurantNameEditText = (EditText) v.findViewById(R.id.restaurant_name_edit_text);
        mRestaurantEmailEditText = (EditText) v.findViewById(R.id.restaurant_email_edit_text);
        mRestaurantPasswordEditText = (EditText) v.findViewById(R.id.restaurant_password_edit_text);
        mRestaurantLocationButton = (Button) v.findViewById(R.id.restaurant_set_location_button);
        mRestaurantRegisterButton = (Button) v.findViewById(R.id.restaurant_register_button);
        mRestaurantProfileImageView = (ImageView) v.findViewById(R.id.restaurant_profile_image_view);
        mAddImageButton = (ImageButton) v.findViewById(R.id.add_image_button);

        mRestaurantLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        mRestaurantRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(Util.isNetworkAvailable(getActivity()))
                {
                    if(mLocation!=null){
                        mValidator.validate();
                    }else{
                        Toast.makeText(getActivity(), "Please set location", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(),R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                }

            }
        });


        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,CHOOSE_PHOTO);
            }
        });



        return v;

    }

    private void getLocation()
    {
        FragmentManager manager = getFragmentManager();
        LocationConfirmationDialog dialog = new LocationConfirmationDialog();
        dialog.show(manager,LOCATION_CONFIRMATION);

        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.i(TAG,"location:" + location);
                    mLocation = location;
                }


            });
        }catch(SecurityException e){

            Toast.makeText(getActivity(), "Location permission missing", Toast.LENGTH_SHORT).show();
        }


    }

    private void registerRestaurant()
    {
        OkHttpClient client = new OkHttpClient();

        final String restaurantName = mRestaurantNameEditText.getText().toString();
        String email = mRestaurantEmailEditText.getText().toString();
        String password = mRestaurantPasswordEditText.getText().toString();
        double latitude = mLocation.getLatitude();
        double longitude = mLocation.getLongitude();

        RequestBody requestBody = new FormBody.Builder()
                .add("restaurant_name",restaurantName)
                .add("email",email)
                .add("password",password)
                .add("latitude",latitude+"")
                .add("longitude",longitude+"")
                .build();
        Request request =  new Request.Builder().url(REGISTER_RESTAURANT_URL).post(requestBody).build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if(response.isSuccessful())
                    {
                        String responseStr = response.body().string();

                        Log.v(TAG,"OK");
                        Log.v(TAG,responseStr);
                        //Intent intent = UserLoginActivity.newIntent(getActivity());
                        //startActivity(intent)
                        addToFirebase(responseStr);
                    }else{
                        Log.v(TAG,"Failed");
                    }
                } catch (IOException e) {
                    Log.e(TAG,"Exception caught ",e);
                }catch (JSONException e){
                    Log.e(TAG,"Invalid json",e);
                }
            }
        });

        //add profile picture

    }



    private void addToFirebase(String response)throws JSONException
    {
        DatabaseReference restaurantReference = FirebaseDatabase.getInstance().getReference("restaurants");
        GeoFire geofire = new GeoFire(restaurantReference);


        JSONObject jsonBody = new JSONObject(response);
        Boolean status = jsonBody.getBoolean("status");

        if(status){
            id = jsonBody.getString("id");
            geofire.setLocation(id,new GeoLocation(mLocation.getLatitude(),mLocation.getLongitude()));
        }

        GeoLocation loc = new GeoLocation(mLocation.getLatitude(),mLocation.getLongitude());
        GeoQuery geoquery = geofire.queryAtLocation(loc,0.5);

        geoquery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.v(TAG,"key : "+key+" location :"+location.toString());
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


        if(mProfileImageUri!=null)
        {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageReference = storage.getReferenceFromUrl("gs://dish-97f1e.appspot.com");

            StorageReference imageRef = storageReference.child("restaurants/profile-pic/"+id+"profile-pic.jpg");
            UploadTask uploadTask = imageRef.putFile(mProfileImageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Failed to upload picture", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.v(TAG,downloadUrl.toString());

                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("image_url",downloadUrl.toString())
                            .add("restaurant_id",id)
                            .build();

                    Request request =  new Request.Builder().url(ADD_PROFILE_IMAGE_RESTAURANT_URL).post(requestBody).build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {


                            if(response.isSuccessful())
                            {
                                Log.v(TAG,response.body().string());

                            }
                        }
                    });







                }
            });
        }


        Intent intent = RestaurantLoginActivity.newIntent(getActivity());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();



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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK){
            if(requestCode==CHOOSE_PHOTO)
            {
                mProfileImageUri = data.getData();

                Log.v(TAG,mProfileImageUri.toString());
                Picasso.with(getActivity()).load(mProfileImageUri).fit().centerCrop().into(mRestaurantProfileImageView);
            }
        }
    }
}
