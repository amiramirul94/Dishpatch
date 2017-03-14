package com.android.dishpatch.dishpatch.Controller.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.dishpatch.dishpatch.ui.Activity.DispatchMapActivity;
import com.android.dishpatch.dishpatch.Model.Order;
import com.android.dishpatch.dishpatch.R;


public class DispatchOrderStatusFragment extends Fragment {

    private static final String DISPATCH_ORDER_ARGS = "DISPATCH_ORDER_ARGS";
    private static final String TAG = "DispatchOrderStatus";
    private static final int REQUEST_DISPATCH = 1;

    private Order mOrder;

    private TextView customerIdTextView;
    private TextView orderStatusTextView;
    private TextView destinationOrderTextView;
    private TextView pickupLocationInfo;
    private TextView amountForwardedInfoTextView;
    private TextView earningsTextView;
    private Button mMapButton;

    public static DispatchOrderStatusFragment newInstance(Order order){

        Bundle args = new Bundle();
        args.putParcelable(DISPATCH_ORDER_ARGS,order);

        DispatchOrderStatusFragment fragment = new DispatchOrderStatusFragment();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOrder = getArguments().getParcelable(DISPATCH_ORDER_ARGS);
        //Log.d(TAG,mDispatch.getDestination());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dispatch_order_status,container,false);

        customerIdTextView = (TextView) v.findViewById(R.id.customer_name);
        orderStatusTextView = (TextView) v.findViewById(R.id.order_status);
        destinationOrderTextView = (TextView) v.findViewById(R.id.destination_info);
        pickupLocationInfo = (TextView) v.findViewById(R.id.pickup_location_info);
        amountForwardedInfoTextView = (TextView) v.findViewById(R.id.amount_forwarded_info);
        earningsTextView = (TextView) v.findViewById(R.id.earnings_info);

        customerIdTextView.setText(mOrder.getCustomer().getName());
        orderStatusTextView.setText(mOrder.getTrack().getStatus());
        destinationOrderTextView.setText(mOrder.getLocation());
        pickupLocationInfo.setText(mOrder.getRestaurant().getName());
        String forward = String.format("RM %.2f",mOrder.getMenu().getPrice());
        amountForwardedInfoTextView.setText(forward);
        String earn = String.format("RM %.2f", 2.00);
        earningsTextView.setText(earn);

        mMapButton = (Button) v.findViewById(R.id.view_map_button);

        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = DispatchMapActivity.newIntent(getActivity(),mOrder);

                startActivityForResult(i,REQUEST_DISPATCH);
            }
        });


        return v;
    }


}
