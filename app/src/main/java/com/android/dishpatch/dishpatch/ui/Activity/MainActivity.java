package com.android.dishpatch.dishpatch.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.dishpatch.dishpatch.Controller.SharedPreferences.DishpatchPreferences;
import com.android.dishpatch.dishpatch.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button mUserButton;
    private Button mRestaurantButton;
    private Boolean isUserLoggedIn;
    private Boolean isRestaurantLoggedIn;

    public static Intent newIntent(Context context)
    {
        Intent i = new Intent(context,MainActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        isUserLoggedIn = DishpatchPreferences.getUserLoggedIn(this);
        isRestaurantLoggedIn = DishpatchPreferences.getRstaurantLoggedIn(this);
        Log.v(TAG, isUserLoggedIn.toString());
        if(isUserLoggedIn){

            Intent intent = CustomerActivity.newIntent(this);
            startActivity(intent);

        }else if(isRestaurantLoggedIn){
            Intent intent = RestaurantActivity.newIntent(this);
            startActivity(intent);
        }


        mUserButton = (Button) findViewById(R.id.user_button);
        mRestaurantButton = (Button) findViewById(R.id.restaurant_button);

        mUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = UserLoginActivity.newIntent(v.getContext());

                startActivity(i);
            }
        });


        mRestaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = RestaurantLoginActivity.newIntent(v.getContext());
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        isUserLoggedIn = DishpatchPreferences.getUserLoggedIn(this);
        Log.v(TAG, isUserLoggedIn.toString());
        if(isUserLoggedIn){

            Intent intent = CustomerActivity.newIntent(this);
            startActivity(intent);

        }else if(isRestaurantLoggedIn){
            Intent intent = RestaurantActivity.newIntent(this);
            startActivity(intent);

        }
    }
}