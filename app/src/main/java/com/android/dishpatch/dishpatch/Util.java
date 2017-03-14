package com.android.dishpatch.dishpatch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Lenovo on 4/27/2016.
 */
public class Util {

    public static final String RESTAURANT_ORDER = "RESTAURANT_ORDER";

    public static Boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static boolean isGooglePlayServiceEnabled(Context context)
    {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }



}
