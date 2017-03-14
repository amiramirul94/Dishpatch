package com.android.dishpatch.dishpatch.Controller.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dishpatch.dishpatch.Model.DishpatchCentral;
import com.android.dishpatch.dishpatch.ui.Activity.ConfirmationActivity;
import com.android.dishpatch.dishpatch.Model.Menu;
import com.android.dishpatch.dishpatch.Model.Restaurant;
import com.android.dishpatch.dishpatch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2/17/2016.
 */
public class SelectFoodFragment extends Fragment {

    private static final String ARG_RESTAURANT = "RESTAURANT";
    private static final String TAG = "SelectFoodFragment";
    public static final int REQUEST_RESTAURANT = 0;

    private RecyclerView mRecyclerView;
    private SelectFoodAdapter mAdapter;
    private Restaurant mRestaurant;
    private List<Menu> mOrderList = new ArrayList<>();
    private List<Menu>  mMenuList = new ArrayList<>();
    private Button mDoneOrderButton;
    private String mSubtitle;


    public static SelectFoodFragment newInstance(Restaurant restaurant)
    {
        Bundle args = new Bundle();
        SelectFoodFragment fragment = new SelectFoodFragment();
        args.putParcelable(ARG_RESTAURANT, restaurant);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mRestaurant =  getArguments().getParcelable(ARG_RESTAURANT);

        updateSubtitle();
        new GetMenuTask().execute();


    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_select_food_list,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.select_food_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mDoneOrderButton = (Button) v.findViewById(R.id.done_order_button);
        mDoneOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOrderList.isEmpty())
                {
                    Toast.makeText(getActivity(),"Please select at least one food",Toast.LENGTH_LONG).show();
                }else{

                    Intent i= ConfirmationActivity.newIntent(getActivity(),mRestaurant,mOrderList);
                    startActivityForResult(i,REQUEST_RESTAURANT);
                    getActivity().finish();
                }
            }
        });

        return v;
    }

    private void updateUi()
    {
        mMenuList = DishpatchCentral.get(getActivity()).getCustomerMenuList();
        mAdapter = new SelectFoodAdapter(mMenuList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateSubtitle()
    {
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if(mOrderList.size()<=1)
        {
            mSubtitle = getString(R.string.subtitle_num_order_single,mOrderList.size());
        }else if(mOrderList.size()>1)
        {
            mSubtitle = getString(R.string.subtitle_num_order_multiple,mOrderList.size());
        }

        activity.getSupportActionBar().setSubtitle(mSubtitle);


    }

    private class GetMenuTask extends AsyncTask<Void,Void,List<Menu>>{

        @Override
        protected List<Menu> doInBackground(Void... params) {

            DishpatchCentral.get(getActivity()).createCustomerMenuList(mRestaurant.getId());
            return null;
        }

        @Override
        protected void onPostExecute(List<Menu> menus) {
            super.onPostExecute(menus);
            updateUi();
        }
    }

    private class SelectFoodAdapter extends RecyclerView.Adapter<SelectFoodViewHolder>
    {
        private List<Menu> mMenus;

        public SelectFoodAdapter(List<Menu> menu)
        {
            mMenus = menu;
        }

        @Override
        public SelectFoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.select_food_list_item,parent,false);

            return new SelectFoodViewHolder(v);
        }

        @Override
        public void onBindViewHolder(SelectFoodViewHolder holder, int position) {
            Menu menu = mMenus.get(position);
            holder.bindMenu(menu);
        }

        @Override
        public int getItemCount() {
            return mMenus.size();
        }
    }

    private class SelectFoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView mFoodNameTextView;
        private TextView mPriceTextView;
        private LinearLayout mMainLinearLayout;
        private ImageView mFoodImageView;
        private Menu mMenu;

        public SelectFoodViewHolder(View itemView)
        {
            super(itemView);

            mFoodNameTextView = (TextView) itemView.findViewById(R.id.food_name_text_view);
            mPriceTextView = (TextView) itemView.findViewById(R.id.food_price_text_view);
            mMainLinearLayout = (LinearLayout) itemView.findViewById(R.id.container_food_list_linear_layout);
            mFoodImageView = (ImageView) itemView.findViewById(R.id.food_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindMenu(Menu menu)
        {
            mMenu = menu;



            mFoodNameTextView.setText(mMenu.getFood());
            String priceString = String.format("RM %.2f",mMenu.getPrice());
            mPriceTextView.setText(priceString);

            Uri uri = Uri.parse(mMenu.getPictureUrl());

            if(uri!=null)
            {
                Picasso.with(getActivity()).load(uri).centerCrop().fit().into(mFoodImageView);
            }
            if(mOrderList.contains(mMenu))
            {
                mMainLinearLayout.setBackgroundResource(R.drawable.box_container_background_selected);
            }else{

                mMainLinearLayout.setBackgroundResource(R.drawable.box_container_background_normal);
            }

        }


        @Override
        public void onClick(View v) {

            //if the menu is in the order list remove and change the background to normal
            if(mOrderList.contains(mMenu)){
                mOrderList.remove(mMenu);
                mMainLinearLayout.setBackgroundResource(R.drawable.box_container_background_normal);
                updateSubtitle();
            }else {
                //if does not contain, add the menu in the order list and change background to selected
                mOrderList.add(mMenu);
                mMainLinearLayout.setBackgroundResource(R.drawable.box_container_background_selected);
                updateSubtitle();
            }

        }
    }





}
