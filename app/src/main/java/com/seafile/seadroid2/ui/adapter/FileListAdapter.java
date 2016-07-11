package com.seafile.seadroid2.ui.adapter;

import android.content.Context;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.interf.MultiItemTypeSupport;
import com.seafile.seadroid2.ui.base.BaseViewHolder;
import com.seafile.seadroid2.ui.base.MultiItemCommonAdapter;

import java.util.List;

/**
 * 文件列表适配器
 * Created by Alfred on 2016/7/11.
 */
public class FileListAdapter extends MultiItemCommonAdapter<SeafDirent> {
	private static  final int TYPE_ITEM = 0;
	private static  final int TYPE_FOOTER_ITEM = 1;

	public FileListAdapter(Context context, final List<SeafDirent> datas) {
		super(context, datas, new MultiItemTypeSupport<SeafDirent>() {
			@Override
			public int getLayoutId(int itemType) {
				return itemType == TYPE_ITEM ? R.layout.item_file_list : R.layout.recyclerview_footview_layout;
			}

			@Override
			public int getItemViewType(int position, SeafDirent seafDirent) {
				return datas.size() + 1 == position ? TYPE_FOOTER_ITEM : TYPE_ITEM;
			}
		});
	}

	@Override
	public int getItemCount() {
		return mDatas == null ? 0 : mDatas.size() + 1;
	}

	@Override
	public void bindData(BaseViewHolder viewHolder, SeafDirent seafDirent, int position) {
		if (viewHolder.getLayoutId() == R.layout.item_file_list){
			viewHolder.setText(R.id.title_item_file_list_tv,seafDirent.getTitle());
			viewHolder.setText(R.id.date_item_file_list_tv,seafDirent.getSubtitle());
		}
	}
}
