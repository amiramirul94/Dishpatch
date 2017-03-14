package com.android.dishpatch.dishpatch.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.android.dishpatch.dishpatch.Controller.Fragment.DispatchOrderStatusFragment;
import com.android.dishpatch.dishpatch.Model.DishpatchCentral;
import com.android.dishpatch.dishpatch.Model.Order;
import com.android.dishpatch.dishpatch.R;

public class DispatchOrderStatusActivity extends AppCompatActivity {

    private static final String TAG = "DispatchStatus";
    private static final String EXTRA_DISPATCH_DATA = "com.android.dispatch.dispatch.Dispatch";
    public static Intent newIntent(Context packageContext, Order order)
    {
        Intent intent = new Intent(packageContext,DispatchOrderStatusActivity.class);
        intent.putExtra(EXTRA_DISPATCH_DATA,order);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_order_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Order Status");
        setSupportActionBar(toolbar);

        //Log.d(TAG,getIntent().getParcelableExtra(EXTRA_DISPATCH_DATA).toString());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.fragment_dispatch_order_status_container);

        if(fragment==null)
        {
            Order order;

            order = (Order) DishpatchCentral.get(this).getValue("ORDER_DATA");

            fragment = DispatchOrderStatusFragment.newInstance(order);
            fm.beginTransaction()
                    .add(R.id.fragment_dispatch_order_status_container,fragment)
                    .commit();
        }
    }

}
