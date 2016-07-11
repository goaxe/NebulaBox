package com.seafile.seadroid2.ui.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.ui.base.BaseFragment;

import butterknife.ButterKnife;

/**
 * 个人
 * Created by Alfred on 2016/7/11.
 */
public class PersonalFragment extends BaseFragment {

//	@Bind(R.id.category)

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_personal,container,false);
		ButterKnife.bind(this,view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
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
