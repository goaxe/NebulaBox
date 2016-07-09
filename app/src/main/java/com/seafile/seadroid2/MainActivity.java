package com.seafile.seadroid2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.seafile.seadroid2.account.Account;
import com.seafile.seadroid2.network.SeafConnection;
import com.seafile.seadroid2.account.AccountInfo;
import com.seafile.seadroid2.account.AccountManager;
import com.seafile.seadroid2.data.DataManager;
import com.seafile.seadroid2.data.SeafRepo;
import com.seafile.seadroid2.data.SeafStarredFile;
import com.seafile.seadroid2.data.ServerInfo;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button button;
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.buttion);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Account loginAccount = new Account("http://166.111.131.62:6789/", "1@qq.com", null);
                new MyThread(loginAccount).start();
            }
        });
        accountManager = new AccountManager(getApplicationContext());
    }

    class MyThread extends Thread {

        private Account loginAccount;
        private SeafConnection sc;

        public MyThread(Account loginAccount) {
            this.loginAccount = loginAccount;
            this.sc = new SeafConnection(loginAccount);
        }

        @Override
        public void run() {
            try {
                Log.e("====", "start run");
                if (!sc.doLogin("123456")) {
                    return;
                }

                DataManager dataManager = new DataManager(loginAccount);
                AccountInfo accountInfo = dataManager.getAccountInfo();
                // replace email address/username given by the user with the address known by the server.
                Log.e(TAG, loginAccount.toString());
                Log.e(TAG, accountInfo.getEmail());
                loginAccount = new Account(loginAccount.server, accountInfo.getEmail(), loginAccount.token);
                ServerInfo serverInfo = dataManager.getServerInfo();

                accountManager.setAccount(loginAccount);
                accountManager.setServerInfo(serverInfo);

                Log.e(TAG, accountManager.getAccount().toString());
                Log.e(TAG, accountManager.getServerInfo().toString());
                List<SeafRepo> repos = dataManager.getReposFromServer();
                for (SeafRepo repo : repos) {
                    Log.e(TAG, repo.getID() + " " + repo.getName() + " " + repo.getTitle());
                }

                List<SeafStarredFile> starredFiles = dataManager.getStarredFiles();
                Thread.sleep(2000);
                for (SeafStarredFile file : starredFiles) {
                    dataManager.unstar(file.getRepoID(), file.getPath());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
