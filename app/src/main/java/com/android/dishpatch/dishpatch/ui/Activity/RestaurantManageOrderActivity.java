package com.android.dishpatch.dishpatch.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.dishpatch.dishpatch.Model.DishpatchCentral;
import com.android.dishpatch.dishpatch.Model.Order;
import com.android.dishpatch.dishpatch.R;
import com.android.dishpatch.dishpatch.Controller.Fragment.RestaurantManageOrderFragment;
import com.android.dishpatch.dishpatch.Util;

public class RestaurantManageOrderActivity extends AppCompatActivity {

    private static final String TAG = "ManageOrderActivity";

    public static Intent newIntent(Context packageContext)
    {
        Intent intent = new Intent(packageContext,RestaurantManageOrderActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_manage_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Manage Order");
        setSupportActionBar(toolbar);



        Order order = (Order) DishpatchCentral.get(this).getValue(Util.RESTAURANT_ORDER);

        Log.d(TAG,order.toString());

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.container_fragment_manage_order);

        if(fragment==null)
        {
            fragment = RestaurantManageOrderFragment.newInstance(order);

            fm.beginTransaction()
                    .add(R.id.container_fragment_manage_order,fragment)
                    .commit();

        }



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
