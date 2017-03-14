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

import com.android.dishpatch.dishpatch.ui.Activity.TrackInfoActivity;
import com.android.dishpatch.dishpatch.Model.Order;
import com.android.dishpatch.dishpatch.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Lenovo on 2/28/2016.
 */
public class TrackFragment extends Fragment {

    private List<Order> mOrderList = new ArrayList<>();
    private RecyclerView mOrderListRecyclerView;
    private TrackListAdapter mAdapter;


    public static TrackFragment newInstance()
    {
        return new TrackFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOrderList.add(new Order());
        mOrderList.add(new Order());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_list_recycler_view,container,false);

        mOrderListRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mOrderListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        return v;
    }

    private void updateUI()
    {
        mAdapter = new TrackListAdapter(mOrderList);
        mOrderListRecyclerView.setAdapter(mAdapter);
    }

    private class TrackListAdapter extends RecyclerView.Adapter<TrackListViewHolder>{

        private List<Order> mTrackList= new ArrayList<>();
        public TrackListAdapter(List<Order> trackList)
        {
            mTrackList = trackList;
        }
        @Override
        public TrackListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            View v = inflater.inflate(R.layout.track_list_item,parent,false);

            return new TrackListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TrackListViewHolder holder, int position) {

            holder.bindTrackList(mTrackList.get(position));
        }

        @Override
        public int getItemCount() {
            return mTrackList.size();
        }
    }


    private class TrackListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mOrderDescTextView;
        private TextView mStatusTextView;
        private TextView mDateTextView;
        private Order mOrder;

        public TrackListViewHolder(View itemView) {
            super(itemView);

            mOrderDescTextView = (TextView) itemView.findViewById(R.id.order_desc_text_view);
            mStatusTextView = (TextView) itemView.findViewById(R.id.status_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            itemView.setOnClickListener(this);

        }

        public void bindTrackList(Order order)
        {
            String orderText="";
            String dateString;
            mOrder = order;


            SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");

            Date dtrack = mOrder.getTrack().getDate();

            dateString = df.format(dtrack);



                orderText = getString(R.string.track_item_one,mOrder.getMenu().getFood(),mOrder.getRestaurant().getName());


            mOrderDescTextView.setText(orderText);
            mStatusTextView.setText(mOrder.getTrack().getStatus());
            mDateTextView.setText(dateString);


        }

        @Override
        public void onClick(View v) {

            Intent i = TrackInfoActivity.newIntent(getActivity());
            startActivity(i);
        }
    }
}
