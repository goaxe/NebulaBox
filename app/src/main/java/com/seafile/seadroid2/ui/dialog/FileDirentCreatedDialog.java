package com.seafile.seadroid2.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.SeadroidApplication;
import com.seafile.seadroid2.util.StringUtils;
import com.seafile.seadroid2.util.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 创建文件夹的对话框
 * Created by Alfred on 2016/7/13.
 */
public class FileDirentCreatedDialog extends DialogFragment {

	@Bind(R.id.name_dialog_create_file_et)
	EditText nameEditText;
	@Bind(R.id.confirm_dialog_create_file_btn)
	Button confirmBtn;
	@Bind(R.id.cancel_dialog_create_file_btn)
	Button cancelBtn;

	public onFileDirentCreatedListener onFileDirentCreatedListener;

	public interface onFileDirentCreatedListener{
		void OnFileDirentCreated(String fileDirentName);
	}

	public void setOnFileDirentCreatedListener(onFileDirentCreatedListener onFileDirentCreatedListener){
		this.onFileDirentCreatedListener = onFileDirentCreatedListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_file,null);
		ButterKnife.bind(this,view);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(view)
				.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String fileName = nameEditText.getText().toString();
						if (StringUtils.isEmpty(fileName)){
							ToastUtils.show(SeadroidApplication.getAppContext(),"文件夹名称不能为空!", Toast.LENGTH_SHORT);
							return;
						}
						if (onFileDirentCreatedListener != null){
							onFileDirentCreatedListener.OnFileDirentCreated(fileName);
						}
						dismiss();
					}
				})
				.setNegativeButton(R.string.cancel,null);
		return builder.create();

	}


}
