package com.seafile.seadroid2.ui.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.seafile.seadroid2.R;
import com.seafile.seadroid2.global.ActivityManager;

/**
 * Activity的父类
 * Created by Alfred on 2016/7/8.
 */
public class BaseActivity extends AppCompatActivity {
	private ProgressDialog progressDialog ;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityManager.push(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityManager.removeActivityFromStack(this);
	}

	private void initLoadingDialog(){
		if (progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage(getString(R.string.settings_cuc_loading));
			progressDialog.setCancelable(false);
		}
	}

	protected void showLoadingDialog(){
		initLoadingDialog();
		progressDialog.show();
	}

	protected void dismissLoadingDialog(){
		if (progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}


}
