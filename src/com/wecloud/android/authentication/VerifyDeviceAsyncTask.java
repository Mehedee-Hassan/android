package com.wecloud.android.authentication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


import com.wecloud.android.ui.activity.FileDisplayActivity;

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


public class VerifyDeviceAsyncTask extends AsyncTask<String, Void, String> {

    private String imei;
    Context context;
//    ProgressDialog progdialog;

    public VerifyDeviceAsyncTask(String imei, Context context) {

        this.imei = imei;

        this.context = context;

    }

    @Override
    protected void onPreExecute() {
   /*     progdialog = new ProgressDialog(context);
        progdialog.setMessage("Please Wait");
        progdialog.show();
*/
        super.onPreExecute();
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

            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("imei", imei);
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
            
            Thread.sleep(1500);


        } catch (IOException |  InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

//        progdialog.dismiss();

        JSONObject ajsonObj;
        if (result.length() > 0) {
            try {
                ajsonObj = new JSONObject(result);
                Log.e("ajson",ajsonObj+"");
                String message = ajsonObj.getString("message");
                String status = ajsonObj.getString("status");

                if (status.equals("S")) {
                    context.startActivity(new Intent(context, FileDisplayActivity.class));
                    ((Activity) context).finish();

                } else {
                    AlertDialog.Builder deviceCheckAlert = new AlertDialog.Builder(context);
                    deviceCheckAlert.setMessage(message);
                    deviceCheckAlert.setCancelable(false);
                    deviceCheckAlert.setNeutralButton("OK", new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((Activity) context).finish();

                        }
                    });
                   
                    AlertDialog dialog = deviceCheckAlert.create();
                    dialog.show();
                    TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
                    messageView.setGravity(Gravity.CENTER);

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
