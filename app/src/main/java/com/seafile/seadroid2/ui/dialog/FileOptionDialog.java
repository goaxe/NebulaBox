package com.seafile.seadroid2.ui.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.seafile.seadroid2.R;

/**
 * 文件操作的列表对话框
 * Created by Alfred on 2016/7/13.
 */
public class FileOptionDialog extends DialogFragment {
    private String fileName;
    private String[] list;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void OnItemClick(DialogInterface dialog, String[] list, int which);
    }


    public String[] getList() {
        return list;
    }

    public void setList(String[] list) {
        this.list = list;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (list == null) {
            return super.onCreateDialog(savedInstanceState);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.item_dialog_file_option, null);
        if (!TextUtils.isEmpty(fileName)) {
            builder.setTitle(fileName);
        }

        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(dialog, list, which);
                }
            }
        });
        return builder.create();
    }


}
