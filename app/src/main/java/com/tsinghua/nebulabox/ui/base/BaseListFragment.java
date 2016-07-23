//package com.seafile.seadroid2.ui.base;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.TextView;
//
//import com.mingle.widget.LoadingView;
//import com.seafile.seadroid2.R;
//import com.seafile.seadroid2.data.SeafDirent;
//import com.seafile.seadroid2.ui.adapter.SeafItemAdapter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//
///**
// * Created by Alfred on 2016/7/11.
// */
//public abstract class BaseListFragment<T> extends BaseFragment<T> implements SwipeRefreshLayout.OnRefreshListener {
//    @Bind(R.id.refresh_layout_base_list_srlayout)
//    SwipeRefreshLayout swipeRefreshLayout;
//    @Bind(R.id.recycler_view_base_list_rl)
//    RecyclerView recyclerView;
//    @Bind(R.id.status_base_list_fl)
//    FrameLayout statusFrameLayout;
//    @Bind(R.id.loadingView_base_list_lv)
//    LoadingView loadingView;
//    @Bind(R.id.status_base_list_tv)
//    TextView statusTextView;
//
//    private int lastVisibleItem;
//    protected List<SeafDirent> list;
//    protected SeafItemAdapter fileListAdapter;
//    private LinearLayoutManager linearLayoutManager;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_base_list, container, false);
//        ButterKnife.bind(this, view);
//
//        initView();
//        initListener();
//        swipeRefreshLayout.setRefreshing(true);
//        return view;
//    }
//
//    private void initView() {
//        linearLayoutManager = new LinearLayoutManager(mActivity);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        loadingView.setVisibility(View.VISIBLE);
//
//        list = new ArrayList<>();
////		fileListAdapter = new SeafItemAdapter(mActivity,list);
////		recyclerView.setAdapter(fileListAdapter);
//
//    }
//
//    private void initListener() {
//        recyclerView.addOnScrollListener(new PauseOnScrollListener());
//        swipeRefreshLayout.setOnRefreshListener(this);
//    }
//
//    protected abstract void onRefreshStart(); //下拉刷新数据
//
//    protected abstract void onScrollLast(); //上拉加载数据
//
//    protected abstract int emptyDataString(); //数据为空时的显示文字
//
//    class PauseOnScrollListener extends RecyclerView.OnScrollListener {OnScrollListener
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
//            switch (newState) {
//                case RecyclerView.SCROLL_STATE_IDLE:
//                    //RecyclerView目前不滑动
//                    int size = recyclerView.getAdapter().getItemCount();
//                    if (lastVisibleItem + 1 == size && fileListAdapter.isFootViewShown() &&
//                            !fileListAdapter.getFooterViewText().equals(getString(R.string.load_data_adequate))) {
//                        onScrollLast();
//                    }
//                    break;
//                case RecyclerView.SCROLL_STATE_DRAGGING:
//                    //RecyclerView开始滑动
//                    break;
//                case RecyclerView.SCROLL_STATE_SETTLING:
//                    //RecyclerView惯性移动
//                    break;
//            }
//        }
//
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        ButterKnife.unbind(this);
//    }
//
//
//    @Override
//    public void onRefresh() {
//        onRefreshStart();
//    }
//}
