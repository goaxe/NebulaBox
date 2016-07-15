package com.seafile.seadroid2.ui.fragment.main;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeafException;
import com.seafile.seadroid2.data.DataManager;
import com.seafile.seadroid2.data.SeafRepo;
import com.seafile.seadroid2.ui.adapter.FileListAdapter;
import com.seafile.seadroid2.ui.base.BaseFragment;
import com.seafile.seadroid2.ui.dialog.FileOptionDialog;
import com.seafile.seadroid2.util.ConcurrentAsyncTask;
import com.seafile.seadroid2.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人
 * Created by Alfred on 2016/7/11.
 */
public class PersonalFragment extends BaseFragment implements View.OnClickListener, FileOptionDialog.OnItemClickListener {

	@Bind(R.id.category_personal_tv)
	TextView categoryTextView;
	@Bind(R.id.sort_personal_tv)
	TextView sortTextView;
	@Bind(R.id.create_personal_tv)
	TextView createTextView;
	@Bind(R.id.transfer_personal_tv)
	TextView transferTextView;

	private String[] categoryOptionalList;
	private String[] sortOptionalList;

	private FileOptionDialog categoryDialog;
	private FileOptionDialog sortDialog;

	private List<SeafRepo> pictureList;
	private List<SeafRepo> videoList;
	private List<SeafRepo> movieList;
	private List<SeafRepo> txtList;
	private List<SeafRepo> appList;
	private List<SeafRepo> allList;

	private String[] pictureFormat;
	private String[] videoFormat;
	private String[] movieFormat;
	private String[] txtFormat;
	private String[] appFormat;

	private FileListAdapter adapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_personal, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		categoryOptionalList = getResources().getStringArray(R.array.category_files_options_array);
		sortOptionalList = getResources().getStringArray(R.array.sorts_files_options_array);
		categoryDialog = new FileOptionDialog();
		categoryDialog.setList(categoryOptionalList);
		sortDialog = new FileOptionDialog();
		sortDialog.setList(sortOptionalList);

		Resources resources = getResources();
		pictureFormat = resources.getStringArray(R.array.format_picture);
		videoFormat = resources.getStringArray(R.array.format_video);
		movieFormat = resources.getStringArray(R.array.format_movie);
		appFormat = resources.getStringArray(R.array.format_app);
		txtFormat = resources.getStringArray(R.array.format_app);
		
		allList = new ArrayList<>();
		adapter = new FileListAdapter(mActivity,allList);


	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}

	@OnClick({R.id.category_personal_tv, R.id.sort_personal_tv, R.id.create_personal_tv, R.id.transfer_personal_tv})
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.category_personal_tv:
				sortDialog.dismiss();
				categoryDialog.show(getFragmentManager(), "categoryDialog");
				break;
			case R.id.sort_personal_tv:
				categoryDialog.dismiss();
				sortDialog.show(getFragmentManager(), "sortDialog");
				break;
			case R.id.create_personal_tv:
				break;
			case R.id.transfer_personal_tv:
				break;
		}
	}

	@Override
	public void OnItemClick(DialogInterface dialog, int which) {
		switch (which) {
			case 1:
				//照片,按文件排序
				if (((FileOptionDialog) dialog).getList().length > 2) {
					//照片
					pictureList = Utils.categoryFile(allList,pictureFormat);
					adapter.setDatas(pictureList);
				}else {

				}
				break;
			case 2:
				//音乐,按时间倒序排序
				if (((FileOptionDialog) dialog).getList().length > 2) {
					//音乐
					videoList = Utils.categoryFile(allList,videoFormat);
					adapter.setDatas(videoList);
				}
				break;
			case 3:
				//影视
				movieList = Utils.categoryFile(allList,movieFormat);
				adapter.setDatas(movieList);
				break;
			case 4:
				//文档
				txtList = Utils.categoryFile(allList,txtFormat);
				adapter.setDatas(txtList);
				break;
			case 5:
				//应用
				appList = Utils.categoryFile(allList,appFormat);
				adapter.setDatas(appList);
				break;
			case 6:
				//全部
				adapter.setDatas(allList);
				break;
		}
	}

	private class LoadTask extends AsyncTask<Void, Void, List<SeafRepo> > {
		SeafException err = null;
		DataManager dataManager;

		public LoadTask(DataManager dataManager) {
			this.dataManager = dataManager;
		}

		@Override
		protected void onPreExecute() {
//			if (mRefreshType == REFRESH_ON_CLICK
//					|| mRefreshType == REFRESH_ON_OVERFLOW_MENU
//					|| mRefreshType == REFRESH_ON_RESUME) {
//				showLoading(true);
//			} else if (mRefreshType == REFRESH_ON_PULL) {
//
//			}
		}

		@Override
		protected List<SeafRepo> doInBackground(Void... params) {
			try {
				return dataManager.getReposFromServer();
			} catch (SeafException e) {
				err = e;
				return null;
			}
		}

//		private void displaySSLError() {
//			if (mActivity == null)
//				return;
//
//			if (getNavContext().inRepo()) {
//				return;
//			}
//
//			showError(R.string.ssl_error);
//		}

		private void resend() {
			if (mActivity == null)
				return;

			if (getNavContext().inRepo()) {
				return;
			}
			ConcurrentAsyncTask.execute(new LoadTask(dataManager));
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(List<SeafRepo> rs) {
			if (mActivity == null)
				// this occurs if user navigation to another activity
				return;

			/*if (mRefreshType == REFRESH_ON_CLICK
					|| mRefreshType == REFRESH_ON_OVERFLOW_MENU
					|| mRefreshType == REFRESH_ON_RESUME) {
				showLoading(false);
			} else if (mRefreshType == REFRESH_ON_PULL) {
				String lastUpdate = ((MainActivity)mActivity).getDataManager().getLastPullToRefreshTime(DataManager.PULL_TO_REFRESH_LAST_TIME_FOR_REPOS_FRAGMENT);
				//mListView.onRefreshComplete(lastUpdate);
				refreshLayout.setRefreshing(false);
				getDataManager().saveLastPullToRefreshTime(System.currentTimeMillis(), DataManager.PULL_TO_REFRESH_LAST_TIME_FOR_REPOS_FRAGMENT);
				mPullToRefreshStopRefreshing = 0;
			}*/

			if (getNavContext().inRepo()) {
				// this occurs if user already navigate into a repo
				return;
			}
			allList = rs;
			adapter.setDatas(allList);

			// Prompt the user to accept the ssl certificate
			/*if (err == SeafException.sslException) {
				SslConfirmDialog dialog = new SslConfirmDialog(dataManager.getAccount(),
						new SslConfirmDialog.Listener() {
							@Override
							public void onAccepted(boolean rememberChoice) {
								Account account = dataManager.getAccount();
								CertsManager.instance().saveCertForAccount(account, rememberChoice);
								resend();
							}

							@Override
							public void onRejected() {
								displaySSLError();
							}
						});
				dialog.show(getFragmentManager(), SslConfirmDialog.FRAGMENT_TAG);
				return;
			}*/

		/*	if (err != null) {
				if (err.getCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
					// Token expired, should login again
					ToastUtils.show(mActivity, R.string.err_token_expired);
					logoutWhenTokenExpired();
				} else {
					Log.e(DEBUG_TAG, "failed to load repos: " + err.getMessage());
					showError(R.string.error_when_load_repos);
					return;
				}
			}*/
/*
			if (rs != null) {
				getDataManager().setReposRefreshTimeStamp();
				updateAdapterWithRepos(rs);
			} else {
				Log.i(DEBUG_TAG, "failed to load repos");
				showError(R.string.error_when_load_repos);
			}*/
		}
	}

}
