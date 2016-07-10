package com.seafile.seadroid2.global;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Alfred on 2016/7/10.
 */
public class AccountsSharedPreferencesHelper {
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;

	private static volatile AccountsSharedPreferencesHelper accountsSharedPreferencesHelper;
	private static final String ACCOUNT_NAME = "account";
	private static final String TOKEN_NAME = "token";
	private static final String ACCOUNT_SHARED_FILE_NAME = "account_info";

	public AccountsSharedPreferencesHelper(Context context) {
		sharedPreferences = context.getSharedPreferences(ACCOUNT_SHARED_FILE_NAME, Context.MODE_PRIVATE);
	}

	public static AccountsSharedPreferencesHelper getInstance(Context context) {

		if (accountsSharedPreferencesHelper == null) {
			synchronized (AccountsSharedPreferencesHelper.class) {
				if (accountsSharedPreferencesHelper == null) {
					accountsSharedPreferencesHelper = new AccountsSharedPreferencesHelper(context);
				}
			}
		}
		return accountsSharedPreferencesHelper;
	}


	public void putAccountInfo(String acountName, String token) {
		editor = sharedPreferences.edit();
		editor.putString(ACCOUNT_NAME, acountName);
		editor.putString(TOKEN_NAME, token);
		editor.commit();
	}

	public String getAccountName() {
		return sharedPreferences.getString(ACCOUNT_NAME, "");
	}


	public String getTokenName() {
		return sharedPreferences.getString(TOKEN_NAME, "");
	}

}
