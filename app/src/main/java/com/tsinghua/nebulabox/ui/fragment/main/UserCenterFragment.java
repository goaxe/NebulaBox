
package com.tsinghua.nebulabox.ui.fragment.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Maps;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tsinghua.nebulabox.R;
import com.tsinghua.nebulabox.SeafConnection;
import com.tsinghua.nebulabox.SeafException;
import com.tsinghua.nebulabox.SettingsManager;
import com.tsinghua.nebulabox.account.Account;
import com.tsinghua.nebulabox.account.AccountInfo;
import com.tsinghua.nebulabox.account.AccountManager;
import com.tsinghua.nebulabox.avatar.Avatar;
import com.tsinghua.nebulabox.avatar.AvatarManager;
import com.tsinghua.nebulabox.data.DataManager;
import com.tsinghua.nebulabox.data.StorageManager;
import com.tsinghua.nebulabox.ui.activity.LoginActivity;
import com.tsinghua.nebulabox.ui.base.BaseFragment;
import com.tsinghua.nebulabox.ui.dialog.ClearCacheTaskDialog;
import com.tsinghua.nebulabox.ui.dialog.SwitchStorageTaskDialog;
import com.tsinghua.nebulabox.ui.dialog.TaskDialog;
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
    ImageView avatarImageView;
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
    @Bind(R.id.cache_location_user_center_rl)
    RelativeLayout cacheLocationRelativeLayout;
    @Bind(R.id.cache_location_user_center_tv)
    TextView cacheLocationTextView;
//    @Bind(R.id.cache_location_view)
//    View cacheLocationView;
    @Bind(R.id.version_code_user_center_rl)
    RelativeLayout versionRelativeLayout;
    @Bind(R.id.version_code_user_center_tv)
    TextView versionTextView;
    @Bind(R.id.about_user_center_rl)
    RelativeLayout aboutRelativeLayout;
    @Bind(R.id.logout_user_center_btn)
    Button logoutButton;

    private AccountManager accountManager;
    private AvatarManager avatarManager;
    private DataManager dataManager;
    private SettingsManager settingsManager;
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
        avatarManager = new AvatarManager();
        settingsManager =SettingsManager.instance();
        dataManager = new DataManager(accountManager.getAccount());
        nickTextView.setText(accountManager.getAccount().getEmail());


        String signature = accountManager.getAccount().getSignature();
        AccountInfo info = getAccountInfoBySignature(signature);
        if (info != null) {
            String spaceUsed = info.getSpaceUsed();
            capacityTextView.setText(spaceUsed);
        }

        calculateCacheSize();
        loadAvatarUrls(48);
        ConcurrentAsyncTask.execute(new RequestAccountInfoTask(), accountManager.getAccount());


        String appVersion = null;
        try {
            appVersion = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            KLog.e("app version name not found exception");
            appVersion = getString(R.string.not_available);
        }
        versionTextView.setText(appVersion);

        if (storageManager.supportsMultipleStorageLocations()) {
            updateStorageLocationSummary();
        } else {
            cacheLocationRelativeLayout.setVisibility(View.GONE);
//            cacheLocationView.setVisibility(View.GONE);
        }
        settingsManager.registerSharedPreferencesListener(settingsListener);
    }

    private void updateStorageLocationSummary() {
        String summary = storageManager.getStorageLocation().description;
        cacheLocationTextView.setText(summary);
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

    @OnClick(R.id.cache_location_user_center_rl)
    public void changeCacheLocation() {
        new SwitchStorageTaskDialog().show(getFragmentManager(), "Select cache location");
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
        builder.setMessage(R.string.about_self_content);
        builder.setCancelable(true);
        builder.show();
    }

    private void calculateCacheSize() {
        ConcurrentAsyncTask.execute(new CalculateCacheTask());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        settingsManager.unregisterSharedPreferencesListener(settingsListener);
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
            capacityTextView.setText(spaceUsage);
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

    /**
     * asynchronously load avatars
     *
     * @param avatarSize set a avatar size in one of 24*24, 32*32, 48*48, 64*64, 72*72, 96*96
     */
    public void loadAvatarUrls(int avatarSize) {
//        List<Avatar> avatars;
//
//        if (!Utils.isNetworkOn() || !avatarManager.isNeedToLoadNewAvatars()) {
//            // Toast.makeText(AccountsActivity.this, getString(R.string.network_down), Toast.LENGTH_SHORT).show();
//
//            // use cached avatars
//            avatars = avatarManager.getAvatarList();
//
//            if (avatars == null) {
//                return;
//            }
//
//            // set avatars url to adapter
//            adapter.setAvatars((ArrayList<Avatar>) avatars);
//
//            // notify adapter data changed
//            adapter.notifyDataSetChanged();
//
//            return;
//        }

        LoadAvatarUrlsTask task = new LoadAvatarUrlsTask(avatarSize);

        ConcurrentAsyncTask.execute(task);

    }

    private class LoadAvatarUrlsTask extends AsyncTask<Void, Void, Avatar> {

        //        private List<Avatar> avatars;
        private int avatarSize;
        private SeafConnection httpConnection;

        public LoadAvatarUrlsTask(int avatarSize) {
            this.avatarSize = avatarSize;
//            this.avatars = Lists.newArrayList();
        }

        @Override
        protected Avatar doInBackground(Void... params) {
            // reuse cached avatars
//            avatars = avatarManager.getAvatarList();

            // contains accounts who don`t have avatars yet
//            List<Account> acts = avatarManager.getAccountsWithoutAvatars();

            // contains new avatars in order to persist them to database
//            List<Avatar> newAvatars = new ArrayList<Avatar>(acts.size());

            // load avatars from server
//            for (Account account : acts) {
            httpConnection = new SeafConnection(accountManager.getAccount());

            String avatarRawData = null;
            try {
                avatarRawData = httpConnection.getAvatar(accountManager.getAccount().getEmail(), avatarSize);
            } catch (SeafException e) {
                e.printStackTrace();
                return null;
            }

            Avatar avatar = avatarManager.parseAvatar(avatarRawData);
            if (avatar == null)
                return null;

            avatar.setSignature(accountManager.getAccount().getSignature());

//                avatars.add(avatar);

//                newAvatars.add(avatar);
//            }

            // save new added avatars to database
//            avatarManager.saveAvatarList(newAvatars);

            return avatar;
        }

        @Override
        protected void onPostExecute(Avatar avatar) {
            if (avatar == null) {
                return;
            }

            if (getAvatarUrl(accountManager.getAccount(), avatar) != null) {
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .extraForDownloader(accountManager.getAccount())
                        .showStubImage(R.drawable.default_avatar)
                        // .delayBeforeLoading(1000)
                        .showImageOnLoading(R.drawable.default_avatar)
                        .showImageForEmptyUri(R.drawable.default_avatar)
                        .showImageOnFail(R.drawable.default_avatar)
                        .resetViewBeforeLoading()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .displayer(new RoundedBitmapDisplayer(1000))
                        .build();
                ImageLoader.getInstance().displayImage(getAvatarUrl(accountManager.getAccount(), avatar), avatarImageView, options);
            }
            ImageLoader.getInstance().handleSlowNetwork(true);
        }

        private String getAvatarUrl(Account account, Avatar avatar) {
            if (avatar == null) {
                return null;
            }
//            for (Avatar avatar : avatars) {
            if (avatar.getSignature().equals(account.getSignature())) {
                return avatar.getUrl();
            }
//            }

            return null;
        }
    }

    class UpdateStorageSLocationSummaryTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void ret) {
            updateStorageLocationSummary();
        }

    }

    private SharedPreferences.OnSharedPreferenceChangeListener settingsListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                    switch (key) {
                        case SettingsManager.SHARED_PREF_STORAGE_DIR:
                            ConcurrentAsyncTask.execute(new UpdateStorageSLocationSummaryTask());
                            break;
                    }
                }
            };

}
