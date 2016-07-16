package com.seafile.seadroid2.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.seafile.seadroid2.R;
import com.seafile.seadroid2.data.SeafCachedFile;
import com.seafile.seadroid2.data.SeafDirent;
import com.seafile.seadroid2.data.SeafGroup;
import com.seafile.seadroid2.data.SeafItem;
import com.seafile.seadroid2.data.SeafRepo;
import com.seafile.seadroid2.ui.base.BaseViewHolder;
import com.seafile.seadroid2.ui.base.CommonAdapter;
import com.seafile.seadroid2.util.WidgetUtils;

import java.util.List;

/**
 * 文件列表适配器
 * Created by Alfred on 2016/7/11.
 */
public class SeafItemAdapter extends CommonAdapter<SeafItem> {

//	private String footerViewText;
//	private boolean isFootViewShown;
//
//	private static final int TYPE_ITEM = 0;
//	private static final int TYPE_FOOTER_ITEM = 1;

    public SeafItemAdapter(Context context, int layoutId, List<SeafItem> datas) {
        super(context, layoutId, datas);
    }

    public SeafItemAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    public void add(SeafItem seafItem){
        mDatas.add(seafItem);
    }

    @Override
    public void bindData(BaseViewHolder viewHolder, SeafItem seafItem, int position) {
        if (seafItem instanceof SeafRepo){
            getRepoView((SeafRepo) seafItem,viewHolder,position);

        }else if(seafItem instanceof SeafGroup){
            getGroupView((SeafGroup)seafItem,viewHolder,position);

        }else if (seafItem instanceof SeafCachedFile){
            getCacheView((SeafCachedFile)seafItem,viewHolder,position);

        }else{
            getDirentView((SeafDirent)seafItem,viewHolder,position);

        }
    }

    private void getRepoView(SeafRepo repo, BaseViewHolder baseViewHolder, int position) {
        baseViewHolder.setVisible(R.id.list_item_multi_select_btn,View.GONE);
        baseViewHolder.setVisible(R.id.list_item_download_status_icon,View.GONE);
        baseViewHolder.setVisible(R.id.list_item_title,View.GONE);
        baseViewHolder.setVisible(R.id.list_item_subtitle,View.GONE);
        baseViewHolder.setVisible(R.id.expandable_toggle_button,View.INVISIBLE);
        ImageLoader.getInstance().displayImage("drawbale://" + repo.getIcon(),(ImageView) baseViewHolder.getView(R.id.list_item_icon),WidgetUtils.iconOptions);

    }

    private void getGroupView(SeafGroup seafGroup,BaseViewHolder baseViewHolder,int position){

    }
    private void getCacheView(SeafCachedFile seafCachedFile, BaseViewHolder baseViewHolder, int position){


    }
    private void getDirentView(final SeafDirent seafDirent, BaseViewHolder baseViewHolder, int position){
        baseViewHolder.setVisible(R.id.list_item_multi_select_btn,View.GONE);
//      baseViewHolder.setImageResource(R.id.list_item_multi_select_btn,R.drawable.multi_select_item_unchecked);
        baseViewHolder.setText(R.id.list_item_title,seafDirent.getTitle());
        if (seafDirent.isDir()){
            baseViewHolder.setVisible(R.id.list_item_download_status_icon,View.GONE);
            baseViewHolder.setVisible(R.id.list_item_download_status_progressbar,View.GONE);
            baseViewHolder.setText(R.id.list_item_subtitle,seafDirent.getSubtitle());
            baseViewHolder.setVisible(R.id.expandable_toggle_button,View.VISIBLE);
            ImageLoader.getInstance().displayImage("drawbale://" + seafDirent.getIcon(),(ImageView) baseViewHolder.getView(R.id.list_item_icon),WidgetUtils.iconOptions);
        }else{
            baseViewHolder.setVisible(R.id.list_item_download_status_icon,View.GONE);
            baseViewHolder.setVisible(R.id.list_item_download_status_progressbar,View.GONE);
            baseViewHolder.setVisible(R.id.expandable_toggle_button,View.VISIBLE);

        }
        baseViewHolder.setOnClickListener(R.id.expandable_toggle_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seafDirent.isDir()){

                }else{

                }
            }
        });
    }
}
