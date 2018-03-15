package com.wecloud.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.wecloud.android.authentication.RegistrationActivity;

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

public class AddNewUser extends AsyncTask<String, Void, String> {

    private String imei, sim_serial, sim_subscriber_id, username, password, userphone, useremail, userfullname, verification_code;
    Context context;
    private EditText usernameET, passwordET;
    private ProgressDialog progDial;

    public AddNewUser(Context context, EditText usernameET, EditText passwordET) {

      
        this.context = context;
        this.usernameET = usernameET;
        this.passwordET = passwordET;

    }

    @Override
    protected void onPreExecute() {
        progDial = new ProgressDialog(context);
        progDial.setMessage("Please Wait");
        progDial.show();
        super.onPreExecute();
    }
    
    @Override
    protected String doInBackground(String... params) {
        String response = "";
        // String encoding = new
        // String(Base64.encodeBase64("admin:power0fwe".getBytes()));
        this.imei = params[1];
        this.sim_serial = params[2];
        this.sim_subscriber_id = params[3];
        this.username = params[4];
        this.password = params[5];
        this.userphone = params[6];
        this.useremail = params[7];
        this.userfullname = params[8];
        this.verification_code = params[9];
        try {

            URL url = new URL(params[0]);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            // conn.setRequestProperty("Authorization", "Basic " + encoding);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("userid", username)
                    .appendQueryParameter("password", password).appendQueryParameter("phone", userphone)
                    .appendQueryParameter("imei", imei).appendQueryParameter("sim_serial", sim_serial)
                    .appendQueryParameter("subscriber_id", sim_subscriber_id).appendQueryParameter("email", useremail)
                    .appendQueryParameter("fullname", userfullname).appendQueryParameter("verification_code", verification_code);
            String query = builder.build().getEncodedQuery();
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
        } catch (IOException  e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        progDial.dismiss();

        Log.e("result>",result+"");

        JSONObject ajsonObj;
        if (result.length() > 0) {
            try {
                ajsonObj = new JSONObject(result);

                String message = ajsonObj.getString("message");
                String status = ajsonObj.getString("status");

                if (status.equals("S")) {
//                    usernameET.setText(username);
//                    passwordET.setText(password);

                    ((RegistrationActivity)context).stopActivity();
                    Log.e("password>>",username+"<<>>"+password+"");

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
                Toast.makeText(context, "Check your internet "+e, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Check your internet>", Toast.LENGTH_LONG).show();
        }
    }
}
