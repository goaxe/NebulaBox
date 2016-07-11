package com.seafile.seadroid2.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeafException;
import com.seafile.seadroid2.bean.Account;
import com.seafile.seadroid2.bean.AccountInfo;
import com.seafile.seadroid2.data.DataManager;
import com.seafile.seadroid2.global.AccountsSharedPreferencesHelper;
import com.seafile.seadroid2.global.ActivityIntentHelper;
import com.seafile.seadroid2.global.ActivityManager;
import com.seafile.seadroid2.network.SeafConnection;
import com.seafile.seadroid2.ui.base.BaseActivity;
import com.seafile.seadroid2.util.ConcurrentAsyncTask;
import com.seafile.seadroid2.util.Utils;
import com.seafile.seadroid2.util.log.KLog;

import org.json.JSONException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录页面
 * <p/>
 * Created by Alfred on 2016/7/8.
 */
public class LoginActivity extends BaseActivity {

    @Bind(R.id.server_url_login_et)
    EditText serverUrlEditText;
    @Bind(R.id.email_address_login_et)
    EditText emailAddressEditText;
    @Bind(R.id.password_login_et)
    EditText passwordEditText;
    @Bind(R.id.login_button_login_btn)
    Button loginBtn;
	@Bind(R.id.status_login_tv)
	TextView statusTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
		ButterKnife.bind(this);

		checkAccountIfLogin();
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    private void checkAccountIfLogin() {
		String account,token;
		AccountsSharedPreferencesHelper accountsSharedPreferencesHelper = AccountsSharedPreferencesHelper.getInstance(this);
		account = accountsSharedPreferencesHelper.getAccountName();
		token = accountsSharedPreferencesHelper.getTokenName();
		if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)){
			ActivityIntentHelper.gotoMainActivity(this);
            ActivityManager.finishCurrent();
		}
	}

	@OnClick(R.id.login_button_login_btn)
    public void login() {
        String serverURL = serverUrlEditText.getText().toString().trim();
        String email = emailAddressEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (serverURL.length() == 0) {
            serverUrlEditText.setText(R.string.err_server_andress_empty);
            return;
        }

        if (email.length() == 0) {
            emailAddressEditText.setError(getResources().getString(R.string.err_email_empty));
            return;
        }

        if (password.length() == 0) {
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

		loginBtn.setEnabled(false);
		Account account = new Account(serverURL,email,null);
		ConcurrentAsyncTask.execute(new LoginTask(account, password));
    }
    private class LoginTask extends AsyncTask<Void, Void, String> {
        Account loginAccount;
        SeafException err = null;
        String passwd;

        public LoginTask(Account loginAccount, String passwd) {
            this.loginAccount = loginAccount;
            this.passwd = passwd;
        }

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            showLoadingDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            if (params.length != 0)
                return "Error number of parameter";

            return doLogin();
        }

        private void resend() {
            ConcurrentAsyncTask.execute(new LoginTask(loginAccount, passwd));
        }

        @Override
        protected void onPostExecute(final String result) {
            dismissLoadingDialog();
//            if (err == SeafException.sslException) {
//                SslConfirmDialog dialog = new SslConfirmDialog(loginAccount,
//                        new SslConfirmDialog.Listener() {
//                            @Override
//                            public void onAccepted(boolean rememberChoice) {
//                                CertsManager.instance().saveCertForAccount(loginAccount, rememberChoice);
//                                resend();
//                            }
//
//                            @Override
//                            public void onRejected() {
//                                statusView.setText(result);
//                                loginButton.setEnabled(true);
//                            }
//                        });
//                dialog.show(getSupportFragmentManager(), SslConfirmDialog.FRAGMENT_TAG);
//                return;
//            }

            if (result != null && result.equals("Success")) {

//                Intent retData = new Intent();
//                retData.putExtras(getIntent());
//                retData.putExtra(android.accounts.AccountManager.KEY_ACCOUNT_NAME, loginAccount.getSignature());
//                retData.putExtra(android.accounts.AccountManager.KEY_AUTHTOKEN, loginAccount.getToken());
//                retData.putExtra(android.accounts.AccountManager.KEY_ACCOUNT_TYPE, getIntent().getStringExtra(SeafileAuthenticatorActivity.ARG_ACCOUNT_TYPE));
//                retData.putExtra(SeafileAuthenticatorActivity.ARG_EMAIL, loginAccount.getEmail());
//                retData.putExtra(SeafileAuthenticatorActivity.ARG_SERVER_URI, loginAccount.getServer());
//
//                setResult(RESULT_OK, retData);
//                finish();
				AccountsSharedPreferencesHelper helper = new AccountsSharedPreferencesHelper(LoginActivity.this);
				helper.putAccountInfo(loginAccount.getEmail(),loginAccount.getToken());
				ActivityIntentHelper.gotoMainActivity(LoginActivity.this);
				ActivityManager.finishCurrent();
            } else {
                statusTextView.setText(result);
            }
            loginBtn.setEnabled(true);
        }

        private String doLogin() {
            SeafConnection sc = new SeafConnection(loginAccount);

            try {
                // if successful, this will place the auth token into "loginAccount"
                if (!sc.doLogin(passwd))
                    return getString(R.string.err_login_failed);

                // fetch email address from the server
                DataManager manager = new DataManager(loginAccount);
                AccountInfo accountInfo = manager.getAccountInfo();

                if (accountInfo == null)
                    return "Unknown error";

                // replace email address/username given by the user with the address known by the server.
                loginAccount = new Account(loginAccount.server, accountInfo.getEmail(), loginAccount.token);

                return "Success";

            } catch (SeafException e) {
                err = e;
                if (e == SeafException.sslException) {
                    return getString(R.string.ssl_error);
                }
                switch (e.getCode()) {
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        return getString(R.string.err_wrong_user_or_passwd);
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        return getString(R.string.invalid_server_address);
                    default:
                        return e.getMessage();
                }
            } catch (JSONException e) {
                return e.getMessage();
            }
        }
    }

}
