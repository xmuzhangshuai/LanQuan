package com.lanquan.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseV4Fragment;
import com.lanquan.config.Constants.Config;
import com.lanquan.customwidget.CircleBitmapDisplayer;
import com.lanquan.jsonobject.JsonChannel;
import com.lanquan.jsonobject.JsonMyArticle;
import com.lanquan.jsonobject.JsonMyChannel;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.DateTimeTools;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * 类描述 ：主页面--个人中心页面 类名： MainPersonalFragment.java Copyright: Copyright (c)2015
 * Company: zhangshuai
 * 
 * @author: zhangshuai
 * @version: 1.0 创建时间: 2015-8-6 上午10:59:47
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

		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		navText.setText(userPreference.getU_nickname());
		imageLoader.displayImage(userPreference.getU_avatar(), headImageView, getCircleHeadImageOptions());
		commentCountTextView.setText("" + userPreference.getArticle_count());
		channelCountTextView.setText("" + userPreference.getChannel_count());
		joinDateTextView.setText(DateTimeTools.DateToStringWithYMD(userPreference.getU_CreatTime()) + "  加入");
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
		settingBtn.setOnClickListener(this);
		headImageView.setOnClickListener(this);

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
			getFragmentManager().beginTransaction().replace(R.id.channel_fragment_container, myChannelByChannel, "myChannelByChannel").addToBackStack(null).commit();
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
			intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE, userPreference.getU_avatar());
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
			break;
		default:
			break;
		}
	}

	/**
	 * 进入详情页面
	 */
	private void goToDetail(int channelId, final int type) {

		RequestParams params = new RequestParams();
		params.put("channel_id", channelId);
		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage("正在加载...");
		dialog.setCancelable(false);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				dialog.show();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				LogTool.i("获取指定频道信息：response:   " + response);
				try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					if (status.equals(JsonTool.STATUS_SUCCESS)) {
						String data = jsonObject.getString("data");
						JsonChannel jsonChannel = JsonChannel.getJsonChannelByJsonString(data);
						if (jsonChannel != null) {
							Intent intent = null;
							switch (type) {
							case 0:
								intent = new Intent(getActivity(), ChannelPhotoActivity.class);
								break;
							case 1:
								intent = new Intent(getActivity(), ChannelTextActivity.class);
								break;
							case 2:
								intent = new Intent(getActivity(), ChannelPunchCardActivity.class);
								break;

							default:
								break;
							}
							if (intent != null) {
								startActivity(intent.putExtra(ChannelPhotoActivity.JSONCHANNEL, jsonChannel));
								getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
							}

						} else {
							LogTool.e("JsonChannel为空");
						}
					} else {
						LogTool.e("获取指定频道信息：fail");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("获取指定频道失败" + errorResponse);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				dialog.dismiss();
				super.onFinish();
			}
		};
		AsyncHttpClientTool.post(getActivity(), "api/channel/channels", params, responseHandler);
	}

	/**
	 * 类描述 ：按时间 类名： MainPersonalFragment.java Copyright: Copyright (c)2015
	 * Company: zhangshuai
	 * 
	 * @author: zhangshuai
	 * @version: 1.0 创建时间: 2015-8-21 下午2:02:00
	 */
	public class MyChannelByTime extends BaseV4Fragment {
		private View rootView;// 根View

		protected boolean pauseOnScroll = false;
		protected boolean pauseOnFling = true;
		private TableLayout tableLayout;
		private List<JsonMyArticle> jsonMyArticleList;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			jsonMyArticleList = new ArrayList<JsonMyArticle>();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			rootView = inflater.inflate(R.layout.fragment_my_channel_by_time, container, false);

			findViewById();// 初始化views
			initView();
			if (jsonMyArticleList.size() == 0) {
				getDataTask(0);
			}

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
			if (jsonMyArticleList != null) {
				for (int i = 0; i < jsonMyArticleList.size(); i++) {
					tableLayout.addView(getView(i, null), i);
				}
			}
		}

		public void setList() {
			if (jsonMyArticleList != null) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						tableLayout.removeAllViews();
						if (jsonMyArticleList != null) {
							for (int i = 0; i < jsonMyArticleList.size(); i++) {
								tableLayout.addView(getView(i, null), i);
							}
						}
					}
				}).run();
			} else {
				LogTool.e("Time: setlist,为空");
			}
		}

		/**
		 * 网络获取数据
		 */
		public void getDataTask(int p) {
			if (p < 0) {
				mPullRefreshScrollView.postDelayed(new Runnable() {
					@Override
					public void run() {
						mPullRefreshScrollView.onRefreshComplete();
						ToastTool.showShort(getActivity(), "没有更多了！");
					}
				}, 100);
				return;
			}
			final int page = p;
			RequestParams params = new RequestParams();
			params.put("access_token", userPreference.getAccess_token());
			params.put("pageIndex", page);
			params.put("pageSize", Config.PAGE_NUM);
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					LogTool.i("按时间" + response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						String status = jsonObject.getString("status");
						if (status.equals(JsonTool.STATUS_SUCCESS)) {

							String data = jsonObject.getString("data");
							JSONArray jsonArray = new JSONArray(data);
							if (jsonArray != null) {
								if (page == 0) {
									if (jsonArray.length() < Config.PAGE_NUM) {
										pageNow = -1;
									}
									jsonMyArticleList.clear();
									for (int i = 0; i < jsonArray.length(); i++) {
										JsonMyArticle jsonMyArticle = JsonMyArticle.getJsonMyArticle(jsonArray.getJSONObject(i));
										if (jsonMyArticle != null) {
											jsonMyArticleList.add(jsonMyArticle);
										} else {
											LogTool.e("转化jsonMyChannel为空");
										}
									}
								}
								// 如果是获取更多
								else if (page > 0) {
									if (jsonArray.length() < Config.PAGE_NUM) {
										pageNow = -1;
										ToastTool.showShort(getActivity(), "没有更多了！");
									}
									for (int i = 0; i < jsonArray.length(); i++) {
										JsonMyArticle jsonMyArticle = JsonMyArticle.getJsonMyArticle(jsonArray.getJSONObject(i));
										if (jsonMyArticle != null) {
											jsonMyArticleList.add(jsonMyArticle);
										} else {
											LogTool.e("转化jsonmessage为空");
										}
									}
								}
							}
							setList();
						} else {
							LogTool.e("按频道获取个人频道列表失败");
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					LogTool.e("按时间获取个人文章" + errorResponse);
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					super.onFinish();
					mPullRefreshScrollView.onRefreshComplete();
				}

			};
			AsyncHttpClientTool.post(getActivity(), "api/user/myArticles", params, responseHandler);

		}

		public View getView(int position, View convertView) {
			// TODO Auto-generated method stub
			View view = convertView;
			final JsonMyArticle jsonMyArticle = jsonMyArticleList.get(position);
			if (jsonMyArticle == null) {
				return null;
			}

			final ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.by_time_list_item, null);
				holder = new ViewHolder();
				holder.headImageView = (ImageView) view.findViewById(R.id.head_image);
				holder.titleTextView = (TextView) view.findViewById(R.id.title);
				holder.timeTextView = (TextView) view.findViewById(R.id.time);
				holder.contentTextView = (TextView) view.findViewById(R.id.content);
				holder.itemImageView = (ImageView) view.findViewById(R.id.item_image);
				holder.favorBtn = (CheckBox) view.findViewById(R.id.favor_btn);
				holder.favorCountTextView = (TextView) view.findViewById(R.id.favor_count);
				holder.deleteBtn = (ImageView) view.findViewById(R.id.delete_btn);
				holder.commentCountTextView = (TextView) view.findViewById(R.id.comment_count);
				view.setTag(holder); // 给View添加一个格外的数据
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来
			}

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//					goToDetail(jsonMyArticle.getChannel_id(), jsonMyArticle.get);
				}
			});

			// 设置头像
			if (!TextUtils.isEmpty(jsonMyArticle.getIcon()) && !jsonMyArticle.getIcon().equals("null")) {
				imageLoader.displayImage(jsonMyArticle.getIcon(), holder.headImageView, ImageLoaderTool.getCircleHeadImageOptions());
				// if (userPreference.getU_id() != channel.getC_userid()) {
				// // 点击头像进入详情页面
				// holder.headImageView.setOnClickListener(new OnClickListener()
				// {
				//
				// @Override
				// public void onClick(View v) {
				// // TODO Auto-generated method stub
				// // Intent intent = new Intent(getActivity(),
				// // PersonDetailActivity.class);
				// // intent.putExtra(UserTable.U_ID,
				// // jsonPostItem.getP_userid());
				// // intent.putExtra(UserTable.U_NICKNAME,
				// // jsonPostItem.getP_username());
				// // intent.putExtra(UserTable.U_SMALL_AVATAR,
				// // jsonPostItem.getP_small_avatar());
				// // startActivity(intent);
				// // getActivity().overridePendingTransition(R.anim.zoomin2,
				// // R.anim.zoomout);
				// }
				// });
				// }
			}

			// 设置内容
			holder.contentTextView.setText(jsonMyArticle.getMessage());

			// 设置题目
			holder.titleTextView.setText(jsonMyArticle.getChannel_title());

			// 设置日期
			holder.timeTextView.setText(DateTimeTools.getInterval(jsonMyArticle.getCreate_time()));

			// 设置被赞次数
			// holder.favorCountTextView.setText("" + jsonMyArticle.getLight());

			// 设置图片
			if (!TextUtils.isEmpty(jsonMyArticle.getImage_url()) && !jsonMyArticle.getImage_url().equals("null")) {
				imageLoader.displayImage(jsonMyArticle.getImage_url(), holder.itemImageView, ImageLoaderTool.getImageOptions());
				holder.itemImageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(), ImageShowerActivity.class);
						intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE, jsonMyArticle.getImage_url());
						startActivity(intent);
						getActivity().overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
					}
				});
			}

			return view;
		}

		private class ViewHolder {
			public ImageView headImageView;
			public TextView titleTextView;
			public TextView timeTextView;
			public TextView contentTextView;
			public CheckBox favorBtn;
			public TextView favorCountTextView;
			public ImageView deleteBtn;
			public TextView commentCountTextView;
			public ImageView itemImageView;
		}
	}

	/**
	 * 类描述 ：按频道 类名： MainPersonalFragment.java Copyright: Copyright (c)2015
	 * Company: zhangshuai
	 * 
	 * @author: zhangshuai
	 * @version: 1.0 创建时间: 2015-8-21 下午2:02:18
	 */
	public class MyChannelByChannel extends BaseV4Fragment {
		private View rootView;// 根View

		protected boolean pauseOnScroll = false;
		protected boolean pauseOnFling = true;
		private TableLayout tableLayout;
		private List<JsonMyChannel> jsonMyChannelList;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			userPreference = BaseApplication.getInstance().getUserPreference();
			jsonMyChannelList = new ArrayList<JsonMyChannel>();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			rootView = inflater.inflate(R.layout.fragment_my_channel_by_time, container, false);

			findViewById();// 初始化views
			initView();
			if (jsonMyChannelList.size() == 0) {
				getDataTask(0);
			}

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
			if (jsonMyChannelList != null) {
				for (int i = 0; i < jsonMyChannelList.size(); i++) {
					tableLayout.addView(getView(i, null), i);
				}
			}
		}

		public void setList() {
			if (jsonMyChannelList != null) {
				tableLayout.removeAllViews();
				if (jsonMyChannelList != null) {
					for (int i = 0; i < jsonMyChannelList.size(); i++) {
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
			if (p < 0) {
				mPullRefreshScrollView.postDelayed(new Runnable() {
					@Override
					public void run() {
						mPullRefreshScrollView.onRefreshComplete();
						ToastTool.showShort(getActivity(), "没有更多了！");
					}
				}, 100);
				return;
			}
			final int page = p;
			RequestParams params = new RequestParams();
			params.put("access_token", userPreference.getAccess_token());
			params.put("pageIndex", page);
			params.put("pageSize", Config.PAGE_NUM);
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					LogTool.i("按频道" + response);
					try {
						JSONObject jsonObject = new JSONObject(response);
						String status = jsonObject.getString("status");
						if (status.equals(JsonTool.STATUS_SUCCESS)) {

							String data = jsonObject.getString("data");
							JSONArray jsonArray = new JSONArray(data);
							if (jsonArray != null) {
								if (page == 0) {
									if (jsonArray.length() < Config.PAGE_NUM) {
										pageNow = -1;
									}
									jsonMyChannelList.clear();
									for (int i = 0; i < jsonArray.length(); i++) {
										JsonMyChannel jsonMyChannel = JsonMyChannel.getJsonMyChannel(jsonArray.getJSONObject(i));
										if (jsonMyChannel != null) {
											jsonMyChannelList.add(jsonMyChannel);
										} else {
											LogTool.e("转化jsonMyChannel为空");
										}
									}
								}
								// 如果是获取更多
								else if (page > 0) {
									if (jsonArray.length() < Config.PAGE_NUM) {
										pageNow = -1;
										ToastTool.showShort(getActivity(), "没有更多了！");
									}
									for (int i = 0; i < jsonArray.length(); i++) {
										JsonMyChannel jsonMyChannel = JsonMyChannel.getJsonMyChannel(jsonArray.getJSONObject(i));
										if (jsonMyChannel != null) {
											jsonMyChannelList.add(jsonMyChannel);
										} else {
											LogTool.e("转化jsonmessage为空");
										}
									}
								}
							}
							setList();
						} else {
							LogTool.e("按频道获取个人频道列表失败");
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					LogTool.e("按频道获取个人文章列表失败" + errorResponse);
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					super.onFinish();
					mPullRefreshScrollView.onRefreshComplete();
				}

			};
			AsyncHttpClientTool.post(getActivity(), "api/user/myChannels", params, responseHandler);
		}

		public View getView(int position, View convertView) {
			// TODO Auto-generated method stub
			View view = convertView;
			final JsonMyChannel jsonChannel = jsonMyChannelList.get(position);
			if (jsonChannel == null) {
				return null;
			}

			final ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.by_channel_list_item, null);
				holder = new ViewHolder();
				holder.channelIconImageView = (ImageView) view.findViewById(R.id.channel_icon);
				holder.titleTextView = (TextView) view.findViewById(R.id.title);
				view.setTag(holder); // 给View添加一个格外的数据
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来
			}

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					goToDetail(jsonChannel.getChannel_id(), jsonChannel.getType());
				}
			});

			if (!TextUtils.isEmpty(jsonChannel.getIcon()) && !jsonChannel.getIcon().equals("null")) {
				imageLoader.displayImage(jsonChannel.getIcon(), holder.channelIconImageView, ImageLoaderTool.getCircleHeadImageOptions());
			} else {
				imageLoader.displayImage("drawable://" + R.drawable.photoconor, holder.channelIconImageView, ImageLoaderTool.getCircleHeadImageOptions());
			}

			holder.titleTextView.setText(jsonChannel.getChannel_title());

			return view;
		}

		private class ViewHolder {
			public ImageView channelIconImageView;
			public TextView titleTextView;
		}
	}

	public static DisplayImageOptions getCircleHeadImageOptions() {
		DisplayImageOptions options = null;
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_headimage)// 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.default_headimage) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.default_headimage) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.displayer(new CircleBitmapDisplayer()) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
		return options;
	}
}
