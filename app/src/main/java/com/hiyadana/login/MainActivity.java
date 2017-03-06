package com.hiyadana.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hiyadana.login.model.Response;
import com.hiyadana.login.model.User;
import com.hiyadana.login.network.NetworkUtil;
import com.hiyadana.login.utils.Constants;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.hiyadana.login.utils.Validation.validateEmail;
import static com.hiyadana.login.utils.Validation.validateField;
import static com.hiyadana.login.utils.Validation.validatePassword;

public class MainActivity extends AppCompatActivity {
    final static public String TAG = AppCompatActivity.class.getSimpleName();

    private NumberPicker mNp;
    private Button mButtonRegister;
    private Button mButtonLogin;
    private Button mButtonLogout;

    private String mLoginNickname;
    private String mLoginPassword;

    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(mSharedPreferences.getString(Constants.NICKNAME,"noname") == "noname") {
            setContentView(R.layout.activity_main);
            mButtonRegister = (Button)findViewById(R.id.btn_register);
            mButtonLogin = (Button)findViewById(R.id.btn_login);
            initLoginView();
            mButtonRegister.setOnClickListener(v -> onRegister());
            mButtonLogin.setOnClickListener(v -> onLogin());
        }
        else {
            setContentView(R.layout.activity_home);
            mButtonLogout = (Button)findViewById(R.id.btn_logout);
            mButtonLogout.setOnClickListener(v -> onLogout());
        }

        mSubscriptions = new CompositeSubscription();
        //mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity()); // on the fragment
    }
    private void initLoginView() {
        mNp = (NumberPicker)findViewById(R.id.np_birth);
        mNp.setMinValue(1900);
        mNp.setMaxValue(2017);
        mNp.setValue(2000);
    }
    private void onRegister() {
        String mNickName = ((EditText)findViewById(R.id.et_nickname)).getText().toString();
        String mPassword1 = ((EditText)findViewById(R.id.et_pw_1)).getText().toString();
        String mPassword2 = ((EditText)findViewById(R.id.et_pw_2)).getText().toString();
        String mEmail = ((EditText)findViewById(R.id.et_email)).getText().toString();
        String mGender;
        int mBirth;
        String[] mExtra = ((EditText)findViewById(R.id.et_extra)).getText().toString().split("\\s+");
        RadioGroup rg = (RadioGroup)findViewById(R.id.radiogroup_gender);
        RadioButton rb = (RadioButton)findViewById(rg.getCheckedRadioButtonId());
        int tag = Integer.parseInt(rb.getTag().toString());
        if(tag == 1)
            mGender = "man";
        else if(tag == 2)
            mGender = "woman";
        else
            mGender = "animal";

        mBirth = mNp.getValue();

        Log.d(TAG, mNickName+"/"+mPassword1+"/"+mPassword2+"/"+mEmail+"/"+mGender+"/"+mBirth);
        for(int i = 0; i<mExtra.length;i++)
            Log.d(TAG, "Extra["+i+"] : " + mExtra[i]);

        int err=0;
        String e = "";
        if(!validateField(mNickName)) {
            err++;
            e = "nickname error";
        }
        if(!validatePassword(mPassword1, mPassword2)) {
            err++;
            e = "password error";
        }
        if(!validateEmail(mEmail)) {
            err++;
            e = "email error";
        }

        if(err == 0) {
            User user = new User();
            user.setNickname(mNickName);
            user.setPassword(mPassword1);
            user.setEmail(mEmail);
            user.setGender(mGender);
            user.setBirthYear(mBirth);
            user.setExtra(mExtra);
            registerProcess(user);
        } else {
            Toast.makeText(this, e, Toast.LENGTH_SHORT).show();
        }
    }

    private void registerProcess(User user) {
        mSubscriptions.add(NetworkUtil.getRetrofit().register(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleRegisterResponse, this::handleError));
    }

    private void handleRegisterResponse(Response response) {
        Log.d(TAG, response.getMessage());
        Toast.makeText(this, response.getMessage(),Toast.LENGTH_SHORT).show();
    }

    private void handleError(Throwable error) {
        if (error instanceof HttpException) {
            Gson gson = new GsonBuilder().create();
            try {
                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                Log.d(TAG, response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG,"Network Error!"+error.getMessage());
            error.printStackTrace();
        }
    }

    private void onLogin() {
        mLoginNickname = ((EditText)findViewById(R.id.et_login_nickname)).getText().toString();
        mLoginPassword = ((EditText)findViewById(R.id.et_login_password)).getText().toString();

        int err = 0;
        String errText = "";
        if(!validateField(mLoginNickname)) {
            err++;
            errText = "nickname error";
        }
        if(!validateField(mLoginPassword)) {
            err++;
            errText = "password error";
        }

        if(err == 0)
            loginProcess(mLoginNickname, mLoginPassword);
    }

    private void loginProcess(String nickname,String password) {
        mSubscriptions.add(NetworkUtil.getRetrofit(nickname,password).login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleLoginResponse, this::handleError));
    }

    private void handleLoginResponse(Response response) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.TOKEN,response.getToken());
        editor.putString(Constants.NICKNAME,response.getMessage());
        editor.apply();
        Log.d(TAG, "token: " + response.getToken());
        Log.d(TAG, "nickname: " + response.getMessage());
        ((EditText)findViewById(R.id.et_login_nickname)).setText(null);
        ((EditText)findViewById(R.id.et_login_password)).setText(null);

        //Intent intent = new Intent(getActivity(), ProfileActivity.class);
        //startActivity(intent);
    }
    private void onLogout() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(Constants.TOKEN);
        editor.remove(Constants.NICKNAME);
        editor.commit();
    }
}
