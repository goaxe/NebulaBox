package com.tsinghua.nebulabox.ui.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsinghua.nebulabox.interf.OnItemClickListener;
import com.tsinghua.nebulabox.interf.OnItemLongClickListener;
import com.tsinghua.nebulabox.util.log.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用的adapter,同时适用于单个item的adapter
 *
 * @param <T>
 * @author alfred
 */
public abstract class CommonAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

	protected Context mContext;
	protected int mLayoutId;
	protected List<T> mDatas;
	protected LayoutInflater mInflater;

	private OnItemClickListener mOnItemClickListener;
	private OnItemLongClickListener mOnItemLongClickListener;

	public CommonAdapter(Context context, int layoutId, List<T> datas) {
		mContext = context;
		mLayoutId = layoutId;
		mDatas = datas;
		mInflater = LayoutInflater.from(context);
		KLog.i("CommentAdapter","commentAdapter");
	}

	public CommonAdapter(Context context, int layoutId){
		this(context,layoutId,new ArrayList<T>());
	}

	@Override
	public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		KLog.i("CommonAdapter","onCreateViewHolder");
		BaseViewHolder baseViewHolder = BaseViewHolder.get(mContext, null, parent, mLayoutId, -1);
		setListener(parent, baseViewHolder, viewType);
		return baseViewHolder;
	}

	protected boolean isEnabled(int viewType) {
		return true;
	}

	protected int getPosition(RecyclerView.ViewHolder viewHolder){
		return viewHolder.getPosition();
	}

	@Override
	public int getItemCount() {
		KLog.i("CommonAdapter", mDatas.size() + "getCount");
		return mDatas == null ? 0 : mDatas.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}

	public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
		mOnItemLongClickListener = onItemLongClickListener;
	}

	protected void setListener(final ViewGroup parent, final BaseViewHolder baseViewHolder, int viewType){
		if (!isEnabled(viewType))
			return;

		baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mOnItemClickListener != null){
					int position = getPosition(baseViewHolder);
					mOnItemClickListener.onItemClck(parent,view,mDatas.get(position),position);
				}
			}
		});

		baseViewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				if (mOnItemClickListener != null){
					int position = getPosition(baseViewHolder);
					return mOnItemLongClickListener.onItemLongClick(parent,view,mDatas.get(position),position);
				}
				return false;
			}
		});
	}

	@Override
	public void onBindViewHolder(BaseViewHolder baseViewHolder, int position) {
		baseViewHolder.updatePosition(position);
		bindData(baseViewHolder,mDatas.get(position),position);
	}

	public void addItemData(T t) {
		if (t == null) {
			return;
		}
		mDatas.add(t);
		notifyDataSetChanged();
	}

	public void addItemDataForPosition(T t, int position) {
		if (t == null) {
			return;
		}
		mDatas.add(position, t);
		notifyDataSetChanged();
	}

	public void removeItemDataForPosition(int position) {
		mDatas.remove(position);
		notifyDataSetChanged();
	}

	public void removeItemDataForObject(T t) {
		mDatas.remove(t);
		notifyDataSetChanged();
	}

	public void addFirstPageDatas(List<T> list) {
		if (list == null) {
			return;
		}
		mDatas.clear();
		mDatas.addAll(list);
		notifyDataSetChanged();
	}

	public void addOtherPageDatas(List<T> list) {
		if (list == null) {
			return;
		}
		mDatas.addAll(list);
		notifyDataSetChanged();
	}

	public List<T> getDatas() {
		return mDatas;
	}
	public void setDatas(List<T> datas){
		mDatas = datas;
		notifyDataSetChanged();
	}
	/**
	 * 数据绑定view
	 *
	 * @param viewHolder
	 * @param t
	 */
	public abstract void bindData(BaseViewHolder viewHolder, T t, int position);

}
