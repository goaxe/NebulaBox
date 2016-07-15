package com.seafile.seadroid2.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.seafile.seadroid2.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 创建文件夹的对话框
 * Created by Alfred on 2016/7/13.
 */
public class FileCreatedDialog extends DialogFragment{

	@Bind(R.id.name_dialog_create_file_et)
	EditText nameEditText;
	@Bind(R.id.confirm_dialog_create_file_btn)
	Button confirmBtn;
	@Bind(R.id.cancel_dialog_create_file_btn)
	Button cancelBtn;


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_file,null);
		ButterKnife.bind(this,view);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view)
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.setNegativeButton(R.string.cancel,null);
		return builder.create();

	}


}
