package com.lanquan.ui;

import com.lanquan.R;
import com.lanquan.base.BaseV4Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/** 
 * 类描述 ：主页面--发现页面，包括最新和推荐两个子页面
 * 类名： MainFindFragment.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-6 上午10:59:19  
*/
public class MainFindFragment extends BaseV4Fragment implements OnClickListener {
	public final static String TAG = "MainFindFragment";
	private View rootView;// 根View

	private int index;
	private int currentTabIndex;

	private View[] mTabs;
	ViewPager mViewPager;
	MainFindPagerAdapter mPagerAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_mainfind, container, false);

		mPagerAdapter = new MainFindPagerAdapter(getFragmentManager());

		findViewById();// 初始化views
		initView();

		mViewPager.setAdapter(mPagerAdapter);

		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mViewPager = (ViewPager) rootView.findViewById(R.id.mian_find_pager);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		mTabs = new View[2];
		mTabs[0] = (View) rootView.findViewById(R.id.recommendBtn);
		mTabs[1] = (View) rootView.findViewById(R.id.newBtn);
		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);

		for (View view : mTabs) {
			view.setOnClickListener(this);
		}

		// viewPager绑定事件
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (position != currentTabIndex) {
					mTabs[currentTabIndex].setSelected(false);
				}
				currentTabIndex = position;
				mTabs[currentTabIndex].setSelected(true);
			}
		});
	}

	/**
	 * 刷新
	 */
	public void refreshData() {
		if (currentTabIndex == 0) {
			MainFindRecommendFragment mainFindRecommendFragment = (MainFindRecommendFragment) getFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.mian_find_pager + ":0");
			if (mainFindRecommendFragment != null) {
				mainFindRecommendFragment.refreshData();
			}
		} else if (currentTabIndex == 1) {
			MainFindNewFragment mainFindNewFragment = (MainFindNewFragment) getFragmentManager().findFragmentByTag("android:switcher:" + R.id.mian_find_pager + ":1");
			if (mainFindNewFragment != null) {
				mainFindNewFragment.refreshData();
			}
		}
	}

	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onSubTabClicked(View view) {
		switch (view.getId()) {
		case R.id.recommendBtn:
			index = 0;
			break;
		case R.id.newBtn:
			index = 1;
			break;
		}
		if (currentTabIndex != index) {
			mViewPager.setCurrentItem(index, true);
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
		refreshData();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.recommendBtn:
			onSubTabClicked(v);
			break;
		case R.id.newBtn:
			onSubTabClicked(v);
			break;

		default:
			break;
		}
	}

	/** 
	 * 类描述 ：
	 * 类名： MainFindFragment.java  
	 * Copyright:   Copyright (c)2015    
	 * Company:     zhangshuai   
	 * @author:     zhangshuai    
	 * @version:    1.0    
	 * 创建时间:    2015-8-6 下午3:17:39  
	*/
	public class MainFindPagerAdapter extends FragmentPagerAdapter {

		public MainFindPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			Fragment fragment = null;

			switch (position) {
			case 0:
				fragment = new MainFindRecommendFragment();
				break;
			case 1:
				fragment = new MainFindNewFragment();
				break;
			default:
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}
	}

}
