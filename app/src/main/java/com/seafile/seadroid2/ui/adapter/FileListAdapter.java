package com.seafile.seadroid2.ui.adapter;

import android.content.Context;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.data.SeafRepo;
import com.seafile.seadroid2.ui.base.BaseViewHolder;
import com.seafile.seadroid2.ui.base.CommonAdapter;

import java.util.List;

/**
 * 文件列表适配器
 * Created by Alfred on 2016/7/11.
 */
public class FileListAdapter extends CommonAdapter<SeafRepo> {

//	private String footerViewText;
//	private boolean isFootViewShown;
//
//	private static final int TYPE_ITEM = 0;
//	private static final int TYPE_FOOTER_ITEM = 1;

    public FileListAdapter(Context context, int layoutId, List<SeafRepo> datas) {
        super(context, layoutId, datas);
    }

    public FileListAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    public void bindData(BaseViewHolder viewHolder, SeafRepo seafRepo, int position) {
        viewHolder.setText(R.id.title_item_file_list_tv, seafRepo.getTitle());
        viewHolder.setText(R.id.date_item_file_list_tv, seafRepo.getSubtitle());
        if (!seafRepo.isFile) {
            viewHolder.setVisible(R.id.expandable_toggle_button, false);
        } else {
            viewHolder.setVisible(R.id.expandable_toggle_button, true);
        }

//		if (viewHolder.getLayoutId() == R.layout.item_file_list) {
//			viewHolder.setText(R.id.title_item_file_list_tv, SeafRepo.getTitle());
//			viewHolder.setText(R.id.date_item_file_list_tv, SeafRepo.getSubtitle());
//		} else if (viewHolder.getLayoutId() == R.layout.recyclerview_footview_layout) {
//			if (!StringUtils.isEmpty(footerViewText)) {
//				viewHolder.setText(R.id.tv_loading_more, footerViewText);
//			}
//			viewHolder.setVisible(R.id.tv_loading_more, isFootViewShown);
//		}
    }

//	public String getFooterViewText() {
//		return footerViewText;
//	}
//
//	public void setFooterViewText(String footerViewText) {
//		this.footerViewText = footerViewText;
//		notifyItemChanged(getItemCount());
//	}
//
//	public void setFooterViewText(int footerViewTextId){
//		this.footerViewText = mContext.getResources().getString(footerViewTextId);
//		notifyDataSetChanged();
//	}
//
//	public boolean isFootViewShown() {
//		return isFootViewShown;
//	}
//
//	public void setFootViewShown(boolean footViewShown) {
//		isFootViewShown = footViewShown;
//		notifyDataSetChanged();
//	}
}
