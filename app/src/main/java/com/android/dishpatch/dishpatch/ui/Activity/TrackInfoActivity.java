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
import android.view.MenuItem;

import com.android.dishpatch.dishpatch.R;
import com.android.dishpatch.dishpatch.Controller.Fragment.TrackInfoFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class TrackInfoActivity extends AppCompatActivity {

    private static final int REQUEST_ERROR = 0;

    public static Intent newIntent(Context context)
    {
        Intent intent = new Intent(context,TrackInfoActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment =  fm.findFragmentById(R.id.map_fragment_container);


        if(fragment==null)
        {
            fragment =  TrackInfoFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment_container,fragment).commit();
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume()
    {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home)
        {
            Intent i = CustomerActivity.newIntent(this);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
