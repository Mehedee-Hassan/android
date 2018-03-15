package com.wecloud.android.authentication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.owncloud.android.R;


public class ForgotPasswordActivity extends Activity {

   Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        context = this;


    }


}
