package com.wecloud.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

public class ResetPasswordAsyncTask extends AsyncTask<String, Void, String> {

    private String imei, sim_serial, sim_subscriber_id, token, username;
    Context context;    
   

    public ResetPasswordAsyncTask(Context context) {      
        
        this.context = context;
            

    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";
        
        this.imei = params[1];
        this.sim_serial = params[2];
        this.sim_subscriber_id = params[3];
        this.token = params[4];
        this.username = params[5];
        

        try {

            URL url = new URL(params[0]);
            Log.e("url>>",url+"");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            // conn.setRequestProperty("Authorization", "Basic " + encoding);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()                   
                    .appendQueryParameter("imei", imei).appendQueryParameter("sim_serial", sim_serial)
                    .appendQueryParameter("sim_sub_id", sim_subscriber_id).appendQueryParameter("token", token)
                    .appendQueryParameter("userid", username);
            String query = builder.build().getEncodedQuery();
//            imei=359519030204335&sim_serial=8988038505065444491&sim_sub_id=470038506544449&token=ewri324ndf98034lksdr43n4jedr&userid=mehedee

            Log.e("query>>",query+"");
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
        // Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

        JSONObject ajsonObj;
        if (result.length() > 0) {
            try {
                ajsonObj = new JSONObject(result);

                String message = ajsonObj.getString("message");
                String status = ajsonObj.getString("status");

                if (status.equals("S")) {
                    AlertDialog.Builder responseAlert = new AlertDialog.Builder(context);
                    responseAlert.setMessage(message).setPositiveButton("OK", new OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();                            
                        }
                    }).show();                                                    

                } else {
                   Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                   Log.e(">>>",message+"");
                }
            } catch (JSONException e) {
                Toast.makeText(context, "Check your internet", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Check your internet", Toast.LENGTH_LONG).show();
        }
    }

}
