package com.seafile.seadroid2.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.account.AccountManager;
import com.seafile.seadroid2.bean.Account;
import com.seafile.seadroid2.bean.AccountInfo;
import com.seafile.seadroid2.data.DataManager;
import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.data.SeafRepo;
import com.seafile.seadroid2.data.ServerInfo;
import com.seafile.seadroid2.fileschooser.MultiFileChooserActivity;
import com.seafile.seadroid2.global.AccountsSharedPreferencesHelper;
import com.seafile.seadroid2.network.SeafConnection;
import com.seafile.seadroid2.transfer.TransferService;
import com.seafile.seadroid2.ui.ToastUtils;
import com.seafile.seadroid2.ui.base.BaseActivity;
import com.seafile.seadroid2.ui.dialog.UploadChoiceDialog;
import com.seafile.seadroid2.ui.fragment.main.PersonalFragment;
import com.seafile.seadroid2.ui.fragment.main.ShareFragment;
import com.seafile.seadroid2.ui.fragment.main.StarListFragment;
import com.seafile.seadroid2.ui.fragment.main.UploadFragment;
import com.seafile.seadroid2.ui.fragment.main.UserCenterFragment;
import com.seafile.seadroid2.ui.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class TestActivity extends BaseActivity implements View.OnClickListener {


    public static final int PICK_FILES_REQUEST = 1;
    public static final int PICK_PHOTOS_VIDEOS_REQUEST = 2;
    public static final int PICK_FILE_REQUEST = 3;
    public static final int TAKE_PHOTO_REQUEST = 4;
    public static final int CHOOSE_COPY_MOVE_DEST_REQUEST = 5;
    public static final int DOWNLOAD_FILE_REQUEST = 6;

    public static final String PICK_FILE_DIALOG_FRAGMENT_TAG = "pick_file_fragment";

    public static final String DEBUG_TAG = "TestActivity";
    private List<SeafRepo> repos;


    private List<Integer> tabsImagesUnselectedList;
    private List<Integer> tabsImagesSelectedList;
    private List<Fragment> fragmentList;

    private Button uploadFileBtn;
    private Button commonApiBtn;
    private Button transferBtn;
    private Activity mActivity;

    private AccountManager accountManager;
    private DataManager dataManager;
    private TransferService txService = null;

    //	private Fragment currentFragment;
    private int currentFragmentIndex = 0;

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

    @Bind({R.id.personal_main_iv, R.id.share_main_iv, R.id.upload_main_iv, R.id.star_main_iv, R.id.usercenter_main_iv})
    List<ImageView> tabsImageViewList;
    @Bind({R.id.personal_main_ll, R.id.share_main_ll, R.id.upload_main_ll, R.id.star_main_ll, R.id.usercenter_main_ll})
    List<LinearLayout> tabsLinearLayoutList;
    @Bind({R.id.personal_main_tv, R.id.share_main_tv, R.id.upload_main_tv, R.id.star_main_tv, R.id.usercenter_main_tv})
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

        getSupportFragmentManager().beginTransaction().add(R.id.content_main_fl, fragmentList.get(currentFragmentIndex)).commitAllowingStateLoss();


        for (int i = 0; i < fragmentList.size(); i++) {
            tabsLinearLayoutList.get(i).setOnClickListener(this);
        }

        uploadFileBtn = (Button) findViewById(R.id.upload_file);
        uploadFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFile();
            }
        });

        commonApiBtn = (Button) findViewById(R.id.common_api);
        commonApiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Account loginAccount = new Account("http://192.168.199.144:8000/", "1@qq.com", null);
                new MyThread(loginAccount).start();

            }
        });

        mActivity = this;

        transferBtn = (Button) findViewById(R.id.transfer_list_btn);
        transferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, TransferActivity.class);
                startActivity(intent);
            }
        });

/*		commonApiBtn = (Button) findViewById(R.id.buttion1);
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
		});*/
        accountManager = new AccountManager(getApplicationContext());
        AccountsSharedPreferencesHelper accountsSharedPreferencesHelper = AccountsSharedPreferencesHelper.getInstance(this);
        dataManager = new DataManager(new Account(accountsSharedPreferencesHelper.getServerUrl(), accountsSharedPreferencesHelper.getAccountName(), accountsSharedPreferencesHelper.getTokenName()));

        Intent txIntent = new Intent(this, TransferService.class);
        startService(txIntent);

        Intent bIntent = new Intent(this, TransferService.class);
        bindService(bIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

//	@Override
//	protected int getFragmentContentId() {
//		return R.id.content_main_fl;
//	}

    private void pickFile() {
//         if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
        UploadChoiceDialog dialog = new UploadChoiceDialog();
        dialog.show(getSupportFragmentManager(), PICK_FILE_DIALOG_FRAGMENT_TAG);
//        } else {
//            Intent target = Utils.createGetContentIntent();
//            Intent intent = Intent.createChooser(target, getString(R.string.choose_file));
//        Log.e(DEBUG_TAG, "start choose");
//
//            startActivityForResult(intent, TestActivity.PICK_FILE_REQUEST);
////        }
    }

    private void initVariable() {
        tabsImagesUnselectedList = new ArrayList<>();
        tabsImagesUnselectedList.add(R.drawable.ic_filter_drama_white_36dp);
        tabsImagesUnselectedList.add(R.drawable.ic_share_white_36dp);
        tabsImagesUnselectedList.add(R.drawable.upload_36);
        tabsImagesUnselectedList.add(R.drawable.ic_share_white_36dp);
        tabsImagesUnselectedList.add(R.drawable.self_48);

        tabsImagesSelectedList = new ArrayList<>();
        tabsImagesSelectedList.add(R.drawable.ic_filter_drama_black_36dp);
        tabsImagesSelectedList.add(R.drawable.share_grey_36);
        tabsImagesSelectedList.add(R.drawable.upload_grey_36);
        tabsImagesSelectedList.add(R.drawable.share_grey_36);
        tabsImagesSelectedList.add(R.drawable.self_grey_48);

        fragmentList = new ArrayList<>();
        fragmentList.add(PersonalFragment.newInstance(new PersonalFragment()));
        fragmentList.add(ShareFragment.newInstance(new ShareFragment()));
        fragmentList.add(UploadFragment.newInstance(new UploadFragment()));
        fragmentList.add(StarListFragment.newInstance(new StarListFragment()));
        fragmentList.add(UserCenterFragment.newInstance(new UserCenterFragment()));
    }

    /**
     * 切换fragment
     *
     * @param tabIndex 下一个fragment的下标(要切换到的fragment)
     */
    private void switchFragment(int tabIndex) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment = fragmentList.get(tabIndex);
        Fragment currentFragment = fragmentList.get(currentFragmentIndex);
        if (nextFragment != currentFragment) {
            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (nextFragment.isAdded()) {
                transaction.hide(currentFragment).show(nextFragment).commitAllowingStateLoss();
            } else {
                transaction.hide(currentFragment).add(R.id.content_main_fl, nextFragment).commitAllowingStateLoss();
            }
            switchTextAndImage(tabIndex);
            this.currentFragmentIndex = tabIndex;
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SeafRepo repo = repos.get(0);
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
                        txService.addUploadTask(accountManager.getAccount(), repo.getID(), repo.getName(), "/", path, false, false);
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
                        txService.addUploadTask(accountManager.getAccount(), repo.getID(), repo.getName(), "/", path, false, false);
                    }
                }
                break;
            default:
                break;
        }
    }





/*	public void addUpdateTask(String repoID, String repoName, String targetDir, String localFilePath) {
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
	}*/

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
                Log.e(DEBUG_TAG, loginAccount.toString());
                Log.e(DEBUG_TAG, accountInfo.getEmail());
                loginAccount = new Account(loginAccount.server, accountInfo.getEmail(), loginAccount.token);
                ServerInfo serverInfo = dataManager.getServerInfo();

                accountManager.setAccount(loginAccount);
                accountManager.setServerInfo(serverInfo);

                Log.e(DEBUG_TAG, accountManager.getAccount().toString());
                Log.e(DEBUG_TAG, accountManager.getServerInfo().toString());
                repos = dataManager.getReposFromServer();
                for (SeafRepo repo : repos) {
                    Log.e(DEBUG_TAG, repo.getID() + " " + repo.getName() + " " + repo.getTitle());
                }

                SeafRepo downloadRepo = repos.get(0);
                SeafRepo uploadRepo = repos.get(1);
                List<SeafDirent> dirents = dataManager.getDirentsFromServer(downloadRepo.getID(), "/");
//				for (SeafDirent dirent : dirents) {
//					Log.e(DEBUG_TAG, dirent.getTitle() + " " + dirent.getSubtitle());
//					if (!dirent.isDir()) {
//						txService.addDownloadTask(accountManager.getAccount(), downloadRepo.getName(), downloadRepo.getID(), "/" + dirent.getTitle());
//					}
//				}

                // wait the download task finish, then we can upload our local files
//				txService.addUploadTask(accountManager.getAccount(), uploadRepo.getID(), uploadRepo.getName(), "/", "/storage/emulated/0/Seafile/1@qq.com (166.111.131.62)/askjdfkljsd/settings.jar", false, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
