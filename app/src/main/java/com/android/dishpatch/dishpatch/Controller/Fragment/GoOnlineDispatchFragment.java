package com.android.dishpatch.dishpatch.Controller.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.dishpatch.dishpatch.Controller.SharedPreferences.DishpatchPreferences;
import com.android.dishpatch.dishpatch.Service.DispatchLocationServices;
import com.android.dishpatch.dishpatch.ui.Activity.DispatchActivity;
import com.android.dishpatch.dishpatch.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lenovo on 3/8/2016.
 */
public class GoOnlineDispatchFragment extends Fragment {


    private static final int LOCATION_PERMISSION = 1;
    private static final String TAG = GoOnlineDispatchFragment.class.getSimpleName();
    private static final String ADD_DISPATCHER_URL = "http://insvite.com/php/add_dispatcher.php";
    private Button mGoOnline;


    public static GoOnlineDispatchFragment newInstance()
    {
        return new GoOnlineDispatchFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dispatch,container,false);

        mGoOnline = (Button) v.findViewById(R.id.go_online_dispatch);

        mGoOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDispatcherToServer();

                Intent intent = DispatchLocationServices.newIntent(getActivity());
                getActivity().startService(intent);

                DishpatchPreferences.setDispatchOnline(getActivity(),true);

                FragmentTransaction fm = getFragmentManager().beginTransaction();
                Fragment fragment = DispatchListFragment.newInstance();
                fm.replace(R.id.dishpatch_layout,fragment);
                fm.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null).commit();




//                Intent intent = DispatchActivity.newIntent(getActivity());
//                startActivity(intent);
//                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        return v;
    }



    private void sendDispatcherToServer()
    {

        int id = DishpatchPreferences.getUserId(getActivity());
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder().add("customer_id",id+"").build();

        Request request = new Request.Builder().url(ADD_DISPATCHER_URL).post(requestBody).build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               Log.v(TAG,"SUCCESFULLY ADDED");
            }
        });



    }


    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }





}
