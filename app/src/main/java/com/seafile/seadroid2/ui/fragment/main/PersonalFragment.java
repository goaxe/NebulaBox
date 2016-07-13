package com.seafile.seadroid2.ui.fragment.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.ui.base.BaseFragment;
import com.seafile.seadroid2.ui.dialog.FileOptionDialog;

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

	private List<SeafDirent> pictureList;
	private List<SeafDirent> musicList;
	private List<SeafDirent> movieList;
	private List<SeafDirent> txtList;
	private List<SeafDirent> appList;
	private List<SeafDirent> allList;

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
				}else {
//					for (int i = 0 ; i < )
				}
				break;
			case 2:
				//音乐,按时间倒序排序
				if (((FileOptionDialog) dialog).getList().length > 2) {
					//音乐
				}
				break;
			case 3:
				//影视
				break;
			case 4:
				//文档
				break;
			case 5:
				//应用
				break;
			case 6:
				//全部
				break;
		}
	}

//	private NavContext getNavContext() {
//		return mActivity.getNavContext();
//	}
//
//	public void showFileBottomSheet(String title, final SeafDirent dirent) {
//		final String repoName = getNavContext().getRepoName();
//		final String repoID = getNavContext().getRepoID();
//		final String dir = getNavContext().getDirPath();
//		final String path = Utils.pathJoin(dir, dirent.name);
//		final String filename = dirent.name;
//		final String localPath = getDataManager().getLocalRepoFile(repoName, repoID, path).getPath();
//		final BottomSheet.Builder builder = new BottomSheet.Builder(mActivity);
//		builder.title(title).sheet(R.menu.bottom_sheet_op_file).listener(new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				switch (which) {
//					case R.id.share:
//						mActivity.shareFile(repoID, path);
//						break;
//					case R.id.delete:
//						mActivity.deleteFile(repoID, repoName, path);
//						break;
//					case R.id.copy:
//						mActivity.copyFile(repoID, repoName, dir, filename, false);
//						break;
//					case R.id.move:
//						mActivity.moveFile(repoID, repoName, dir, filename, false);
//						break;
//					case R.id.rename:
//						mActivity.renameFile(repoID, repoName, path);
//						break;
//					case R.id.update:
//						mActivity.addUpdateTask(repoID, repoName, dir, localPath);
//						break;
//					case R.id.download:
//						mActivity.downloadFile(dir, dirent.name);
//						break;
//					case R.id.export:
//						mActivity.exportFile(dirent.name);
//						break;
//					case R.id.star:
//						mActivity.starFile(repoID, dir, filename);
//						break;
//				}
//			}
//		}).show();
//
//		SeafRepo repo = getDataManager().getCachedRepoByID(repoID);
//		if (repo != null && repo.encrypted) {
//			builder.remove(R.id.share);
//		}
//
//		SeafCachedFile cf = getDataManager().getCachedFile(repoName, repoID, path);
//		if (cf!= null) {
//			builder.remove(R.id.download);
//		} else {
//			builder.remove(R.id.update);
//		}
//
//	}
}
