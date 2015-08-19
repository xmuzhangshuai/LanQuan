package com.lanquan.ui;

import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseV4Fragment;
import com.lanquan.utils.DateTimeTools;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.UserPreference;

/** 
 * 类描述 ：主页面--个人中心页面
 * 类名： MainPersonalFragment.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-6 上午10:59:47  
*/
public class MainPersonalFragment extends BaseV4Fragment implements OnClickListener {
	private View rootView;// 根View
	private TextView navText;
	private View settingBtn;
	private ImageView headImageView;
	private TextView commentCountTextView;
	private TextView channelCountTextView;
	private TextView joinDateTextView;

	private int index;
	private int currentTabIndex;

	private View[] mTabs;
	private ViewPager mViewPager;
	private MyChannelPagerAdapter mPagerAdapter;

	private UserPreference userPreference;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userPreference = BaseApplication.getInstance().getUserPreference();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_mainpersonal, container, false);
		mPagerAdapter = new MyChannelPagerAdapter(getFragmentManager());

		findViewById();// 初始化views
		initView();

		mViewPager.setAdapter(mPagerAdapter);

		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		navText = (TextView) rootView.findViewById(R.id.nav_text);
		mViewPager = (ViewPager) rootView.findViewById(R.id.my_channel_pager);
		headImageView = (ImageView) rootView.findViewById(R.id.headimage);
		commentCountTextView = (TextView) rootView.findViewById(R.id.comment_count);
		channelCountTextView = (TextView) rootView.findViewById(R.id.channel_count);
		joinDateTextView = (TextView) rootView.findViewById(R.id.join_time);
		settingBtn = rootView.findViewById(R.id.right_btn_bg);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		navText.setText("帅哥");
		settingBtn.setOnClickListener(this);
		imageLoader.displayImage(userPreference.getU_small_avatar(), headImageView, ImageLoaderTool.getCircleHeadImageOptions());
		headImageView.setOnClickListener(this);

		commentCountTextView.setText("1");
		channelCountTextView.setText("8");
		joinDateTextView.setText(DateTimeTools.DateToStringWithYMD(new Date()) + "  加入");

		mTabs = new View[2];
		mTabs[0] = (View) rootView.findViewById(R.id.byTimeBtn);
		mTabs[1] = (View) rootView.findViewById(R.id.byChannelBtn);
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
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onSubTabClicked(View view) {
		switch (view.getId()) {
		case R.id.byTimeBtn:
			index = 0;
			break;
		case R.id.byChannelBtn:
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
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.byTimeBtn:
			onSubTabClicked(v);
			break;
		case R.id.byChannelBtn:
			onSubTabClicked(v);
			break;
		case R.id.right_btn_bg:
			getActivity().startActivity(new Intent(getActivity(), SettingActivity.class));
			break;
		case R.id.headimage:
			Intent intent = new Intent(getActivity(), ImageShowerActivity.class);
			intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE, userPreference.getU_small_avatar());
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
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
	public class MyChannelPagerAdapter extends FragmentPagerAdapter {

		public MyChannelPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			Fragment fragment = null;

			switch (position) {
			case 0:
				fragment = new MyChannelByTime();
				break;
			case 1:
				fragment = new MyChannelByChannel();
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
