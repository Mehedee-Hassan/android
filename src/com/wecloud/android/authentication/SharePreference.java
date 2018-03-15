package com.wecloud.android.authentication;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rashed on 5/2/2016.
 */
public class SharePreference {

    // App Preferences
    private static final String PREFS_FILE_NAME = "AppPreferences";
    private static final String ACCESSTOKEN = "accesstoken";
    private static final String IMEI_CHECK = "loged_id";




    // access token
    public static String getAccessToken(final Context con) {
        return con.getSharedPreferences(SharePreference.PREFS_FILE_NAME,
                Context.MODE_PRIVATE).getString(SharePreference.ACCESSTOKEN, "");
    }

    public static void setAccessToken(final Context con, final String accesstoken) {
        final SharedPreferences prefs = con.getSharedPreferences(
                SharePreference.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SharePreference.ACCESSTOKEN, accesstoken);
        editor.commit();
    }
    //
    public static String getImeiCheck(final Context con) {
        return con.getSharedPreferences(SharePreference.PREFS_FILE_NAME,
                Context.MODE_PRIVATE).getString(SharePreference.IMEI_CHECK, "UnVerified");
    }

    public static void setImeiCheck(final Context con, final String imei_check) {
        final SharedPreferences prefs = con.getSharedPreferences(
                SharePreference.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SharePreference.IMEI_CHECK, imei_check);
        editor.commit();
    }


}
