package com.seafile.seadroid2.ui.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.ui.base.BaseFragment;

/**
 * Created by Alfred on 2016/7/11.
 */
public class UserCenterFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_center, container, false);
    }
}
