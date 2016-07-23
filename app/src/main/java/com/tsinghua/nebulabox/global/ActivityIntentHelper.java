package com.tsinghua.nebulabox.global;

import android.content.Context;
import android.content.Intent;

import com.tsinghua.nebulabox.ui.activity.MainActivity;

/**
 * Activity跳转管理页面(所有的activity跳转集中到此类)
 * Created by Alfred on 2016/7/9.
 */
public class ActivityIntentHelper {

	public static void gotoMainActivity(Context context){
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}
}
