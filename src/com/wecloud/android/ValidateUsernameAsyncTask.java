package com.wecloud.android;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class ValidateUsernameAsyncTask extends AsyncTask<String, Void, String> {

    private String imei, sim_serial, sim_subscriber_id, username;
    Context context;
    Button positiveBtn;
    private ImageView tickmark;
   

    public ValidateUsernameAsyncTask(String imei, String sim_serial, String sim_subscriber_id, String username,
                                     Context context, Button positiveBtn, ImageView tickmark) {

        this.imei = imei;
        this.sim_serial = sim_serial;
        this.sim_subscriber_id = sim_subscriber_id;
        this.username = username;        
        this.context = context;
        this.positiveBtn = positiveBtn;   
        this.tickmark = tickmark;

    }

    @Override
    protected String doInBackground(String... server) {
        String response = "";
       

        try {

            URL url = new URL(server[0]);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            // conn.setRequestProperty("Authorization", "Basic " + encoding);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("userid", username)                    
                    .appendQueryParameter("imei", imei).appendQueryParameter("sim_serial", sim_serial)
                    .appendQueryParameter("subscriber_id", sim_subscriber_id);
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
        if (result.length() > 0) {
            try {
                ajsonObj = new JSONObject(result);

                String message = ajsonObj.getString("message");
                String status = ajsonObj.getString("status");

                if (status.equals("S")) {
                    positiveBtn.setClickable(true);
                    tickmark.setImageResource(R.drawable.ic_ok);
                    tickmark.setVisibility(View.VISIBLE);
                    

                } else {
//                   Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                   tickmark.setImageResource(R.drawable.ic_cross);
                   tickmark.setVisibility(View.VISIBLE);
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
