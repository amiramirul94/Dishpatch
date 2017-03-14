package com.android.dishpatch.dishpatch.Controller.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.dishpatch.dishpatch.Model.Order;
import com.android.dishpatch.dishpatch.R;

import java.util.Date;

/**
 * Created by Lenovo on 4/27/2016.
 */
public class RestaurantManageOrderFragment extends Fragment {


    private static final String ORDER_ARGS = "ORDER_ARGS";

    private TextView customerNameTextView;
    private TextView itemDescTextView;
    private TextView priceDescTextView;
    private TextView timeElapsedTextView;
    private TextView progressTextView;

    private ImageButton reverseButton;
    private ImageButton forwardButton;

    private String[] progress = {"cooking","ready for delivery","delivered"};
    private static int currentProgress = 0;

    private Order mOrder;

    public static RestaurantManageOrderFragment newInstance(Order order)
    {
        Bundle args = new Bundle();
        args.putParcelable(ORDER_ARGS,order);

        RestaurantManageOrderFragment fragment = new RestaurantManageOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_restaurant_manage_order,container,false);
        mOrder = (Order) getArguments().get(ORDER_ARGS);
        Date date = new Date();


        customerNameTextView = (TextView) v.findViewById(R.id.customer_name);
        itemDescTextView = (TextView) v.findViewById(R.id.item_desc_text_view);
        priceDescTextView = (TextView) v.findViewById(R.id.price_desc_text_view);
        timeElapsedTextView = (TextView) v.findViewById(R.id.time_elapsed_desc_text_view);
        progressTextView = (TextView) v.findViewById(R.id.progress_text_view);
        reverseButton = (ImageButton) v.findViewById(R.id.reverse_progress_button);
        forwardButton = (ImageButton) v.findViewById(R.id.forward_progress_button);

        customerNameTextView.setText(mOrder.getCustomer().getName());
        itemDescTextView.setText(mOrder.getMenu().getFood());
        String price = String.format("RM %.2f",mOrder.getMenu().getPrice());
        priceDescTextView.setText(price);
        timeElapsedTextView.setText("2 minutes");

        progressTextView.setText(mOrder.getTrack().getStatus());



        if(currentProgress==0)
        {
            reverseButton.setEnabled(false);
        }else if(currentProgress==(progress.length-1))
        {
            forwardButton.setEnabled(false);
        }

        reverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentProgress--;
                updateProgress();
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentProgress++;
                updateProgress();
            }
        });





        return v;
    }

    private void updateProgress()
    {
        if(currentProgress==0)
        {
            reverseButton.setEnabled(false);
        }else if(currentProgress==(progress.length-1))
        {
            forwardButton.setEnabled(false);
        }else {

            reverseButton.setEnabled(true);
            forwardButton.setEnabled(true);
        }

        mOrder.getTrack().setStatus(progress[currentProgress]);
        progressTextView.setText(progress[currentProgress]);
    }





}
