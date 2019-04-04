package cn.qimate.test.core.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import java.util.Stack;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 */
public class AppManager {

	private static Stack<Activity> activityStack;
	private static AppManager instance;

	private AppManager() {
	}

	/**
     * 单实例 , UI无需考虑多线程同步问题
     */
	public static AppManager getAppManager() {
		if (instance == null) {
			instance = new AppManager();
		}
		return instance;
	}
	
	/**
     * 添加Activity到栈
     */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}
	
	public Stack<Activity> getActivitys(){
		
		return activityStack;
	}
	/**
     * 获取当前Activity（栈顶Activity）
     */
	public Activity currentActivity() {
		if (activityStack == null || activityStack.isEmpty()) {
			return null;
		}
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
     * 获取当前Activity（栈顶Activity） 没有找到则返回null
     */
	public Activity findActivity(Class<?> cls) {
		Activity activity = null;
		for (Activity aty : activityStack) {
			if (aty.getClass().equals(cls)) {
				activity = aty;
				break;
			}
		}
		return activity;
	}

	 /**
     * 结束当前Activity（栈顶Activity）
     */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	 /**
     * 结束指定的Activity(重载)
     */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
     * 结束指定的Activity(重载)
     */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
     * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
     * 
     * @param cls
     */
	public void finishOthersActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity != null) {
				if (!(activity.getClass().equals(cls))) {
					activity.finish();
					activityStack.remove(activity);
				}
			}
		}
	}

	/**
     * 结束所有Activity
     */
	public void finishAllActivity() {
		for (Activity activity : activityStack) {
			if (activity != null) {
				activity.finish();
			}
		}
		activityStack.clear();
	}

	 /**
     * 应用程序退出
     */
	@SuppressLint("NewApi")
	public void AppExit(Context context) {
		System.exit(0);//正常退出App
		finishAllActivity();
	}
}