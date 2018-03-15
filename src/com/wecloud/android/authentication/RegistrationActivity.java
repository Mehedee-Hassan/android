package com.wecloud.android.authentication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.owncloud.android.R;
import com.wecloud.android.AddNewUser;
import com.wecloud.android.ValidateUsernameAsyncTask;
import com.wecloud.android.VerifyPhoneAsyncTask_main;

import org.apache.commons.httpclient.HttpException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class RegistrationActivity extends AppCompatActivity {

    Context context;
    EditText usernameET,userpasswordET,userphoneET,useremailET,userFullNameET;
    ImageView tickmark,password_view;
    private Button btn_register,btn_possitive;
    private EditText mUsernameInput;
    private EditText mPasswordInput;
     EditText edt_verification;
    String imei,sim_serial,sim_subscriber_id;
    LinearLayout linlay_verification,linlay_reg_layout;
    Button btn_send;
    private Toolbar mToolbar;

    // add user and phone

    private String imei1, sim_serial1, sim_subscriber_id1, username1, password1, userphone1, useremail1, userfullname1;

    private ProgressDialog progDial;
    private EditText usernameET1, passwordET1;
    private BroadcastReceiver smsReceiver;
    String verification_code = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_dialog);
        context = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.left_back_);
        upArrow.setColorFilter(Color.parseColor("#d2d2d2"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initUI();
    }

//ca201802214731
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initUI() {



        try {
           TelephonyManager telman=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
           imei=telman.getDeviceId();
           sim_serial=telman.getSimSerialNumber();
           sim_subscriber_id=telman.getSubscriberId();
        }catch (SecurityException se){

        }
        linlay_reg_layout = (LinearLayout) findViewById(R.id.linlay_reg_layout);
        linlay_reg_layout.setVisibility(View.VISIBLE);

        linlay_verification = (LinearLayout) findViewById(R.id.linlay_verification);
        linlay_verification.setVisibility(View.GONE);
        edt_verification = (EditText) findViewById(R.id.edt_verification);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aamracloud_add_user_url = context.getString(R.string.url_aamracloud_add_user);
//////
                                        new AddNewUser(context, usernameET, passwordET1)
                                                .execute(aamracloud_add_user_url,imei1, sim_serial1, sim_subscriber_id1, username1, password1, userphone1, useremail1, userfullname1, verification_code);
//
            }
        });
        mUsernameInput = (EditText) findViewById(R.id.account_username);
        mPasswordInput = (EditText) findViewById(R.id.account_password);
        usernameET = (EditText) findViewById(R.id.newusername);
        userpasswordET = (EditText) findViewById(R.id.newuserpassword);
        userphoneET = (EditText) findViewById(R.id.newuserphone);
        useremailET = (EditText) findViewById(R.id.newuseremail);
        userFullNameET = (EditText) findViewById(R.id.newuserfullname);
        tickmark = (ImageView) findViewById(R.id.image_tick);
        password_view = (ImageView) findViewById(R.id.ic_password_view);
        btn_possitive = (Button) findViewById(R.id.btn_possitive);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String verify_phone_url = getString(R.string.url_verify_phone);
                String username = usernameET.getText().toString().trim();
                String userphone = userphoneET.getText().toString().trim();
                String password = userpasswordET.getText().toString().trim();
                String useremail = useremailET.getText().toString().trim();
                String fullname = userFullNameET.getText().toString().trim();

                if (userphone.startsWith("+88")) {
                    userphone = userphone.replace("+88", "");
                }

                if (username.length() > 0 && userphone.length() > 0 && password.length() > 0 && useremail.length() > 0) {

                    if (userphone.startsWith("01") && userphone.length() == 11) {
                        new VerifyPhoneAsyncTask(context, mUsernameInput, mPasswordInput).execute(verify_phone_url, imei, sim_serial, sim_subscriber_id, username, password, userphone, useremail, fullname);

                    } else {
                        AlertDialog invalidPhoneAlert = new AlertDialog.Builder(context).create();
                        invalidPhoneAlert.setMessage("Invalid Phone Number");

                        invalidPhoneAlert.show();
                    }
                } else {
                    AlertDialog emptyFieldsAlert = new AlertDialog.Builder(context).create();
                    emptyFieldsAlert.setMessage("Fields cannot be empty");

                    emptyFieldsAlert.show();
                }

            }
        });

        usernameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String username = usernameET.getText().toString().trim();

                if (!hasFocus && username.length() > 0) {
                    new ValidateUsernameAsyncTask(imei, sim_serial, sim_subscriber_id, username, context, btn_possitive, tickmark)
                            .execute(getString(R.string.url_validate_username));
                } else if (username.length() == 0) {
                    tickmark.setVisibility(View.INVISIBLE);
                }
            }
        });

        password_view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        userpasswordET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        v.setPressed(true);
                        return true;
                    case MotionEvent.ACTION_UP:
                        userpasswordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        v.setPressed(false);
                        return true;
                }
                return false;
            }
        });

        checkRunTimePermission();
    }

    private void checkRunTimePermission() {
        String[] permissionArrays = new String[]{Manifest.permission.RECEIVE_SMS};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 1);
        } else {
            // if already permition granted
            // PUT YOUR ACTION (Like Open cemara etc..)


        }
    }

    public void stopActivity(){
        finish();
    }


    public class VerifyPhoneAsyncTask extends AsyncTask<String, Void, String> {

//        private String imei, sim_serial, sim_subscriber_id, username, password, userphone, useremail, userfullname;
//        private Context context;
//        private ProgressDialog progDial;
        private EditText usernameET, passwordET;
        private BroadcastReceiver smsReceiver;
//
//        String verification_code = "";
        public VerifyPhoneAsyncTask(Context context, EditText usernameET, EditText passwordET) {


            this.usernameET = usernameET;
            this.passwordET = passwordET;

            //   showVerificationDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDial = new ProgressDialog(context);
            progDial.setMessage("Waiting for SMS. Please Wait...");
            progDial.setCancelable(false);
            linlay_reg_layout.setVisibility(View.GONE);
            linlay_verification.setVisibility(View.VISIBLE);
            progDial.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            progDial.show();


            progDial.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    try{
                        context.unregisterReceiver(smsReceiver);
                    }catch(IllegalArgumentException e){
                        e.printStackTrace();
                    }
                }
            });

            smsReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(final Context context, Intent intent) {

                    final Bundle bundle = intent.getExtras();

                    Log.e("Read sms","Read sms intent"+"");

                    progDial.dismiss();

                    try {

                        if (bundle != null) {

                            final Object[] pdusObj = (Object[]) bundle.get("pdus");

                            for (int i = 0; i < pdusObj.length; i++) {

                                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                                String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                                String senderNum = phoneNumber;
                                String message = currentMessage.getDisplayMessageBody();


                                if (message.contains("WE Cloud Verification Code:")||senderNum.trim().equals("aamra.")||senderNum.trim().equals("AAMRA")||senderNum.trim().equals("01819-249868")) {
                                    String[] message_fragment = message.split(":");
                                    verification_code = message_fragment[1].trim();
                                    Toast.makeText(context, "j>>"+verification_code, Toast.LENGTH_LONG).show();

                                    edt_verification.setText(verification_code);

                                }

                            } // end for loop
                        } // bundle is null

                    } catch (Exception e) {
                        Toast.makeText(context, "Error receiving SMS"+e, Toast.LENGTH_LONG).show();
                        Log.e("","");

                    }
                }
            };

            IntentFilter smsIntentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            context.registerReceiver(smsReceiver, smsIntentFilter);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";

            imei1 = params[1];
            sim_serial1 = params[2];
            sim_subscriber_id1 = params[3];
            username1 = params[4];
            password1 = params[5];
            userphone1 = params[6];
            useremail1 = params[7];
            userfullname1 = params[8];

            // String encoding = new
            // String(Base64.encodeBase64("admin:power0fwe".getBytes()));

            try {

                URL url = new URL(params[0]);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                // conn.setRequestProperty("Authorization", "Basic " + encoding);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("userid", username1)
                        .appendQueryParameter("phone", userphone1)
                        .appendQueryParameter("imei", imei1)
                        .appendQueryParameter("sim_serial", sim_serial1)
                        .appendQueryParameter("subscriber_id", sim_subscriber_id1)
                        .appendQueryParameter("email", useremail1)
                        .appendQueryParameter("fullname", userfullname1);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";
                    throw new HttpException(responseCode + "");
                }
                // Log.d("adduser", response);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            JSONObject ajsonObj;
            Log.e("rasult>>",result.toString()+"");
            if (result.length() > 0) {
                try {
                    ajsonObj = new JSONObject(result);

                    String message = ajsonObj.getString("message");
                    String status = ajsonObj.getString("status");


                    if (status.equals("F")) {
                        progDial.dismiss();
                    }

                    if (status.equals("S")) {
                        Runnable progressRunnable = new Runnable() {

                            @Override
                            public void run() {
                                progDial.dismiss();
                            }
                        };

                        Handler pdCanceller = new Handler();
                        pdCanceller.postDelayed(progressRunnable, 60000);

                    } else {
                        AlertDialog.Builder alertdialog3 = new AlertDialog.Builder(context);
                        alertdialog3.setMessage(message).setNegativeButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                        alertdialog3.show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(context, "Your internet is too slow..", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Your internet is too slow..", Toast.LENGTH_LONG).show();
            }
        }


    }

}
