package com.seafile.seadroid2.ui.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.global.ActivityManager;

/**
 * Activity的父类
 * Created by Alfred on 2016/7/8.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    // variables that control the Action Bar auto hide behavior (aka "quick recall")
    private boolean mActionBarAutoHideEnabled = false;

    // Primary toolbar and drawer toggle
    private Toolbar mActionBarToolbar;

    private int mActionBarAutoHideMinY = 0;

    private int mActionBarAutoHideSensivity = 0;

    private int mActionBarAutoHideSignal = 0;

    private boolean mActionBarShown = true;

    protected int screenWidth;

	protected FragmentManager mFragmentManager;
	protected FragmentTransaction mFragmentTransation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        ActivityManager.push(this);

		mFragmentManager = getSupportFragmentManager();
    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                // Depending on which version of Android you are on the Toolbar or the ActionBar may be
                // active so the a11y description is set here.
                mActionBarToolbar.setNavigationContentDescription(getResources().getString(R.string
                        .navdrawer_description_a11y));
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

//    @Override
//    public void setContentView(int layoutResID) {
//        super.setContentView(layoutResID);
//        getActionBarToolbar();
//    }

        /**
     * Initializes the Action Bar auto-hide (aka Quick Recall) effect.
     */
    private void initActionBarAutoHide() {
        mActionBarAutoHideEnabled = true;
        mActionBarAutoHideMinY = getResources().getDimensionPixelSize(
                R.dimen.action_bar_auto_hide_min_y);
        mActionBarAutoHideSensivity = getResources().getDimensionPixelSize(
                R.dimen.action_bar_auto_hide_sensivity);
    }

    //布局文件ID
//    protected abstract int getContentViewId();

    //布局中Fragment的ID
    protected int getFragmentContentId(){
		return 0;
	}

    //添加fragment
    protected void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            mFragmentManager.beginTransaction()
                    .replace(getFragmentContentId(), fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    //移除fragment
    protected void removeFragment() {
        if (mFragmentManager.getBackStackEntryCount() > 1) {
            mFragmentManager.popBackStack();
        } else {
            finish();
        }
    }

    //返回键返回事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (mFragmentManager.getBackStackEntryCount() == 1) {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

	/**
	 * Share a file. Generating a file share link and send the link to someone
	 * through some app.
	 * @param repoID
	 * @param path
	 */
//	public void shareFile(String repoID, String path) {
//		WidgetUtils.chooseShareApp(this, repoID, path, false, account);
//	}
//
//	public void shareDir(String repoID, String path) {
//		WidgetUtils.chooseShareApp(this, repoID, path, true, account);
//	}
//
//	public void renameFile(String repoID, String repoName, String path) {
//		doRename(repoID, repoName, path, false);
//	}
//
//	public void renameDir(String repoID, String repoName, String path) {
//		doRename(repoID, repoName, path, true);
//	}
//
//	private void doRename(String repoID, String repoName, String path, boolean isdir) {
//		final RenameFileDialog dialog = new RenameFileDialog();
//		dialog.init(repoID, path, isdir, account);
//		dialog.setTaskDialogLisenter(new TaskDialog.TaskDialogListener() {
//			@Override
//			public void onTaskSuccess() {
//				ToastUtils.show(BrowserActivity.this, R.string.rename_successful);
//				ReposFragment reposFragment = getReposFragment();
//				if (currentPosition == INDEX_LIBRARY_TAB && reposFragment != null) {
//					reposFragment.refreshView();
//				}
//			}
//		});
//		dialog.show(getSupportFragmentManager(), TAG_RENAME_FILE_DIALOG_FRAGMENT);
//	}
//
//	public void deleteFile(String repoID, String repoName, String path) {
//		doDelete(repoID, repoName, path, false);
//	}
//
//	public void deleteDir(String repoID, String repoName, String path) {
//		doDelete(repoID, repoName, path, true);
//	}
//
//	private void doDelete(String repoID, String repoName, String path, boolean isdir) {
//		final DeleteFileDialog dialog = new DeleteFileDialog();
//		dialog.init(repoID, path, isdir, account);
//		dialog.setTaskDialogLisenter(new TaskDialog.TaskDialogListener() {
//			@Override
//			public void onTaskSuccess() {
//				ToastUtils.show(BrowserActivity.this, R.string.delete_successful);
//				ReposFragment reposFragment = getReposFragment();
//				if (currentPosition == INDEX_LIBRARY_TAB && reposFragment != null) {
//					reposFragment.refreshView();
//				}
//			}
//		});
//		dialog.show(getSupportFragmentManager(), TAG_DELETE_FILE_DIALOG_FRAGMENT);
//	}
//
//	public void copyFile(String srcRepoId, String srcRepoName, String srcDir, String srcFn, boolean isdir) {
//		chooseCopyMoveDest(srcRepoId, srcRepoName, srcDir, srcFn, isdir, CopyMoveContext.OP.COPY);
//	}
//
//	public void moveFile(String srcRepoId, String srcRepoName, String srcDir, String srcFn, boolean isdir) {
//		chooseCopyMoveDest(srcRepoId, srcRepoName, srcDir, srcFn, isdir, CopyMoveContext.OP.MOVE);
//	}
//
//	public void starFile(String srcRepoId, String srcDir, String srcFn) {
//		getStarredFragment().doStarFile(srcRepoId, srcDir, srcFn);
//	}
//
//    protected void enableActionBarAutoHide(final ListView listView) {
//        initActionBarAutoHide();
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            /** The heights of all items. */
//            private Map<Integer, Integer> heights = new HashMap<Integer, Integer>();
//            private int lastCurrentScrollY = 0;
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
//                                 int totalItemCount) {
//
//                // Get the first visible item's view.
//                View firstVisibleItemView = view.getChildAt(0);
//                if (firstVisibleItemView == null) {
//                    return;
//                }
//
//                // Save the height of the visible item.
//                heights.put(firstVisibleItem, firstVisibleItemView.getHeight());
//
//                // Calculate the height of all previous (hidden) items.
//                int previousItemsHeight = 0;
//                for (int i = 0; i < firstVisibleItem; i++) {
//                    previousItemsHeight += heights.get(i) != null ? heights.get(i) : 0;
//                }
//
//                int currentScrollY = previousItemsHeight - firstVisibleItemView.getTop()
//                        + view.getPaddingTop();
//
//                onMainContentScrolled(currentScrollY, currentScrollY - lastCurrentScrollY);
//
//                lastCurrentScrollY = currentScrollY;
//            }
//        });
//    }

        /**
     * Indicates that the main content has scrolled (for the purposes of showing/hiding
     * the action bar for the "action bar auto hide" effect). currentY and deltaY may be exact
     * (if the underlying view supports it) or may be approximate indications:
     * deltaY may be INT_MAX to mean "scrolled forward indeterminately" and INT_MIN to mean
     * "scrolled backward indeterminately".  currentY may be 0 to mean "somewhere close to the
     * start of the list" and INT_MAX to mean "we don't know, but not at the start of the list"
     */
    private void onMainContentScrolled(int currentY, int deltaY) {
        if (deltaY > mActionBarAutoHideSensivity) {
            deltaY = mActionBarAutoHideSensivity;
        } else if (deltaY < -mActionBarAutoHideSensivity) {
            deltaY = -mActionBarAutoHideSensivity;
        }

        if (Math.signum(deltaY) * Math.signum(mActionBarAutoHideSignal) < 0) {
            // deltaY is a motion opposite to the accumulated signal, so reset signal
            mActionBarAutoHideSignal = deltaY;
        } else {
            // add to accumulated signal
            mActionBarAutoHideSignal += deltaY;
        }

        boolean shouldShow = currentY < mActionBarAutoHideMinY ||
                (mActionBarAutoHideSignal <= -mActionBarAutoHideSensivity);
        autoShowOrHideActionBar(shouldShow);
    }

    protected void autoShowOrHideActionBar(boolean show) {
        if (show == mActionBarShown) {
            return;
        }

        mActionBarShown = show;
        //onActionBarAutoShowOrHide(show);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivityFromStack(this);
    }

    private void initLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.settings_cuc_loading));
            progressDialog.setCancelable(false);
        }
    }

    public void showLoadingDialog() {
        initLoadingDialog();
        progressDialog.show();
    }

    public void dismissLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
