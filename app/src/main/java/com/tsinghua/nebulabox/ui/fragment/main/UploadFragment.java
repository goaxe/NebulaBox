package com.tsinghua.nebulabox.ui.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsinghua.nebulabox.R;
import com.tsinghua.nebulabox.ui.base.BaseFragment;

/**
 * Created by Alfred on 2016/7/11.
 */
public class UploadFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }
}
