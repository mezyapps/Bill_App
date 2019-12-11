package com.mezyapps.bill_app.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedLoginUtils {

    public static final String LOGIN_PREFERENCE = "Login_preference";
    public static final String DATABASE_PREFERENCE = "Database_preference";
    public static final String IS_LOGIN = "IS_LOGIN";
    public static final String USER_ID = "USER_ID";
    public static final String NAME = "NAME";
    public static final String MOBILE = "MOBILE";
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static String getLoginSharedUtils(Context mContext) {
        preferences = mContext.getSharedPreferences(LOGIN_PREFERENCE, mContext.MODE_PRIVATE);
        return preferences.getString(IS_LOGIN, "");
    }

    public static void putLoginSharedUtils(Context mContext) {
        preferences = mContext.getSharedPreferences(LOGIN_PREFERENCE, mContext.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(IS_LOGIN, "true");
        editor.commit();
    }

    public static void removeLoginSharedUtils(Context mContext) {
        preferences = mContext.getSharedPreferences(LOGIN_PREFERENCE, mContext.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(IS_LOGIN, "false");
        editor.commit();
    }

    public static void addUser(Context mContext,String user_id,String name,String mobile)
    {
        preferences = mContext.getSharedPreferences(LOGIN_PREFERENCE, mContext.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(USER_ID,user_id);
        editor.putString(NAME,name);
        editor.putString(MOBILE,mobile);
        editor.commit();
    }

    public static String  getUserId(Context mContext) {
        preferences = mContext.getSharedPreferences(LOGIN_PREFERENCE, mContext.MODE_PRIVATE);
        String user_id=preferences.getString(USER_ID, "");
        return user_id;
    }
    public static String  getUserName(Context mContext) {
        preferences = mContext.getSharedPreferences(LOGIN_PREFERENCE, mContext.MODE_PRIVATE);
        String name=preferences.getString(NAME, "");
        return name;
    }
    public static String  getUserMobile(Context mContext) {
        preferences = mContext.getSharedPreferences(LOGIN_PREFERENCE, mContext.MODE_PRIVATE);
        String name=preferences.getString(MOBILE, "");
        return name;
    }
}
