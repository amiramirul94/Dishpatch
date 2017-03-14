package com.android.dishpatch.dishpatch.Service;

import android.util.Log;

import com.android.dishpatch.dishpatch.Controller.SharedPreferences.DishpatchPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lenovo on 8/18/2016.
 */
public class InstanceIdService extends FirebaseInstanceIdService {

    private static final String REGISTER_RESTAURANT_TOKEN_URL = "http://insvite.com/php/add_token_restaurant.php";
    private static final String UPDATE_CUSTOMER_TOKEN_URL = "http://insvite.com/php/add_customer_token.php";

    private static final String TAG = InstanceIdService.class.getSimpleName() ;


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.v(TAG,token);
        if(DishpatchPreferences.getRstaurantLoggedIn(this))
        {
            registerRestaurantToken(token);
        }else if(DishpatchPreferences.getUserLoggedIn(this)){
            registerCustomerToken(token);
        }

    }

    private void registerRestaurantToken(String token) {

        int id = DishpatchPreferences.getUserId(this);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("restaurant_id",id+"")
                .add("token",token)
                .build();

        Request request = new Request.Builder()
                .url(REGISTER_RESTAURANT_TOKEN_URL)
                .post(body)
                .build();

        Call call = client.newCall(request);
        try {
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void registerCustomerToken(String token)
    {
        int id = DishpatchPreferences.getUserId(this);

        OkHttpClient client = new OkHttpClient();



        RequestBody requestBody = new FormBody.Builder().add("customer_id",id+"").add("token",token).build();

        Request request = new Request.Builder().post(requestBody).url(UPDATE_CUSTOMER_TOKEN_URL).build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(response.isSuccessful())
                {
                    Log.v(TAG,"token updated");
                }
            }
        });

    }
}
