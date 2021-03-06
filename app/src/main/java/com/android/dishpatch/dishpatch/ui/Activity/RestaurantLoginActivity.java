package com.android.dishpatch.dishpatch.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.dishpatch.dishpatch.R;
import com.android.dishpatch.dishpatch.Controller.Fragment.RestaurantLoginFragment;

public class RestaurantLoginActivity extends AppCompatActivity {

    public static Intent newIntent(Context context)
    {
        Intent i = new Intent(context,RestaurantLoginActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_login);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.restaurant_login_fragment_container);

        if(fragment==null)
        {
            fragment = RestaurantLoginFragment.newInstance();

            fm.beginTransaction()
                    .add(R.id.restaurant_login_fragment_container,fragment)
                    .commit();

        }
    }
}
