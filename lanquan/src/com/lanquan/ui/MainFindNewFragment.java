package com.lanquan.ui;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseV4Fragment;
import com.lanquan.config.Constants.Config;
import com.lanquan.jsonobject.JsonChannel;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.JsonChannelTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

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
import android.widget.TextView;

/** 
 * 类描述 ：主页面——发现页面——最新页面
 * 类名： MainFindNewFragment.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-6 下午3:19:38  
*/
public class MainFindNewFragment extends BaseV4Fragment {
	private View rootView;// 根View
	private TextView navText;
	private View rightBtn;

	private PullToRefreshListView postListView;
	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;
	private UserPreference userPreference;

	private LinkedList<JsonChannel> jsonChannelList;
	private int pageNow = 0;// 控制页数
	private ChannelAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userPreference = BaseApplication.getInstance().getUserPreference();
		jsonChannelList = new LinkedList<JsonChannel>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_mainfind_new, container, false);

		findViewById();// 初始化views
		initView();

		// 获取数据
		getDataTask(pageNow);

		postListView.setMode(Mode.BOTH);
		mAdapter = new ChannelAdapter();
		postListView.setAdapter(mAdapter);

		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		postListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 201 && resultCode == 10) {
			if (data != null) {
				int position = data.getIntExtra("position", -1);
				int is_focus = data.getIntExtra("is_focus", 0);
				if (position > -1) {
					jsonChannelList.get(position).setIs_focus(is_focus);
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		postListView = (PullToRefreshListView) rootView.findViewById(R.id.new_channel_list);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		// 设置上拉下拉刷新事件
		postListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				pageNow = 0;
				getDataTask(pageNow);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				if (pageNow >= 0)
					++pageNow;
				//				if (pageNow < 0)
				//					pageNow = 0;
				getDataTask(pageNow);
			}
		});
	}

	public void refreshData() {
		pageNow = 0;
		if (postListView != null) {
			postListView.setRefreshing();
		}
		//		if (jsonChannelList.size() > 0) {
		//			postListView.setVisibility(View.VISIBLE);
		//			emptyView.setVisibility(View.GONE);
		//		} else {
		//			postListView.setVisibility(View.GONE);
		//			emptyView.setVisibility(View.VISIBLE);
		//		}
	}

	/**
	 * 网络获取数据
	 */
	private void getDataTask(int p) {
		if (p < 0) {
			postListView.postDelayed(new Runnable() {
				@Override
				public void run() {
					postListView.onRefreshComplete();
					ToastTool.showShort(getActivity(), "没有更多了！");
				}
			}, 100);
			return;
		}
		final int page = p;
		RequestParams params = new RequestParams();
		params.put("pageIndex", page);
		params.put("pageSize", Config.PAGE_NUM);
		params.put("sort", "update_time");
		params.put("from", 0);
		params.put("recommend", 0);
		if (userPreference.getUserLogin()) {
			//			params.put("user_id", userPreference.getU_id());
			params.put("access_token", userPreference.getAccess_token());
		}

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				LogTool.i("最新频道" + response);
				JsonTool jsonTool = new JsonTool(response);
				String status = jsonTool.getStatus();
				if (status.equals(JsonTool.STATUS_SUCCESS)) {
					JSONObject jsonObject = jsonTool.getJsonObject();
					try {
						JsonChannelTools jsonChannelTools = new JsonChannelTools(jsonObject.getString("data"));
						List<JsonChannel> temp;
						if (userPreference.getUserLogin()) {
							temp = jsonChannelTools.getJsonChannelListWithFoucs();
						} else {
							temp = jsonChannelTools.getJsonChannelList();
						}

						// 如果是首次获取数据
						if (temp != null) {
							if (page == 0) {
								if (temp.size() < Config.PAGE_NUM) {
									pageNow = -1;
								}
								jsonChannelList = new LinkedList<JsonChannel>();
								jsonChannelList.addAll(temp);
							}
							// 如果是获取更多
							else if (page > 0) {
								if (temp.size() < Config.PAGE_NUM) {
									pageNow = -1;
								}
								jsonChannelList.addAll(temp);
							}
							mAdapter.notifyDataSetChanged();
						} else {
							LogTool.e("temp为空");
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					LogTool.e("最新频道列表:" + statusCode + response);
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("获取最新频道列表onFailure" + errorResponse);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				postListView.onRefreshComplete();
			}

		};
		AsyncHttpClientTool.post(getActivity(), "api/channel/channels", params, responseHandler);
	}

	/** 
	 * 类描述 ：推荐列表频道适配器
	 * 类名： MainFindRecommendFragment.java  
	 * Copyright:   Copyright (c)2015    
	 * Company:     zhangshuai   
	 * @author:     zhangshuai    
	 * @version:    1.0    
	 * 创建时间:    2015-8-7 上午9:55:03  
	*/
	class ChannelAdapter extends BaseAdapter {

		private class ViewHolder {
			public ImageView channelIconImageView;
			public TextView titleTextView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return jsonChannelList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return jsonChannelList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
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

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int type = jsonChannel.getType();
					Intent intent = null;
					if (type == 0) {// 图文
						intent = new Intent(getActivity(), ChannelPhotoActivity.class);
					} else if (type == 1) {
						intent = new Intent(getActivity(), ChannelTextActivity.class);
					} else if (type == 2) {
						intent = new Intent(getActivity(), ChannelPunchCardActivity.class);
					}
					if (intent != null) {
						intent.putExtra(ChannelPhotoActivity.JSONCHANNEL, jsonChannel);
						intent.putExtra("position", position);
						startActivityForResult(intent, 201);
						getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					}
				}
			});

			String icon = jsonChannel.getIcon();
			if (icon != null && !icon.isEmpty() && !icon.equals("null")) {
				imageLoader.displayImage(icon, holder.channelIconImageView, ImageLoaderTool.getCircleHeadImageOptions());
			} else {
				imageLoader.displayImage("drawable://" + R.drawable.photoconor, holder.channelIconImageView, ImageLoaderTool.getCircleHeadImageOptions());
			}
			holder.titleTextView.setText(jsonChannel.getTitle());

			return view;
		}
	}
}
