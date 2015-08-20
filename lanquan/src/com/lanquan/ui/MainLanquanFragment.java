package com.lanquan.ui;

import java.util.Date;
import java.util.LinkedList;

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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseV4Fragment;
import com.lanquan.jsonobject.JsonChannel;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.UserPreference;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/** 
 * 类描述 ：主页面--篮圈页面
 * 类名： MainLanquanFragment.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-6 上午10:58:57  
*/
public class MainLanquanFragment extends BaseV4Fragment {
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
		rootView = inflater.inflate(R.layout.fragment_mainlanquan, container, false);

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
	protected void findViewById() {
		// TODO Auto-generated method stub
		navText = (TextView) rootView.findViewById(R.id.nav_text);
		rightBtn = rootView.findViewById(R.id.right_btn_bg);
		postListView = (PullToRefreshListView) rootView.findViewById(R.id.lanquan_list);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		navText.setText("篮圈");
		rightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().startActivity(new Intent(getActivity(), CreatChannelChooseActivity.class));
			}
		});
		// 设置上拉下拉刷新事件
		postListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
						| DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				pageNow = 0;
				getDataTask(pageNow);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
						| DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				if (pageNow >= 0)
					++pageNow;
				getDataTask(pageNow);
			}
		});
	}

	/**
	 * 网络获取数据
	 */
	private void getDataTask(int p) {
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
		// AsyncHttpClientTool.post(getActivity(), "post/getQuanziPost", params,
		// responseHandler);
		JsonChannel channel1 = new JsonChannel(1, 1, "帅哥", "drawable://" + R.drawable.headimage1, "drawable://" + R.drawable.headimage1, "库里值不值MVP",
				"drawable://" + R.drawable.channel1, new Date(), 23);

		JsonChannel channel2 = new JsonChannel(1, 1, "啦啦", "drawable://" + R.drawable.headimage2, "drawable://" + R.drawable.headimage2, "什么装备值得买",
				"drawable://" + R.drawable.channel2, new Date(), 23);

		JsonChannel channel3 = new JsonChannel(1, 1, "玛丽", "drawable://" + R.drawable.headimage3, "drawable://" + R.drawable.headimage3, "如何提高篮球技术",
				"drawable://" + R.drawable.channel3, new Date(), 23);

		JsonChannel channel4 = new JsonChannel(1, 1, "没灭", "drawable://" + R.drawable.headimage4, "drawable://" + R.drawable.headimage4, "詹姆斯到底有多强",
				"drawable://" + R.drawable.channel4, new Date(), 23);

		JsonChannel channel5 = new JsonChannel(1, 1, "轩辕", "drawable://" + R.drawable.headimage5, "drawable://" + R.drawable.headimage5, "出来打球", "drawable://"
				+ R.drawable.channel5, new Date(), 23);

		JsonChannel channel6 = new JsonChannel(1, 1, "小泽", "drawable://" + R.drawable.headimage6, "drawable://" + R.drawable.headimage6, "投篮技术", "drawable://"
				+ R.drawable.channel6, new Date(), 23);
		jsonChannelList.add(channel1);
		jsonChannelList.add(channel2);
		jsonChannelList.add(channel3);
		jsonChannelList.add(channel4);
		jsonChannelList.add(channel5);
		jsonChannelList.add(channel6);
		// mAdapter.notifyDataSetChanged();
		postListView.onRefreshComplete();
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
					Intent intent;
					if (position == 1) {
						intent = new Intent(getActivity(), ChannelPunchCardActivity.class);
					} else {
						intent = new Intent(getActivity(), ChannelPhotoActivity.class);
					}
					intent.putExtra(ChannelPhotoActivity.JSONCHANNEL, jsonChannel);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

				}
			});

			imageLoader.displayImage(jsonChannel.getC_small_avatar(), holder.channelIconImageView, ImageLoaderTool.getHeadImageOptions(10));
			holder.titleTextView.setText(jsonChannel.getC_title());

			return view;
		}
	}
}
