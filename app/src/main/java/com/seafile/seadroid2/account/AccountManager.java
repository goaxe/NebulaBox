package com.seafile.seadroid2.account;

import android.content.Context;
import android.content.SharedPreferences;

import com.seafile.seadroid2.data.ServerInfo;


/**
 * Account Manager.<br>
 * note the differences between {@link Account} and {@link AccountInfo}<br>
 */

public class AccountManager {
    @SuppressWarnings("unused")
    private static String DEBUG_TAG = "AccountManager";

    public static final String SHARED_PREF_NAME = "latest_account";
    public static final String SHARED_PREF_ACCOUNT_NAME = "com.seafile.seadroid.account_name";

    public static final String AUTHTOKEN_TYPE = "api2";
    /**
     * Key of Server URI in userData
     */
    public final static String KEY_SERVER_URI = "server";
    /**
     * Key of email in userData
     */
    public final static String KEY_EMAIL = "email";
    public final static String KEY_TOKEN = "token";
    /**
     * Key of Server version in userData
     */
    public final static String KEY_SERVER_VERSION = "version";
    /**
     * Key of Server Feature-list in userData
     */
    public final static String KEY_SERVER_FEATURES = "features";

    /** used to manage multi Accounts when user switch between different Accounts */
    private SharedPreferences actMangeSharedPref;
    private SharedPreferences.Editor editor;

    private Context ctx;

    public AccountManager(Context context) {
        this.ctx = context;
        actMangeSharedPref = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = actMangeSharedPref.edit();
    }

    public Account getAccount() {
        String server = actMangeSharedPref.getString(KEY_SERVER_URI, "");
        String email = actMangeSharedPref.getString(KEY_EMAIL, "");
        String token = actMangeSharedPref.getString(KEY_TOKEN, "");
        return new Account(server, email, token);
    }

    public void setAccount(Account account) {
        editor.putString(KEY_SERVER_URI, account.getServer());
        editor.putString(KEY_EMAIL, account.getEmail());
        editor.putString(KEY_TOKEN, account.getToken());
        editor.commit();
    }

    public void setServerInfo(ServerInfo serverInfo) {
        editor.putString(KEY_SERVER_URI, serverInfo.getUrl());
        editor.putString(KEY_SERVER_VERSION, serverInfo.getVersion());
        editor.putString(KEY_SERVER_FEATURES, serverInfo.getFeatures());
        editor.commit();
    }

    public ServerInfo getServerInfo() {
        String server = actMangeSharedPref.getString(KEY_SERVER_URI, "");
        String version = actMangeSharedPref.getString(KEY_SERVER_VERSION, "");
        String features = actMangeSharedPref.getString(KEY_SERVER_FEATURES, "");
        ServerInfo info = new ServerInfo(server, version, features);
        return info;
    }

    public void signOutAccount(Account account) {
        editor.putString(AUTHTOKEN_TYPE, "");
        editor.commit();
    }
}