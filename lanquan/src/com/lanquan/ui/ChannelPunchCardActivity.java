package com.lanquan.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.config.Constants.Config;
import com.lanquan.customwidget.MyAlertDialog;
import com.lanquan.customwidget.MyMenuDialog;
import com.lanquan.jsonobject.JsonChannel;
import com.lanquan.jsonobject.JsonChannelComment;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.DateTimeTools;
import com.lanquan.utils.FileSizeUtil;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.ImageTools;
import com.lanquan.utils.JsonChannelTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LocationTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
	private static final int TAKE_PICTURE = 300;
	private static final int CHOOSE_PICTURE = 301;
	private String photoUri;// 图片地址
	private View leftButton;// 导航栏左侧按钮
	private View concernBtn;// 关注按钮
	private View infoBtn;// 信息按钮
	private TextView titleTextView;// 频道名称
	private View punchBtn;// 打卡频道

	private PullToRefreshListView channelListView;
	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;
	private UserPreference userPreference;

	private LinkedList<JsonChannelComment> jsonChannelCommentList;
	private int pageNow = 0;// 控制页数
	private CommentAdapter mAdapter;
	private JsonChannel jsonChannel;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private String detailLocation;// 详细地址
	private String latitude;
	private String longtitude;
	private int position;
	Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_punch_card_channel);
		userPreference = BaseApplication.getInstance().getUserPreference();
		mLocationClient = LocationTool.initLocation(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		jsonChannelCommentList = new LinkedList<JsonChannelComment>();
		jsonChannel = (JsonChannel) getIntent().getSerializableExtra(JSONCHANNEL);
		position = getIntent().getIntExtra("position", -1);

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
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		channelListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling));
		if (userPreference.getUserLogin()) {
			if (jsonChannel.getIs_focus() == 1) {
				concernBtn.setVisibility(View.GONE);
				infoBtn.setVisibility(View.VISIBLE);
			} else {
				concernBtn.setVisibility(View.VISIBLE);
				infoBtn.setVisibility(View.GONE);
			}
			punchBtn.setVisibility(View.VISIBLE);
		} else {
			infoBtn.setVisibility(View.GONE);
			punchBtn.setVisibility(View.GONE);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		LogTool.e("requestCode" + requestCode + "resultCode" + resultCode + "data" + data);
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK && resultCode != 2) {
			photoUri = null;
			return;
		}

		if (requestCode == TAKE_PICTURE) {// 拍照
			PunchDialogFragment prev = (PunchDialogFragment) getFragmentManager().findFragmentByTag("punch_dialog");
			if (prev != null) {
				prev.setImage();
			}
		} else if (requestCode == CHOOSE_PICTURE) {// 相册
			try {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = ChannelPunchCardActivity.this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				photoUri = cursor.getString(columnIndex);
				LogTool.i(cursor.getString(columnIndex));
				cursor.close();
				PunchDialogFragment prev = (PunchDialogFragment) getFragmentManager().findFragmentByTag("punch_dialog");
				if (prev != null) {
					prev.setImage();
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else if (requestCode == 1 && resultCode == 2) {
			if (data != null) {
				int is_focus = data.getIntExtra("is_focus", 1);
				jsonChannel.setIs_focus(is_focus);
				if (is_focus == 1) {
					concernBtn.setVisibility(View.GONE);
					infoBtn.setVisibility(View.VISIBLE);
				} else if (is_focus == 0) {
					concernBtn.setVisibility(View.VISIBLE);
					infoBtn.setVisibility(View.GONE);
				}
			}
		}
	}

	// 跳转时执行setresult
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent mIntent = new Intent();
		mIntent.putExtra("position", position);
		mIntent.putExtra("is_focus", jsonChannel.getIs_focus());
		setResult(10, mIntent);
		super.onBackPressed();
	}

	public void refreshData() {
		pageNow = 0;
		if (channelListView != null) {
			channelListView.setRefreshing();
		}
		//		if (jsonChannelList.size() > 0) {
		//			postListView.setVisibility(View.VISIBLE);
		//			emptyView.setVisibility(View.GONE);
		//		} else {
		//			postListView.setVisibility(View.GONE);
		//			emptyView.setVisibility(View.VISIBLE);
		//		}
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
		concernBtn = findViewById(R.id.right_btn_bg);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		titleTextView.setText(jsonChannel.getTitle());
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
				if (pageNow < 0)
					pageNow = 0;
				getDataTask(pageNow);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			Intent mIntent = new Intent();
			mIntent.putExtra("position", position);
			mIntent.putExtra("is_focus", jsonChannel.getIs_focus());
			setResult(10, mIntent);
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.right_btn_bg:
			concern();
			break;
		case R.id.right_btn_bg2:
			startActivityForResult(new Intent(ChannelPunchCardActivity.this, ChannelInfoActivity.class).putExtra(JSONCHANNEL, jsonChannel), 1);
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
	 * 位置监听类
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {
				latitude = "" + location.getLatitude();
				longtitude = "" + location.getLongitude();
				detailLocation = location.getAddrStr();// 详细地址
			}
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
		final int page = p;
		RequestParams params = new RequestParams();
		params.put("channel_id", jsonChannel.getChannel_id());
		params.put("pageIndex", page);
		params.put("pageSize", Config.PAGE_NUM);
		params.put("sort", "create_time");
		// params.put("user_id", userPreference.getU_id());
		params.put("access_token", userPreference.getAccess_token());

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				JsonTool jsonTool = new JsonTool(response);
				String status = jsonTool.getStatus();
				if (status.equals(JsonTool.STATUS_SUCCESS)) {
					JSONObject jsonObject = jsonTool.getJsonObject();
					try {
						JsonChannelTools jsonChannelTools = new JsonChannelTools(jsonObject.getString("data"));
						List<JsonChannelComment> temp = jsonChannelTools.getJsonChannelCommentList();
						// 如果是首次获取数据
						if (page == 0) {
							if (temp.size() < Config.PAGE_NUM) {
								pageNow = -1;
							}
							jsonChannelCommentList = new LinkedList<JsonChannelComment>();
							jsonChannelCommentList.addAll(temp);
							mAdapter.notifyDataSetChanged();
						}
						// 如果是获取更多
						else if (page > 0) {
							if (temp.size() < Config.PAGE_NUM) {
								pageNow = -1;
								ToastTool.showShort(ChannelPunchCardActivity.this, "没有更多了！");
							}
							jsonChannelCommentList.addAll(temp);
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
				LogTool.e("获取最新频道列表失败" + errorResponse);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				channelListView.onRefreshComplete();
			}

		};
		AsyncHttpClientTool.post(ChannelPunchCardActivity.this, "api/article/articles", params, responseHandler);
	}

	/**
	 *关注 
	 */
	private void concern() {
		if (userPreference.getUserLogin()) {
			RequestParams params = new RequestParams();
			params.put("channel_id", jsonChannel.getChannel_id());
			params.put("access_token", userPreference.getAccess_token());
			final Dialog dialog = showProgressDialog("请稍后...");
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
					JsonTool jsonTool = new JsonTool(response);
					String status = jsonTool.getStatus();
					if (status.equals(JsonTool.STATUS_SUCCESS)) {
						ToastTool.showShort(ChannelPunchCardActivity.this, jsonTool.getMessage());
						jsonChannel.setIs_focus(1);
						infoBtn.setVisibility(View.VISIBLE);
						concernBtn.setVisibility(View.GONE);
					} else {
						LogTool.e(jsonTool.getMessage());
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					LogTool.e("关注频道" + errorResponse);
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					super.onFinish();
					dialog.dismiss();
				}
			};
			AsyncHttpClientTool.post(ChannelPunchCardActivity.this, "api/channel/focus", params, responseHandler);
		} else {
			vertifyToLogin();
		}

	}

	/**
	* 弹出确认登陆的对话框
	*/
	private void vertifyToLogin() {
		final MyAlertDialog dialog = new MyAlertDialog(ChannelPunchCardActivity.this);
		dialog.setTitle("提示");
		dialog.setMessage("是否去登录？");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent intent = new Intent(ChannelPunchCardActivity.this, LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		};
		dialog.setPositiveButton("确定", comfirm);
		dialog.setNegativeButton("取消", cancle);
		dialog.show();
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
			public ImageView itemImageView;
			public TextView locationTextView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return jsonChannelCommentList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return jsonChannelCommentList.get(position);
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
			final JsonChannelComment channel = jsonChannelCommentList.get(position);
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
				holder.locationTextView = (TextView) view.findViewById(R.id.location);
				view.setTag(holder); // 给View添加一个格外的数据
			} else {
				holder = (ViewHolder) view.getTag(); // 把数据取出来
			}

			if (channel.getUser_id() == userPreference.getU_id()) {
				holder.deleteBtn.setVisibility(View.VISIBLE);
				holder.deleteBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						deleteComment(channel.getArticle_id(), position);
					}
				});
			} else {
				holder.deleteBtn.setVisibility(View.GONE);
			}

			if (channel.getIslight() == 0) {
				holder.favorBtn.setChecked(false);
				holder.favorCountTextView.setVisibility(View.GONE);
			} else if (channel.getIslight() == 1) {
				holder.favorBtn.setChecked(true);
				holder.favorCountTextView.setVisibility(View.VISIBLE);
			}

			// 设置位置
			holder.locationTextView.setText(channel.getAddress());

			// 点亮事件
			holder.favorBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (userPreference.getUserLogin()) {
						RequestParams params = new RequestParams();
						params.put("article_id", channel.getArticle_id());
						params.put("access_token", userPreference.getAccess_token());
						TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

							@Override
							public void onSuccess(int statusCode, Header[] headers, String response) {
								// TODO Auto-generated method stub
								LogTool.i(statusCode + "===" + response);
								JsonTool jsonTool = new JsonTool(response);

								String status = jsonTool.getStatus();
								String message = jsonTool.getMessage();
								if (status.equals("success")) {

									if (holder.favorBtn.isChecked()) {
										channel.setLight(channel.getLight() + 1);
										channel.setIslight(1);
										holder.favorCountTextView.setText("" + channel.getLight());
										holder.favorCountTextView.setVisibility(View.VISIBLE);
									} else {
										channel.setLight(channel.getLight() - 1);
										// 标记为未亮
										channel.setIslight(0);
										holder.favorCountTextView.setText("" + channel.getLight());
										holder.favorCountTextView.setVisibility(View.GONE);
									}

									LogTool.i(message);
								} else {
									LogTool.e(message);
								}
							}

							@Override
							public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
								// TODO Auto-generated method stub
								LogTool.e("点亮评论服务器错误，代码" + statusCode + errorResponse);
							}

						};
						AsyncHttpClientTool.post(ChannelPunchCardActivity.this, "api/article/light", params, responseHandler);
					} else {
						vertifyToLogin();
						holder.favorBtn.setChecked(false);
					}
				}
			});

			// 设置头像
			if (!TextUtils.isEmpty(channel.getAvatar())) {
				imageLoader.displayImage(channel.getAvatar(), holder.headImageView, ImageLoaderTool.getCircleHeadImageOptions());
				if (userPreference.getU_id() != channel.getUser_id()) {
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
			holder.contentTextView.setText(channel.getMessage());

			// 设置姓名
			holder.nameTextView.setText(channel.getNickame());

			// 设置日期
			holder.timeTextView.setText(DateTimeTools.getInterval(channel.getCreate_time()));

			// 设置被赞次数
			holder.favorCountTextView.setText("" + channel.getLight());

			// 设置图片
			if (channel.getImage_url() != null && !channel.getImage_url().isEmpty()) {
				imageLoader.displayImage(channel.getImage_url(), holder.itemImageView, ImageLoaderTool.getImageOptions());
				holder.itemImageView.setVisibility(View.VISIBLE);
				holder.itemImageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(ChannelPunchCardActivity.this, ImageShowerActivity.class);
						intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE, channel.getImage_url());
						startActivity(intent);
						overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
					}
				});
			} else {
				holder.itemImageView.setVisibility(View.GONE);
			}

			return view;
		}
	}

	// 传递评论Id,用于删除评论（如果是自己发表的）
	public void deleteComment(final int article_id, final int position) {
		final MyAlertDialog dialog = new MyAlertDialog(ChannelPunchCardActivity.this);
		dialog.setTitle("删除");
		dialog.setMessage("确定要删除评论吗？");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				RequestParams params = new RequestParams();
				params.put("article_id", article_id);
				params.put("access_token", userPreference.getAccess_token());
				TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

					@Override
					public void onSuccess(int statusCode, Header[] headers, String response) {
						// TODO Auto-generated method stub
						LogTool.i(statusCode + "===" + response);
						JsonTool jsonTool = new JsonTool(response);

						String status = jsonTool.getStatus();
						String message = jsonTool.getMessage();
						if (status.equals("success")) {
							jsonChannelCommentList.remove(position);
							mAdapter.notifyDataSetChanged();
							LogTool.i(message);
						} else {
							LogTool.e(message);
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
						// TODO Auto-generated method stub
						LogTool.e("删除评论服务器错误，代码" + statusCode + errorResponse);
					}

				};
				AsyncHttpClientTool.post(ChannelPunchCardActivity.this, "api/article/delete", params, responseHandler);
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		};
		dialog.setPositiveButton("确定", comfirm);
		dialog.setNegativeButton("取消", cancle);
		dialog.show();
	}

	/**
	 * 从相册选择图片
	 */
	private void choosePhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, CHOOSE_PICTURE);
	}

	/**
	 * 拍照
	 */
	private void takePhoto() {
		try {
			File uploadFileDir = new File(Environment.getExternalStorageDirectory(), "/lanquan/image");
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (!uploadFileDir.exists()) {
				uploadFileDir.mkdirs();
			}
			String fileName;

			// 删除上一次的临时文件
			SharedPreferences sharedPreferences = getSharedPreferences("temp", Context.MODE_WORLD_WRITEABLE);
			ImageTools.deletePhotoAtPathAndName(Environment.getExternalStorageDirectory().getAbsolutePath(), sharedPreferences.getString("tempName", ""));

			// 保存本次截图临时文件名字
			fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
			Editor editor = sharedPreferences.edit();
			editor.putString("tempName", fileName);
			editor.commit();

			File picFile = new File(uploadFileDir, fileName);

			if (!picFile.exists()) {
				picFile.createNewFile();
			}

			photoUri = picFile.getAbsolutePath();
			Uri takePhotoUri = Uri.fromFile(picFile);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoUri);
			startActivityForResult(cameraIntent, TAKE_PICTURE);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	* 显示对话框，从拍照和相册选择图片来源
	* 
	* @param context
	* @param isCrop
	*/
	private void showPicturePicker(Context context) {

		final MyMenuDialog myMenuDialog = new MyMenuDialog(ChannelPunchCardActivity.this);
		myMenuDialog.setTitle("图片来源");
		ArrayList<String> list = new ArrayList<String>();
		list.add("拍照");
		list.add("相册");
		myMenuDialog.setMenuList(list);
		OnItemClickListener listener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:// 如果是拍照
					myMenuDialog.dismiss();
					takePhoto();
					break;
				case 1:// 从相册选取
					myMenuDialog.dismiss();
					choosePhoto();
					break;

				default:
					break;
				}
			}
		};
		myMenuDialog.setListItemClickListener(listener);

		myMenuDialog.show();
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
					showPicturePicker(getActivity());
				}
			});
		}

		public void setImage() {
			if (photoUri != null && photoUri.length() > 0) {
				headImage.setVisibility(View.VISIBLE);
				cameraImage.setVisibility(View.GONE);
				imageLoader.displayImage("file://" + photoUri, headImage, ImageLoaderTool.getCircleHeadImageOptions());
			} else {
				LogTool.e("图片地址为空");
			}
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.confirm:
				String contentString = contenteEditText.getText().toString();
				if (contentString == null || contentString.isEmpty()) {
					contentString = "嘿！我在这儿！";
				}
				if (photoUri != null && photoUri.length() > 0) {
					uploadImage(photoUri, contentString);
				} else {
					punchCard(contentString, "");
				}
				PunchDialogFragment.this.dismiss();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 打卡
	 */
	private void punchCard(String content, String imageUrl) {
		RequestParams params = new RequestParams();
		params.put("message", content);
		params.put("image_url", imageUrl);
		params.put("address", detailLocation);
		params.put("latitude", latitude);
		params.put("longitude", longtitude);
		params.put("access_token", userPreference.getAccess_token());
		params.put("channel_id", jsonChannel.getChannel_id());
		if (dialog == null) {
			dialog = showProgressDialog("请稍后...");
			dialog.setCancelable(false);
			dialog.show();
		}

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				dialog.dismiss();
				super.onFinish();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				JsonTool jsonTool = new JsonTool(response);
				String status = jsonTool.getStatus();
				if (status.equals(JsonTool.STATUS_SUCCESS)) {
					userPreference.setArticle_count(userPreference.getArticle_count() + 1);
					ToastTool.showShort(ChannelPunchCardActivity.this, jsonTool.getMessage());
					refreshData();
				} else if (status.equals(JsonTool.STATUS_FAIL)) {
					LogTool.e(jsonTool.getMessage());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("打卡错误" + statusCode + errorResponse);
			}
		};
		AsyncHttpClientTool.post("api/article/create", params, responseHandler);
	}

	/**
	 * 上传图片
	 * @param filePath
	 */
	public void uploadImage(final String imageUrl, final String content) {
		LogTool.e("路径：" + imageUrl);
		File f = new File(imageUrl);
		if (!f.exists() || FileSizeUtil.getFileOrFilesSize(imageUrl, FileSizeUtil.SIZETYPE_KB) < 1) {
			return;
		}
		String tempPath = Environment.getExternalStorageDirectory() + "/lanquan/image";
		String photoName = "temp" + ".jpg";
		File file = ImageTools.compressBySizeAndQuality(tempPath, photoName, imageUrl, 400);
		dialog = showProgressDialog("正在发布...");
		dialog.setCancelable(false);

		if (file.exists()) {
			RequestParams params = new RequestParams();
			try {
				params.put("userfile", file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					dialog.show();
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					LogTool.i("" + statusCode + response);
					JsonTool jsonTool = new JsonTool(response);
					String status = jsonTool.getStatus();
					if (status.equals(JsonTool.STATUS_SUCCESS)) {
						JSONObject jsonObject = jsonTool.getJsonObject();
						if (jsonObject != null) {
							String url;
							try {
								url = jsonObject.getString("url");
								LogTool.i("url" + url);
								// 图片上传成功后调用评论
								punchCard(content, url);
								photoUri = "";
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else if (status.equals(JsonTool.STATUS_FAIL)) {
						LogTool.e("上传fail");
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					LogTool.e("图像上传失败！" + errorResponse);
				}
			};
			AsyncHttpClientTool.post("api/file/upload", params, responseHandler);
		} else {
			LogTool.e("本地文件为空");
		}
	}

}
