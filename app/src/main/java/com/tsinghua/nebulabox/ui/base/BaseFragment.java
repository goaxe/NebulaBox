package com.tsinghua.nebulabox.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsinghua.nebulabox.ui.NavContext;
import com.tsinghua.nebulabox.ui.activity.MainActivity;

/**
 * Created by Alfred on 2016/7/11.
 */
public abstract class BaseFragment<T> extends Fragment{

	protected MainActivity mActivity;
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
		mActivity = (MainActivity) context;
		mContext = context;
	}

	public NavContext getNavContext() {
		return navContext;
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
