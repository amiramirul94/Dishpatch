package com.android.dishpatch.dishpatch.Controller.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.dishpatch.dishpatch.ui.Activity.DispatchListActivity;
import com.android.dishpatch.dishpatch.R;

/**
 * Created by Lenovo on 3/9/2016.
 */
public class MainDispatchFragment extends Fragment {

    private Button mAcceptDispatchButton;

    public static MainDispatchFragment newInstance()
    {
        return new MainDispatchFragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.main_dispatch_fragment,container,false);
        mAcceptDispatchButton = (Button) v.findViewById(R.id.accept_dispatch);

        mAcceptDispatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = DispatchListActivity.newIntent(getActivity());
                startActivity(i);

            }
        });
        return v;
    }
}
