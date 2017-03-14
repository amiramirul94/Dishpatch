package com.android.dishpatch.dishpatch.Controller.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.android.dishpatch.dishpatch.Controller.Fragment.Dialog.DeleteConfirmationDialog;
import com.android.dishpatch.dishpatch.Controller.SharedPreferences.DishpatchPreferences;
import com.android.dishpatch.dishpatch.Model.DishpatchCentral;
import com.android.dishpatch.dishpatch.Model.Menu;
import com.android.dishpatch.dishpatch.R;
import com.android.dishpatch.dishpatch.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
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
 * Created by Lenovo on 5/6/2016.
 */
public class RestaurantEditMenuFragment extends Fragment {

    private static final String MENU_ARGS= "MENU_ARGS";
    private static final String EXTRA_SAVED_RESULT = "com.dishpatch.dishpatch.RestaurantEditMenuFragment";
    private static final String ADD_MENU_URL = "http://insvite.com/php/add_menu.php";
    private static final String ADD_MENU_IMAGE_URL = "http://insvite.com/php/add_menu_image.php";
    private static final String UPDATE_MENU_URL = "http://insvite.com/php/update_menu.php";
    private static final String DELETE_MENU_URL = "http://insvite.com/php/delete_menu.php";
    private static final String TAG = RestaurantEditMenuFragment.class.getSimpleName() ;
    private static final int REQUEST_PHOTO= 1;
    private static final String DELETE_CONFIRMATION = "REQUEST_DELETE_CONFIRMATION";
    private static final int REQUEST_DELETE_CONFIRMATION = 0;
    private static final String SAVED_MENU = "com.android.dishpatch.dishpatch.Menu";


    private Menu mMenu;
    @NotEmpty
    private EditText mFoodNameEditText;
    @NotEmpty
    private EditText mPriceEditText;

    private Switch mAvailabilitySwitch;
    private Button mSaveButton;
    private ImageButton mAddImageButton;
    private ImageView mFoodImageView;
    private Boolean isSaved=false;
    private Validator mValidator;
    private Boolean isSuccesful=false;


    private Uri mFoodImageUri;
    private Boolean isImageAdded=true;
    private int userId;
    private Integer foodId=null;
    private boolean isDeleteConfirmed;

    public static RestaurantEditMenuFragment newInstance(int id)
    {
        Bundle args = new Bundle();
        args.putInt(MENU_ARGS,id);

        RestaurantEditMenuFragment fragment = new RestaurantEditMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static Boolean wasSaved(Intent result){
        return result.getBooleanExtra(EXTRA_SAVED_RESULT,false);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        int id = getArguments().getInt(MENU_ARGS);
        mMenu = DishpatchCentral.get(getActivity()).getMenu(id);
        mValidator = new Validator(this);
        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {

                Log.v(TAG,"Validation succeeded");
                if(mMenu!=null)
                {
                    updateMenu();


                }else if(mMenu==null) {
                    addMenu();

                }
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                Toast.makeText(getActivity(), "Validation failed", Toast.LENGTH_SHORT).show();
            }
        });

        userId = DishpatchPreferences.getUserId(getActivity());

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu_edit,container,false);

        mFoodNameEditText = (EditText) v.findViewById(R.id.food_edit_text);
        mPriceEditText = (EditText) v.findViewById(R.id.price_edit_text);
        mSaveButton = (Button) v.findViewById(R.id.save_menu_button);
        mAvailabilitySwitch= (Switch) v.findViewById(R.id.availability_switch);
        mAddImageButton = (ImageButton) v.findViewById(R.id.add_image_button);
        mFoodImageView = (ImageView) v.findViewById(R.id.food_image_view);

        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_PHOTO);
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSaved=true;
                setSavedResult(isSaved);

                if(Util.isNetworkAvailable(getActivity()))
                {
                    mValidator.validate();
                }else {
                    Toast.makeText(getActivity(),R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                }

            }
        });



        if(mMenu!=null)
        {
            mFoodNameEditText.setText(mMenu.getFood());

            String priceText = String.format("%.2f",mMenu.getPrice());
            mPriceEditText.setText(priceText);
            foodId= mMenu.getMenuId();
            if(mMenu.getPictureUrl()!=null)
            {
                if(!mMenu.getPictureUrl().isEmpty()){
                    Picasso.with(getActivity()).load(mMenu.getPictureUrl()).fit().centerCrop().into(mFoodImageView);
                }
            }

            if(mMenu.getAvailable()!=null)
            {
                mAvailabilitySwitch.setChecked(mMenu.getAvailable());
            }

        }



        return v;


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_MENU,mMenu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState!=null)
        {
            mMenu = savedInstanceState.getParcelable(SAVED_MENU);

        }

    }

    private void setSavedResult(boolean b) {
        Intent data = new Intent();
        data.putExtra(EXTRA_SAVED_RESULT,b);
        getActivity().setResult(Activity.RESULT_OK,data);
    }



    private void updateMenu(){
        mMenu.setFood(mFoodNameEditText.getText().toString());
        mMenu.setPrice(Float.parseFloat(mPriceEditText.getText().toString()));
        mMenu.setAvailable(mAvailabilitySwitch.isChecked());

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("menu_id",mMenu.getMenuId()+"")
                .add("menu_name",mMenu.getFood())
                .add("price",mMenu.getPrice()+"")
                .add("availability",mMenu.getAvailable()+"")
                .build();

        Request request = new Request.Builder().url(UPDATE_MENU_URL).post(requestBody).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(response.isSuccessful())
                {
                    Log.v(TAG,"succesfully updated");

                    if(isImageAdded){
                        Log.v(TAG,"Image have been added");
                        while(foodId==null){
                        }
                        addImageToFirebase();
                    }

                    getActivity().finish();


                }

            }
        });


    }

    private void addMenu()
    {
        mMenu = new Menu();
        mMenu.setFood(mFoodNameEditText.getText().toString());
        mMenu.setPrice(Float.parseFloat(mPriceEditText.getText().toString()));
        mMenu.setAvailable(mAvailabilitySwitch.isChecked());


        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody =  new FormBody.Builder()
                .add("id",userId+"")
                .add("menu_name",mMenu.getFood())
                .add("price",mMenu.getPrice()+"")
                .add("availability",mMenu.getAvailable().toString())
                .build();

        Request request = new Request.Builder().url(ADD_MENU_URL).post(requestBody).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isSuccesful=false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    Log.v(TAG,"succesfully added");
                    DishpatchCentral.get(getActivity()).addMenu(mMenu);

                    String body = response.body().string();
                    Log.v(TAG,body);



                    try {
                        JSONObject jsonBody = new JSONObject(body);

                        foodId = Integer.parseInt(jsonBody.getString("id"));
                        Log.v(TAG,"Food Id ="+foodId);
                    }catch (JSONException e){

                        Log.v(TAG,e.toString());
                    }

                }else{
                    Log.v(TAG,"failed to add");

                }
            }
        });


        if(isImageAdded){
            Log.v(TAG,"Image have been added");
            while(foodId==null){
            }
            addImageToFirebase();
        }

        getActivity().finish();

    }

    private void deleteMenu()
    {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder().add("menu_id",mMenu.getMenuId()+"").build();

        Request request = new Request.Builder().url(DELETE_MENU_URL).post(requestBody).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful())
                {
                    getActivity().finish();
                }
            }
        });
    }

    private void addImageToFirebase()
    {
        if(mFoodImageUri!=null)
        {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference reference = storage.getReferenceFromUrl("gs://dish-97f1e.appspot.com");

            StorageReference foodImageRef = reference.child("restaurants/menu/"+userId+"-"+foodId+"-"+mMenu.getFood());
            UploadTask uploadTask = foodImageRef.putFile(mFoodImageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    OkHttpClient client = new OkHttpClient();
                    Log.v(TAG,downloadUri.toString());
                    RequestBody requestBody = new FormBody.Builder()
                            .add("menu_id",foodId+"")
                            .add("image_url",downloadUri.toString())
                            .build();

                    Request request = new Request.Builder().url(ADD_MENU_IMAGE_URL).post(requestBody).build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {


                        }
                    });







                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_edit_menu,menu);

        MenuItem deleteItem = menu.findItem(R.id.menu_delete_food);
        deleteItem.setEnabled(mMenu!=null);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                setSavedResult(isSaved);
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            case R.id.menu_delete_food:
                FragmentManager manager = getFragmentManager();
                DeleteConfirmationDialog deleteConfirmationDialog = DeleteConfirmationDialog.newInstance();
                deleteConfirmationDialog.setTargetFragment(RestaurantEditMenuFragment.this,REQUEST_DELETE_CONFIRMATION);
                deleteConfirmationDialog.show(manager,DELETE_CONFIRMATION);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==Activity.RESULT_OK)
        {
            if(requestCode==REQUEST_PHOTO)
            {
                mFoodImageUri = data.getData();

                Picasso.with(getActivity()).load(mFoodImageUri).fit().centerCrop().into(mFoodImageView);
                isImageAdded=true;
            }else if(requestCode==REQUEST_DELETE_CONFIRMATION) {
                isDeleteConfirmed = data.getBooleanExtra(DeleteConfirmationDialog.EXTRA_CONFIRMATION,false);
                Log.v(TAG,isDeleteConfirmed+"");

                if(isDeleteConfirmed){
                    deleteMenu();
                }
            }
        }
    }
}
