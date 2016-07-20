package com.tsinghua.nebulabox.ui.base;

import android.content.Context;
import android.view.ViewGroup;

import com.tsinghua.nebulabox.interf.MultiItemTypeSupport;
import com.tsinghua.nebulabox.util.log.KLog;

import java.util.List;

public abstract class MultiItemCommonAdapter<T> extends CommonAdapter<T> {

	protected MultiItemTypeSupport<T> mMultiItemTypeSupport;

	public MultiItemCommonAdapter(Context context, List<T> datas, MultiItemTypeSupport<T> multiItemTypeSupport) {
		super(context, -1, datas);
		KLog.i("MultiItemCommonAdapter","data size = " + datas.size());
		mMultiItemTypeSupport = multiItemTypeSupport;
		if (mMultiItemTypeSupport == null) {
			throw new IllegalArgumentException("The interface of MultiItemTypeSupport can not be null !");
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (mMultiItemTypeSupport != null) {
			return mMultiItemTypeSupport.getItemViewType(position,mDatas.get(position));
		}
		return super.getItemViewType(position);
	}


	@Override
	public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		KLog.i("MultiItemCommonAdapter","onCreateViewHolder");
		if (mMultiItemTypeSupport == null) {
			return super.onCreateViewHolder(parent, viewType);
		}

		int layoutId = mMultiItemTypeSupport.getLayoutId(viewType);
		BaseViewHolder baseViewHolder = BaseViewHolder.get(mContext, null, parent, layoutId, -1);
		setListener(parent, baseViewHolder, viewType);
		return baseViewHolder;
	}

	//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		if (mMultiItemTypeSupport == null) {
//			return super.getView(position, convertView, parent);
//		}
//		int layoutId = mMultiItemTypeSupport.getLayoutId(position, getItem(position));
//		BaseViewHolder viewHolder = BaseViewHolder.get(mContext, convertView, parent, layoutId, position);
//		bindData(viewHolder, getItem(position), position);
//		return viewHolder.getConvertView();
//	}

}
