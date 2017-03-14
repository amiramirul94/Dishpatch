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

import com.android.dishpatch.dishpatch.Util;
import com.android.dishpatch.dishpatch.ui.Activity.CustomerActivity;
import com.android.dishpatch.dishpatch.ui.Activity.OrderActivity;
import com.android.dishpatch.dishpatch.ui.Activity.UserRegistrationActivity;
import com.android.dishpatch.dishpatch.Controller.SharedPreferences.DishpatchPreferences;
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
public class UserLoginFragment extends Fragment {

    private static final String TAG = UserLoginFragment.class.toString() ;

    @Email
    private EditText mEmailEditText;


    @Password(min = 6,scheme = Password.Scheme.ANY)
    private EditText mPasswordEditText;

    private Button mLoginButton;
    private TextView mRegisterTextView;


    private Validator validator;
    private String login_url = "http://insvite.com/php/user_login.php";

    public static UserLoginFragment newInstance()
    {
        return new UserLoginFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        validator = new Validator(this);
        validator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                Log.v(TAG,"Validated");
                login();




            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                Log.v(TAG,"Vaidation failed");
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login_user,container, false);

        mEmailEditText = (EditText) v.findViewById(R.id.username_edit_text);
        mPasswordEditText = (EditText) v.findViewById(R.id.password_edit_text);
        mLoginButton = (Button) v.findViewById(R.id.login_button);
        mRegisterTextView = (TextView) v.findViewById(R.id.register_text_view);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Util.isNetworkAvailable(getActivity()))
                {
                    validator.validate();

                }else {
                    Toast.makeText(getActivity(),R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                }

            }
        });

        mRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = UserRegistrationActivity.newIntent(v.getContext());
                startActivity(i);
            }
        });




        return v;

    }

    private void login()
    {
        //receive values
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("email",email)
                .add("password",password)
                .build();

        Request request =  new Request.Builder().url(login_url).post(requestBody).build();
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


    private void verifyLogin(JSONObject jsonBody) throws JSONException
    {

        Boolean isSuccesful = jsonBody.getBoolean("status");
        int id;

        if(isSuccesful)
        {
            id= Integer.parseInt(jsonBody.getString("id"));
            Log.v(TAG,id+"");
            Intent i = CustomerActivity.newIntent(getActivity());
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            DishpatchPreferences.setUserLoggedIn(getActivity(),true);
            DishpatchPreferences.setPrefUserId(getActivity(),id);

            startActivity(i);
            getActivity().finish();
        }else{
            Toast.makeText(getActivity(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
        }
    }



}
