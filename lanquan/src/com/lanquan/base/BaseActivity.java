package com.lanquan.base;

import com.lanquan.utils.NetworkUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

/** 
 * 类描述 ：自定义的继承了Activity的抽象类，实现了findViewById和initView两个抽象方法，子类必须实现覆盖
 * 类名： BaseActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-1 上午9:45:04  
*/

public abstract class BaseActivity extends Activity {
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	public static final String TAG = BaseActivity.class.getSimpleName();

	protected Handler mHandler = null;

	// 写一个广播的内部类，当收到动作时，结束activity
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			close();
		}
	};

	// 监听网络状态
	private BroadcastReceiver netBroadCastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (!NetworkUtils.isNetworkAvailable(BaseActivity.this)) {
				NetworkUtils.networkStateTips(BaseActivity.this);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		AppManager.getInstance().addActivity(this);
		// PushAgent.getInstance(this).onAppStart();

		// 在onCreate中注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction("close");
		registerReceiver(this.broadcastReceiver, filter); // 注册
	}

	/** 
	 * 关闭 
	 */
	public void close() {
		Intent intent = new Intent();
		intent.setAction("close"); // 说明动作
		sendBroadcast(intent);// 该函数用于发送广播
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);// 在onDestroy注销广播。
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 卸载广播
		if (netBroadCastReceiver != null) {
			BaseActivity.this.unregisterReceiver(netBroadCastReceiver);
		}

		// 友盟统计
		MobclickAgent.onPageEnd(this.getClass().getName());// 统计页面
		MobclickAgent.onPause(this); // 统计时长
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		BaseActivity.this.registerReceiver(netBroadCastReceiver, intentFilter);

		// 友盟统计
		MobclickAgent.onPageStart(this.getClass().getCanonicalName());// 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/**
	 * 绑定控件id
	 */
	protected abstract void findViewById();

	/**
	 * 初始化控件
	 */
	protected abstract void initView();

	/**
	 * 通过类名启动Activity
	 * 
	 * @param pClass
	 */
	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	/**
	 * 通过类名启动Activity，并且含有Bundle数据
	 * 
	 * @param pClass
	 * @param pBundle
	 */
	protected void openActivity(Class<?> pClass, Bundle pBundle) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	/**
	 * 通过Action启动Activity
	 * 
	 * @param pAction
	 */
	protected void openActivity(String pAction) {
		openActivity(pAction, null);
	}

	/**
	 * 通过Action启动Activity，并且含有Bundle数据
	 * 
	 * @param pAction
	 * @param pBundle
	 */
	protected void openActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	protected void DisPlay(String content) {
		Toast.makeText(this, content, 1).show();
	}

	/** 加载进度条 **/
	@SuppressLint("ShowToast")
	public Dialog showProgressDialog(String msg) {
		ProgressDialog dialog = new ProgressDialog(this);
		if (!TextUtils.isEmpty(msg)) {
			dialog.setMessage(msg);
		} else {
			dialog.setMessage("请稍候...");
		}
		dialog.show();
		return dialog;
	}

}
