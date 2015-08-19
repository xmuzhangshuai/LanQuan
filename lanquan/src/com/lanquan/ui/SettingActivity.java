package com.lanquan.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.lanquan.R;
import com.lanquan.base.BaseFragmentActivity;

/**
 * 类名称：SettingActivity
 * 类描述：设置页面
 * 创建人： 张帅
 * 创建时间：2014年8月17日 下午4:30:12
 *
 */
public class SettingActivity extends BaseFragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);

		findViewById();
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		/**********加载设置主页面**********/
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		SettingMainFragment settingMainFragment = new SettingMainFragment();
		fragmentTransaction.add(R.id.set_container, settingMainFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}

}
