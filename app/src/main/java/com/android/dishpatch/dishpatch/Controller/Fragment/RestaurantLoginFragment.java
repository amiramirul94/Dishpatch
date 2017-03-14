package com.android.dishpatch.dishpatch.Controller.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dishpatch.dishpatch.Controller.SharedPreferences.DishpatchPreferences;
import com.android.dishpatch.dishpatch.Util;
import com.android.dishpatch.dishpatch.ui.Activity.RestaurantActivity;
import com.android.dishpatch.dishpatch.ui.Activity.RestaurantRegistrationActivity;
import com.android.dishpatch.dishpatch.R;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lenovo on 6/26/2016.
 */
public class RestaurantLoginFragment extends Fragment {

    private static final String restaurant_login_url = "http://insvite.com/php/restaurant_login.php";
    private static final String TAG = RestaurantLoginFragment.class.getSimpleName();

    @Email
    private EditText mRestaurantEmailEditText;

    @Password(min=6, scheme = Password.Scheme.ANY )
    private EditText mPasswordEditText;

    private Button mLoginButton;
    private TextView mRegisterTextView;

    private Validator mValidator;

    public static RestaurantLoginFragment newInstance()
    {
        return new RestaurantLoginFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mValidator = new Validator(this);
        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                login();
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_restaurant_login,container,false);

        mRestaurantEmailEditText = (EditText) v.findViewById(R.id.restaurant_email_edit_text);
        mPasswordEditText = (EditText) v.findViewById(R.id.password_edit_text);
        mLoginButton = (Button) v.findViewById(R.id.login_button);
        mRegisterTextView = (TextView) v.findViewById(R.id.register_text_view);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Util.isNetworkAvailable(getActivity()))
                {
                    mValidator.validate();

                }else{
                    Toast.makeText(getActivity(),R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                }
//                Intent i = RestaurantActivity.newIntent(v.getContext());
//                startActivity(i);
            }
        });

        mRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = RestaurantRegistrationActivity.newIntent(v.getContext());
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                getActivity().finish();
            }
        });
        return v;
    }


    private void login()
    {
        //receive values
        String email = mRestaurantEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("email",email)
                .add("password",password)
                .build();

        Request request =  new Request.Builder().url(restaurant_login_url).post(requestBody).build();
        Call call = client.newCall(request);


        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v(TAG,"Connection failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if(response.isSuccessful())
                    {
                        String json = response.body().string();
                        Log.v(TAG,"OK");
                        Log.v(TAG,json);
                        JSONObject jsonBody = new JSONObject(json);
                        verifyLogin(jsonBody);
                    }else{
                        Log.v(TAG,"Failed");
                    }
                } catch (IOException e) {
                    Log.e(TAG,"Exception caught ",e);
                }catch (JSONException e){
                    Log.e(TAG,"Invalid json",e);
                }
            }
        });
    }

    private void verifyLogin(JSONObject jsonBody) throws JSONException {

        Boolean isSuccesful = jsonBody.getBoolean("status");
        String name;
        int id;

        if(isSuccesful)
        {
            name= jsonBody.getString("name");
            id = Integer.parseInt(jsonBody.getString("id"));
            Log.v(TAG,name);
            Intent i = RestaurantActivity.newIntent(getActivity());
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            DishpatchPreferences.setPrefRestaurantLogin(getActivity(),true);
            DishpatchPreferences.setPrefUserId(getActivity(),id);
            startActivity(i);
            getActivity().finish();
        }else{
            Toast.makeText(getActivity(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
        }
    }
}
