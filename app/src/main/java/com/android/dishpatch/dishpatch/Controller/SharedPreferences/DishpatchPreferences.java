package com.android.dishpatch.dishpatch.Controller.SharedPreferences;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Lenovo on 7/26/2016.
 */
public class DishpatchPreferences {
    private static String PREF_IS_LOGIN ="IS_USER_LOGIN";
    private static String PREF_RESTAURANT_LOGIN = "IS_RESTAURANT_LOGIN";
    private static String PREF_USER_ID = "USER_ID";
    private static String PREF_DISPATCH_ONLINE = "IS_DISPATCH_ONLINE";

    public static Boolean getUserLoggedIn(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_IS_LOGIN,false);
    }

    public static Boolean getRstaurantLoggedIn(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_RESTAURANT_LOGIN,false);
    }

    public static int getUserId(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_USER_ID,-1);
    }
    public static void setUserLoggedIn(Context context, Boolean isLoggedIn)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_IS_LOGIN,isLoggedIn).apply();
    }

    public static void setPrefRestaurantLogin(Context context, Boolean isLoggedIn)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_RESTAURANT_LOGIN,isLoggedIn).apply();
    }

    public static void setPrefUserId(Context context,int id)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_USER_ID,id).apply();
    }

    public static void setDispatchOnline(Context context,boolean isOnline)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_DISPATCH_ONLINE,isOnline).apply();
    }

    public static boolean getIsDispatchOnline(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_DISPATCH_ONLINE,false);
    }
}
