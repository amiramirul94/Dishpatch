package com.android.dishpatch.dishpatch.ui.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.android.dishpatch.dishpatch.Controller.Fragment.DispatchMapFragment;
import com.android.dishpatch.dishpatch.Model.Order;
import com.android.dishpatch.dishpatch.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class DispatchMapActivity extends AppCompatActivity {

    private static final int REQUEST_ERROR = 0;
    private static final String EXTRA_DISPATCH= "EXTRA_DISPATCH";


    private Order mOrder;

    public static Intent newIntent(Context packageContext,Order order)
    {
        Intent intent = new Intent(packageContext,DispatchMapActivity.class);
        intent.putExtra(EXTRA_DISPATCH,order);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Dispatch Map");
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.dispatch_map_fragment_container);

        if(fragment==null)
        {
            fragment = DispatchMapFragment.newInstance(mOrder);

            mOrder = getIntent().getParcelableExtra(EXTRA_DISPATCH);

            fm.beginTransaction()
                    .add(R.id.dispatch_map_fragment_container,fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(errorCode!= ConnectionResult.SUCCESS)
        {
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, REQUEST_ERROR, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });

            errorDialog.show();
        }
    }
}
