package com.android.dishpatch.dishpatch.Model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.dishpatch.dishpatch.Controller.SharedPreferences.DishpatchPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lenovo on 4/21/2016.
 */
public class DishpatchCentral {
    private static final String TAG = DishpatchCentral.class.getSimpleName();
    private static DishpatchCentral sDishpatchCentral;
    private List<Order> mDispatchOrders= new ArrayList<>();
    private HashMap<String,Object> mDishpatchMap = new HashMap<>();
    private List<Menu> mMenuList = new ArrayList<>();// For Restaurant use
    private List<Menu> mCustomerMenuList = new ArrayList<>();//For customer use
    private List<Restaurant> mRestaurantList = new ArrayList<>();
    private static final String GET_MENU_URL = "http://insvite.com/php/restaurant_get_menu.php?restaurant_id=";
    private static final String GET_RESTAURANT_URL = "http://insvite.com/php/get_restaurants.php?id_list=";
    private static final String GET_MENU_CUSTOMER ="http://insvite.com/php/get_menu.php?restaurant_id=";

    public static DishpatchCentral get(Context context)
    {
        if(sDishpatchCentral==null)
        {
            sDishpatchCentral=new DishpatchCentral(context);
        }

        return sDishpatchCentral;
    }

    private DishpatchCentral(Context context)
    {


    }


    public List<Order> getDispatchOrders()
    {
        return mDispatchOrders;
    }

    public void setDispatchOrders(List<Order> dispatchOrders) {
        mDispatchOrders = dispatchOrders;
    }


    //Manage menu for restaurant
    public List<Menu> createMenuList(int id)
    {
        OkHttpClient client = new OkHttpClient();
        String request_url = GET_MENU_URL+id;
        Request request = new Request.Builder().url(request_url).build();
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful())
            {

                String responseBody = response.body().string();
                Log.v(TAG,responseBody);

                try {
                    mMenuList=populateList(responseBody);
                }catch (JSONException e) {
                    Log.v(TAG,e.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return mMenuList;

    }

    //For restaurant
    private List<Menu> populateList(String responseBody) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONArray menuArray = jsonObject.getJSONArray("menu");

        List<Menu> mMenuItems= new ArrayList<>();
        for(int i=0; i<menuArray.length(); i++)
        {
            JSONObject item = menuArray.getJSONObject(i);
            int id = item.getInt("menu_id");
            String name = item.getString("menu_name");
            double price = item.getDouble("price");
            Boolean availability = item.getBoolean("availability");
            String picture = item.getString("picture");
            Menu menu = new Menu(id,name,price,availability,picture);
            mMenuItems.add(menu);



        }

        return mMenuItems;

    }


    public List<Restaurant> createRestaurantList(List<Integer> keys)
    {
        List<Restaurant> restaurants = new ArrayList<>();
        int i=0;
        String keyString="";
        for (int k: keys) {
            if(i==0){
                keyString += k+"";
            }else{
                keyString+= ","+k;
            }
            i++;
        }

        Log.v(TAG,keyString);

        OkHttpClient client = new OkHttpClient();
        String requestString = GET_RESTAURANT_URL+keyString;
        Request request = new Request.Builder().url(requestString).build();
        Call call = client.newCall(request);

        try {
            Response response = call.execute();

            if(response.isSuccessful())
            {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                Log.v(TAG,jsonObject.toString());
                restaurants = populateRestaurantList(jsonObject);
                mRestaurantList = restaurants;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }  catch (IOException e){
            e.printStackTrace();
        }
        return restaurants;
    }

    private List<Restaurant> populateRestaurantList(JSONObject json) throws JSONException
    {
        JSONArray jsonArray = json.getJSONArray("restaurants");
        List<Restaurant> restaurants = new ArrayList<>();
        for(int i=0; i<jsonArray.length(); i++)
        {
            JSONObject restaurantObject = jsonArray.getJSONObject(i);
            int id = Integer.parseInt(restaurantObject.getString("id"));
            String name = restaurantObject.getString("name");
            double latitude = Double.parseDouble(restaurantObject.getString("latitude"));
            double longitude = Double.parseDouble(restaurantObject.getString("longitude"));
            Uri profileUri = Uri.parse(restaurantObject.getString("profile_image"));
            Log.v(TAG,profileUri.toString());

            Restaurant restaurant = new Restaurant(id,name,profileUri,latitude,longitude);
            restaurants.add(restaurant);
        }

        return restaurants;
    }

    //Get Menu For customers
    public List<Menu> createCustomerMenuList(int id)
    {

        List<Menu> temp = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        String requestString = GET_MENU_CUSTOMER+id;

        Request request = new Request.Builder().url(requestString).build();

        Call call = client.newCall(request);
        try {
            Response response = call.execute();

            if(response.isSuccessful())
            {
                String responseBody = response.body().string();

                Log.v(TAG,responseBody);

                JSONObject jsonBody = new JSONObject(responseBody);
                temp = populateCustomerMenuList(jsonBody);
                mCustomerMenuList = temp;

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mCustomerMenuList;

    }


    private List<Menu> populateCustomerMenuList(JSONObject jsonObject) throws JSONException{

        List<Menu> menus = new ArrayList<>();
        JSONArray menuArray = jsonObject.getJSONArray("menu");

        for(int i=0; i<menuArray.length(); i++)
        {
            JSONObject menuObject = menuArray.getJSONObject(i);
            int id = Integer.parseInt(menuObject.getString("menu_id"));
            String name = menuObject.getString("name");
            double price = menuObject.getDouble("price");
            String picture = menuObject.getString("picture");

            Menu menu = new Menu(id,name,price,true,picture);
            menus.add(menu);
        }

        return menus;


    }
    public List<Menu> getMenuList() {


        return mMenuList;
    }

    public void setMenuList(List<Menu> menuList) {
        mMenuList = menuList;
    }

    public Menu getMenu(int id)
    {
        for(Menu menu: mMenuList)
        {
            if(menu.getMenuId()==id)
            {
                return menu;
            }

        }

        return null;

    }

    public List<Menu> getCustomerMenuList() {
        return mCustomerMenuList;
    }

    public void setCustomerMenuList(List<Menu> customerMenuList) {
        mCustomerMenuList = customerMenuList;
    }

    public List<Restaurant> getRestaurantList() {
        return mRestaurantList;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        mRestaurantList = restaurantList;
    }

    public void addMenu(Menu menu)
    {
        mMenuList.add(menu);
    }
    public void putValue(String key, Object o)
    {
        mDishpatchMap.put(key,o);
    }

    public Object getValue(String key)
    {
        return mDishpatchMap.get(key);
    }
}
