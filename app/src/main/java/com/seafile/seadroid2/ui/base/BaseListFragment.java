package com.seafile.seadroid2.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mingle.widget.LoadingView;
import com.seafile.seadroid2.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Alfred on 2016/7/11.
 */
public class BaseListFragment extends BaseFragment {
	@Bind(R.id.refresh_layout_base_list_srlayout)
	SwipeRefreshLayout swipeRefreshLayout;
	@Bind(R.id.recycler_view_base_list_rl)
	RecyclerView recyclerView;
	@Bind(R.id.status_base_list_fl)
	FrameLayout statusFrameLayout;
	@Bind(R.id.loadingView_base_list_lv)
	LoadingView loadingView;
	@Bind(R.id.status_base_list_tv)
	TextView statusTextView;

	private List
	private LinearLayoutManager linearLayoutManager;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_base_list,container,false);
		ButterKnife.bind(this,view);
		return view;
	}

	private void initView(){
		linearLayoutManager = new LinearLayoutManager(mActivity);
		recyclerView.setLayoutManager(linearLayoutManager);
		loadingView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}


}
