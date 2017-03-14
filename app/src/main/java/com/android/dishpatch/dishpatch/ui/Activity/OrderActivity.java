package com.android.dishpatch.dishpatch.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.dishpatch.dishpatch.Controller.Fragment.GoOnlineDispatchFragment;
import com.android.dishpatch.dishpatch.R;
import com.android.dishpatch.dishpatch.Controller.Fragment.RestaurantListFragment;
import com.android.dishpatch.dishpatch.Controller.Fragment.TrackFragment;

public class OrderActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String FRAGMENT_FLAG =  "Fragment_extra_order_activity";
    public static final String TRACK_FRAGMENT = "TRACK_FRAGMENT";
    private final String TAG = "OrderActivity";

    private Fragment mFragment=null;
    public static Intent newIntent(Context context,String flag)
    {
        Intent  intent = new Intent(context,OrderActivity.class);
        intent.putExtra(FRAGMENT_FLAG,flag);

        return intent;

    }

    public static Intent newIntent(Context context)
    {
        Intent intent = new Intent(context,OrderActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String frag_flag;


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        frag_flag = getIntent().getStringExtra(FRAGMENT_FLAG);



        if(frag_flag!=null)
        {
            Log.i(TAG,frag_flag);

            if(frag_flag==TRACK_FRAGMENT)
            {
                mFragment = TrackFragment.newInstance();
            }
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.order_fragment_container);

        if(fragment==null)
        {

            fragment = RestaurantListFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.order_fragment_container,fragment)
                    .commit();
        }else{

            if(mFragment!=null)
            {
                fragment = mFragment;
            }else{
                fragment = RestaurantListFragment.newInstance();

            }

            fm.beginTransaction()
                    .replace(R.id.order_fragment_container, fragment)
                    .commit();

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;

        if (id == R.id.nav_order) {

            fragment = RestaurantListFragment.newInstance();
            fm.beginTransaction()
                    .replace(R.id.order_fragment_container,fragment)
                    .commit();

        } else if (id == R.id.nav_track) {

            fragment = TrackFragment.newInstance();

            fm.beginTransaction()
                    .replace(R.id.order_fragment_container,fragment)
                    .commit();

        } else if (id == R.id.nav_dispatch) {

            fragment = GoOnlineDispatchFragment.newInstance();

            fm.beginTransaction().replace(R.id.order_fragment_container,fragment)
                    .commit();

        } else if (id == R.id.nav_billing) {

        } else if (id == R.id.nav_settings) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
