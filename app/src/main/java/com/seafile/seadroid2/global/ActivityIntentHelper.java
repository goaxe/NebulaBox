package com.seafile.seadroid2.global;

import android.content.Context;
import android.content.Intent;

import com.seafile.seadroid2.ui.activity.TestActivity;

/**
 * Activity跳转管理页面(所有的activity跳转集中到此类)
 * Created by Alfred on 2016/7/9.
 */
public class ActivityIntentHelper {

	public static void gotoMainActivity(Context context){
		Intent intent = new Intent(context, TestActivity.class);
		context.startActivity(intent);
	}
}
