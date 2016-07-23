package com.tsinghua.nebulabox.ui.fragment.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.tsinghua.nebulabox.fileschooser.MultiFileChooserActivity;
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
public class PersonalFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, FileOptionDialog.OnItemClickListener, OnItemLongClickListener {

    private static final String DEBUG_TAG = "PersonalFragment";

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
    @Bind(R.id.refresh_layout_personal_srlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view_personal_rl)
    ListView recyclerView;
    @Bind(R.id.empty_rl)
    RelativeLayout emptyRelativeLayout;
    @Bind(R.id.empty_iv)
    ImageView emptyImageView;

    private String[] categoryOptionalList;
    private String[] sortOptionalList;

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
    private int lastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    private DataManager dataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        audioFormat = resources.getStringArray(R.array.format_audio);
        videoFormat = resources.getStringArray(R.array.format_video);
        appFormat = resources.getStringArray(R.array.format_app);
        txtFormat = resources.getStringArray(R.array.format_document);

        dataManager = mActivity.getDataManager();

        allDirentList = new ArrayList<>();
        adapter = new SeafItemAdapter((MainActivity) mActivity);
//		adapter.setOnItemClickListener(this);
//		adapter.setOnItemLongClickListener(this);

        linearLayoutManager = new LinearLayoutManager(mActivity);
//		recyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL, 10, ContextCompat.getColor(mContext, R.color.app_main_color)));
//        recyclerView.addOnScrollListener(new PauseOnScrollListener());
//		recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NavContext navContext = getNavContext();
                SeafItem o = adapter.getItem(i);
                boolean inRepo = navContext.inRepo();
                Log.e(DEBUG_TAG, "inRepo:" + inRepo + " " + navContext.getRepoID() + o.getClass());

                if (inRepo) {
                    if (o instanceof SeafDirent) {
                        SeafDirent seafDirent = (SeafDirent) o;
                        if (seafDirent.isDir()) {
                            String currentPath = navContext.getDirPath();
                            String newPath = currentPath.endsWith("/") ?
                                    currentPath + seafDirent.name : currentPath + "/" + seafDirent.name;
                            navContext.setDir(newPath, seafDirent.id);
                            refreshView(false);
                        } else {
                            mActivity.onFileSelected(seafDirent);
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
        });


        swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
//        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(mActivity,R.color.app_main_color));
        swipeRefreshLayout.setColorSchemeColors(R.color.swipe_refresh_color_1, R.color.swipe_refresh_color_2, R.color.swipe_refresh_color_3, R.color.swipe_refresh_color_4);
        refreshView(true);
    }

    public RelativeLayout getEmptyView(){

        return emptyRelativeLayout;
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
                addFile();
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
                }else {
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
        }else{
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
            optionLinearLayout.setEnabled(true);
            categoryTextView.setEnabled(true);
            sortTextView.setEnabled(true);
            createTextView.setEnabled(true);
            transferTextView.setEnabled(true);
        } else {
//            mActivity.disableUpButton();
            navToReposView(forceRefresh);
            optionLinearLayout.setEnabled(false);
            categoryTextView.setEnabled(false);
            sortTextView.setEnabled(false);
            createTextView.setEnabled(false);
            transferTextView.setEnabled(false);
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
                Log.e(DEBUG_TAG, "========================");
                for (SeafRepo repo : repos) {
                    Log.e(DEBUG_TAG, repo.getName() + " " + repo.id);
                }
                Log.e(DEBUG_TAG, "========================");
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
            if (mActivity == null)
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
                    case R.id.rename:
                        mActivity.renameFile(repoID, repoName, path);
                        break;
                    case R.id.update:
//                        mActivity.addUpdateTask(repoID, repoName, dir, localPath);
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
        if (cf!= null) {
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
                    // create file
                    showNewFileDialog();
                }
                else if (which == 1) {
                    // create folder
                    showNewDirDialog();
                }
                else if (which == 2) {
                    // upload file
                    pickFile();
                }
                else if (which == 3) {
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
        Intent intent = new Intent(mActivity, MultiFileChooserActivity.class);
        mActivity.startActivityForResult(intent, MainActivity.PICK_FILES_REQUEST);

//         if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//        UploadChoiceDialog dialog = new UploadChoiceDialog();
//        dialog.show(mActivity.getSupportFragmentManager(), mActivity.PICK_FILE_DIALOG_FRAGMENT_TAG);
//        } else {
//            Intent target = Utils.createGetContentIntent();
//            Intent intent = Intent.createChooser(target, getString(R.string.choose_file));
//        Log.e(DEBUG_TAG, "start choose");
//
//            startActivityForResult(intent, MainActivity.PICK_FILE_REQUEST);
//        }
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
        if (repos.size() > 0) {
            addReposToAdapter(repos);
//            adapter.sortFiles(SettingsManager.instance().getSortFilesTypePref(),
//                    SettingsManager.instance().getSortFilesOrderPref());
            adapter.notifyChanged();
            recyclerView.setVisibility(View.VISIBLE);
            emptyImageView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyImageView.setVisibility(View.VISIBLE);
        }
        // Collapses the currently open view
        //mListView.collapse();
    }

}
