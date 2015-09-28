package com.lanquan.ui;

import java.util.LinkedList;

import org.apache.http.Header;
import org.json.JSONArray;
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
import com.lanquan.jsonobject.JsonMyMessage;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.DateTimeTools;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
 * 类描述 ：主页面--动态页面 类名： MainMsgFragment.java Copyright: Copyright (c)2015 Company:
 * zhangshuai
 * 
 * @author: zhangshuai
 * @version: 1.0 创建时间: 2015-8-6 上午10:59:38
 */
public class MainMsgFragment extends BaseV4Fragment {
	private View rootView;// 根View
	private TextView navText;
	private PullToRefreshListView messageListView;

	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;
	private int pageNow = 0;// 控制页数
	private MyMessageAdapter mAdapter;
	private UserPreference userPreference;
	private LinkedList<JsonMyMessage> messageList;
	private int unread_count = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userPreference = BaseApplication.getInstance().getUserPreference();
		messageList = new LinkedList<JsonMyMessage>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_mainmsg, container, false);

		findViewById();// 初始化views
		initView();
		// 获取数据
		getDataTask(pageNow);

		messageListView.setMode(Mode.BOTH);
		mAdapter = new MyMessageAdapter();
		messageListView.setAdapter(mAdapter);
		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		navText = (TextView) rootView.findViewById(R.id.nav_text);
		messageListView = (PullToRefreshListView) rootView.findViewById(R.id.msg_list);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		navText.setText("动态");
		// 设置上拉下拉刷新事件
		messageListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

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
				if (pageNow < 0)
					pageNow = 0;
				getDataTask(pageNow);
			}
		});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		messageListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling));
	}

	/**
	 * 网络获取数据
	 */
	private void getDataTask(int p) {
		final int page = p;
		RequestParams params = new RequestParams();
		params.put("access_token", userPreference.getAccess_token());
		params.put("pageIndex", page);
		params.put("pageSize", Config.PAGE_NUM);
		params.put("sort", "create_time");
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub

				try {
					LogTool.i("个人中心消息模块" + response);
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					unread_count = jsonObject.getInt("unread_count");
					if (status.equals(JsonTool.STATUS_SUCCESS)) {

						String data = jsonObject.getString("data");
						JSONArray jsonArray = new JSONArray(data);
						if (jsonArray != null) {
							if (page == 0) {
								if (jsonArray.length() < Config.PAGE_NUM) {
									pageNow = -1;
								}
								messageList.clear();
								for (int i = 0; i < jsonArray.length(); i++) {
									JsonMyMessage jsonMyMessage = JsonMyMessage.getJsonMyMessage(jsonArray.getJSONObject(i));
									if (jsonMyMessage != null) {
										messageList.add(jsonMyMessage);
									} else {
										LogTool.e("转化jsonmessage为空");
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
									JsonMyMessage jsonMyMessage = JsonMyMessage.getJsonMyMessage(jsonArray.getJSONObject(i));
									if (jsonMyMessage != null) {
										messageList.add(jsonMyMessage);
									} else {
										LogTool.e("转化jsonmessage为空");
									}
								}
							}

						}
						mAdapter.notifyDataSetChanged();
					} else {
						LogTool.e("获取消息列表失败");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("--->" + statusCode + errorResponse);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				messageListView.onRefreshComplete();
			}

		};
		AsyncHttpClientTool.post(getActivity(), "api/user/message", params, responseHandler);
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
	 * 
	 * 类名称：MessageAdapter 类描述：适配器 创建人： 张帅 创建时间：2014年9月14日 下午3:41:15
	 *
	 */
	class MyMessageAdapter extends BaseAdapter {
		private class ViewHolder {
			public ImageView headImageView;
			public TextView nameTextView;
			public ImageView itemImageView;
			public TextView timeTextView;
			public TextView commentTextView;
			public TextView itemTextView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return messageList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return messageList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			final JsonMyMessage jsonMyMessage = messageList.get(position);
			if (jsonMyMessage == null) {
				return null;
			}

			final ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.my_msg_list_item, null);
				holder = new ViewHolder();
				holder.headImageView = (ImageView) view.findViewById(R.id.head_image);
				holder.nameTextView = (TextView) view.findViewById(R.id.name);
				holder.itemImageView = (ImageView) view.findViewById(R.id.item_image);
				holder.timeTextView = (TextView) view.findViewById(R.id.time);
				holder.commentTextView = (TextView) view.findViewById(R.id.comment);
				holder.itemTextView = (TextView) view.findViewById(R.id.item_text);
				view.setTag(holder); // 给View添加一个格外的数据
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来
			}

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					goToDetail(jsonMyMessage.getObject_id(), jsonMyMessage.getObject_type());
				}
			});

			// 设置头像
			if (!TextUtils.isEmpty(jsonMyMessage.getAvatar())) {

				imageLoader.displayImage(jsonMyMessage.getAvatar(), holder.headImageView, ImageLoaderTool.getCircleHeadImageOptions());

				if (userPreference.getU_id() != jsonMyMessage.getTo_user_id()) {
					// 点击头像进入详情页面
					holder.headImageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							// Intent intent = new Intent(getActivity(),
							// PersonDetailActivity.class);
							// intent.putExtra(UserTable.U_ID,
							// jsonMyMessage.getUserid());
							// intent.putExtra(UserTable.U_NICKNAME,
							// jsonMyMessage.getUsername());
							// intent.putExtra(UserTable.U_SMALL_AVATAR,
							// jsonMyMessage.getSmall_avatar());
							// startActivity(intent);
							// getActivity().overridePendingTransition(R.anim.zoomin2,
							// R.anim.zoomout);
						}
					});
				}
			}

			// 设置姓名
			holder.nameTextView.setText(jsonMyMessage.getNickname());

			// 设置日期
			holder.timeTextView.setText(DateTimeTools.getMonAndDay(jsonMyMessage.getCreate_time()));

			// 设置的内容
			if (!TextUtils.isEmpty(jsonMyMessage.getImage_url())) {// 如果有图片
				imageLoader.displayImage(jsonMyMessage.getImage_url(), holder.itemImageView, ImageLoaderTool.getImageOptions());
				holder.commentTextView.setText("赞了你的图片");
				holder.itemTextView.setVisibility(View.GONE);
				holder.itemImageView.setVisibility(View.VISIBLE);
			} else {
				holder.itemTextView.setText(jsonMyMessage.getContent());
				holder.commentTextView.setText("赞了你的想法");
				holder.itemTextView.setVisibility(View.VISIBLE);
				holder.itemImageView.setVisibility(View.GONE);
			}

			return view;
		}
	}
}
