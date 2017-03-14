package com.android.dishpatch.dishpatch.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.android.dishpatch.dishpatch.Model.Restaurant;
import com.android.dishpatch.dishpatch.R;
import com.android.dishpatch.dishpatch.Controller.Fragment.SelectFoodFragment;

public class SelectFoodActivity extends AppCompatActivity {

    public static final String EXTRA_RESTAURANT = "com.android.dishpatch.dishpatch.restaurant_extra";
    public static final String RESTAURANT_SAVED = "RESTAURANT_STATE";
    public static final String TAG = "SelectFoodActivity";
    private Restaurant mRestaurant;

    public static Intent newIntent(Context context,Restaurant restaurant)
    {
        Intent intent = new Intent(context,SelectFoodActivity.class);
        intent.putExtra(EXTRA_RESTAURANT, restaurant);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_food);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        mRestaurant =  getIntent().getParcelableExtra(EXTRA_RESTAURANT);

        toolbar.setTitle(mRestaurant.getName());
        setSupportActionBar(toolbar);




        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.select_food_fragment_container);

        if(fragment==null)
        {

            fragment = SelectFoodFragment.newInstance(mRestaurant);

            fm.beginTransaction()
                    .add(R.id.select_food_fragment_container,fragment)
                    .commit();
        }



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }





}
