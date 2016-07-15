package com.seafile.seadroid2.ui.fragment.main;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeafException;
import com.seafile.seadroid2.data.DataManager;
import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.data.SeafRepo;
import com.seafile.seadroid2.interf.OnItemClickListener;
import com.seafile.seadroid2.interf.OnItemLongClickListener;
import com.seafile.seadroid2.ui.NavContext;
import com.seafile.seadroid2.ui.activity.MainActivity;
import com.seafile.seadroid2.ui.adapter.FileListAdapter;
import com.seafile.seadroid2.ui.base.BaseFragment;
import com.seafile.seadroid2.ui.dialog.FileOptionDialog;
import com.seafile.seadroid2.ui.widget.RecycleViewDivider;
import com.seafile.seadroid2.util.ConcurrentAsyncTask;
import com.seafile.seadroid2.util.Utils;
import com.seafile.seadroid2.util.log.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人
 * Created by Alfred on 2016/7/11.
 */
public class PersonalFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, FileOptionDialog.OnItemClickListener, OnItemLongClickListener {

    @Bind(R.id.category_personal_tv)
    TextView categoryTextView;
    @Bind(R.id.sort_personal_tv)
    TextView sortTextView;
    @Bind(R.id.create_personal_tv)
    TextView createTextView;
    @Bind(R.id.transfer_personal_tv)
    TextView transferTextView;
    @Bind(R.id.refresh_layout_personal_srlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view_personal_rl)
    RecyclerView recyclerView;

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
    private int lastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    private NavContext navContext;
    private DataManager dataManager;

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
        categoryDialog.setOnItemClickListener(this);
        sortDialog = new FileOptionDialog();
        sortDialog.setList(sortOptionalList);
        sortDialog.setOnItemClickListener(this);

        Resources resources = getResources();
        pictureFormat = resources.getStringArray(R.array.format_picture);
        videoFormat = resources.getStringArray(R.array.format_video);
        movieFormat = resources.getStringArray(R.array.format_movie);
        appFormat = resources.getStringArray(R.array.format_app);
        txtFormat = resources.getStringArray(R.array.format_app);

        dataManager = ((MainActivity) mActivity).getDataManager();

        allList = new ArrayList<>();
        adapter = new FileListAdapter(mActivity, R.layout.item_file_list);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);

        linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL, 10, ContextCompat.getColor(mContext, R.color.app_main_color)));
//        recyclerView.addOnScrollListener(new PauseOnScrollListener());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


        swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
//        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(mActivity,R.color.app_main_color));
        swipeRefreshLayout.setColorSchemeColors(R.color.swipe_refresh_color_1, R.color.swipe_refresh_color_2, R.color.swipe_refresh_color_3, R.color.swipe_refresh_color_4);
        refreshView(true);
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
                                KLog.i("sortDialog = " + sortDialog);
                                if (sortDialog.isVisible()) {
                                    sortDialog.dismiss();
                                }
                                categoryDialog.show(getFragmentManager(), "categoryDialog");
                                break;
                            case R.id.sort_personal_tv:
                                if (categoryDialog.isVisible()) {
                    categoryDialog.dismiss();
                }
                sortDialog.show(getFragmentManager(), "sortDialog");
                break;
            case R.id.create_personal_tv:
                break;
            case R.id.transfer_personal_tv:
                break;
        }
    }

    @Override
    public void OnItemClick(DialogInterface dialog,String[] list, int which) {
        switch (which) {
            case 1:
                //照片,按文件排序
                if (list.length > 2) {
                    //照片
                    pictureList = Utils.categoryFile(allList, pictureFormat);
                    adapter.setDatas(pictureList);
                } else {

                }
                break;
            case 2:
                //音乐,按时间倒序排序
                if (list.length > 2) {
                    //音乐
                    videoList = Utils.categoryFile(allList, videoFormat);
                    adapter.setDatas(videoList);

                }
                break;
            case 3:
                //影视
                movieList = Utils.categoryFile(allList, movieFormat);
                adapter.setDatas(movieList);
                break;
            case 4:
                //文档
                txtList = Utils.categoryFile(allList, txtFormat);
                adapter.setDatas(txtList);
                break;
            case 5:
                //应用
                appList = Utils.categoryFile(allList, appFormat);
                adapter.setDatas(appList);
                break;
            case 6:
                //全部
                adapter.setDatas(allList);
                break;
        }
    }

    @Override
    public void onRefresh() {
        allList.clear();
        ConcurrentAsyncTask.execute(new LoadTask(getDataManager()));
    }

    public DataManager getDataManager() {
        return dataManager;
    }

//	class PauseOnScrollListener extends RecyclerView.OnScrollListener {
//		@Override
//		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//			super.onScrollStateChanged(recyclerView, newState);
//			switch (newState) {
//				case RecyclerView.SCROLL_STATE_IDLE:
//					//RecyclerView目前不滑动
//					int size = recyclerView.getAdapter().getItemCount();
//					if (lastVisibleItem + 1 == size && adapter.isFootViewShown() &&
//							!adapter.getFooterViewText().equals(getString(R.string.load_data_adequate))) {
//						onScrollLast();
//					}
//					break;
//				case RecyclerView.SCROLL_STATE_DRAGGING:
//					//RecyclerView开始滑动
//					break;
//				case RecyclerView.SCROLL_STATE_SETTLING:
//					//RecyclerView惯性移动
//					break;
//			}
//		}
//
//		@Override
//		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//			super.onScrolled(recyclerView, dx, dy);
//			lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//		}
//	}


    @Override
    public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
        return false;
    }

    @Override
    public void onItemClck(ViewGroup parent, View view, Object o, int position) {
        NavContext navContext = getNavContext();
        if (o instanceof SeafRepo){
            SeafRepo seafRepo = (SeafRepo)o;
            navContext.setRepoID(seafRepo.id);
            navContext.setRepoName(seafRepo.getName());
            navContext.setDir("/", seafRepo.root);
            refreshView(false);
        }
        SeafRepo seafRepo = (SeafRepo) o;
        if (seafRepo.isFile) {

        } else {

        }
    }

    public void refreshView(boolean forceRefresh){
        NavContext navContext = getNavContext();
        if (navContext.inRepo()) {
//            if (mActivity.getCurrentPosition() == BrowserActivity.INDEX_LIBRARY_TAB) {
//                mActivity.enableUpButton();
//            }
            navToDirectory(forceRefresh);
        } else {
//            mActivity.disableUpButton();
            navToReposView(forceRefresh);
        }
    }

    private void navToReposView(boolean forceRefresh) {
        ConcurrentAsyncTask.execute(new LoadTask(getDataManager()));
    }

    private void navToDirectory(boolean forceRefresh) {
//        ConcurrentAsyncTask.execute(new LoadDirTask(getDataManager()),
//                navContext.getRepoName(),
//                navContext.getRepoID(),
//                navContext.getDirPath());
    }

    @Override
    public NavContext getNavContext() {
        return ((MainActivity)mActivity).getNavContext();
    }

    private class LoadTask extends AsyncTask<Void, Void, List<SeafRepo>> {
        SeafException err = null;
        DataManager dataManager;

        public LoadTask(DataManager dataManager) {
            this.dataManager = dataManager;
        }

        @Override
        protected void onPreExecute() {
            if (mActivity == null) {
                return;
            }
//			adapter.setFootViewShown(true);
//			adapter.setFooterViewText(ContextCompat.getColor(mContext, R.string.loading_data));
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
            allList.clear();
            allList.addAll(rs);
            adapter.setDatas(allList);
//            adapter.setFootViewShown(false);
            swipeRefreshLayout.setRefreshing(false);

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

        private class LoadDirTask extends AsyncTask<String, Void, List<SeafDirent> > {

            SeafException err = null;
            String myRepoName;
            String myRepoID;
            String myPath;

            DataManager dataManager;

            public LoadDirTask(DataManager dataManager) {
                this.dataManager = dataManager;
            }

            @Override
            protected void onPreExecute() {
//                if (mRefreshType == REFRESH_ON_CLICK
//                        || mRefreshType == REFRESH_ON_OVERFLOW_MENU
//                        || mRefreshType == REFRESH_ON_RESUME) {
//                    showLoading(true);
//                } else if (mRefreshType == REFRESH_ON_PULL) {
                    // mHeadProgress.setVisibility(ProgressBar.VISIBLE);
//                }
            }

            @Override
            protected List<SeafDirent> doInBackground(String... params) {
                if (params.length != 3) {
                    KLog.d("Wrong params to LoadDirTask");
                    return null;
                }

                myRepoName = params[0];
                myRepoID = params[1];
                myPath = params[2];
                try {
                    return dataManager.getDirentsFromServer(myRepoID, myPath);
                } catch (SeafException e) {
                    err = e;
                    return null;
                }

            }

            private void resend() {
                if (mActivity == null)
                    return;
                NavContext nav = getNavContext();
                if (!myRepoID.equals(nav.getRepoID()) || !myPath.equals(nav.getDirPath())) {
                    return;
                }

                ConcurrentAsyncTask.execute(new LoadDirTask(dataManager), myRepoName, myRepoID, myPath);
            }

            private void displaySSLError() {
                if (mActivity == null)
                    return;

                NavContext nav = getNavContext();
                if (!myRepoID.equals(nav.getRepoID()) || !myPath.equals(nav.getDirPath())) {
                    return;
                }
//                showError(R.string.ssl_error);
            }

            // onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute(List<SeafDirent> dirents) {
                if (mActivity == null)
                    // this occurs if user navigation to another activity
                    return;
                allList.clear();
//                allList.add(dirents)
                swipeRefreshLayout.setRefreshing(false);
//                if (mRefreshType == REFRESH_ON_CLICK
//                        || mRefreshType == REFRESH_ON_OVERFLOW_MENU
//                        || mRefreshType == REFRESH_ON_RESUME) {
//                    showLoading(false);
//                } else if (mRefreshType == REFRESH_ON_PULL) {
//                    String lastUpdate = getDataManager().getLastPullToRefreshTime(DataManager.PULL_TO_REFRESH_LAST_TIME_FOR_REPOS_FRAGMENT);
//                    //mListView.onRefreshComplete(lastUpdate);
//                    swipeRefreshLayout.setRefreshing(false);
//                    getDataManager().saveLastPullToRefreshTime(System.currentTimeMillis(), DataManager.PULL_TO_REFRESH_LAST_TIME_FOR_REPOS_FRAGMENT);
//                    mPullToRefreshStopRefreshing = 0;
//                }

                NavContext nav = getNavContext();
                if (!myRepoID.equals(nav.getRepoID()) || !myPath.equals(nav.getDirPath())) {
                    return;
                }

//                if (err == SeafException.sslException) {
//                    SslConfirmDialog dialog = new SslConfirmDialog(dataManager.getAccount(),
//                            new SslConfirmDialog.Listener() {
//                                @Override
//                                public void onAccepted(boolean rememberChoice) {
//                                    Account account = dataManager.getAccount();
//                                    CertsManager.instance().saveCertForAccount(account, rememberChoice);
//                                    resend();
//                                }
//
//                                @Override
//                                public void onRejected() {
//                                    displaySSLError();
//                                }
//                            });
//                    dialog.show(getFragmentManager(), SslConfirmDialog.FRAGMENT_TAG);
//                    return;
//                }
//
//                if (err != null) {
//                    if (err.getCode() == SeafConnection.HTTP_STATUS_REPO_PASSWORD_REQUIRED) {
//                        showPasswordDialog();
//                    } else if (err.getCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
//                        // Token expired, should login again
//                        ToastUtils.show(mActivity, R.string.err_token_expired);
//                        logoutWhenTokenExpired();
//                    } else if (err.getCode() == HttpURLConnection.HTTP_NOT_FOUND) {
//                        ToastUtils.show(mActivity, String.format("The folder \"%s\" was deleted", myPath));
//                    } else {
//                        Log.d(DEBUG_TAG, "failed to load dirents: " + err.getMessage());
//                        err.printStackTrace();
//                        showError(R.string.error_when_load_dirents);
//                    }
//                    return;
//                }
//
//                if (dirents == null) {
//                    showError(R.string.error_when_load_dirents);
//                    Log.i(DEBUG_TAG, "failed to load dir");
//                    return;
//                }
                getDataManager().setDirsRefreshTimeStamp(myRepoID, myPath);
//                updateAdapterWithDirents(dirents);
            }
        }
    }

}
