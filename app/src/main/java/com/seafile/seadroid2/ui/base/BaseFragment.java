package com.seafile.seadroid2.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seafile.seadroid2.ui.NavContext;

/**
 * Created by Alfred on 2016/7/11.
 */
public abstract class BaseFragment extends Fragment{

	protected Activity mActivity;
	protected Context mContext;

	private NavContext navContext = new NavContext();

	public static Fragment newInstance(BaseFragment baseFragment){
		Bundle bundle = new Bundle();
		baseFragment.setArguments(bundle);
		return  baseFragment;

	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mActivity = (Activity) context;
		mContext = context;
	}

	public NavContext getNavContext() {
		return navContext;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
