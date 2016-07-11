package com.seafile.seadroid2.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.account.AccountManager;
import com.seafile.seadroid2.ui.base.BaseActivity;
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


public class MainActivity extends BaseActivity implements View.OnClickListener {


	private List<Integer> tabsImagesUnselectedList;
	private List<Integer> tabsImagesSelectedList;
	private List<Fragment> fragmentList;

	private Button commonApiBtn;
	private Button transferBtn;
	private AccountManager accountManager;
//	private TransferService txService = null;

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

/*	ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			TransferService.TransferBinder binder = (TransferService.TransferBinder) service;
			txService = binder.getService();

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			txService = null;
		}
	};*/


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

/*		Intent txIntent = new Intent(this, TransferService.class);
		startService(txIntent);

		Intent bIntent = new Intent(this, TransferService.class);
		bindService(bIntent, mConnection, Context.BIND_AUTO_CREATE);*/
	}

//	@Override
//	protected int getFragmentContentId() {
//		return R.id.content_main_fl;
//	}

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

		fragmentList  = new ArrayList<>();
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
//		if (txService != null) {
//			unbindService(mConnection);
//			txService = null;
//		}
		super.onDestroy();
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


/*	class MyThread extends Thread {

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
	}*/
}
