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

import com.android.dishpatch.dishpatch.Service.DispatchLocationServices;
import com.android.dishpatch.dishpatch.ui.Activity.DispatchOrderStatusActivity;
import com.android.dishpatch.dishpatch.Model.DishpatchCentral;
import com.android.dishpatch.dishpatch.Model.Order;
import com.android.dishpatch.dishpatch.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 3/18/2016.
 */
public class DispatchListFragment extends Fragment {


    private List<Order> mOrderList=new ArrayList<>();
    private RecyclerView mRecyclerView;
    private DispatchAdapter mDispatchAdapter;

    public static DispatchListFragment newInstance()
    {
        return new DispatchListFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for(int i=0; i<10; i++)
        {
            Order order = new Order();

            mOrderList.add(order);


        }


        DishpatchCentral.get(getActivity()).setDispatchOrders(mOrderList);


    }

    @Override
    public void onResume() {
        super.onResume();

        Intent i = DispatchLocationServices.newIntent(getActivity());
        getActivity().startService(i);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dispatch_list,container,false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.dispatch_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return v;
    }
    private void updateUI()
    {
        mDispatchAdapter = new DispatchAdapter(mOrderList);
        mRecyclerView.setAdapter(mDispatchAdapter);
    }

    private class DispatchAdapter extends RecyclerView.Adapter<DispatchList>{

        private List<Order> mDispatches= new ArrayList<>();

        public DispatchAdapter(List<Order> d)
        {
            mDispatches = d;
        }

        @Override
        public DispatchList onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.dispatch_order_item_layout,parent,false);

            return new DispatchList(v);
        }

        @Override
        public void onBindViewHolder(DispatchList holder, int position) {

            Order order = mDispatches.get(position);
            holder.bindDispatch(order);


        }

        @Override
        public int getItemCount() {
            return mDispatches.size();
        }
    }

    private class DispatchList extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mDestinationTextView;
        private TextView mPickupTextView;
        private TextView mStatusTextView;
        private Order mOrder;

        public DispatchList(View itemView)
        {
            super(itemView);

            mDestinationTextView = (TextView) itemView.findViewById(R.id.delivery_destination);
            mPickupTextView = (TextView) itemView.findViewById(R.id.pickup_location);
            mStatusTextView = (TextView) itemView.findViewById(R.id.status);
            itemView.setOnClickListener(this);
        }

        public void bindDispatch(Order order)
        {
            mOrder = order;
            mDestinationTextView.setText(mOrder.getCustomer().getName());
            mPickupTextView.setText(mOrder.getRestaurant().getName());
            mStatusTextView.setText(mOrder.getTrack().getStatus());
        }


        @Override
        public void onClick(View v) {

            DishpatchCentral.get(getActivity()).putValue("ORDER_DATA",mOrder);
            Intent i = DispatchOrderStatusActivity.newIntent(getActivity(),mOrder);
            startActivity(i);
        }
    }

}
