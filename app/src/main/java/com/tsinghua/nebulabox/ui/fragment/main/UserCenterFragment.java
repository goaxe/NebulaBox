package com.tsinghua.nebulabox.ui.fragment.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Maps;
import com.tsinghua.nebulabox.R;
import com.tsinghua.nebulabox.SeafException;
import com.tsinghua.nebulabox.account.Account;
import com.tsinghua.nebulabox.account.AccountInfo;
import com.tsinghua.nebulabox.account.AccountManager;
import com.tsinghua.nebulabox.data.DataManager;
import com.tsinghua.nebulabox.data.StorageManager;
import com.tsinghua.nebulabox.ui.activity.LoginActivity;
import com.tsinghua.nebulabox.ui.base.BaseFragment;
import com.tsinghua.nebulabox.ui.dialog.ClearCacheTaskDialog;
import com.tsinghua.nebulabox.ui.dialog.TaskDialog;
import com.tsinghua.nebulabox.ui.widget.CircleImageView;
import com.tsinghua.nebulabox.util.ConcurrentAsyncTask;
import com.tsinghua.nebulabox.util.log.KLog;

import org.apache.commons.io.FileUtils;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Alfred on 2016/7/11.
 */
public class UserCenterFragment extends BaseFragment {

    @Bind(R.id.avatar_user_center_iv)
    CircleImageView avatarImageView;
    @Bind(R.id.nick_name_user_center_tv)
    TextView nickTextView;
    @Bind(R.id.capacity_user_center_pb)
    ProgressBar capacityProgressBar;
    @Bind(R.id.capacity_user_center_tv)
    TextView capacityTextView;
    @Bind(R.id.clear_cache_user_center_rl)
    RelativeLayout cacheClearRelativeLayout;
    @Bind(R.id.size_cache_user_center_tv)
    TextView cacheSizeTextView;
    @Bind(R.id.version_code_user_center_rl)
    RelativeLayout versionRelativeLayout;
    @Bind(R.id.version_code_user_center_tv)
    TextView versionTextView;
    @Bind(R.id.about_user_center_rl)
    RelativeLayout aboutRelativeLayout;
    @Bind(R.id.logout_user_center_btn)
    Button logoutButton;

    private AccountManager accountManager;
    private DataManager dataManager;
    private StorageManager storageManager = StorageManager.getInstance();
    private static Map<String, AccountInfo> accountInfoMap = Maps.newHashMap();


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_center, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        accountManager = mActivity.getAccountManager();
        dataManager = new DataManager(accountManager.getAccount());
        nickTextView.setText(accountManager.getAccount().getServer());


        String signature = accountManager.getAccount().getSignature();
        AccountInfo info = getAccountInfoBySignature(signature);
        if (info != null) {
            String spaceUsed = info.getSpaceUsed();
            nickTextView.setText(spaceUsed);
        }

        calculateCacheSize();

        String appVersion = null;
        try {
            appVersion = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            KLog.e("app version name not found exception");
            appVersion = getString(R.string.not_available);
        }
        versionTextView.setText(appVersion);
    }

    @OnClick(R.id.logout_user_center_btn)
    public void logout() {
        // popup a dialog to confirm sign out request
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(getString(R.string.settings_account_sign_out_title));
        builder.setMessage(getString(R.string.settings_account_sign_out_confirm));
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Account account = accountManager.getAccount();

                // sign out operations
                accountManager.signOutAccount(account);

                // password auto clear
//                if (settingsMgr.isPasswordAutoClearEnabled()) {
//                    clearPasswordSilently();
//                }

                // restart BrowserActivity (will go to AccountsActivity)
                Intent intent = new Intent(mActivity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dismiss
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @OnClick(R.id.clear_cache_user_center_rl)
    public void clearCache() {
        ClearCacheTaskDialog dialog = new ClearCacheTaskDialog();
        dialog.setTaskDialogLisenter(new TaskDialog.TaskDialogListener() {
            @Override
            public void onTaskSuccess() {
                // refresh cache size
                calculateCacheSize();
                Toast.makeText(mActivity, getString(R.string.settings_clear_cache_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTaskFailed(SeafException e) {
                Toast.makeText(mActivity, getString(R.string.settings_clear_cache_failed), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getFragmentManager(), "DialogFragment");
    }

    @OnClick(R.id.about_user_center_rl)
    public void aboutSelf() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("NebulaBox应用是由清华大学开发");
        builder.setCancelable(true);
        builder.show();
    }

    private void calculateCacheSize() {
        ConcurrentAsyncTask.execute(new CalculateCacheTask());
    }


    class CalculateCacheTask extends AsyncTask<String, Void, Long> {

        @Override
        protected Long doInBackground(String... params) {
            return storageManager.getUsedSpace();
        }

        @Override
        protected void onPostExecute(Long aLong) {
            String total = FileUtils.byteCountToDisplaySize(aLong);
            cacheSizeTextView.setText(total);
        }

    }

    /**
     * automatically update Account info, like space usage, total space size, from background.
     */
    class RequestAccountInfoTask extends AsyncTask<Account, Void, AccountInfo> {

        @Override
        protected void onPreExecute() {
            mActivity.setSupportProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected AccountInfo doInBackground(Account... params) {
            AccountInfo accountInfo = null;

            if (params == null) return null;

            try {
                // get account info from server
                accountInfo = dataManager.getAccountInfo();
            } catch (Exception e) {
                KLog.e("could not get account info!" + e);
            }

            return accountInfo;
        }

        @Override
        protected void onPostExecute(AccountInfo accountInfo) {
            mActivity.setSupportProgressBarIndeterminateVisibility(false);

            if (accountInfo == null) return;

            // update Account info settings
            nickTextView.setText(getCurrentUserIdentifier());
            String spaceUsage = accountInfo.getSpaceUsed();
            cacheSizeTextView.setText(spaceUsage);
            Account currentAccount = dataManager.getAccount();
            if (currentAccount != null)
                saveAccountInfo(currentAccount.getSignature(), accountInfo);
        }
    }

    public void saveAccountInfo(String signature, AccountInfo accountInfo) {
        accountInfoMap.put(signature, accountInfo);
    }

    public AccountInfo getAccountInfoBySignature(String signature) {
        if (accountInfoMap.containsKey(signature))
            return accountInfoMap.get(signature);
        else
            return null;
    }


    public String getCurrentUserIdentifier() {
        Account account = accountManager.getAccount();

        if (account == null)
            return "";

        return account.getDisplayName();
    }

}
