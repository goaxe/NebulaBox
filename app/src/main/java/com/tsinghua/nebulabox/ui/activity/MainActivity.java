package com.tsinghua.nebulabox.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.google.common.collect.Lists;
import com.tsinghua.nebulabox.R;
import com.tsinghua.nebulabox.SeafException;
import com.tsinghua.nebulabox.account.Account;
import com.tsinghua.nebulabox.account.AccountManager;
import com.tsinghua.nebulabox.data.DataManager;
import com.tsinghua.nebulabox.data.SeafDirent;
import com.tsinghua.nebulabox.data.SeafRepo;
import com.tsinghua.nebulabox.data.SeafStarredFile;
import com.tsinghua.nebulabox.fileschooser.MultiFileChooserActivity;
import com.tsinghua.nebulabox.notification.DownloadNotificationProvider;
import com.tsinghua.nebulabox.transfer.DownloadTaskInfo;
import com.tsinghua.nebulabox.transfer.TransferService;
import com.tsinghua.nebulabox.ui.CopyMoveContext;
import com.tsinghua.nebulabox.ui.NavContext;
import com.tsinghua.nebulabox.ui.ToastUtils;
import com.tsinghua.nebulabox.ui.WidgetUtils;
import com.tsinghua.nebulabox.ui.adapter.SeafItemAdapter;
import com.tsinghua.nebulabox.ui.base.BaseActivity;
import com.tsinghua.nebulabox.ui.dialog.CopyMoveDialog;
import com.tsinghua.nebulabox.ui.dialog.DeleteFileDialog;
import com.tsinghua.nebulabox.ui.dialog.RenameFileDialog;
import com.tsinghua.nebulabox.ui.dialog.TaskDialog;
import com.tsinghua.nebulabox.ui.fragment.StarredFragment;
import com.tsinghua.nebulabox.ui.fragment.main.ReposFragment;
import com.tsinghua.nebulabox.ui.fragment.main.UserCenterFragment;
import com.tsinghua.nebulabox.util.ConcurrentAsyncTask;
import com.tsinghua.nebulabox.util.Utils;
import com.tsinghua.nebulabox.util.UtilsJellyBean;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity implements View.OnClickListener, StarredFragment.OnStarredFileSelectedListener {

    @Override
    public void onStarredFileSelected(SeafStarredFile starredFile) {
        final String repoID = starredFile.getRepoID();
        final SeafRepo repo = dataManager.getCachedRepoByID(repoID);
        if (repo == null) return;

        final String repoName = repo.getName();
        final String filePath = starredFile.getPath();
        final String dirPath = Utils.getParentPath(filePath);

        // Encrypted repo doesn\`t support gallery,
        // because pic thumbnail under encrypted repo was not supported at the server side
        if (Utils.isViewableImage(starredFile.getTitle()) && !repo.encrypted) {
            WidgetUtils.startGalleryActivity(this, repoName, repoID, dirPath, starredFile.getTitle(), accountManager.getAccount());
            return;
        }

        final File localFile = dataManager.getLocalCachedFile(repoName, repoID, filePath, null);
        if (localFile != null) {
            WidgetUtils.showFile(this, localFile);
            return;
        }

        startFileActivity(repoName, repoID, filePath, repo.canLocalDecrypt(), repo.encVersion);

    }

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

    public static final int INDEX_LIBRARY_TAB = 0;


    public File takeCameraPhotoTempFile = null;

    private List<Integer> tabsImagesUnselectedList;
    private List<Integer> tabsImagesSelectedList;
    private List<Fragment> fragmentList;

    private AccountManager accountManager;
    private DataManager dataManager;
    private TransferService txService = null;
    private CopyMoveContext copyMoveContext;
    private Intent copyMoveIntent;

    private int currentFragmentIndex = 0;

    private NavContext navContext = new NavContext();

    //    @Bind(R.id.avatar_toolbar_main_iv)
//    CircleImageView avatarImageView;
//    @Bind(R.id.username_toolbar_main_tv)
//    TextView userNameTextView;
//    @Bind(R.id.search_toolbar_main_iv)
//    ImageView searchImageView;
//    @Bind(R.id.more_toolbar_main_iv)
//    ImageView moreImageView;
    @Bind(R.id.title_toolbar_main_tv)
    TextView titleTextView;
    @Bind(R.id.subtitle_toolbar_main_tv)
    public TextView subTitleTextView;
    @Bind(R.id.content_main_fl)
    FrameLayout contentFrameLayout;

    @Bind({R.id.personal_main_iv, R.id.star_main_iv, R.id.usercenter_main_iv})
    List<ImageView> tabsImageViewList;
    @Bind({R.id.personal_main_ll, R.id.star_main_ll, R.id.usercenter_main_ll})
    List<LinearLayout> tabsLinearLayoutList;
    @Bind({R.id.personal_main_tv, R.id.star_main_tv, R.id.usercenter_main_tv})
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
        tabsImagesSelectedList = new ArrayList<>();
        tabsImagesSelectedList.add(R.drawable.ic_filter_drama_white_36dp);
        tabsImagesSelectedList.add(R.drawable.ic_star_blue);
        tabsImagesSelectedList.add(R.drawable.ic_center_blue);

        tabsImagesUnselectedList = new ArrayList<>();
        tabsImagesUnselectedList.add(R.drawable.ic_self_grey);
        tabsImagesUnselectedList.add(R.drawable.ic_star_gray);
        tabsImagesUnselectedList.add(R.drawable.self_grey_48);

        fragmentList = new ArrayList<>();
        fragmentList.add(ReposFragment.newInstance(new ReposFragment()));
//        fragmentList.add(UploadFragment.newInstance(new UploadFragment()));
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

        if (tabIndex == 0) {
            titleTextView.setText("空间");
            subTitleTextView.setVisibility(View.VISIBLE);
        } else if (tabIndex == 1) {
            titleTextView.setText("星标");
            subTitleTextView.setVisibility(View.GONE);
        } else {
            titleTextView.setText("个人中心");
            subTitleTextView.setVisibility(View.GONE);
        }
    }


    public ReposFragment getReposFragment() {
        return (ReposFragment) getFragment(0);
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // We can't show the CopyMoveDialog in onActivityResult, this is a
        // workaround found in
        // http://stackoverflow.com/questions/16265733/failure-delivering-result-onactivityforresult/18345899#18345899
        if (copyMoveIntent != null) {
            String dstRepoId, dstDir;
            dstRepoId = copyMoveIntent.getStringExtra(SeafilePathChooserActivity.DATA_REPO_ID);
            dstDir = copyMoveIntent.getStringExtra(SeafilePathChooserActivity.DATA_DIR);
            copyMoveContext.setDest(dstRepoId, dstDir);
            doCopyMove();
            copyMoveIntent = null;
        }
    }

    private void doCopyMove() {
        if (!copyMoveContext.checkCopyMoveToSubfolder()) {
            ToastUtils.show(this, copyMoveContext.isCopy()
                    ? R.string.cannot_copy_folder_to_subfolder
                    : R.string.cannot_move_folder_to_subfolder);
            return;
        }
        final CopyMoveDialog dialog = new CopyMoveDialog();
        dialog.init(accountManager.getAccount(), copyMoveContext);
        dialog.setCancelable(false);
        dialog.setTaskDialogLisenter(new TaskDialog.TaskDialogListener() {
            @Override
            public void onTaskSuccess() {
                ToastUtils.show(MainActivity.this, copyMoveContext.isCopy()
                        ? R.string.copied_successfully
                        : R.string.moved_successfully);
                if (copyMoveContext.batch) {
                    List<SeafDirent> cachedDirents = getDataManager().getCachedDirents(getNavContext().getRepoID(),
                            getNavContext().getDirPath());

                    // refresh view
                    if (getReposFragment().getAdapter() != null) {
                        getReposFragment().getAdapter().setItems(cachedDirents);
                        getReposFragment().getAdapter().notifyDataSetChanged();
                    }

                    if (cachedDirents.size() == 0)
                        getReposFragment().getEmptyView().setVisibility(View.VISIBLE);
                    return;
                }

                if (copyMoveContext.isMove()) {
                    ReposFragment reposFragment = getReposFragment();
                    if (currentFragmentIndex == INDEX_LIBRARY_TAB && reposFragment != null) {
                        reposFragment.refreshView(false);
                    }
                }
            }
        });
        dialog.show(getSupportFragmentManager(), TAG_COPY_MOVE_DIALOG_FRAGMENT);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    @Override
    public void onBackPressed() {
        if (currentFragmentIndex != 0 || !navContext.inRepo()) {
            super.onBackPressed();
            return;
        }
        if (navContext.isRepoRoot()) {
            navContext.setRepoID(null);
        } else {
            String parentPath = Utils.getParentPath(navContext
                    .getDirPath());
            navContext.setDir(parentPath, null);
        }
        subTitleTextView.setText(navContext.getDirPath());
        ReposFragment reposFragment = (ReposFragment) fragmentList.get(currentFragmentIndex);
        reposFragment.refreshView(false);
    }

    private Fragment getFragment(int index) {
        return fragmentList.get(index);
    }

    public ReposFragment getPersonalFragment() {
        return (ReposFragment) getFragment(0);
    }

    public StarredFragment getStarredFragment() {
        return (StarredFragment) getFragment(1);
    }

    public void showFileBottomSheet(String title, final SeafDirent dirent) {
        getPersonalFragment().showFileBottomSheet(title, dirent);
    }

    public void showDirBottomSheet(String title, final SeafDirent dirent) {
        getPersonalFragment().showDirBottomSheet(title, dirent);
    }

    public void shareFile(String repoID, String path) {
        WidgetUtils.chooseShareApp(this, repoID, path, false, accountManager.getAccount());
    }

    public void shareDir(String repoID, String path) {
        WidgetUtils.chooseShareApp(this, repoID, path, true, accountManager.getAccount());
    }

    public void starFile(String srcRepoId, String srcDir, String srcFn) {
        getStarredFragment().doStarFile(srcRepoId, srcDir, srcFn);
    }

    public void renameFile(String repoID, String repoName, String path) {
        doRename(repoID, repoName, path, false);
    }

    public void renameDir(String repoID, String repoName, String path) {
        doRename(repoID, repoName, path, true);
    }

    public void copyFile(String srcRepoId, String srcRepoName, String srcDir, String srcFn, boolean isdir) {
        chooseCopyMoveDest(srcRepoId, srcRepoName, srcDir, srcFn, isdir, CopyMoveContext.OP.COPY);
    }

    public void moveFile(String srcRepoId, String srcRepoName, String srcDir, String srcFn, boolean isdir) {
        chooseCopyMoveDest(srcRepoId, srcRepoName, srcDir, srcFn, isdir, CopyMoveContext.OP.MOVE);
    }

    public void deleteDir(String repoID, String repoName, String path) {
        doDelete(repoID, repoName, path, true);
    }


    private void doRename(String repoID, String repoName, String path, boolean isdir) {
        final RenameFileDialog dialog = new RenameFileDialog();
        dialog.init(repoID, path, isdir, getDataManager().getAccount());
        dialog.setTaskDialogLisenter(new TaskDialog.TaskDialogListener() {
            @Override
            public void onTaskSuccess() {
                ToastUtils.show(MainActivity.this, R.string.rename_successful);
                ReposFragment reposFragment = (ReposFragment) fragmentList.get(0);
                if (currentFragmentIndex == 0 && reposFragment != null) {
                    reposFragment.refreshView(false);
                }
            }
        });
        dialog.show(getSupportFragmentManager(), TAG_RENAME_FILE_DIALOG_FRAGMENT);
    }

    private void chooseCopyMoveDest(String repoID, String repoName, String path,
                                    String filename, boolean isdir, CopyMoveContext.OP op) {
        copyMoveContext = new CopyMoveContext(repoID, repoName, path, filename,
                isdir, op);
        Intent intent = new Intent(this, SeafilePathChooserActivity.class);
        intent.putExtra(SeafilePathChooserActivity.DATA_ACCOUNT, accountManager.getAccount());
        SeafRepo repo = dataManager.getCachedRepoByID(repoID);
        if (repo.encrypted) {
            intent.putExtra(SeafilePathChooserActivity.ENCRYPTED_REPO_ID, repoID);
        }
        startActivityForResult(intent, CHOOSE_COPY_MOVE_DEST_REQUEST);
        return;
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

    private void doDelete(String repoID, String repoName, String path, boolean isdir) {
        final DeleteFileDialog dialog = new DeleteFileDialog();
        dialog.init(repoID, path, isdir, accountManager.getAccount());
        dialog.setTaskDialogLisenter(new TaskDialog.TaskDialogListener() {
            @Override
            public void onTaskSuccess() {
                ToastUtils.show(MainActivity.this, R.string.delete_successful);
                ReposFragment reposFragment = getPersonalFragment();
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
                        txService.addUploadTask(accountManager.getAccount(), navContext.getRepoID(), navContext.getRepoName(), navContext.getDirPath(), path, false, false);
                    }
                }
                break;

            case PICK_FILE_REQUEST:
                if (resultCode == RESULT_OK) {
                    List<Uri> uriList = UtilsJellyBean.extractUriListFromIntent(data);
                    Log.e(DEBUG_TAG, "pick_file_request, uriSize = " + uriList.size());
                    if (uriList.size() > 0) {
//                        ConcurrentAsyncTask.execute(new SAFLoadRemoteFileTask(), uriList.toArray(new Uri[]{}));
                        for (Uri uri : uriList) {
                            InputStream in = null;
                            OutputStream out = null;

                            try {
                                File tempDir = DataManager.createTempDir();
                                File tempFile = new File(tempDir, Utils.getFilenamefromUri(MainActivity.this, uri));

                                if (!tempFile.createNewFile()) {
                                    throw new RuntimeException("could not create temporary file");
                                }

                                in = getContentResolver().openInputStream(uri);
                                out = new FileOutputStream(tempFile);
                                IOUtils.copy(in, out);

                                String path = tempFile.getAbsolutePath();
                                Log.e(DEBUG_TAG, "path is " + path);
                                txService.addUploadTask(accountManager.getAccount(), navContext.getRepoID(), navContext.getRepoName(), navContext.getDirPath(), path, false, false);

                            } catch (IOException e) {
                                Log.d(DEBUG_TAG, "Could not open requested document", e);
                            } catch (RuntimeException e) {
                                Log.d(DEBUG_TAG, "Could not open requested document", e);
                            } finally {
                                IOUtils.closeQuietly(in);
                                IOUtils.closeQuietly(out);
                            }
                        }
                    } else {
                        ToastUtils.show(MainActivity.this, R.string.saf_upload_path_not_available);
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

                    if (takeCameraPhotoTempFile == null) {
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
                break;
            case CHOOSE_COPY_MOVE_DEST_REQUEST:
                if (resultCode == RESULT_OK) {
                    if (!Utils.isNetworkOn()) {
                        ToastUtils.show(this, R.string.network_down);
                        return;
                    }

                    copyMoveIntent = data;

                    onPostResume();
                }
                break;

            default:
                break;
        }
    }

    public void onFileSelected(SeafDirent dirent) {
        final String fileName = dirent.name;
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

    public void deleteFiles(final String repoID, String path, List<SeafDirent> dirents) {
        final DeleteFileDialog dialog = new DeleteFileDialog();
        dialog.init(repoID, path, dirents, accountManager.getAccount());
        dialog.setCancelable(false);
        dialog.setTaskDialogLisenter(new TaskDialog.TaskDialogListener() {
            @Override
            public void onTaskSuccess() {
                ToastUtils.show(MainActivity.this, R.string.delete_successful);
                if (getDataManager() != null) {
                    List<SeafDirent> cachedDirents = getDataManager().getCachedDirents(repoID,
                            getNavContext().getDirPath());
                    getReposFragment().getAdapter().setItems(cachedDirents);
                    getReposFragment().getAdapter().notifyDataSetChanged();
                    // update contextual action bar (CAB) title
                    getReposFragment().updateContextualActionBar();
                    if (cachedDirents.size() == 0)
                        getReposFragment().getEmptyView().setVisibility(View.VISIBLE);
                }
            }
        });
        dialog.show(getSupportFragmentManager(), TAG_DELETE_FILES_DIALOG_FRAGMENT);
    }

    public void copyFiles(String srcRepoId, String srcRepoName, String srcDir, List<SeafDirent> dirents) {
        chooseCopyMoveDestForMultiFiles(srcRepoId, srcRepoName, srcDir, dirents, CopyMoveContext.OP.COPY);
    }

    private void chooseCopyMoveDestForMultiFiles(String repoID, String repoName, String dirPath, List<SeafDirent> dirents, CopyMoveContext.OP op) {
        copyMoveContext = new CopyMoveContext(repoID, repoName, dirPath, dirents, op);
        Intent intent = new Intent(this, SeafilePathChooserActivity.class);
        intent.putExtra(SeafilePathChooserActivity.DATA_ACCOUNT, accountManager.getAccount());
        SeafRepo repo = getDataManager().getCachedRepoByID(repoID);
        if (repo.encrypted) {
            intent.putExtra(SeafilePathChooserActivity.ENCRYPTED_REPO_ID, repoID);
        }
        startActivityForResult(intent, MainActivity.CHOOSE_COPY_MOVE_DEST_REQUEST);
    }

    public void moveFiles(String srcRepoId, String srcRepoName, String srcDir, List<SeafDirent> dirents) {
        chooseCopyMoveDestForMultiFiles(srcRepoId, srcRepoName, srcDir, dirents, CopyMoveContext.OP.MOVE);
    }

    public void downloadFiles(String repoID, String repoName, String dirPath, List<SeafDirent> dirents) {
        if (!Utils.isNetworkOn()) {
            ToastUtils.show(this, R.string.network_down);
            return;
        }

        DownloadFilesTask task = new DownloadFilesTask(repoID, repoName, dirPath, dirents);
        ConcurrentAsyncTask.execute(task);
    }

    class DownloadFilesTask extends AsyncTask<Void, Void, Void> {
        private String repoID, repoName, dirPath;
        private List<SeafDirent> dirents;
        private SeafException err;
        private int fileCount;

        public DownloadFilesTask(String repoID, String repoName, String dirPath, List<SeafDirent> dirents) {
            this.repoID = repoID;
            this.repoName = repoName;
            this.dirPath = dirPath;
            this.dirents = dirents;
        }

        @Override
        protected void onPreExecute() {
            showLoadingDialog();

        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<String> dirPaths = Lists.newArrayList(dirPath);
            for (int i = 0; i < dirPaths.size(); i++) {
                if (i > 0) {
                    try {
                        dirents = getDataManager().getDirentsFromServer(repoID, dirPaths.get(i));
                    } catch (SeafException e) {
                        err = e;
                        Log.e(DEBUG_TAG, e.getMessage() + e.getCode());
                    }
                }

                if (dirents == null)
                    continue;

                for (SeafDirent seafDirent : dirents) {
                    if (seafDirent.isDir()) {
                        // download files recursively
                        dirPaths.add(Utils.pathJoin(dirPaths.get(i), seafDirent.name));
                    } else {
                        File localCachedFile = getDataManager().getLocalCachedFile(repoName,
                                repoID,
                                Utils.pathJoin(dirPaths.get(i),
                                        seafDirent.name),
                                seafDirent.id);
                        if (localCachedFile != null) {
                            continue;
                        }

                        // txService maybe null if layout orientation has changed
                        // e.g. landscape and portrait switch
                        if (txService == null)
                            return null;

                        final SeafRepo repo = dataManager.getCachedRepoByID(repoID);
                        if (repo != null && repo.canLocalDecrypt()) {
                            txService.addTaskToDownloadQue(accountManager.getAccount(),
                                    repoName,
                                    repoID,
                                    Utils.pathJoin(dirPaths.get(i),
                                            seafDirent.name),
                                    true,
                                    repo.encVersion);
                        } else {
                            txService.addTaskToDownloadQue(accountManager.getAccount(),
                                    repoName,
                                    repoID,
                                    Utils.pathJoin(dirPaths.get(i),
                                            seafDirent.name));
                        }
                        fileCount++;
                    }

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // update ui
            dismissLoadingDialog();

            if (err != null) {
                ToastUtils.show(MainActivity.this, R.string.transfer_list_network_error);
                return;
            }

            if (fileCount == 0)
                ToastUtils.show(MainActivity.this, R.string.transfer_download_no_task);
            else {
                ToastUtils.show(MainActivity.this,
                        getResources().getQuantityString(R.plurals.transfer_download_started,
                                fileCount,
                                fileCount));

                if (!txService.hasDownloadNotifProvider()) {
                    DownloadNotificationProvider provider =
                            new DownloadNotificationProvider(txService.getDownloadTaskManager(),
                            txService);
                    txService.saveDownloadNotifProvider(provider);
                }

            }
        }
    }

}
