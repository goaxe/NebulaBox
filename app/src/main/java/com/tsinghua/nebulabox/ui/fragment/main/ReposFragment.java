package com.tsinghua.nebulabox.ui.fragment.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.tsinghua.nebulabox.R;
import com.tsinghua.nebulabox.SeafException;
import com.tsinghua.nebulabox.data.DataManager;
import com.tsinghua.nebulabox.data.SeafCachedFile;
import com.tsinghua.nebulabox.data.SeafDirent;
import com.tsinghua.nebulabox.data.SeafGroup;
import com.tsinghua.nebulabox.data.SeafItem;
import com.tsinghua.nebulabox.data.SeafRepo;
import com.tsinghua.nebulabox.gallery.MultipleImageSelectionActivity;
import com.tsinghua.nebulabox.interf.OnItemClickListener;
import com.tsinghua.nebulabox.interf.OnItemLongClickListener;
import com.tsinghua.nebulabox.ui.NavContext;
import com.tsinghua.nebulabox.ui.ToastUtils;
import com.tsinghua.nebulabox.ui.activity.MainActivity;
import com.tsinghua.nebulabox.ui.activity.TransferActivity;
import com.tsinghua.nebulabox.ui.adapter.SeafItemAdapter;
import com.tsinghua.nebulabox.ui.base.BaseFragment;
import com.tsinghua.nebulabox.ui.dialog.FileOptionDialog;
import com.tsinghua.nebulabox.ui.dialog.NewDirDialog;
import com.tsinghua.nebulabox.ui.dialog.NewFileDialog;
import com.tsinghua.nebulabox.ui.dialog.TaskDialog;
import com.tsinghua.nebulabox.ui.widget.CircleImageView;
import com.tsinghua.nebulabox.util.ConcurrentAsyncTask;
import com.tsinghua.nebulabox.util.Utils;
import com.tsinghua.nebulabox.util.log.KLog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人
 * Created by Alfred on 2016/7/11.
 */
public class ReposFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, FileOptionDialog.OnItemClickListener, OnItemLongClickListener {

    private static final String DEBUG_TAG = "ReposFragment";

    @Bind(R.id.option_personal_ll)
    LinearLayout optionLinearLayout;
    @Bind(R.id.category_personal_tv)
    TextView categoryTextView;
    @Bind(R.id.sort_personal_tv)
    TextView sortTextView;
    @Bind(R.id.create_personal_tv)
    TextView createTextView;
    @Bind(R.id.transfer_personal_tv)
    TextView transferTextView;
    @Bind(R.id.line_personal_view)
    View lineView;
    @Bind(R.id.refresh_layout_personal_srlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view_personal_rl)
    ListView mListView;
    @Bind(R.id.empty_rl)
    RelativeLayout emptyRelativeLayout;
    @Bind(R.id.empty_iv)
    ImageView emptyImageView;

    private String[] categoryOptionalTextList;
    private String[] sortOptionalTextList;
    private String[] uploadOptionalTextList;

    private int[] categoryOptionalIconList = new int[]{R.drawable.icon_picture,R.drawable.icon_music,R.drawable.icon_video,R.drawable.icon_document,R.drawable.icon_app,R.drawable.icon_all};
    private int[] sortOptionalIconList = new int[]{R.drawable.icon_sort_name_reverse,R.drawable.icon_sort_date_reverse};
    private int[] uploadOptionaIconList = new int[]{R.drawable.icon_folder_created,R.drawable.icon_upload_file,R.drawable.icon_play_camera};

    private FileOptionDialog categoryDialog;
    private FileOptionDialog sortDialog;

    private List<SeafDirent> pictureList;
    private List<SeafDirent> videoList;
    private List<SeafDirent> movieList;
    private List<SeafDirent> txtList;
    private List<SeafDirent> appList;

    private List<SeafDirent> fileNameDirentList;
    private List<SeafDirent> dateDirentList;

    private List<SeafDirent> allDirentList;

    private String[] pictureFormat;
    private String[] audioFormat;
    private String[] videoFormat;
    private String[] txtFormat;
    private String[] appFormat;

    private SeafItemAdapter adapter;
    private ActionMode mActionMode;
    private int lastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    private DataManager dataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repos, container, false);
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

        Resources resources = getResources();
        categoryOptionalTextList = resources.getStringArray(R.array.category_files_options_array);
        sortOptionalTextList = resources.getStringArray(R.array.sorts_files_options_array);
        uploadOptionalTextList = resources.getStringArray(R.array.add_file_options_array);

        categoryDialog = new FileOptionDialog();
        categoryDialog.setList(categoryOptionalTextList);
        categoryDialog.setOnItemClickListener(this);
        sortDialog = new FileOptionDialog();
        sortDialog.setList(sortOptionalTextList);
        sortDialog.setOnItemClickListener(this);

        pictureFormat = resources.getStringArray(R.array.format_picture);
        audioFormat = resources.getStringArray(R.array.format_audio);
        videoFormat = resources.getStringArray(R.array.format_video);
        appFormat = resources.getStringArray(R.array.format_app);
        txtFormat = resources.getStringArray(R.array.format_document);

        dataManager = mActivity.getDataManager();

        allDirentList = new ArrayList<>();
        adapter = new SeafItemAdapter(mActivity);

        linearLayoutManager = new LinearLayoutManager(mActivity);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NavContext navContext = getNavContext();
                SeafItem seafItem = adapter.getItem(i);
                boolean inRepo = navContext.inRepo();

                if (inRepo) {
                    if (seafItem instanceof SeafDirent) {
                        SeafDirent seafDirent = (SeafDirent) seafItem;
                        if (seafDirent.isDir()) {
                            String currentPath = navContext.getDirPath();
                            String newPath = currentPath.endsWith("/") ?
                                    currentPath + seafDirent.name : currentPath + "/" + seafDirent.name;
                            navContext.setDir(newPath, seafDirent.id);
                            refreshView(false);
                            mActivity.subTitleTextView.setText(newPath);
                        } else {
                            mActivity.onFileSelected(seafDirent);
                        }
                    } else {
                        return;
                    }
                } else if (seafItem instanceof SeafRepo) {
                    SeafRepo seafRepo = (SeafRepo) seafItem;
                    navContext.setRepoID(seafRepo.id);
                    navContext.setRepoName(seafRepo.getName());
                    navContext.setDir("/", seafRepo.root);
                    refreshView(false);
                    mActivity.subTitleTextView.setText(seafRepo.getName());
                } else {
                    Log.e(DEBUG_TAG, "split line");
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                startContextualActionMode(position);
                return true;
            }
        });



        swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
//        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(mActivity,R.color.app_main_color));
        swipeRefreshLayout.setColorSchemeColors(R.color.swipe_refresh_color_1, R.color.swipe_refresh_color_2, R.color.swipe_refresh_color_3, R.color.swipe_refresh_color_4);
        refreshView(true);
    }

    public void startContextualActionMode(int position) {
        startContextualActionMode();

        NavContext nav = getNavContext();
        if (adapter == null || !nav.inRepo()) return;

        adapter.toggleSelection(position);
        updateContextualActionBar();

    }

    public void startContextualActionMode() {
        NavContext nav = getNavContext();
        if (!nav.inRepo()) return;

        if (mActionMode == null) {
            // start the actionMode
            mActionMode = mActivity.startSupportActionMode(new ActionModeCallback());
        }

    }

    public void updateContextualActionBar() {

        if (mActionMode == null) {
            // there are some selected items, start the actionMode
            mActionMode = mActivity.startSupportActionMode(new ActionModeCallback());
        } else {
            // Log.d(DEBUG_TAG, "mActionMode.setTitle " + adapter.getCheckedItemCount());
            mActionMode.setTitle(getResources().getQuantityString(
                    R.plurals.transfer_list_items_selected,
                    adapter.getCheckedItemCount(),
                    adapter.getCheckedItemCount()));
        }

    }



    public RelativeLayout getEmptyView() {

        return emptyRelativeLayout;
    }

    private void initOptionalPopupWindow(final int index) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.popup_categoty, null);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        GridView gridView = (GridView) view.findViewById(R.id.grid_popup_category_gv);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(optionLinearLayout);
        popupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        OptionalGridViewAdapter adapter = loadOptionalPopupData(index);
        if (adapter == null) {
            return;
        }
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //照片,按文件排序
                        if (index == 0) {
                            //照片
                            pictureList = Utils.categoryFile(allDirentList, pictureFormat);
                            setCategoryDataToAdapter(pictureList);
                        } else if (index == 1) {
                            fileNameDirentList = Utils.sortFileByFileName(allDirentList);
                            setCategoryDataToAdapter(fileNameDirentList);
                        } else if (index == 2) {
                            showNewDirDialog();
                        }
                        break;
                    case 1:
                        //音乐,按时间倒序排序
                        if (index == 0) {
                            //音乐
                            videoList = Utils.categoryFile(allDirentList, audioFormat);
                            setCategoryDataToAdapter(videoList);
                        } else if (index == 1) {
                            dateDirentList = Utils.sortFileByDate(allDirentList);
                            setCategoryDataToAdapter(dateDirentList);
                        } else if (index == 2) {
                            pickFile();
//                            pickPhotos();
                        }
                        break;
                    case 2:
                        //影视
                        if (index == 0) {
                            movieList = Utils.categoryFile(allDirentList, videoFormat);
                            setCategoryDataToAdapter(movieList);
                        } else if (index == 2) {
                            CameraTakePhoto();
                        }
                        break;
                    case 3:
                        //文档
                        txtList = Utils.categoryFile(allDirentList, txtFormat);
                        setCategoryDataToAdapter(txtList);
                        break;
                    case 4:
                        //应用
                        appList = Utils.categoryFile(allDirentList, appFormat);
                        setCategoryDataToAdapter(appList);
                        break;
                    case 5:
                        //全部
                        setCategoryDataToAdapter(allDirentList);
                        break;
                }
                popupWindow.dismiss();
            }

        });
    }

    public OptionalGridViewAdapter loadOptionalPopupData(int index) {
        OptionalGridViewAdapter adapter = new OptionalGridViewAdapter();
        if (index == 0) {
            adapter.setIconList(categoryOptionalIconList);
            adapter.setTitleList(categoryOptionalTextList);
        } else if (index == 1) {
            adapter.setIconList(sortOptionalIconList);
            adapter.setTitleList(sortOptionalTextList);
        } else if (index == 2) {
            adapter.setTitleList(uploadOptionalTextList);
            adapter.setIconList(uploadOptionaIconList);
        } else {
            return null;
        }
        return adapter;
    }

    private class OptionalGridViewAdapter extends BaseAdapter {

        private String[] titleList;
        private int[] iconList;

        public void setTitleList(String[] titleList) {
            this.titleList = titleList;
        }

        public void setIconList(int[] iconList) {
            this.iconList = iconList;
        }

        @Override
        public int getCount() {
            return titleList.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_popup_category, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.iconImageView = (CircleImageView) convertView.findViewById(R.id.icon_item_popup_category_iv);
                viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.title_item_popup_category_tv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.titleTextView.setText(titleList[position]);
            viewHolder.iconImageView.setImageResource(iconList[position]);

            return convertView;
        }
    }

    class ViewHolder {
        CircleImageView iconImageView;
        TextView titleTextView;
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
                initOptionalPopupWindow(0);
                break;
            case R.id.sort_personal_tv:
                initOptionalPopupWindow(1);
                break;
            case R.id.create_personal_tv:
                initOptionalPopupWindow(2);
                break;
            case R.id.transfer_personal_tv:
                Intent intent = new Intent(mActivity, TransferActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void OnItemClick(DialogInterface dialog, String[] list, int which) {
        switch (which) {
            case 0:
                //照片,按文件排序
                if (list.length > 2) {
                    //照片
                    pictureList = Utils.categoryFile(allDirentList, pictureFormat);
                    setCategoryDataToAdapter(pictureList);
                } else {
                    fileNameDirentList = Utils.sortFileByFileName(allDirentList);
                    setCategoryDataToAdapter(fileNameDirentList);
                }
                break;
            case 1:
                //音乐,按时间倒序排序
                if (list.length > 2) {
                    //音乐
                    videoList = Utils.categoryFile(allDirentList, audioFormat);
                    setCategoryDataToAdapter(videoList);
                } else {
                    dateDirentList = Utils.sortFileByDate(allDirentList);
                    setCategoryDataToAdapter(dateDirentList);
                }
                break;
            case 2:
                //影视
                movieList = Utils.categoryFile(allDirentList, videoFormat);
                setCategoryDataToAdapter(movieList);
                break;
            case 3:
                //文档
                txtList = Utils.categoryFile(allDirentList, txtFormat);
                setCategoryDataToAdapter(txtList);
                break;
            case 4:
                //应用
                appList = Utils.categoryFile(allDirentList, appFormat);
                setCategoryDataToAdapter(appList);
                break;
            case 5:
                //全部
                setCategoryDataToAdapter(allDirentList);
                break;
        }
    }

    private void setCategoryDataToAdapter(List<SeafDirent> list) {
        if (list.size() > 0) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            emptyRelativeLayout.setVisibility(View.GONE);

            adapter.clear();
            for (SeafDirent seafDirent : list) {
                adapter.add(seafDirent);
            }
            adapter.notifyDataSetChanged();
        } else {
            swipeRefreshLayout.setVisibility(View.GONE);
            emptyRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        allDirentList.clear();
        refreshView(true);
    }

    public DataManager getDataManager() {
        return dataManager;
    }


    @Override
    public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
        return false;
    }

    @Override
    public void onItemClck(ViewGroup parent, View view, Object o, int position) {
        NavContext navContext = getNavContext();
        if (navContext.inRepo()) {
            if (o instanceof SeafDirent) {
                SeafDirent seafDirent = (SeafDirent) o;
                if (seafDirent.isDir()) {
                    String currentPath = navContext.getDirPath();
                    String newPath = currentPath.endsWith("/") ?
                            currentPath + seafDirent.name : currentPath + "/" + seafDirent.name;
                    navContext.setDir(newPath, seafDirent.id);
                    refreshView(false);
                } else {
                }
            } else
                return;
        } else {
            SeafRepo seafRepo = (SeafRepo) o;
            navContext.setRepoID(seafRepo.id);
            navContext.setRepoName(seafRepo.getName());
            navContext.setDir("/", seafRepo.root);
            refreshView(false);
        }

    }

    public void refreshView(boolean forceRefresh) {
        NavContext navContext = getNavContext();
        if (navContext.inRepo()) {
//            if (mActivity.getCurrentPosition() == BrowserActivity.INDEX_LIBRARY_TAB) {
//                mActivity.enableUpButton();
//            }
            navToDirectory(forceRefresh);
            optionLinearLayout.setVisibility(View.VISIBLE);
            lineView.setVisibility(View.VISIBLE);
            mActivity.subTitleTextView.setVisibility(View.VISIBLE);
        } else {
//            mActivity.disableUpButton();
            navToReposView(forceRefresh);
            optionLinearLayout.setVisibility(View.GONE);
            lineView.setVisibility(View.GONE);
            mActivity.subTitleTextView.setVisibility(View.GONE);
        }
    }

    private void navToReposView(boolean forceRefresh) {
        emptyRelativeLayout.setVisibility(View.GONE);
        if (!forceRefresh) {
            List<SeafRepo> repos = getDataManager().getReposFromCache();
            if (repos != null) {
                updateAdapterWithRepos(repos);
                return;
            }
        }
        ConcurrentAsyncTask.execute(new LoadTask(getDataManager()));
    }


    private void navToDirectory(boolean forceRefresh) {
        NavContext navContext = getNavContext();
        if (navContext == null) {
            Log.e(DEBUG_TAG, "navcontext is null");
        } else {
            Log.e(DEBUG_TAG, navContext.getRepoName() + navContext.getDirID() + navContext.getDirPath());
        }
        ConcurrentAsyncTask.execute(new LoadDirTask(getDataManager()),
                navContext.getRepoName(),
                navContext.getRepoID(),
                navContext.getDirPath());
    }

    @Override
    public NavContext getNavContext() {
        return ((MainActivity) mActivity).getNavContext();
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

        }

        @Override
        protected List<SeafRepo> doInBackground(Void... params) {
            try {
                List<SeafRepo> repos = dataManager.getReposFromServer();
                for (SeafRepo repo : repos) {
                    Log.e(DEBUG_TAG, repo.getName() + " " + repo.id);
                }
                return repos;
            } catch (SeafException e) {
                return null;
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<SeafRepo> rs) {
            if (mActivity == null)
                // this occurs if user navigation to another activity
                return;

            if (getNavContext().inRepo()) {
                // this occurs if user already navigate into a repo
                return;
            }
            updateAdapterWithRepos(rs);
//            adapter.setFootViewShown(false);
            swipeRefreshLayout.setRefreshing(false);

        }
    }

    private class LoadDirTask extends AsyncTask<String, Void, List<SeafDirent>> {

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
            KLog.i(dirents);
            if (mActivity == null || dirents == null)
                // this occurs if user navigation to another activity
                return;
            allDirentList.clear();
            allDirentList.addAll(dirents);
            adapter.clear();
            if (dirents.size() > 0) {
                for (SeafDirent seafDirent : dirents) {
                    adapter.add(seafDirent);
                }
            }
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);

            NavContext nav = getNavContext();
            if (!myRepoID.equals(nav.getRepoID()) || !myPath.equals(nav.getDirPath())) {
                return;
            }

            getDataManager().setDirsRefreshTimeStamp(myRepoID, myPath);
        }
    }

    public void showFileBottomSheet(String title, final SeafDirent dirent) {
        final String repoName = getNavContext().getRepoName();
        final String repoID = getNavContext().getRepoID();
        final String dir = getNavContext().getDirPath();
        final String path = Utils.pathJoin(dir, dirent.name);
        final String filename = dirent.name;
        final String localPath = getDataManager().getLocalRepoFile(repoName, repoID, path).getPath();
        final BottomSheet.Builder builder = new BottomSheet.Builder(mActivity);
        builder.title(title).sheet(R.menu.bottom_sheet_op_file).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.share:
                        mActivity.shareFile(repoID, path);
                        break;
                    case R.id.delete:
                        mActivity.deleteFile(repoID, repoName, path);
                        break;
                    case R.id.copy:
                        mActivity.copyFile(repoID, repoName, dir, filename, false);
                        break;
                    case R.id.move:
                        mActivity.moveFile(repoID, repoName, dir, filename, false);
                        break;
                    case R.id.history:
                        mActivity.historyFile(repoID, path);
                       break;
                    case R.id.rename:
                        mActivity.renameFile(repoID, repoName, path);
                        break;
                    case R.id.download:
                        mActivity.downloadFile(dir, dirent.name);
                        break;
                    case R.id.star:
                        mActivity.starFile(repoID, dir, filename);
                        break;
                }
            }
        }).show();

        SeafRepo repo = getDataManager().getCachedRepoByID(repoID);
        if (repo != null && repo.encrypted) {
            builder.remove(R.id.share);
        }

        SeafCachedFile cf = getDataManager().getCachedFile(repoName, repoID, path);
        if (cf != null) {
            builder.remove(R.id.download);
        } else {
            builder.remove(R.id.update);
        }
    }

    public void showDirBottomSheet(String title, final SeafDirent dirent) {
        final String repoName = getNavContext().getRepoName();
        final String repoID = getNavContext().getRepoID();
        final String dir = getNavContext().getDirPath();
        final String path = Utils.pathJoin(dir, dirent.name);
        final String filename = dirent.name;
        final BottomSheet.Builder builder = new BottomSheet.Builder(mActivity);
        builder.title(title).sheet(R.menu.bottom_sheet_op_dir).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.share:
                        mActivity.shareDir(repoID, path);
                        break;
                    case R.id.delete:
                        mActivity.deleteDir(repoID, repoName, path);
                        break;
                    case R.id.copy:
                        mActivity.copyFile(repoID, repoName, dir, filename, false);
                        break;
                    case R.id.move:
                        mActivity.moveFile(repoID, repoName, dir, filename, false);
                        break;
                    case R.id.rename:
                        mActivity.renameDir(repoID, repoName, path);
                        break;
//                    case R.id.download:
//                        mActivity.downloadDir(dir, dirent.name, true);
//                        break;
                }
            }
        }).show();
        SeafRepo repo = getDataManager().getCachedRepoByID(repoID);
        if (repo != null && repo.encrypted) {
            builder.remove(R.id.share);
        }
    }

    public SeafItemAdapter getAdapter() {
        return adapter;
    }

    private void addFile() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(getString(R.string.add_file));
        builder.setItems(R.array.add_file_options_array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // create folder
                    showNewDirDialog();
                } else if (which == 1) {
                    // upload file
                    pickFile();
                } else if (which == 2) {
                    // take a photo
                    CameraTakePhoto();
                }
            }
        }).show();
    }

    private void showNewFileDialog() {
        final NewFileDialog dialog = new NewFileDialog();
        NavContext navContext = getNavContext();
        dialog.init(navContext.getRepoID(), navContext.getDirPath(), mActivity.getAccountManager().getAccount());
        dialog.setTaskDialogLisenter(new TaskDialog.TaskDialogListener() {
            @Override
            public void onTaskSuccess() {
                ToastUtils.show(mActivity, "Sucessfully created file " + dialog.getNewFileName());
                refreshView(true);
            }
        });
        dialog.show(mActivity.getSupportFragmentManager(), "NewFileDialogFragment");
    }

    private void showNewDirDialog() {
        final NewDirDialog dialog = new NewDirDialog();
        NavContext navContext = getNavContext();
        dialog.init(navContext.getRepoID(), navContext.getDirPath(), mActivity.getAccountManager().getAccount());
        dialog.setTaskDialogLisenter(new TaskDialog.TaskDialogListener() {
            @Override
            public void onTaskSuccess() {
                ToastUtils.show(mActivity, "Sucessfully created folder " + dialog.getNewDirName());
                refreshView(true);
            }
        });
        dialog.show(mActivity.getSupportFragmentManager(), "NewDirDialogFragment");
    }

    private void pickFile() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            UploadChoiceDialog dialog = new UploadChoiceDialog();
//            dialog.show(getSupportFragmentManager(), PICK_FILE_DIALOG_FRAGMENT_TAG);
//        } else {
            Intent target = Utils.createGetContentIntent();
            Intent intent = Intent.createChooser(target, getString(R.string.choose_file));
            mActivity.startActivityForResult(intent, MainActivity.PICK_FILE_REQUEST);
//        }

//        Intent intent = new Intent(mActivity, MultiFileChooserActivity.class);
//        mActivity.startActivityForResult(intent, MainActivity.PICK_FILES_REQUEST);
    }

    private void pickPhotos() {
        Intent intent = new Intent(mActivity, MultipleImageSelectionActivity.class);
        mActivity.startActivityForResult(intent, MainActivity.PICK_PHOTOS_VIDEOS_REQUEST);

    }

    private void pickVideos() {

    }

    private void pickMusic() {

    }

    private void CameraTakePhoto() {
        Intent imageCaptureIntent = new Intent("android.media.action.IMAGE_CAPTURE");

        try {
            File ImgDir = DataManager.createTempDir();

            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
            mActivity.takeCameraPhotoTempFile = new File(ImgDir, fileName);

            Uri photo = Uri.fromFile(mActivity.takeCameraPhotoTempFile);
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photo);
            mActivity.startActivityForResult(imageCaptureIntent, mActivity.TAKE_PHOTO_REQUEST);

        } catch (IOException e) {
            ToastUtils.show(mActivity, R.string.unknow_error);
        }
    }

    private void addReposToAdapter(List<SeafRepo> repos) {
        if (repos == null)
            return;
        Map<String, List<SeafRepo>> map = Utils.groupRepos(repos);
        List<SeafRepo> personalRepos = map.get(Utils.PERSONAL_REPO);
        if (personalRepos != null) {
            SeafGroup personalGroup = new SeafGroup(mActivity.getResources().getString(R.string.personal));
            adapter.add(personalGroup);
            for (SeafRepo repo : personalRepos)
                adapter.add(repo);
        }

        List<SeafRepo> sharedRepos = map.get(Utils.SHARED_REPO);
        if (sharedRepos != null) {
            SeafGroup sharedGroup = new SeafGroup(mActivity.getResources().getString(R.string.shared));
            adapter.add(sharedGroup);
            for (SeafRepo repo : sharedRepos)
                adapter.add(repo);
        }

        for (Map.Entry<String, List<SeafRepo>> entry : map.entrySet()) {
            String key = entry.getKey();
            if (!key.equals(Utils.PERSONAL_REPO)
                    && !key.endsWith(Utils.SHARED_REPO)) {
                SeafGroup group = new SeafGroup(key);
                adapter.add(group);
                for (SeafRepo repo : entry.getValue()) {
                    adapter.add(repo);
                }
            }
        }
    }


    private void updateAdapterWithRepos(List<SeafRepo> repos) {
        adapter.clear();
        if (repos != null && repos.size() > 0) {
            addReposToAdapter(repos);
//            adapter.sortFiles(SettingsManager.instance().getSortFilesTypePref(),
//                    SettingsManager.instance().getSortFilesOrderPref());
            adapter.notifyChanged();
            mListView.setVisibility(View.VISIBLE);
            emptyImageView.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.GONE);
            emptyImageView.setVisibility(View.VISIBLE);
        }
        // Collapses the currently open view
        //mListView.collapse();
    }

    class ActionModeCallback implements ActionMode.Callback {
        private boolean allItemsSelected;

        public ActionModeCallback() {
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate the menu for the contextual action bar (CAB)
            MenuInflater inflater = mode.getMenuInflater();
//            inflater.inflate(R.menu.repos_fragment_menu, menu);
            inflater.inflate(R.menu.repos_fragment_menu, menu);

            if (adapter == null) return true;

            adapter.setActionModeOn(true);
            adapter.notifyDataSetChanged();

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            /*
             * The ActionBarPolicy determines how many action button to place in the ActionBar
             * and the default amount is 2.
             */
            menu.findItem(R.id.action_mode_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.action_mode_copy).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.action_mode_select_all).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            // Here you can perform updates to the contextual action bar (CAB) due to
            // an invalidate() request
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // Respond to clicks on the actions in the contextual action bar (CAB)
            NavContext nav = mActivity.getNavContext();
            String repoID = nav.getRepoID();
            String repoName = nav.getRepoName();
            String dirPath = nav.getDirPath();
            final List<SeafDirent> selectedDirents = adapter.getSelectedItemsValues();
            if (selectedDirents.size() == 0
                    || repoID == null
                    || dirPath == null) {
                if (item.getItemId() != R.id.action_mode_select_all) {
                    ToastUtils.show(mActivity, R.string.action_mode_no_items_selected);
                    return true;
                }
            }

            switch (item.getItemId()) {
                case R.id.action_mode_select_all:
                    if (!allItemsSelected) {
                        if (adapter == null) return true;

                        adapter.selectAllItems();
                        updateContextualActionBar();
                    } else {
                        if (adapter == null) return true;

                        adapter.deselectAllItems();
                        updateContextualActionBar();
                    }

                    allItemsSelected = !allItemsSelected;
                    break;
                case R.id.action_mode_delete:
                    Log.e(DEBUG_TAG, "delete");
                    mActivity.deleteFiles(repoID, dirPath, selectedDirents);
                    break;
                case R.id.action_mode_copy:
                    Log.e(DEBUG_TAG, "copy");
                    mActivity.copyFiles(repoID, repoName, dirPath, selectedDirents);
                    break;
                case R.id.action_mode_move:
                    Log.e(DEBUG_TAG, "move");
                    mActivity.moveFiles(repoID, repoName, dirPath, selectedDirents);
                    break;
                case R.id.action_mode_download:
                    Log.e(DEBUG_TAG, "download");
                    mActivity.downloadFiles(repoID, repoName, dirPath, selectedDirents);
                    break;

                default:
                    return false;
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (adapter == null) return;

            adapter.setActionModeOn(false);
            adapter.deselectAllItems();

            // Here you can make any necessary updates to the activity when
            // the contextual action bar (CAB) is removed. By default, selected items are deselected/unchecked.
            mActionMode = null;
        }

    }

}
