package com.lanquan.base;

import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**   
*    
* 项目名称：lanquan   
* 类名称：AppManager   
* 类描述：   程序控制类，单一实例，建立Activity的栈，以便管理
* 创建人：张帅  
* 创建时间：2014-1-5 上午10:16:11   
* 修改人：张帅   
* 修改时间：2014-1-5 上午10:16:11   
* 修改备注：   
* @version    
*    
*/
public class AppManager {
	private static Stack<Activity> mActivityStack;
	private static AppManager mAppManager;

	private AppManager() {
	}

	/**
	 * 单一实例
	 */
	public static AppManager getInstance() {
		if (mAppManager == null) {
			mAppManager = new AppManager();
		}
		return mAppManager;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		if (mActivityStack == null) {
			mActivityStack = new Stack<Activity>();
		}
		mActivityStack.add(activity);
	}

	/**
	 * 获取栈顶Activity（堆栈中最后一个压入的）
	 */
	public Activity getTopActivity() {
		Activity activity = mActivityStack.lastElement();
		return activity;
	}

	/**
	 * 结束栈顶Activity（堆栈中最后一个压入的）
	 */
	public void killTopActivity() {
		Activity activity = mActivityStack.lastElement();
		killActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void killActivity(Activity activity) {
		if (activity != null) {
			mActivityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void killActivity(Class<?> cls) {
		for (Activity activity : mActivityStack) {
			if (activity.getClass().equals(cls)) {
				killActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void killAllActivity() {
		for (int i = 0, size = mActivityStack.size(); i < size; i++) {
			if (null != mActivityStack.get(i)) {
				mActivityStack.get(i).finish();
			}
		}
		mActivityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit(Context context) {
		try {
			killAllActivity();
			ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
		}
	}
}
