package com.lanquan.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseV4Fragment;
import com.lanquan.jsonobject.JsonChannel;
import com.lanquan.utils.DateTimeTools;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.LogTool;
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
	public static final String BY_TIME_LIST = "by_time_list";
	public static final String BY_CHANNEL_LIST = "by_channel_list";

	private View rootView;// 根View
	private TextView navText;
	private View settingBtn;
	private ImageView headImageView;
	private TextView commentCountTextView;
	private TextView channelCountTextView;
	private TextView joinDateTextView;
	PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;

	private int index;
	private int currentTabIndex;

	private View[] mTabs;
	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;
	private MyChannelByChannel myChannelByChannel;
	private MyChannelByTime myChannelByTime;

	private UserPreference userPreference;
	private LinkedList<JsonChannel> jsonChannelList;
	private int pageNow = 0;// 控制页数

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userPreference = BaseApplication.getInstance().getUserPreference();
		jsonChannelList = new LinkedList<JsonChannel>();
		myChannelByChannel = new MyChannelByChannel();
		myChannelByTime = new MyChannelByTime();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_mainpersonal, container, false);

		findViewById();// 初始化views
		initView();
		getFragmentManager().beginTransaction().add(R.id.channel_fragment_container, myChannelByTime, "myChannelByTime").show(myChannelByTime).commit();

		// getDataTask(pageNow);

		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		navText = (TextView) rootView.findViewById(R.id.nav_text);
		headImageView = (ImageView) rootView.findViewById(R.id.headimage);
		commentCountTextView = (TextView) rootView.findViewById(R.id.comment_count);
		channelCountTextView = (TextView) rootView.findViewById(R.id.channel_count);
		joinDateTextView = (TextView) rootView.findViewById(R.id.join_time);
		settingBtn = rootView.findViewById(R.id.right_btn_bg);
		mPullRefreshScrollView = (PullToRefreshScrollView) rootView.findViewById(R.id.pull_refresh_scrollview);
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

		mPullRefreshScrollView.setMode(Mode.BOTH);

		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				// TODO Auto-generated method stub
				pageNow = 0;
				if (currentTabIndex == 0) {
					myChannelByTime.getDataTask(pageNow);
				} else if (currentTabIndex == 1) {
					myChannelByChannel.getDataTask(pageNow);
				}

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				// TODO Auto-generated method stub
				if (pageNow >= 0)
					++pageNow;
				if (currentTabIndex == 0) {
					myChannelByTime.getDataTask(pageNow);
				} else if (currentTabIndex == 1) {
					myChannelByChannel.getDataTask(pageNow);
				}
			}
		});

		mScrollView = mPullRefreshScrollView.getRefreshableView();
	}

	private void setFragment(int index) {
		if (index == 0) {
			getFragmentManager().beginTransaction().replace(R.id.channel_fragment_container, myChannelByTime, "myChannelByTime").addToBackStack(null).commit();
		} else if (index == 1) {
			getFragmentManager().beginTransaction().replace(R.id.channel_fragment_container, myChannelByChannel, "myChannelByChannel").addToBackStack(null)
					.commit();
		}

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
			setFragment(index);
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
	 * 类描述 ：按时间
	 * 类名： MainPersonalFragment.java  
	 * Copyright:   Copyright (c)2015    
	 * Company:     zhangshuai   
	 * @author:     zhangshuai    
	 * @version:    1.0    
	 * 创建时间:    2015-8-21 下午2:02:00  
	*/
	public class MyChannelByTime extends BaseV4Fragment {
		private View rootView;// 根View

		protected boolean pauseOnScroll = false;
		protected boolean pauseOnFling = true;
		private TableLayout tableLayout;
		private List<JsonChannel> jsonChannelList;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			userPreference = BaseApplication.getInstance().getUserPreference();
			jsonChannelList = new ArrayList<JsonChannel>();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			rootView = inflater.inflate(R.layout.fragment_my_channel_by_time, container, false);

			findViewById();// 初始化views
			initView();
			getDataTask(0);

			return rootView;
		}

		@Override
		protected void findViewById() {
			// TODO Auto-generated method stub
			tableLayout = (TableLayout) rootView.findViewById(R.id.tablelayout);
		}

		@Override
		protected void initView() {
			// TODO Auto-generated method stub
			if (jsonChannelList != null) {
				for (int i = 0; i < jsonChannelList.size(); i++) {
					tableLayout.addView(getView(i, null), i);
				}
			}
		}

		public void setList() {
			if (jsonChannelList != null) {
				tableLayout.removeAllViews();
				if (jsonChannelList != null) {
					for (int i = 0; i < jsonChannelList.size(); i++) {
						tableLayout.addView(getView(i, null), i);
					}
				}
			} else {
				LogTool.e("Time: setlist,为空");
			}
		}

		/**
		 * 网络获取数据
		 */
		public void getDataTask(int p) {
			// final int page = p;
			// RequestParams params = new RequestParams();
			// params.put("page", pageNow);
			// params.put(UserTable.U_ID, userPreference.getU_id());
			// TextHttpResponseHandler responseHandler = new
			// TextHttpResponseHandler("utf-8") {
			//
			// @Override
			// public void onStart() {
			// // TODO Auto-generated method stub
			// super.onStart();
			// // postListView.setRefreshing();
			// }
			//
			// @Override
			// public void onSuccess(int statusCode, Header[] headers, String
			// response) {
			// // TODO Auto-generated method stub
			// if (statusCode == 200) {
			// List<JsonPostItem> temp = FastJsonTool.getObjectList(response,
			// JsonPostItem.class);
			// if (temp != null) {
			// LogTool.i("获取圈子帖子列表长度" + temp.size());
			// // 如果是首次获取数据
			// if (page == 0) {
			// if (temp.size() < Config.PAGE_NUM) {
			// pageNow = -1;
			// }
			// jsonPostItemList = new LinkedList<JsonPostItem>();
			// jsonPostItemList.addAll(temp);
			// refresh();
			// }
			// // 如果是获取更多
			// else if (page > 0) {
			// if (temp.size() < Config.PAGE_NUM) {
			// pageNow = -1;
			// ToastTool.showShort(getActivity(), "没有更多了！");
			// }
			// jsonPostItemList.addAll(temp);
			// }
			// mAdapter.notifyDataSetChanged();
			// }
			// }
			// }
			//
			// @Override
			// public void onFailure(int statusCode, Header[] headers, String
			// errorResponse, Throwable e) {
			// // TODO Auto-generated method stub
			// LogTool.e("获取圈子帖子列表失败" + errorResponse);
			// }
			//
			// @Override
			// public void onFinish() {
			// // TODO Auto-generated method stub
			// super.onFinish();
			// postListView.onRefreshComplete();
			// }
			//
			// };
			// AsyncHttpClientTool.post(getActivity(), "post/getQuanziPost",
			// params,
			// responseHandler);
			JsonChannel channel1 = new JsonChannel(1, 1, "帅哥", "drawable://" + R.drawable.headimage1, "drawable://" + R.drawable.headimage1, "库里值不值MVP",
					"drawable://" + R.drawable.channel1, new Date(), 23);

			JsonChannel channel2 = new JsonChannel(1, 1, "啦啦", "drawable://" + R.drawable.headimage2, "drawable://" + R.drawable.headimage2, "什么装备值得买",
					"drawable://" + R.drawable.channel2, new Date(), 23);

			JsonChannel channel3 = new JsonChannel(1, 1, "玛丽", "drawable://" + R.drawable.headimage3, "drawable://" + R.drawable.headimage3, "如何提高篮球技术",
					"drawable://" + R.drawable.channel3, new Date(), 23);

			jsonChannelList.add(channel1);
			jsonChannelList.add(channel2);
			jsonChannelList.add(channel3);
			setList();
			mPullRefreshScrollView.onRefreshComplete();
		}

		public View getView(int position, View convertView) {
			// TODO Auto-generated method stub
			View view = convertView;
			LogTool.e("添加");
			final JsonChannel jsonChannel = jsonChannelList.get(position);
			if (jsonChannel == null) {
				return null;
			}

			final ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.channel_list_item, null);
				holder = new ViewHolder();
				holder.channelIconImageView = (ImageView) view.findViewById(R.id.channel_icon);
				holder.titleTextView = (TextView) view.findViewById(R.id.title);
				view.setTag(holder); // 给View添加一个格外的数据
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来
			}

			imageLoader.displayImage(jsonChannel.getC_small_avatar(), holder.channelIconImageView, ImageLoaderTool.getHeadImageOptions(10));
			holder.titleTextView.setText(jsonChannel.getC_title());

			return view;
		}

		private class ViewHolder {
			public ImageView channelIconImageView;
			public TextView titleTextView;
		}
	}

	/** 
	 * 类描述 ：按频道
	 * 类名： MainPersonalFragment.java  
	 * Copyright:   Copyright (c)2015    
	 * Company:     zhangshuai   
	 * @author:     zhangshuai    
	 * @version:    1.0    
	 * 创建时间:    2015-8-21 下午2:02:18  
	*/
	public class MyChannelByChannel extends BaseV4Fragment {
		private View rootView;// 根View

		protected boolean pauseOnScroll = false;
		protected boolean pauseOnFling = true;
		private TableLayout tableLayout;
		private List<JsonChannel> jsonChannelList;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			userPreference = BaseApplication.getInstance().getUserPreference();
			jsonChannelList = new ArrayList<JsonChannel>();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			rootView = inflater.inflate(R.layout.fragment_my_channel_by_time, container, false);

			findViewById();// 初始化views
			initView();
			getDataTask(0);

			return rootView;
		}

		@Override
		protected void findViewById() {
			// TODO Auto-generated method stub
			tableLayout = (TableLayout) rootView.findViewById(R.id.tablelayout);
		}

		@Override
		protected void initView() {
			// TODO Auto-generated method stub
			if (jsonChannelList != null) {
				for (int i = 0; i < jsonChannelList.size(); i++) {
					tableLayout.addView(getView(i, null), i);
				}
			}
		}

		public void setList() {
			if (jsonChannelList != null) {
				LogTool.i("Time: setlist,长度:" + jsonChannelList.size());
				tableLayout.removeAllViews();
				if (jsonChannelList != null) {
					for (int i = 0; i < jsonChannelList.size(); i++) {
						tableLayout.addView(getView(i, null), i);
					}
				}
			} else {
				LogTool.e("Time: setlist,为空");
			}
		}

		/**
		 * 网络获取数据
		 */
		public void getDataTask(int p) {
			// final int page = p;
			// RequestParams params = new RequestParams();
			// params.put("page", pageNow);
			// params.put(UserTable.U_ID, userPreference.getU_id());
			// TextHttpResponseHandler responseHandler = new
			// TextHttpResponseHandler("utf-8") {
			//
			// @Override
			// public void onStart() {
			// // TODO Auto-generated method stub
			// super.onStart();
			// // postListView.setRefreshing();
			// }
			//
			// @Override
			// public void onSuccess(int statusCode, Header[] headers, String
			// response) {
			// // TODO Auto-generated method stub
			// if (statusCode == 200) {
			// List<JsonPostItem> temp = FastJsonTool.getObjectList(response,
			// JsonPostItem.class);
			// if (temp != null) {
			// LogTool.i("获取圈子帖子列表长度" + temp.size());
			// // 如果是首次获取数据
			// if (page == 0) {
			// if (temp.size() < Config.PAGE_NUM) {
			// pageNow = -1;
			// }
			// jsonPostItemList = new LinkedList<JsonPostItem>();
			// jsonPostItemList.addAll(temp);
			// refresh();
			// }
			// // 如果是获取更多
			// else if (page > 0) {
			// if (temp.size() < Config.PAGE_NUM) {
			// pageNow = -1;
			// ToastTool.showShort(getActivity(), "没有更多了！");
			// }
			// jsonPostItemList.addAll(temp);
			// }
			// mAdapter.notifyDataSetChanged();
			// }
			// }
			// }
			//
			// @Override
			// public void onFailure(int statusCode, Header[] headers, String
			// errorResponse, Throwable e) {
			// // TODO Auto-generated method stub
			// LogTool.e("获取圈子帖子列表失败" + errorResponse);
			// }
			//
			// @Override
			// public void onFinish() {
			// // TODO Auto-generated method stub
			// super.onFinish();
			// postListView.onRefreshComplete();
			// }
			//
			// };
			// AsyncHttpClientTool.post(getActivity(), "post/getQuanziPost",
			// params,
			// responseHandler);
			JsonChannel channel1 = new JsonChannel(1, 1, "帅哥", "drawable://" + R.drawable.headimage1, "drawable://" + R.drawable.headimage1, "库里值不值MVP",
					"drawable://" + R.drawable.channel1, new Date(), 23);

			JsonChannel channel2 = new JsonChannel(1, 1, "啦啦", "drawable://" + R.drawable.headimage2, "drawable://" + R.drawable.headimage2, "什么装备值得买",
					"drawable://" + R.drawable.channel2, new Date(), 23);

			JsonChannel channel3 = new JsonChannel(1, 1, "玛丽", "drawable://" + R.drawable.headimage3, "drawable://" + R.drawable.headimage3, "如何提高篮球技术",
					"drawable://" + R.drawable.channel3, new Date(), 23);

			jsonChannelList.add(channel1);
			jsonChannelList.add(channel2);
			jsonChannelList.add(channel3);
			setList();
			mPullRefreshScrollView.onRefreshComplete();
		}

		public View getView(int position, View convertView) {
			// TODO Auto-generated method stub
			View view = convertView;
			LogTool.e("添加");
			final JsonChannel jsonChannel = jsonChannelList.get(position);
			if (jsonChannel == null) {
				return null;
			}

			final ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.channel_list_item, null);
				holder = new ViewHolder();
				holder.channelIconImageView = (ImageView) view.findViewById(R.id.channel_icon);
				holder.titleTextView = (TextView) view.findViewById(R.id.title);
				view.setTag(holder); // 给View添加一个格外的数据
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来
			}

			imageLoader.displayImage(jsonChannel.getC_small_avatar(), holder.channelIconImageView, ImageLoaderTool.getHeadImageOptions(10));
			holder.titleTextView.setText(jsonChannel.getC_title());

			return view;
		}

		private class ViewHolder {
			public ImageView channelIconImageView;
			public TextView titleTextView;
		}
	}
}
