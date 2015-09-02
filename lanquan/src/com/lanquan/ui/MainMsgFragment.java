package com.lanquan.ui;

import java.util.Date;
import java.util.LinkedList;

import org.apache.http.Header;

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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseV4Fragment;
import com.lanquan.jsonobject.JsonMyMessage;
import com.lanquan.table.UserTable;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.DateTimeTools;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/** 
 * 类描述 ：主页面--动态页面
 * 类名： MainMsgFragment.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-6 上午10:59:38  
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
		// final int page = p;
		RequestParams params = new RequestParams();
		params.put("access_token", userPreference.getAccess_token());
		// params.put("page", pageNow);
		// params.put(UserTable.U_ID, userPreference.getU_id());
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				// postListView.setRefreshing();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				LogTool.i("--->" + statusCode + response);
				// if (statusCode == 200) {
				// List<JsonPostItem> temp =
				// FastJsonTool.getObjectList(response, JsonPostItem.class);
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
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("--->" + statusCode + errorResponse);
			}

//			@Override
//			public void onFinish() {
//				// TODO Auto-generated method stub
//				super.onFinish();
//				postListView.onRefreshComplete();
//			}

		};
//		AsyncHttpClientTool.post(getActivity(), "api/user/message", params, responseHandler);
		
		
		JsonMyMessage jsonMyMessage1 = new JsonMyMessage(1, "drawable://" + R.drawable.channel1, "如何提高篮球技术", 1, "帅哥", "男", "drawable://"
				+ R.drawable.headimage3, new Date());
		JsonMyMessage jsonMyMessage2 = new JsonMyMessage(1, "drawable://" + R.drawable.channel2, "什么装备值得买", 1, "坤坤", "男",
				"drawable://" + R.drawable.headimage1, new Date());
		JsonMyMessage jsonMyMessage3 = new JsonMyMessage(1, "", "库里值不值MVP", 1, "蔑视", "男", "drawable://" + R.drawable.headimage7, new Date());
		JsonMyMessage jsonMyMessage4 = new JsonMyMessage(1, "drawable://" + R.drawable.channel4, "詹姆斯到底有多强", 1, "嘟嘟", "男", "drawable://"
				+ R.drawable.headimage6, new Date());
		JsonMyMessage jsonMyMessage5 = new JsonMyMessage(1, "", "如何提高篮球技术", 1, "冰冰", "男", "drawable://" + R.drawable.headimage4, new Date());
		messageList.add(jsonMyMessage1);
		messageList.add(jsonMyMessage2);
		messageList.add(jsonMyMessage3);
		messageList.add(jsonMyMessage4);
		messageList.add(jsonMyMessage5);
		messageListView.onRefreshComplete();
	}

	/**
	 * 进入详情页面
	 */
	private void goToDetail(final int type, int pa_id, int pa_user_id) {
		// RequestParams params = new RequestParams();
		// final ProgressDialog dialog = new ProgressDialog(getActivity());
		// dialog.setMessage("正在加载...");
		// dialog.setCancelable(false);
		// if (type == 0) {
		// params.put(PostTable.P_POSTID, pa_id);
		// params.put(PostTable.P_USERID, pa_user_id);
		// } else if (type == 1) {
		// params.put(ActivityTable.A_ACTID, pa_id);
		// params.put(ActivityTable.A_USERID, pa_user_id);
		// } else {
		// return;
		// }
		//
		// TextHttpResponseHandler responseHandler = new
		// TextHttpResponseHandler("utf-8") {
		// @Override
		// public void onStart() {
		// // TODO Auto-generated method stub
		// super.onStart();
		// dialog.show();
		// }
		//
		// @Override
		// public void onSuccess(int statusCode, Header[] headers, String
		// response) {
		// // TODO Auto-generated method stub
		// if (statusCode == 200) {
		// if (type == 0) {
		// JsonPostItem jsonPostItem = FastJsonTool.getObject(response,
		// JsonPostItem.class);
		// if (jsonPostItem != null) {
		// startActivity(new Intent(getActivity(),
		// PostDetailActivity.class).putExtra(
		// PostDetailActivity.POST_ITEM, jsonPostItem));
		// getActivity().overridePendingTransition(R.anim.push_left_in,
		// R.anim.push_left_out);
		// } else {
		// LogTool.e("返回帖子数据出错" + response);
		// }
		// } else if (type == 1) {
		// JsonActItem jsonActItem = FastJsonTool.getObject(response,
		// JsonActItem.class);
		// if (jsonActItem != null) {
		// startActivity(new Intent(getActivity(),
		// ActDetailActivity.class).putExtra(
		// ActDetailActivity.ACT_ITEM, jsonActItem));
		// getActivity().overridePendingTransition(R.anim.push_left_in,
		// R.anim.push_left_out);
		// } else {
		// LogTool.e("返回帖子数据出错" + response);
		// }
		// }
		// }
		// }
		//
		// @Override
		// public void onFailure(int statusCode, Header[] headers, String
		// errorResponse, Throwable e) {
		// // TODO Auto-generated method stub
		// LogTool.e("获取学校帖子列表失败" + errorResponse);
		// }
		//
		// @Override
		// public void onFinish() {
		// // TODO Auto-generated method stub
		// dialog.dismiss();
		// super.onFinish();
		// }
		//
		// };
		// AsyncHttpClientTool.post(getActivity(), "post/getSchoolPosts",
		// params, responseHandler);
	}

	/**
	 * 
	 * 类名称：MessageAdapter
	 * 类描述：适配器
	 * 创建人： 张帅
	 * 创建时间：2014年9月14日 下午3:41:15
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
					// TODO Auto-generated method stub
					// goToDetail(jsonMyMessage.getType(),
					// jsonMyMessage.getPa_id(), jsonMyMessage.getUserid());
				}
			});

			// 设置头像
			if (!TextUtils.isEmpty(jsonMyMessage.getSmall_avatar())) {

				imageLoader.displayImage(jsonMyMessage.getSmall_avatar(), holder.headImageView, ImageLoaderTool.getHeadImageOptions(10));

				if (userPreference.getU_id() != jsonMyMessage.getUserid()) {
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
			holder.nameTextView.setText(jsonMyMessage.getUsername());

			// 设置日期
			holder.timeTextView.setText(DateTimeTools.getMonAndDay(jsonMyMessage.getFavortime()));

			// 设置的内容
			if (jsonMyMessage.getImage() != null && !jsonMyMessage.getImage().isEmpty()) {// 如果有图片
				imageLoader.displayImage(jsonMyMessage.getImage(), holder.itemImageView, ImageLoaderTool.getImageOptions());
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
