package com.lanquan.ui;

import com.lanquan.R;
import com.lanquan.base.BaseFragmentActivity;
import com.umeng.update.UmengUpdateAgent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;

/** 
 * 类描述 ：登录后主页，包含四个Fragments,MainLanquanFragment、MainFindFragment、MainMsgFragment、MainPernalFragment。
 *        点击后分别进如不同的子页面。
 * 类名： MainActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-6 上午11:19:10  
*/
public class MainActivity extends BaseFragmentActivity {
	private View[] mTabs;
	private MainLanquanFragment lanquanFragment;// 篮圈页面
	private MainFindFragment findFragment;// 发现页面
	private MainMsgFragment msgFragment;// 动态页面
	private MainPersonalFragment personalFragment;// 个人中心页面

	private int index;
	// 当前fragment的index
	private int currentTabIndex = 0;
	private Fragment[] fragments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);

		lanquanFragment = new MainLanquanFragment();
		findFragment = new MainFindFragment();
		msgFragment = new MainMsgFragment();
		personalFragment = new MainPersonalFragment();
		fragments = new Fragment[] { lanquanFragment, findFragment, msgFragment, personalFragment };

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mTabs = new View[4];
		mTabs[0] = (View) findViewById(R.id.btn_container_lanquan);
		mTabs[1] = (View) findViewById(R.id.btn_container_find);
		mTabs[2] = (View) findViewById(R.id.btn_container_msg);
		mTabs[3] = (View) findViewById(R.id.btn_container_personal);
		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, lanquanFragment, MainLanquanFragment.TAG).show(lanquanFragment).commit();
	}

	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.btn_container_lanquan:
			index = 0;
			break;
		case R.id.btn_container_find:
			index = 1;
			break;
		case R.id.btn_container_msg:
			index = 2;
			break;
		case R.id.btn_container_personal:
			index = 3;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {

				if (index == 0) {
					trx.add(R.id.fragment_container, fragments[index], MainLanquanFragment.TAG);
				} else if (index == 1) {
					trx.add(R.id.fragment_container, fragments[index], MainFindFragment.TAG);
				} else if (index == 2) {
					trx.add(R.id.fragment_container, fragments[index], MainMsgFragment.TAG);
				} else {
					trx.add(R.id.fragment_container, fragments[index]);
				}
			}
			trx.show(fragments[index]).commit();
		} else {
			if (currentTabIndex == 0) {
				MainLanquanFragment myLanquanFragment = (MainLanquanFragment) getSupportFragmentManager().findFragmentByTag(MainLanquanFragment.TAG);
				if (myLanquanFragment != null) {
					myLanquanFragment.refreshData();
				}
			} else if (currentTabIndex == 1) {
				MainFindFragment mainFindFragment = (MainFindFragment) getSupportFragmentManager().findFragmentByTag(MainFindFragment.TAG);
				if (mainFindFragment != null) {
					mainFindFragment.refreshData();
				}
			} else if (currentTabIndex == 2) {
				MainMsgFragment mainMsgFragment = (MainMsgFragment) getSupportFragmentManager().findFragmentByTag(MainMsgFragment.TAG);
				if (mainMsgFragment != null) {
					mainMsgFragment.refreshData();
				}
			}
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

}
