package com.android.dishpatch.dishpatch.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.dishpatch.dishpatch.Controller.Fragment.ConfirmationFragment;
import com.android.dishpatch.dishpatch.Model.Menu;
import com.android.dishpatch.dishpatch.Model.Restaurant;
import com.android.dishpatch.dishpatch.R;

import java.util.ArrayList;
import java.util.List;

public class ConfirmationActivity extends AppCompatActivity {

    public static final String EXTRA_RESTAURANT = "EXTRA_RESTAURANT";
    public static final String EXTRA_ORDER_LIST ="EXTRA_ORDER_LIST";

    public static final String TAG = "ConfirmationActivity";
    private Restaurant mRestaurant;
    private List<Menu> mOrderList;


    public static final Intent newIntent(Context context,Restaurant restaurant,List<Menu> orderList)
    {
        Intent intent = new Intent(context,ConfirmationActivity.class);
        intent.putExtra(EXTRA_RESTAURANT,restaurant);
        intent.putParcelableArrayListExtra(EXTRA_ORDER_LIST, (ArrayList<? extends Parcelable>) orderList);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_confirmation);
        toolbar.setTitle("Order Summary");
        setSupportActionBar(toolbar);

        mRestaurant = getIntent().getParcelableExtra(EXTRA_RESTAURANT);
        mOrderList= getIntent().getParcelableArrayListExtra(EXTRA_ORDER_LIST);

        Log.i(TAG,mOrderList.toString());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.confirmation_fragment_container);

        if(fragment==null)
        {
            fragment = ConfirmationFragment.newInstance(mRestaurant,mOrderList);

            fm.beginTransaction()
                    .add(R.id.confirmation_fragment_container,fragment)
                    .commit();
        }




    }

}
