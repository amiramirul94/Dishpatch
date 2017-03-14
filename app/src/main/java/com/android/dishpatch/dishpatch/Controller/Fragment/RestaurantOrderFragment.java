package com.android.dishpatch.dishpatch.Controller.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.dishpatch.dishpatch.ui.Activity.RestaurantManageOrderActivity;
import com.android.dishpatch.dishpatch.Model.DishpatchCentral;
import com.android.dishpatch.dishpatch.Model.Order;
import com.android.dishpatch.dishpatch.R;
import com.android.dishpatch.dishpatch.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 4/23/2016.
 */
public class RestaurantOrderFragment extends Fragment {

    private List<Order> mOrders = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private OrderListAdapter mOrderListAdapter;

    public static RestaurantOrderFragment newInstance()
    {
        return new RestaurantOrderFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for(int i=0; i<10; i++)
        {
            Order order = new Order();
            mOrders.add(order);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_restaurant_order,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.restaurant_order_recycler_view);


        updateUI();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


    }

    private void updateUI()
    {
        mOrderListAdapter = new OrderListAdapter();
        mRecyclerView.setAdapter(mOrderListAdapter);
    }


    private class OrderListAdapter extends RecyclerView.Adapter<OrderViewHolder>{




        @Override
        public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.orders_list_layout,parent,false);
            return new OrderViewHolder(v);
        }

        @Override
        public void onBindViewHolder(OrderViewHolder holder, int position) {

            holder.bindView(mOrders.get(position));
        }

        @Override
        public int getItemCount() {
            return mOrders.size();
        }
    }


    private class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Order mOrder;
        private TextView customerNameTextView;
        private TextView statusTextView;
        private TextView foodItemTextView;

        public OrderViewHolder(View itemView) {
            super(itemView);

            customerNameTextView = (TextView) itemView.findViewById(R.id.customer_name_id);
            statusTextView = (TextView) itemView.findViewById(R.id.status_text_view);
            foodItemTextView = (TextView) itemView.findViewById(R.id.food_item);
            itemView.setOnClickListener(this);
        }

        public void bindView(Order order)
        {
            mOrder = order;

            customerNameTextView.setText(mOrder.getCustomer().getName());
            statusTextView.setText("Cooking");
            foodItemTextView.setText(mOrder.getMenu().getFood());
        }


        @Override
        public void onClick(View v) {

            DishpatchCentral.get(getActivity()).putValue(Util.RESTAURANT_ORDER,mOrder);
            Intent i = RestaurantManageOrderActivity.newIntent(getActivity());
            startActivity(i);
        }
    }
}
