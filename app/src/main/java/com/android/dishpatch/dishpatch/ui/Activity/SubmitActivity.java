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
import com.android.dishpatch.dishpatch.Controller.Fragment.SubmitFragment;

public class SubmitActivity extends AppCompatActivity {

    public static final String EXTRA_RESTAURANT="EXTRA_RESTAURANT";
    public static Intent newIntent(Context context,Restaurant restaurant)
    {
        Intent intent = new Intent(context,SubmitActivity.class);
        intent.putExtra(EXTRA_RESTAURANT, restaurant);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.fragment_submit_container);

        if(fragment==null)
        {
            Restaurant restaurant = getIntent().getParcelableExtra(EXTRA_RESTAURANT);
            fragment = SubmitFragment.newInstance(restaurant);
            fm.beginTransaction()
                    .add(R.id.fragment_submit_container,fragment)
                    .commit();
        }
    }

}
