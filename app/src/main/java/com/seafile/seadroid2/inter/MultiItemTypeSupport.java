package com.seafile.seadroid2.inter;

/**
 * adapter的不同item
 *
 * @author alfred
 * @date 2016年4月20日18:29:55
 */
public interface MultiItemTypeSupport<T> {

	int getLayoutId(int itemType);

	int getItemViewType(int position, T t);

}
