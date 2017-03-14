package com.android.dishpatch.dishpatch.Controller.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.dishpatch.dishpatch.Model.Restaurant;
import com.android.dishpatch.dishpatch.R;

/**
 * Created by Lenovo on 2/26/2016.
 */
public class SubmitFragment extends Fragment {

    public static final String ARGS_RESTAURANT="ARGS_RESTAURANT";
    private Restaurant mRestaurant;

    private TextView mOrderConfirmationTextView;
    private TextView mOkButton;

    public static SubmitFragment newInstance(Restaurant restaurant)
    {
        Bundle args = new Bundle();
        SubmitFragment fragment = new SubmitFragment();
        args.putParcelable(ARGS_RESTAURANT,restaurant);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mRestaurant = getArguments().getParcelable(ARGS_RESTAURANT);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_submit, container, false);

        mOrderConfirmationTextView = (TextView) v.findViewById(R.id.order_confirmation_text_view);

        String confirmString = getString(R.string.order_confirmation,mRestaurant.getName());
        mOrderConfirmationTextView.setText(confirmString);

        mOkButton = (TextView) v.findViewById(R.id.confirm_button);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });


        return v;
    }
}
