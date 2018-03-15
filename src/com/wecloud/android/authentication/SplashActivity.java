package com.wecloud.android.authentication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.owncloud.android.R;
import com.wecloud.android.ui.activity.FileDisplayActivity;


public class SplashActivity extends Activity {

   Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;

       Log.e("", Build.MANUFACTURER+"");

       // WE device check
//       if (Build.MANUFACTURER.equalsIgnoreCase("WE")){
//       checkRunTimePermission();
//       }else{
//           deviceAlert(context);
//       }
  checkRunTimePermission();
    }


    private void checkRunTimePermission() {
        String[] permissionArrays = new String[]{Manifest.permission.READ_PHONE_STATE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 1);
        } else {
            // if already permition granted
            // PUT YOUR ACTION (Like Open cemara etc..)
            Log.e("else block>>","");
            sendRequest();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                  //  Toast.makeText(getApplicationContext(), SharePreference.getImeiCheck(context), Toast.LENGTH_SHORT).show();
                    if (SharePreference.getImeiCheck(context).equalsIgnoreCase("Verified")){
                        context.startActivity(new Intent(context, FileDisplayActivity.class));
                        ((Activity) context).finish();
                      //  Toast.makeText(context, "if", Toast.LENGTH_SHORT).show();
                    }else{
                    sendRequest();
                      //  Toast.makeText(context, "else", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void sendRequest(){

        try {
            TelephonyManager telman = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telman.getDeviceId();
            Log.e("ajson",imei+"");
            new VerifyDeviceAsyncTask(imei, this).execute(getString(R.string.url_verify_device));
        }catch (SecurityException se){

        }
    }
}
