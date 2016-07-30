package com.tsinghua.nebulabox.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tsinghua.nebulabox.R;
import com.tsinghua.nebulabox.SeafConnection;
import com.tsinghua.nebulabox.SeafException;
import com.tsinghua.nebulabox.account.Account;
import com.tsinghua.nebulabox.account.AccountInfo;
import com.tsinghua.nebulabox.account.AccountManager;
import com.tsinghua.nebulabox.data.DataManager;
import com.tsinghua.nebulabox.data.SeafCommit;
import com.tsinghua.nebulabox.data.SeafShare;
import com.tsinghua.nebulabox.global.ActivityIntentHelper;
import com.tsinghua.nebulabox.global.ActivityManager;
import com.tsinghua.nebulabox.ui.base.BaseActivity;
import com.tsinghua.nebulabox.util.ConcurrentAsyncTask;
import com.tsinghua.nebulabox.util.Utils;
import com.tsinghua.nebulabox.util.log.KLog;

import org.json.JSONException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录页面
 * <p>
 * Created by Alfred on 2016/7/8.
 */
public class LoginActivity extends BaseActivity {

    private static final String DEBUG_TAG = "LoginActivity";

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
//    @Bind(R.id.register_login_tv)
//    TextView regitsterTextView;

    public class MyThread extends Thread {

        @Override
        public void run() {
            AccountManager accountManager = new AccountManager(getApplicationContext());
            DataManager dataManager = new DataManager(accountManager.getAccount());
            try {
                List<SeafShare> shares = dataManager.getShareLinks();
                if (shares.isEmpty()) {
                    Log.e(DEBUG_TAG, "shares is empty");
                }
                for (SeafShare share : shares) {
                    Log.e(DEBUG_TAG, share.getPath());
                }

                List<SeafCommit> commits = dataManager.getFileHistory("d8206f34-af9f-4534-861d-31f129071453", "/seafile-tutorial.doc");
                if (commits.isEmpty()) {
                    Log.e(DEBUG_TAG, "commits is empty");
                } else {
                    for (SeafCommit commit : commits) {
                        Log.e(DEBUG_TAG, commit.getCreatorName());
                    }
                }

                commits = dataManager.getLibraryHistory("d8206f34-af9f-4534-861d-31f129071453");
                if (commits.isEmpty()) {
                    Log.e(DEBUG_TAG, "commits is empty");
                } else {
                    for (SeafCommit commit : commits) {
                        Log.e(DEBUG_TAG, commit.getCreatorName());
                    }
                }

            } catch (Exception e) {
                Log.e(DEBUG_TAG, e.toString());
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
//        new MyThread().start();

        //checkAccountIfLogin();
    }


    private void checkAccountIfLogin() {
        AccountManager accountManager = new AccountManager(getApplicationContext());
        Account account = accountManager.getAccount();
        String token = account.getToken();
        if (!TextUtils.isEmpty(token)) {
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
        Account account = new Account(serverURL, email, null);

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

            if (result != null && result.equals("Success")) {
                AccountManager accountManager = new AccountManager(getApplicationContext());
                accountManager.setAccount(loginAccount);

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
