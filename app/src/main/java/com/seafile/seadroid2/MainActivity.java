package com.seafile.seadroid2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.seafile.seadroid2.account.Account;
import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.network.SeafConnection;
import com.seafile.seadroid2.account.AccountInfo;
import com.seafile.seadroid2.account.AccountManager;
import com.seafile.seadroid2.data.DataManager;
import com.seafile.seadroid2.data.SeafRepo;
import com.seafile.seadroid2.data.ServerInfo;
import com.seafile.seadroid2.transfer.TransferService;
import com.seafile.seadroid2.ui.activity.TransferActivity;
import com.seafile.seadroid2.util.Utils;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String DEBUG_TAG = "MainActivity";

    private Button commonApiBtn;
    private Button transferBtn;
    private AccountManager accountManager;
    private TransferService txService = null;
    private Activity activity = null;

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            TransferService.TransferBinder binder = (TransferService.TransferBinder) service;
            txService = binder.getService();
            Log.d(DEBUG_TAG, "bind TransferService");

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            txService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        commonApiBtn = (Button) findViewById(R.id.buttion1);
        commonApiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Account loginAccount = new Account("http://166.111.131.62:6789/", "1@qq.com", null);
                new MyThread(loginAccount).start();
            }
        });
        transferBtn = (Button) findViewById(R.id.button2);
        transferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, TransferActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        accountManager = new AccountManager(getApplicationContext());

        Intent txIntent = new Intent(this, TransferService.class);
        startService(txIntent);
        Log.e(DEBUG_TAG, "start TransferService");

        Intent bIntent = new Intent(this, TransferService.class);
        bindService(bIntent, mConnection, Context.BIND_AUTO_CREATE);
        activity = this;
    }

    @Override
    protected void onDestroy() {
        if (txService != null) {
            unbindService(mConnection);
            txService = null;
        }
        super.onDestroy();
    }



    public void addUpdateTask(String repoID, String repoName, String targetDir, String localFilePath) {
            txService.addTaskToUploadQue(accountManager.getAccount(), repoID, repoName, targetDir, localFilePath, true, true);
    }

    public void addUpdateBlocksTask(String repoID, String repoName, String targetDir, String localFilePath, int version) {
        txService.addTaskToUploadQue(accountManager.getAccount(), repoID, repoName, targetDir, localFilePath, true, true, version);
    }

    private int addUploadTask(String repoID, String repoName, String targetDir, String localFilePath) {
        return txService.addTaskToUploadQue(accountManager.getAccount(), repoID, repoName, targetDir, localFilePath, false, true);
    }

    private int addUploadBlocksTask(String repoID, String repoName, String targetDir, String localFilePath, int version) {
        return txService.addTaskToUploadQue(accountManager.getAccount(), repoID, repoName, targetDir, localFilePath, false, true, version);
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

                SeafRepo downloadRepo = repos.get(0);
                SeafRepo uploadRepo = repos.get(1);
                List<SeafDirent> dirents = dataManager.getDirentsFromServer(downloadRepo.getID(), "/");
                for (SeafDirent dirent : dirents) {
                    Log.e(DEBUG_TAG, dirent.getTitle() + " " + dirent.getSubtitle());
                    if (!dirent.isDir()) {
                        txService.addDownloadTask(accountManager.getAccount(), downloadRepo.getName(), downloadRepo.getID(), "/" + dirent.getTitle());
                    }
                }

                // wait the download task finish, then we can upload our local files
                Thread.sleep(2000);
                txService.addUploadTask(accountManager.getAccount(), uploadRepo.getID(), uploadRepo.getName(), "/", "/storage/emulated/0/Seafile/1@qq.com (166.111.131.62)/askjdfkljsd/settings.jar", false, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
