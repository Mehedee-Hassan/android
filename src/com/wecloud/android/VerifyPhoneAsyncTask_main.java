package com.wecloud.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.owncloud.android.R;

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

public class VerifyPhoneAsyncTask_main extends AsyncTask<String, Void, String> {

    private String imei, sim_serial, sim_subscriber_id, username, password, userphone, useremail, userfullname;
    private Context context;
    private ProgressDialog progDial;
    private EditText usernameET, passwordET;
    private BroadcastReceiver smsReceiver;
    EditText verification_code_entry;
    String verification_code = "";
    public VerifyPhoneAsyncTask_main(Context context, EditText usernameET, EditText passwordET) {
        
        this.context = context;
        this.usernameET = usernameET;
        this.passwordET = passwordET;


    }

    @Override
    protected void onPreExecute() {
          super.onPreExecute();
        progDial = new ProgressDialog(context);
        progDial.setMessage("Waiting for SMS. Please Wait...");
        progDial.setCancelable(false);

        progDial.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();         
            }
        });
        progDial.show();

 
        progDial.setOnDismissListener(new OnDismissListener() {
            
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
                                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);
                                alertDialog2.setTitle("Verification Code");
                                final EditText verification_code_entry = new EditText(context);
                                verification_code_entry.setInputType(InputType.TYPE_CLASS_NUMBER);
                                verification_code_entry.setBackgroundResource(R.drawable.curv_background);
                                verification_code_entry.setLayoutParams(new LinearLayout.LayoutParams(100, 50));
                                verification_code_entry.setText(verification_code);
                                alertDialog2.setCancelable(false);
                                alertDialog2.setView(verification_code_entry).setPositiveButton("OK", new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String aamracloud_add_user_url = context.getString(R.string.url_aamracloud_add_user);
//
                                        new AddNewUser(context, usernameET, passwordET)
                                                .execute(aamracloud_add_user_url,imei, sim_serial, sim_subscriber_id, username, password, userphone, useremail, userfullname, verification_code);
//
                                        Log.e("",usernameET+"<>"+usernameET+"");
                                    }
                                }).setNegativeButton("Cancel", new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                    }
                                });
                                alertDialog2.show();

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
        
        this.imei = params[1];
        this.sim_serial = params[2];
        this.sim_subscriber_id = params[3];
        this.username = params[4];
        this.password = params[5];
        this.userphone = params[6];
        this.useremail = params[7];
        this.userfullname = params[8];
        
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
                    .appendQueryParameter("userid", username)
                    .appendQueryParameter("phone", userphone)
                    .appendQueryParameter("imei", imei)
                    .appendQueryParameter("sim_serial", sim_serial)
                    .appendQueryParameter("subscriber_id", sim_subscriber_id)
                    .appendQueryParameter("email", useremail)
                    .appendQueryParameter("fullname", userfullname);
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

        } catch (IOException  e) {
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
                    alertdialog3.setMessage(message).setNegativeButton("OK", new OnClickListener() {

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
