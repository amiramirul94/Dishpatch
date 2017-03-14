package com.android.dishpatch.dishpatch.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.android.dishpatch.dishpatch.Controller.Fragment.RestaurantManageMenuFragment;
import com.android.dishpatch.dishpatch.Controller.Fragment.RestaurantOrderFragment;
import com.android.dishpatch.dishpatch.Controller.SharedPreferences.DishpatchPreferences;
import com.android.dishpatch.dishpatch.R;
import com.android.dishpatch.dishpatch.Service.InstanceIdService;
import com.android.dishpatch.dishpatch.Service.MessagingService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RestaurantActivity extends AppCompatActivity {

    private static final String TAG = RestaurantActivity.class.getSimpleName() ;
    private static final String REGISTER_RESTAURANT_TOKEN_URL = "http://insvite.com/php/add_token_restaurant.php";


    public static Intent newIntent(Context context)
    {
        Intent intent = new Intent(context,RestaurantActivity.class);
        return intent;
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        int id = DishpatchPreferences.getUserId(this);

        FirebaseMessaging.getInstance().subscribeToTopic(id+"-restaurant-push-notification");
        Log.v(TAG,FirebaseInstanceId.getInstance().getToken());
        String token = FirebaseInstanceId.getInstance().getToken();
        registerRestaurantToken(token);




    }

    private void registerRestaurantToken(String token) {

        int id = DishpatchPreferences.getUserId(this);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("restaurant_id",id+"")
                .add("token",token)
                .build();

        Request request = new Request.Builder()
                .url(REGISTER_RESTAURANT_TOKEN_URL)
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            DishpatchPreferences.setPrefRestaurantLogin(this,false);
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.v(TAG,"TOKEN="+FirebaseInstanceId.getInstance().getToken());
            Intent intent = MainActivity.newIntent(this);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            if(position==0)
            {
                return RestaurantOrderFragment.newInstance();
            }else if(position==1){
                return RestaurantManageMenuFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Orders";
                case 1:
                    return "Menu";
            }
            return null;
        }
    }
}
