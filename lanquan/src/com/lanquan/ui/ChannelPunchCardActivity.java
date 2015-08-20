package com.lanquan.ui;

import java.util.Date;
import java.util.LinkedList;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.jsonobject.JsonChannel;
import com.lanquan.utils.DateTimeTools;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.UserPreference;

/** 
 * 类描述 ：打卡频道
 * 类名： PunchCardChannelActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-19 下午3:59:05  
*/
public class ChannelPunchCardActivity extends BaseActivity implements OnClickListener {
	public final static String JSONCHANNEL = "jsonchannel";
	private View leftButton;// 导航栏左侧按钮
	private View concernBtn;// 关注按钮
	private View infoBtn;// 信息按钮
	private TextView titleTextView;// 频道名称
	private View punchBtn;// 打卡频道
	private PullToRefreshListView channelListView;
	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;
	private UserPreference userPreference;

	private LinkedList<JsonChannel> jsonChannelList;
	private int pageNow = 0;// 控制页数
	private CommentAdapter mAdapter;
	private JsonChannel jsonChannel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_punch_card_channel);
		userPreference = BaseApplication.getInstance().getUserPreference();

		jsonChannelList = new LinkedList<JsonChannel>();
		jsonChannel = (JsonChannel) getIntent().getSerializableExtra(JSONCHANNEL);
		if (jsonChannel == null) {
			finish();
		}

		findViewById();
		initView();

		// 获取数据
		getDataTask(pageNow);

		channelListView.setMode(Mode.BOTH);
		mAdapter = new CommentAdapter();
		channelListView.setAdapter(mAdapter);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		channelListView = (PullToRefreshListView) findViewById(R.id.punch_channel_list);
		leftButton = findViewById(R.id.left_btn_bg);
		titleTextView = (TextView) findViewById(R.id.nav_text);
		concernBtn = findViewById(R.id.right_btn_bg);
		infoBtn = findViewById(R.id.right_btn_bg2);
		punchBtn = findViewById(R.id.inputBar);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		titleTextView.setText(jsonChannel.getC_title());
		leftButton.setOnClickListener(this);
		concernBtn.setOnClickListener(this);
		infoBtn.setOnClickListener(this);
		punchBtn.setOnClickListener(this);

		// 设置上拉下拉刷新事件
		channelListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(ChannelPunchCardActivity.this.getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				pageNow = 0;
				getDataTask(pageNow);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(ChannelPunchCardActivity.this.getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				if (pageNow >= 0)
					++pageNow;
				getDataTask(pageNow);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.right_btn_bg:
			concern();
			break;
		case R.id.right_btn_bg2:
			startActivity(new Intent(ChannelPunchCardActivity.this, ChannelInfoActivity.class));
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.inputBar:
			showScreenPostDialog();
			break;
		default:
			break;
		}
	}

	/**
	 * 显示打卡对话框
	 */
	void showScreenPostDialog() {
		// DialogFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("punch_dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		PunchDialogFragment newFragment = new PunchDialogFragment();
		newFragment.show(ft, "punch_dialog");
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
		channelListView.onRefreshComplete();
	}

	/**
	 *关注 
	 */
	private void concern() {
		infoBtn.setVisibility(View.VISIBLE);
		concernBtn.setVisibility(View.GONE);
	}

	/** 
	 * 类描述 ：打卡频道列表适配器
	 * 类名： ChannelPunchCardActivity.java  
	 * Copyright:   Copyright (c)2015    
	 * Company:     zhangshuai   
	 * @author:     zhangshuai    
	 * @version:    1.0    
	 * 创建时间:    2015-8-19 下午4:21:34  
	*/
	class CommentAdapter extends BaseAdapter {
		private class ViewHolder {
			public ImageView headImageView;
			public TextView nameTextView;
			public TextView timeTextView;
			public TextView contentTextView;
			public CheckBox favorBtn;
			public TextView favorCountTextView;
			public ImageView deleteBtn;
			public TextView commentCountTextView;
			public ImageView itemImageView;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = convertView;
			final JsonChannel channel = jsonChannelList.get(position);
			if (channel == null) {
				return null;
			}

			final ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(ChannelPunchCardActivity.this).inflate(R.layout.channel_punch_comment_list_item, null);
				holder = new ViewHolder();
				holder.headImageView = (ImageView) view.findViewById(R.id.head_image);
				holder.nameTextView = (TextView) view.findViewById(R.id.name);
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

			// 设置头像
			if (!TextUtils.isEmpty(channel.getC_small_avatar())) {
				imageLoader.displayImage(channel.getC_small_avatar(), holder.headImageView, ImageLoaderTool.getCircleHeadImageOptions());
				if (userPreference.getU_id() != channel.getC_userid()) {
					// 点击头像进入详情页面
					holder.headImageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							// Intent intent = new Intent(getActivity(),
							// PersonDetailActivity.class);
							// intent.putExtra(UserTable.U_ID,
							// jsonPostItem.getP_userid());
							// intent.putExtra(UserTable.U_NICKNAME,
							// jsonPostItem.getP_username());
							// intent.putExtra(UserTable.U_SMALL_AVATAR,
							// jsonPostItem.getP_small_avatar());
							// startActivity(intent);
							// getActivity().overridePendingTransition(R.anim.zoomin2,
							// R.anim.zoomout);
						}
					});
				}
			}

			// 设置内容
			holder.contentTextView.setText(channel.getC_title());

			// 设置姓名
			holder.nameTextView.setText(channel.getC_username());

			// 设置日期
			holder.timeTextView.setText(DateTimeTools.getInterval(channel.getC_time()));

			// 设置被赞次数
			holder.favorCountTextView.setText("" + channel.getC_favor_count());

			// 设置图片
			imageLoader.displayImage(channel.getC_big_photo(), holder.itemImageView, ImageLoaderTool.getImageOptions());
			holder.itemImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(ChannelPunchCardActivity.this, ImageShowerActivity.class);
					intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE, channel.getC_big_photo());
					startActivity(intent);
					overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
				}
			});

			return view;
		}
	}

	/** 
	 * 类描述 ：打卡的对话框
	 * 类名： ChannelPunchCardActivity.java  
	 * Copyright:   Copyright (c)2015    
	 * Company:     zhangshuai   
	 * @author:     zhangshuai    
	 * @version:    1.0    
	 * 创建时间:    2015-8-20 下午2:52:57  
	*/
	class PunchDialogFragment extends DialogFragment implements OnClickListener {

		private View rootView;
		private TextView confirmBtn;// 确定
		private View uploadHeadImageBtn;
		private ImageView headImage;// 头像
		private ImageView cameraImage;
		private EditText contenteEditText;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			rootView = inflater.inflate(R.layout.fragment_dialog_punch, container, false);
			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
			getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

			findViewById();
			initView();
			return rootView;
		}

		private void findViewById() {
			confirmBtn = (TextView) rootView.findViewById(R.id.confirm);
			uploadHeadImageBtn = (View) rootView.findViewById(R.id.gethead_btn);
			headImage = (ImageView) rootView.findViewById(R.id.headimage);
			cameraImage = (ImageView) rootView.findViewById(R.id.camera_image);
			contenteEditText = (EditText) rootView.findViewById(R.id.content);
		}

		private void initView() {
			confirmBtn.setOnClickListener(this);
			headImage.setVisibility(View.GONE);
			cameraImage.setVisibility(View.VISIBLE);
			uploadHeadImageBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					showPicturePicker(getActivity());
				}
			});
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.confirm:
				// getChooseState();
				// FragmentPagerAdapter f = (FragmentPagerAdapter)
				// mViewPager.getAdapter();
				// MainExplorePostFragment mainExplorePostFragment =
				// (MainExplorePostFragment) f.instantiateItem(mViewPager, 0);
				// mainExplorePostFragment.screenToRefresh(gender,
				// love_stateString);
				// ScreenDialogFragment.this.dismiss();
				break;
			default:
				break;
			}
		}
	}

}
