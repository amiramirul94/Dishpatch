package com.android.dishpatch.dishpatch.Controller.Fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dishpatch.dishpatch.Controller.SharedPreferences.DishpatchPreferences;
import com.android.dishpatch.dishpatch.Model.DishpatchCentral;
import com.android.dishpatch.dishpatch.Model.Menu;
import com.android.dishpatch.dishpatch.R;
import com.android.dishpatch.dishpatch.Util;
import com.android.dishpatch.dishpatch.ui.Activity.RestaurantEditMenuActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lenovo on 5/6/2016.
 */
public class RestaurantManageMenuFragment extends Fragment {

    private static final String TAG = RestaurantManageMenuFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MenuAdapter mMenuAdapter;
    private FloatingActionButton mAddMenuFloatingActionButton;
    private static final int REQUEST_DATA_CHANGED=0;
    private static final String UPDATE_AVAILABILITY_URL = "http://insvite.com/php/update_food_availability.php";
    List<Menu> menus = new ArrayList<>();
    private int userId;
    private boolean isSuccessful;

    public static RestaurantManageMenuFragment newInstance()
    {
        return new RestaurantManageMenuFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = DishpatchPreferences.getUserId(getActivity());




    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_manage_menu,container,false);


        mRecyclerView = (RecyclerView) v.findViewById(R.id.menu_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAddMenuFloatingActionButton = (FloatingActionButton) v.findViewById(R.id.add_menu_fab);
        mAddMenuFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = RestaurantEditMenuActivity.newIntent(getActivity(),-1);
                startActivityForResult(i,REQUEST_DATA_CHANGED);
            }
        });

        if(Util.isNetworkAvailable(getActivity()))
        {
            new GetMenuTask().execute();
        }else{
            Toast.makeText(getActivity(),R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }


        updateUI();


        return v;



    }

    private void updateUI() {


        DishpatchCentral dishpatchCentral = DishpatchCentral.get(getActivity());
        menus = dishpatchCentral.getMenuList();

        Log.v(TAG,menus.size()+"");

            mMenuAdapter = new MenuAdapter(menus);
            mRecyclerView.setAdapter(mMenuAdapter);




    }



    @Override
    public void onResume() {
        super.onResume();
        if(Util.isNetworkAvailable(getActivity()))
        {
            new GetMenuTask().execute();
        }else{
            Toast.makeText(getActivity(),R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
        updateUI();
    }


    @Override
    public void onPause() {
        super.onPause();
        mMenuAdapter.notifyDataSetChanged();
    }

    private class GetMenuTask extends AsyncTask<Void,Void,List<Menu>>{

        @Override
        protected List<Menu> doInBackground(Void... params) {


            List<Menu> menu = DishpatchCentral.get(getActivity()).createMenuList(userId);
            while (menu.isEmpty()){}
            return menu;
        }

        @Override
        protected void onPostExecute(List<Menu> menu) {

            Log.v(TAG,"On post execute menu="+menu.size());
            menus = menu;
            updateUI();
        }
    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder>{

        private List<Menu> mMenus;

        public MenuAdapter(List<Menu> menus)
        {
            mMenus = menus;
        }
        @Override
        public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.manage_menu_list_item_layout,parent,false);
            return new MenuViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MenuViewHolder holder, int position) {
            Menu menu = mMenus.get(position);
            holder.bindMenu(menu);
        }



        @Override
        public int getItemCount() {
            return mMenus.size();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode!= Activity.RESULT_OK)
        {
            return;
        }

        if(requestCode==REQUEST_DATA_CHANGED)
        {
            if(data==null)
            {
                return;
            }else{
                Boolean result;
                result= RestaurantEditMenuFragment.wasSaved(data);
                Log.v(TAG,result.toString());
            }
        }

    }

    private class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Menu mMenu;
        private ImageView mMenuImageView;
        private TextView mMenuNameTextView;
        private TextView mMenuPriceTextView;
        private Switch mMenuAvailabilitySwitch;
        private TextView mMenuAvailabilityTextView;
        private Boolean mAvailability;

        public MenuViewHolder(View itemView) {
            super(itemView);

            mMenuImageView = (ImageView) itemView.findViewById(R.id.menu_image_view);
            mMenuNameTextView = (TextView) itemView.findViewById(R.id.menu_name_text_view);
            mMenuPriceTextView = (TextView) itemView.findViewById(R.id.price_text_view);
            mMenuAvailabilitySwitch = (Switch) itemView.findViewById(R.id.availability_switch);
            mMenuAvailabilityTextView = (TextView) itemView.findViewById(R.id.availability_text_view);


            itemView.setOnClickListener(this);
        }

        public void bindMenu(Menu menu)
        {

            mMenu = menu;

            mMenuNameTextView.setText(mMenu.getFood());
            String price = String.format("RM%.2f",mMenu.getPrice());
            mMenuPriceTextView.setText(price);

            mAvailability = mMenu.getAvailable();
            mMenuAvailabilitySwitch.setChecked(mAvailability);
            if(mAvailability)
            {
                mMenuAvailabilityTextView.setText("Available");
            }else{
                mMenuAvailabilityTextView.setText("Unavailable");
            }

            mMenuAvailabilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    OkHttpClient client = new OkHttpClient();

                    Log.v(TAG,mMenu.getMenuId()+"");
                    RequestBody requestBody =  new FormBody.Builder()
                            .add("menu_id",mMenu.getMenuId()+"")
                            .add("availability",isChecked+"")
                            .build();
                    Request request = new Request.Builder().url(UPDATE_AVAILABILITY_URL).post(requestBody).build();

                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            isSuccessful=false;
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            isSuccessful=true;

                            Log.v(TAG,response.body().string());
                        }
                    });


                    if(isSuccessful)
                    {

                        if(isChecked){

                            mMenuAvailabilityTextView.setText("Available");
                            mMenu.setAvailable(isChecked);
                        }else{
                            mMenuAvailabilityTextView.setText("Unavailable");
                            mMenu.setAvailable(isChecked);
                        }
                    }


                }
            });

            if(mMenu.getPictureUrl()!=null){

                if(!mMenu.getPictureUrl().isEmpty())
                {
                    Log.v(TAG,"Picture url = "+mMenu.getPictureUrl());
                    Picasso.with(getActivity()).load(menu.getPictureUrl()).fit().centerCrop().into(mMenuImageView);
                }

            }



        }


        @Override
        public void onClick(View v) {
            Intent intent = RestaurantEditMenuActivity.newIntent(getActivity(),mMenu.getMenuId());
            startActivityForResult(intent,REQUEST_DATA_CHANGED);
        }


    }



}
