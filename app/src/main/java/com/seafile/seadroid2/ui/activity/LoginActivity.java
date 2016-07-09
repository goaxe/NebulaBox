package com.seafile.seadroid2.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.ui.base.BaseActivity;
import com.seafile.seadroid2.util.Utils;
import com.seafile.seadroid2.util.log.KLog;

import java.net.MalformedURLException;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 登录页面
 *
 * Created by Alfred on 2016/7/8.
 */
public class LoginActivity extends BaseActivity{

	@Bind(R.id.server_url_login_et)
	EditText serverUrlEditText;
	@Bind(R.id.email_address_login_et)
	EditText emailAddressEditText;
	@Bind(R.id.password_login_et)
	EditText passwordEditText;
	@Bind(R.id.login_button_login_btn)
	Button loginBtn;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@OnClick(R.id.login_button_login_btn)
	private void login(){
		String serverURL = serverUrlEditText.getText().toString().trim();
		String email = emailAddressEditText.getText().toString().trim();
		String passwd = passwordEditText.getText().toString();

		if (serverURL.length() == 0) {
			serverUrlEditText.setText(R.string.err_server_andress_empty);
			return;
		}

		if (email.length() == 0) {
			emailAddressEditText.setError(getResources().getString(R.string.err_email_empty));
			return;
		}

		if (passwd.length() == 0) {
			passwordEditText.setError(getResources().getString(R.string.err_passwd_empty));
			return;
		}

		try {
			serverURL = Utils.cleanServerURL(serverURL);
		} catch (MalformedURLException e) {
			serverUrlEditText.setText(R.string.invalid_server_address);
			KLog.i("Invalid URL " + serverURL);
			return;
		}
	}
}
