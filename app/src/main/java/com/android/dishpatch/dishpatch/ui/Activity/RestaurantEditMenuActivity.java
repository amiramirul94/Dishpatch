package com.android.dishpatch.dishpatch.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.dishpatch.dishpatch.Controller.Fragment.RestaurantEditMenuFragment;
import com.android.dishpatch.dishpatch.Model.DishpatchCentral;
import com.android.dishpatch.dishpatch.Model.Menu;
import com.android.dishpatch.dishpatch.R;

public class RestaurantEditMenuActivity extends AppCompatActivity {

    private static final String MENU_ID = "MENU_ID";
    private static final String TAG = RestaurantEditMenuActivity.class.getSimpleName();

    public static final Intent newIntent(Context context,int id)
    {
        Intent intent = new Intent(context,RestaurantEditMenuActivity.class);
        intent.putExtra(MENU_ID,id);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_edit_menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.fragment_edit_menu_container);

        int id = getIntent().getIntExtra(MENU_ID,0);


        Menu menu = DishpatchCentral.get(this).getMenu(id);

        if(fragment==null)

            fragment = RestaurantEditMenuFragment.newInstance(id);
        {
            fm.beginTransaction().add(R.id.fragment_edit_menu_container,fragment).commit();
        }
    }
}
