package com.seafile.seadroid2.interf;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by baojia on 2016/5/6.
 */
public interface OnItemLongClickListener<T> {
	boolean onItemLongClick(ViewGroup parent, View view, T t, int position);
}
