package com.android.dishpatch.dishpatch.Model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2/16/2016.
 */
public class RestaurantManager {

    private static RestaurantManager sRestaurantManager;
    private Context mContext;
    private List<Restaurant> mRestaurants;

    public static RestaurantManager get(Context context)
    {
        if(sRestaurantManager==null)
        {
            sRestaurantManager= new RestaurantManager(context);
        }

        return sRestaurantManager;
    }

    private RestaurantManager(Context context)
    {
        mContext = context;
        mRestaurants = new ArrayList<>();

        for(int i=0; i<20; i++)
        {
            Restaurant restaurant = new Restaurant();
            restaurant.setName("Restaurant # "+i);
            restaurant.setDistance(100*(i+1));
            restaurant.setRating(4);
            mRestaurants.add(restaurant);
        }

    }

    public List<Restaurant> getRestaurants() {
        return mRestaurants;
    }


}
