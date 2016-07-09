package com.seafile.seadroid2.global;

import android.app.Activity;

import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Activity的页面管理
 * Created by Alfred on 2016/7/9.
 */
public class ActivityManager {
	private static Stack<Activity> stack = new Stack<>();
	private static ReentrantLock lock = new ReentrantLock();

	/**
	 * 结束当前页面
	 */
	public static void finishCurrent() {
		try {
			lock.lock();
			Activity activity = pop();
			if (activity != null) {
				activity.finish();
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 移除堆栈顶部的对象,并作为此方法的值返回该对象
	 */
	public static Activity pop() {
		Activity activity = null;
		if (!stack.isEmpty()) {
			activity = stack.pop();
		}
		return activity;
	}

	/**
	 * 只是执行activity从stack中remove,并不操作activity对象
	 * @param activity
	 */
	public static void removeActivityFromStack(Activity activity){
		try{
			lock.lock();
			for (Activity activityStack :
					stack) {
				if (activityStack != null && activity != null && activity.getClass().getName().equals(activityStack.getClass().getName())){
					stack.remove(activityStack);
					break;
				}
			}
		}finally {
			lock.unlock();
		}
	}

	/**
	 * 结束所有页面
	 */
	public static void finishAll() {
		try {
			lock.lock();
			for (Activity activity :
					stack) {
				if (activity != null) {
					activity.finish();
				}
			}
			stack.clear();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * finish指定的Activity页面
	 *
	 * @param cl
	 */
	public static void finishActivityByClassName(Class cl) {
		try {
			lock.lock();
			for (Activity activity :
					stack) {
				if (activity != null && cl != null && activity.getClass().getName().equals(cl.getName())) {
					activity.finish();
					stack.remove(activity);
					break;
				}
			}
		} finally {
			lock.unlock();
		}
	}

	public static void finishActivityByActivity(Activity currentActivity){
		try{
			lock.lock();
			for (Activity activity :
					stack) {
				if (activity != null && currentActivity != null && activity.getClass().getName().equals(currentActivity.getClass().getName())){
					activity.finish();
					stack.remove(activity);
					break;
				}
			}
		}finally {
			lock.unlock();
		}
	}

	public static void push(Activity activity){
		if (stack != null && activity != null){
			stack.push(activity);
		}
	}

	public static boolean isEmpty(){
		return stack == null || stack.isEmpty();
	}


}
