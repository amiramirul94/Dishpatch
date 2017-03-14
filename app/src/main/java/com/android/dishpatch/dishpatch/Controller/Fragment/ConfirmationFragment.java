package com.android.dishpatch.dishpatch.Controller.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dishpatch.dishpatch.Controller.SharedPreferences.DishpatchPreferences;
import com.android.dishpatch.dishpatch.Model.Order;
import com.android.dishpatch.dishpatch.Service.SubmitOrderService;
import com.android.dishpatch.dishpatch.ui.Activity.SubmitActivity;
import com.android.dishpatch.dishpatch.Model.Menu;
import com.android.dishpatch.dishpatch.Model.Restaurant;
import com.android.dishpatch.dishpatch.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lenovo on 2/23/2016.
 */
public class ConfirmationFragment extends Fragment {

    private static final String RESTAURANT_ARGS = "RESTAURANT_ARGS";
    private static final String ORDER_LIST_ARGS = "ORDER_LIST_ARGS";
    private static final String TAG = "ConfirmationFragment";

    private RecyclerView mRecyclerView;
    private TextView mTotalPriceTextView;
    private Button mSubmitButton;

    private ConfirmationAdapter mAdapter;
    private Restaurant mRestaurant;
    private List<Menu> mMenuList;
    private List<Order> mOrderList = new ArrayList<>();

    public static ConfirmationFragment newInstance(Restaurant restaurant,List<Menu> orderlist)
    {
        Bundle args = new Bundle();
        args.putParcelable(RESTAURANT_ARGS,restaurant);
        args.putParcelableArrayList(ORDER_LIST_ARGS, (ArrayList<? extends Parcelable>) orderlist);


        ConfirmationFragment fragment = new ConfirmationFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mRestaurant = getArguments().getParcelable(RESTAURANT_ARGS);
        mMenuList = getArguments().getParcelableArrayList(ORDER_LIST_ARGS);
        Log.i(TAG, mMenuList.toString()+" Have been received");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_confirmation,container,false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.order_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTotalPriceTextView = (TextView) v.findViewById(R.id.total_price_text_view);
        mSubmitButton = (Button) v.findViewById(R.id.submit_order_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = SubmitActivity.newIntent(getActivity(),mRestaurant);

                JSONObject mainBody = new JSONObject();
                JSONArray menuArray = new JSONArray();
                try {
                mainBody.put("customer_id", DishpatchPreferences.getUserId(getActivity())+"");
                mainBody.put("restaurant_id",mRestaurant.getId()+"");

                    Date date = new Date();
                    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    String formattedDate = formatDate.format(date);

                    mainBody.put("date_time",formattedDate);

                    int counter=0;
                for(Order order:mOrderList)
                {

                    JSONObject menuObject = new JSONObject();

                    menuObject.put("menu_id",order.getMenu().getMenuId()+"");
                    menuObject.put("quantity",order.getQuantity());
                    menuObject.put("remarks",order.getRemarks()+"");

                    if(order.getRemarks()!=null)
                    {
                        Log.v(TAG,order.getRemarks());

                    }
                    Log.v(TAG,mOrderList.size()+"");
                    menuArray.put(counter,menuObject);
                        counter++;
                }


                    mainBody.put("orders",menuArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.v(TAG,mainBody.toString());

                //startActivity(i);

                //getActivity().finish();

                submitOrder(mainBody);
            }
        });



        updateUI();

        return v;
    }

    private void submitOrder(JSONObject menuObject){



        Intent i = SubmitOrderService.newIntent(getActivity(),menuObject);
        getActivity().startService(i);

    }



    private void updateUI()
    {
        float totalPrice=0;

        for(int i = 0; i< mMenuList.size(); i++)
        {
            totalPrice+= mMenuList.get(i).getPrice();
        }

        mAdapter = new ConfirmationAdapter(mMenuList);
        mRecyclerView.setAdapter(mAdapter);

        mTotalPriceTextView.setText("RM "+totalPrice);
    }

    private class ConfirmationAdapter extends RecyclerView.Adapter<ConfirmationHolder>
    {
        private List<Menu> mFoodList;

        public ConfirmationAdapter(List<Menu> order)
        {
            mFoodList = order;
        }

        @Override
        public ConfirmationHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.order_list_item,parent,false);

            return new ConfirmationHolder(v);
        }

        @Override
        public void onBindViewHolder(ConfirmationHolder holder, int position) {
            Menu menu = mFoodList.get(position);
            holder.bindOrder(menu);
        }

        @Override
        public int getItemCount() {
            return mFoodList.size();
        }
    }

    private class ConfirmationHolder extends RecyclerView.ViewHolder{
        private TextView mFoodNameTextView;
        private TextView mFoodPriceTextView;
        private ImageView mFoodImageView;
        private ImageButton mDeleteOrderImageButton;
        private ImageButton mDecreaseQuantityImageButton;
        private ImageButton mIncreaseQuantityImageButton;
        private TextView mQuantityTextView;
        private EditText mRemarksEditText;
        private int quantity = 1;

        private Menu mMenu;
        private Order mOrder;
        public ConfirmationHolder(View itemView)
        {
            super(itemView);

            mFoodNameTextView = (TextView) itemView.findViewById(R.id.order_name_text_view);
            mFoodPriceTextView = (TextView) itemView.findViewById(R.id.order_price_text_view);
            mFoodImageView = (ImageView) itemView.findViewById(R.id.order_food_image_view);
            mDeleteOrderImageButton = (ImageButton) itemView.findViewById(R.id.delete_order_image_button);
            mDecreaseQuantityImageButton = (ImageButton) itemView.findViewById(R.id.decrease_quantity_image_button);
            mIncreaseQuantityImageButton = (ImageButton) itemView.findViewById(R.id.increase_quantity_image_button);
            mQuantityTextView = (TextView) itemView.findViewById(R.id.quantity_text_view);
            mRemarksEditText = (EditText) itemView.findViewById(R.id.remarks_edit_text);

            mDeleteOrderImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenuList.remove(mMenu);
                    mOrderList.remove(mOrder);
                    int i= getAdapterPosition();

                    mAdapter.notifyItemChanged(i);

                    //updateUI();
                }
            });

            if(quantity==1)
            {
                mDecreaseQuantityImageButton.setEnabled(false);
                mIncreaseQuantityImageButton.setEnabled(true);
            }else if(quantity>1&&quantity<10)
            {

            }else if(quantity==10){
                mIncreaseQuantityImageButton.setEnabled(false);
                mDecreaseQuantityImageButton.setEnabled(true);
            }


            mIncreaseQuantityImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(quantity<10&&quantity>=1)
                    {
                        quantity++;
                        mDecreaseQuantityImageButton.setEnabled(true);
                        mIncreaseQuantityImageButton.setEnabled(true);
                    }else if(quantity==10){
                        mIncreaseQuantityImageButton.setEnabled(false);
                        mDecreaseQuantityImageButton.setEnabled(true);

                    }
                    mQuantityTextView.setText(quantity+"");
                    mOrder.setQuantity(quantity);

                }
            });


            mDecreaseQuantityImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(quantity>1&&quantity<=10)
                    {
                        quantity--;
                        mDecreaseQuantityImageButton.setEnabled(true);

                        mIncreaseQuantityImageButton.setEnabled(true);
                    }else{
                        mDecreaseQuantityImageButton.setEnabled(false);

                    }
                    mQuantityTextView.setText(quantity+"");
                    mOrder.setQuantity(quantity);

                }
            });

            mRemarksEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mOrder.setRemarks(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }

        private void bindOrder(Menu menu)
        {
            mMenu = menu;

            mOrder = new Order(mMenu,quantity);

            mOrderList.add(mOrder);
            mFoodNameTextView.setText(mMenu.getFood());
            String priceText = String.format("RM%.2f",mMenu.getPrice());
            mFoodPriceTextView.setText(priceText);

            if(mMenu.getPictureUrl()!=null&&(!mMenu.getPictureUrl().isEmpty()))
            {
                Picasso.with(getActivity()).load(mMenu.getPictureUrl()).fit().centerCrop().into(mFoodImageView);
            }
        }
    }



}
