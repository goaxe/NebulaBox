package com.seafile.seadroid2.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.account.AccountManager;
import com.seafile.seadroid2.account.Account;
import com.seafile.seadroid2.data.DataManager;
import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.data.SeafRepo;
import com.seafile.seadroid2.fileschooser.MultiFileChooserActivity;
import com.seafile.seadroid2.notification.DownloadNotificationProvider;
import com.seafile.seadroid2.transfer.DownloadTaskInfo;
import com.seafile.seadroid2.transfer.TransferService;
import com.seafile.seadroid2.ui.NavContext;
import com.seafile.seadroid2.ui.ToastUtils;
import com.seafile.seadroid2.ui.WidgetUtils;
import com.seafile.seadroid2.ui.adapter.SeafItemAdapter;
import com.seafile.seadroid2.ui.base.BaseActivity;
import com.seafile.seadroid2.ui.dialog.DeleteFileDialog;
import com.seafile.seadroid2.ui.dialog.TaskDialog;
import com.seafile.seadroid2.ui.fragment.StarredFragment;
import com.seafile.seadroid2.ui.fragment.main.PersonalFragment;
import com.seafile.seadroid2.ui.fragment.main.UploadFragment;
import com.seafile.seadroid2.ui.fragment.main.UserCenterFragment;
import com.seafile.seadroid2.ui.widget.CircleImageView;
import com.seafile.seadroid2.util.ConcurrentAsyncTask;
import com.seafile.seadroid2.util.Utils;
import com.seafile.seadroid2.util.log.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String DEBUG_TAG = "MainActivity";


        public static final String OPEN_FILE_DIALOG_FRAGMENT_TAG = "openfile_fragment";
    public static final String PASSWORD_DIALOG_FRAGMENT_TAG = "password_fragment";
    public static final String CHOOSE_APP_DIALOG_FRAGMENT_TAG = "choose_app_fragment";
    public static final String PICK_FILE_DIALOG_FRAGMENT_TAG = "pick_file_fragment";
    public static final int REQUEST_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;

    public static final String TAG_DELETE_FILE_DIALOG_FRAGMENT = "DeleteFileDialogFragment";
    public static final String TAG_DELETE_FILES_DIALOG_FRAGMENT = "DeleteFilesDialogFragment";
    public static final String TAG_RENAME_FILE_DIALOG_FRAGMENT = "RenameFileDialogFragment";
    public static final String TAG_COPY_MOVE_DIALOG_FRAGMENT = "CopyMoveDialogFragment";
    public static final String TAG_SORT_FILES_DIALOG_FRAGMENT = "SortFilesDialogFragment";


    public static final int PICK_FILES_REQUEST = 1;
    public static final int PICK_PHOTOS_VIDEOS_REQUEST = 2;
    public static final int PICK_FILE_REQUEST = 3;
    public static final int TAKE_PHOTO_REQUEST = 4;
    public static final int CHOOSE_COPY_MOVE_DEST_REQUEST = 5;
    public static final int DOWNLOAD_FILE_REQUEST = 6;


    public File takeCameraPhotoTempFile = null;


    private List<Integer> tabsImagesUnselectedList;
    private List<Integer> tabsImagesSelectedList;
    private List<Fragment> fragmentList;

    private AccountManager accountManager;
    private DataManager dataManager;
	private TransferService txService = null;

    private int currentFragmentIndex = 0;

    private NavContext navContext = new NavContext();

    @Bind(R.id.avatar_toolbar_main_iv)
    CircleImageView avatarImageView;
    @Bind(R.id.username_toolbar_main_tv)
    TextView userNameTextView;
    @Bind(R.id.search_toolbar_main_iv)
    ImageView searchImageView;
    @Bind(R.id.more_toolbar_main_iv)
    ImageView moreImageView;
    @Bind(R.id.content_main_fl)
    FrameLayout contentFrameLayout;

    @Bind({R.id.personal_main_iv, R.id.upload_main_iv, R.id.star_main_iv, R.id.usercenter_main_iv})
    List<ImageView> tabsImageViewList;
    @Bind({R.id.personal_main_ll, R.id.upload_main_ll, R.id.star_main_ll, R.id.usercenter_main_ll})
    List<LinearLayout> tabsLinearLayoutList;
    @Bind({R.id.personal_main_tv, R.id.upload_main_tv, R.id.star_main_tv, R.id.usercenter_main_tv})
    List<TextView> tabsTextViewList;

	ServiceConnection mConnection = new ServiceConnection() {
        @Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			TransferService.TransferBinder binder = (TransferService.TransferBinder) service;
			txService = binder.getService();

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
        ButterKnife.bind(this);

        initVariable();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : fragmentList) {
            transaction.add(R.id.content_main_fl, fragment);
        }
        transaction.commitAllowingStateLoss();
        KLog.e("=================" + currentFragmentIndex);
        Log.e(DEBUG_TAG, "==============" + currentFragmentIndex);
        switchFragment(currentFragmentIndex);

//        getSupportFragmentManager().beginTransaction().add(R.id.content_main_fl, fragmentList.get(currentFragmentIndex)).commitAllowingStateLoss();
        for (int i = 0; i < fragmentList.size(); i++) {
            tabsLinearLayoutList.get(i).setOnClickListener(this);
        }

        accountManager = new AccountManager(getApplicationContext());
        dataManager = new DataManager(accountManager.getAccount());

		Intent txIntent = new Intent(this, TransferService.class);
        startService(txIntent);
		Intent bIntent = new Intent(this, TransferService.class);
		bindService(bIntent, mConnection, Context.BIND_AUTO_CREATE);

        Intent intent = getIntent();
        String repoID = intent.getStringExtra("repoID");
        String repoName = intent.getStringExtra("repoName");
        String path = intent.getStringExtra("path");
        String dirID = intent.getStringExtra("dirID");
        if (repoID != null) {
            navContext.setRepoID(repoID);
            navContext.setRepoName(repoName);
            navContext.setDir(path, dirID);
        }
    }

    public NavContext getNavContext() {
        return navContext;
    }

    private void initVariable() {
        tabsImagesUnselectedList = new ArrayList<>();
        tabsImagesUnselectedList.add(R.drawable.ic_filter_drama_white_36dp);
        tabsImagesUnselectedList.add(R.drawable.upload_36);
        tabsImagesUnselectedList.add(R.drawable.ic_share_white_36dp);
        tabsImagesUnselectedList.add(R.drawable.self_48);

        tabsImagesSelectedList = new ArrayList<>();
        tabsImagesSelectedList.add(R.drawable.ic_filter_drama_black_36dp);
        tabsImagesSelectedList.add(R.drawable.upload_grey_36);
        tabsImagesSelectedList.add(R.drawable.share_grey_36);
        tabsImagesSelectedList.add(R.drawable.self_grey_48);

        fragmentList = new ArrayList<>();
        fragmentList.add(PersonalFragment.newInstance(new PersonalFragment()));
        fragmentList.add(UploadFragment.newInstance(new UploadFragment()));
//        fragmentList.add(StarListFragment.newInstance(new StarListFragment()));
        fragmentList.add(new StarredFragment());
        fragmentList.add(UserCenterFragment.newInstance(new UserCenterFragment()));
    }

    /**
     * 切换fragment
     *
     * @param tabIndex 下一个fragment的下标(要切换到的fragment)
     */
    private void switchFragment(int tabIndex) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment nextFragment = fragmentList.get(tabIndex);
//        Fragment currentFragment = fragmentList.get(currentFragmentIndex);
        for (Fragment fragment : fragmentList) {
            if (fragment != nextFragment) {
                transaction.hide(fragment);
            }
        }
        transaction.show(nextFragment).commitAllowingStateLoss();
        switchTextAndImage(tabIndex);
        this.currentFragmentIndex = tabIndex;

//
//        if (nextFragment != currentFragment) {
//            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
//            if (nextFragment.isAdded()) {
//                transaction.hide(currentFragment).show(nextFragment).commitAllowingStateLoss();
//            } else {
//                transaction.hide(currentFragment).add(R.id.content_main_fl, nextFragment).commitAllowingStateLoss();
//            }
//            switchTextAndImage(tabIndex);
//            this.currentFragmentIndex = tabIndex;
//        }
    }

    /**
     * 切换fragment tab字体颜色变化和图标变化
     *
     * @param tabIndex 下一个fragment的下标(要切换到的fragment)
     */
    private void switchTextAndImage(int tabIndex) {
        tabsImageViewList.get(currentFragmentIndex).setImageResource(tabsImagesUnselectedList.get(currentFragmentIndex));
        tabsImageViewList.get(tabIndex).setImageResource(tabsImagesSelectedList.get(tabIndex));
        tabsTextViewList.get(currentFragmentIndex).setTextColor(ContextCompat.getColor(this, R.color.tab_main_unselected));
        tabsTextViewList.get(tabIndex).setTextColor(ContextCompat.getColor(this, R.color.tab_main_selected));
    }


    @Override
    protected void onDestroy() {
		if (txService != null) {
			unbindService(mConnection);
			txService = null;
		}
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < tabsLinearLayoutList.size(); i++) {
            if (tabsLinearLayoutList.get(i).getId() == v.getId()) {
                switchFragment(i);
            }
        }
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    @Override
    public void onBackPressed() {
        if (currentFragmentIndex != 0 || !navContext.inRepo()) {
            super.onBackPressed();
        }
        if (navContext.isRepoRoot()) {
            navContext.setRepoID(null);
        } else {
            String parentPath = Utils.getParentPath(navContext
                    .getDirPath());
            navContext.setDir(parentPath, null);
        }
        PersonalFragment personalFragment = (PersonalFragment) fragmentList.get(currentFragmentIndex);
        personalFragment.refreshView(false);
    }

    private Fragment getFragment(int index) {
        return fragmentList.get(index);
    }

    private PersonalFragment getPersonalFragment() {
        return (PersonalFragment) getFragment(0);
    }

    private StarredFragment getStarredFragment() {
        return (StarredFragment) getFragment(2);
    }

    public void showFileBottomSheet(String title, final SeafDirent dirent) {
        getPersonalFragment().showFileBottomSheet(title, dirent);
    }

    public void showDirBottomSheet(String title, final SeafDirent dirent) {
        getPersonalFragment().showDirBottomSheet(title, dirent);
    }

    public void starFile(String srcRepoId, String srcDir, String srcFn) {
        getStarredFragment().doStarFile(srcRepoId, srcDir, srcFn);
    }

    public void downloadFile(String dir, String fileName) {
        String filePath = Utils.pathJoin(dir, fileName);
        Account account = accountManager.getAccount();
        Log.e(DEBUG_TAG, "===" + account.getServer() + "==========server");

        txService.addDownloadTask(account,
                navContext.getRepoName(),
                navContext.getRepoID(),
                filePath);

        if (!txService.hasDownloadNotifProvider()) {
            DownloadNotificationProvider provider = new DownloadNotificationProvider(txService.getDownloadTaskManager(),
                    txService);
            txService.saveDownloadNotifProvider(provider);
        }

        SeafItemAdapter adapter = getPersonalFragment().getAdapter();
        List<DownloadTaskInfo> infos = txService.getDownloadTaskInfosByPath(navContext.getRepoID(), dir);
        // update downloading progress
        adapter.setDownloadTaskList(infos);
    }

    public void deleteFile(String repoID, String repoName, String path) {
        doDelete(repoID, repoName, path, false);
    }

        public void deleteDir(String repoID, String repoName, String path) {
        doDelete(repoID, repoName, path, true);
    }

    private void doDelete(String repoID, String repoName, String path, boolean isdir) {
        final DeleteFileDialog dialog = new DeleteFileDialog();
        dialog.init(repoID, path, isdir, accountManager.getAccount());
        dialog.setTaskDialogLisenter(new TaskDialog.TaskDialogListener() {
            @Override
            public void onTaskSuccess() {
                ToastUtils.show(MainActivity.this, R.string.delete_successful);
                PersonalFragment reposFragment = getPersonalFragment();
                if (currentFragmentIndex == 0 && reposFragment != null) {
                    reposFragment.refreshView(true);
                }
            }
        });
        dialog.show(getSupportFragmentManager(), TAG_DELETE_FILE_DIALOG_FRAGMENT);
    }


    public AccountManager getAccountManager() {
        return accountManager;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_FILES_REQUEST:
                if (resultCode == RESULT_OK) {
                    Log.e(DEBUG_TAG, "in onActivityResult");
                    String[] paths = data.getStringArrayExtra(MultiFileChooserActivity.MULTI_FILES_PATHS);
                    if (paths == null) {
                        Log.e(DEBUG_TAG, "paths is null");

                        return;
                    }
                    ToastUtils.show(this, getString(R.string.added_to_upload_tasks));
                    for (String path : paths) {
                        Log.e(DEBUG_TAG, path);
                        txService.addUploadTask(accountManager.getAccount(), navContext.getRepoID(), navContext.getRepoName(), navContext.getDirPath(),  path, false, false);
                    }
                }
                break;

            case PICK_PHOTOS_VIDEOS_REQUEST:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> paths = data.getStringArrayListExtra("photos");
                    if (paths == null)
                        return;
                    ToastUtils.show(this, getString(R.string.added_to_upload_tasks));

                    for (String path : paths) {
                        txService.addUploadTask(accountManager.getAccount(), navContext.getRepoID(), navContext.getRepoName(), navContext.getDirPath(), path, false, false);
                    }
                }
                break;
            case TAKE_PHOTO_REQUEST:
                if (resultCode == RESULT_OK) {
                    ToastUtils.show(this, getString(R.string.take_photo_successfully));
                    if (!Utils.isNetworkOn()) {
                        ToastUtils.show(this, R.string.network_down);
                        return;
                    }

                    if(takeCameraPhotoTempFile == null) {
                        ToastUtils.show(this, getString(R.string.saf_upload_path_not_available));
                        Log.i(DEBUG_TAG, "Pick file request did not return a path");
                        return;
                    }
                    ToastUtils.show(this, getString(R.string.added_to_upload_tasks));
                    txService.addTaskToUploadQue(accountManager.getAccount(), navContext.getRepoID(), navContext.getRepoName(), navContext.getDirPath(), takeCameraPhotoTempFile.getAbsolutePath(), false, false);

                }
                break;
            case DOWNLOAD_FILE_REQUEST:
                if (resultCode == RESULT_OK) {
                    File file = new File(data.getStringExtra("path"));
                    WidgetUtils.showFile(MainActivity.this, file);
                }
        case PICK_FILE_REQUEST:
//            if (resultCode == RESULT_OK) {
//                if (!Utils.isNetworkOn()) {
//                    ToastUtils.show(this, R.string.network_down);
//                    return;
//                }
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    List<Uri> uriList = UtilsJellyBean.extractUriListFromIntent(data);
//                    if (uriList.size() > 0) {
//                        ConcurrentAsyncTask.execute(new SAFLoadRemoteFileTask(), uriList.toArray(new Uri[]{}));
//                    } else {
//                        ToastUtils.show(BrowserActivity.this, R.string.saf_upload_path_not_available);
//                    }
//                } else {
//                    Uri uri = data.getData();
//                    if (uri != null) {
//                        ConcurrentAsyncTask.execute(new SAFLoadRemoteFileTask(), uri);
//                    } else {
//                        ToastUtils.show(BrowserActivity.this, R.string.saf_upload_path_not_available);
//                    }
//                }
//            }
            break;


            default:
                break;
        }
    }

    public void onFileSelected(SeafDirent dirent) {
        final String fileName= dirent.name;
        final String repoName = navContext.getRepoName();
        final String repoID = navContext.getRepoID();
        final String dirPath = navContext.getDirPath();
        final String filePath = Utils.pathJoin(navContext.getDirPath(), fileName);
        final SeafRepo repo = dataManager.getCachedRepoByID(repoID);

        // Encrypted repo doesn\`t support gallery,
        // because pic thumbnail under encrypted repo was not supported at the server side
        if (Utils.isViewableImage(fileName)
                && repo != null && !repo.encrypted) {
            WidgetUtils.startGalleryActivity(this, repoName, repoID, dirPath, fileName, accountManager.getAccount());
            return;
        }

        final File localFile = dataManager.getLocalCachedFile(repoName, repoID, filePath, dirent.id);
        if (localFile != null) {
            WidgetUtils.showFile(this, localFile);
            return;
        }

        if (repo == null) return;

        startFileActivity(repoName, repoID, filePath, repo.canLocalDecrypt(), repo.encVersion);
    }

    private void startFileActivity(String repoName, String repoID, String filePath, boolean byBlock, int encVersion) {
        int taskID = 0;
        if (byBlock) {
            taskID = txService.addDownloadTask(accountManager.getAccount(), repoName, repoID, filePath, true, encVersion);
        } else {
            taskID = txService.addDownloadTask(accountManager.getAccount(), repoName, repoID, filePath);
        }
        Intent intent = new Intent(this, FileActivity.class);
        intent.putExtra("repoName", repoName);
        intent.putExtra("repoID", repoID);
        intent.putExtra("filePath", filePath);
        intent.putExtra("account", accountManager.getAccount());
        intent.putExtra("taskID", taskID);
        startActivityForResult(intent, DOWNLOAD_FILE_REQUEST);
    }






}
