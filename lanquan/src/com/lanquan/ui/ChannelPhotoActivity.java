package com.lanquan.ui;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.TextView;

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
import com.lanquan.jsonobject.JsonChannel;
import com.lanquan.jsonobject.JsonChannelComment;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.DateTimeTools;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.JsonChannelTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LocationTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/** 
 * 类描述 ：图文频道详情页面
 * 类名： PhotoChannelDetailActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-7 下午4:04:34  
*/
public class ChannelPhotoActivity extends BaseActivity implements OnClickListener {
	public final static String JSONCHANNEL = "jsonchannel";
	private View leftButton;// 导航栏左侧按钮
	private View concernBtn;// 关注按钮
	private View infoBtn;// 信息按钮
	private TextView titleTextView;// 频道名称
	private PullToRefreshListView channelListView;
	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;
	private UserPreference userPreference;
	private CheckBox popupWindowCheckBox;
	private EditText commentEditText;
	private Button sendBtn;
	private ImageView addImageBtn;
	private View inputBar;

	private LinkedList<JsonChannelComment> jsonChannelCommentList;
	private int pageNow = 0;// 控制页数
	private CommentAdapter mAdapter;
	private JsonChannel jsonChannel;
	private ChoicenessMenuPopupWindow choicenessMenuPopupWindow;
	private InputMethodManager inputMethodManager;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private String detailLocation;// 详细地址
	private String latitude;
	private String longtitude;
	private int position;
	private boolean showRecommentOnly = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photo_channel_detail);
		mLocationClient = LocationTool.initLocation(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		userPreference = BaseApplication.getInstance().getUserPreference();
		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
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
			inputBar.setVisibility(View.VISIBLE);
		} else {
			concernBtn.setVisibility(View.GONE);
			infoBtn.setVisibility(View.GONE);
			inputBar.setVisibility(View.GONE);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 2) {
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

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		channelListView = (PullToRefreshListView) findViewById(R.id.photo_channel_list);
		leftButton = findViewById(R.id.left_btn_bg);
		titleTextView = (TextView) findViewById(R.id.nav_text);
		concernBtn = findViewById(R.id.right_btn_bg);
		infoBtn = findViewById(R.id.right_btn_bg2);
		popupWindowCheckBox = (CheckBox) findViewById(R.id.popupwindowBtn);
		commentEditText = (EditText) findViewById(R.id.msg_et);
		sendBtn = (Button) findViewById(R.id.send_btn);
		addImageBtn = (ImageView) findViewById(R.id.add_image);
		inputBar = findViewById(R.id.inputBar);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		titleTextView.setText(jsonChannel.getTitle());
		leftButton.setOnClickListener(this);
		concernBtn.setOnClickListener(this);
		infoBtn.setOnClickListener(this);
		sendBtn.setOnClickListener(this);
		addImageBtn.setOnClickListener(this);

		choicenessMenuPopupWindow = new ChoicenessMenuPopupWindow(ChannelPhotoActivity.this, jsonChannel.getTitle());

		titleTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (popupWindowCheckBox.isChecked()) {
					popupWindowCheckBox.setChecked(false);
				} else {
					popupWindowCheckBox.setChecked(true);
				}
			}
		});
		choicenessMenuPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				popupWindowCheckBox.setChecked(false);
			}
		});
		popupWindowCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub

				if (isChecked) {
					choicenessMenuPopupWindow.showPopupWindow(titleTextView);
				} else {
					choicenessMenuPopupWindow.closePopupWindow(titleTextView);
				}
			}
		});
		commentEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					sendBtn.setEnabled(true);
				} else {
					sendBtn.setEnabled(false);
				}
			}
		});
		// 设置上拉下拉刷新事件
		channelListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(ChannelPhotoActivity.this.getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				pageNow = 0;
				getDataTask(pageNow);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(ChannelPhotoActivity.this.getApplicationContext(), System.currentTimeMillis(),
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
			startActivityForResult(new Intent(ChannelPhotoActivity.this, ChannelInfoActivity.class).putExtra(JSONCHANNEL, jsonChannel), 1);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.send_btn:
			comment(commentEditText.getText().toString());
			break;
		case R.id.add_image:
			startActivity(new Intent(ChannelPhotoActivity.this, CommentImageActivity.class).putExtra("channel_id", jsonChannel.getChannel_id()));
			break;
		default:
			break;
		}
	}

	/**
	* 隐藏软键盘
	*/
	private void hideKeyboard() {
		inputMethodManager.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
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
	 * 网络获取channel
	 * @param channelId
	 */
	private void getChannel(int channelId) {

	}

	/**
	 * 评论
	 */
	private void comment(String content) {
		RequestParams params = new RequestParams();
		params.put("message", content);
		params.put("image_url", "");
		params.put("address", detailLocation);
		params.put("latitude", latitude);
		params.put("longitude", longtitude);
		params.put("access_token", userPreference.getAccess_token());
		params.put("channel_id", jsonChannel.getChannel_id());
		final Dialog dialog = showProgressDialog("请稍后...");
		dialog.setCancelable(false);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				dialog.show();
			}

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
					ToastTool.showShort(ChannelPhotoActivity.this, jsonTool.getMessage());
					commentEditText.setText("");
					hideKeyboard();
				} else if (status.equals(JsonTool.STATUS_FAIL)) {
					LogTool.e(jsonTool.getMessage());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("创建评论错误" + statusCode + errorResponse);
			}
		};
		AsyncHttpClientTool.post("api/article/create", params, responseHandler);
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
		if (showRecommentOnly) {
			params.put("sort", "recommend");
		} else {
			params.put("sort", "create_time");
		}
		if (userPreference.getUserLogin()) {
//			params.put("user_id", userPreference.getU_id());
			params.put("access_token", userPreference.getAccess_token());
		}

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
								ToastTool.showShort(ChannelPhotoActivity.this, "没有更多了！");
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
		AsyncHttpClientTool.post(ChannelPhotoActivity.this, "api/article/articles", params, responseHandler);
	}

	/**
	 *关注 
	 */
	private void concern() {
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
					ToastTool.showShort(ChannelPhotoActivity.this, jsonTool.getMessage());
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
		AsyncHttpClientTool.post(ChannelPhotoActivity.this, "api/channel/focus", params, responseHandler);
	}

	/** 
	 * 类描述 ：频道列表适配器
	 * 类名： PhotoChannelDetailActivity.java  
	 * Copyright:   Copyright (c)2015    
	 * Company:     zhangshuai   
	 * @author:     zhangshuai    
	 * @version:    1.0    
	 * 创建时间:    2015-8-7 下午4:23:59  
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
				view = LayoutInflater.from(ChannelPhotoActivity.this).inflate(R.layout.channel_comment_list_item, null);
				holder = new ViewHolder();
				holder.headImageView = (ImageView) view.findViewById(R.id.head_image);
				holder.nameTextView = (TextView) view.findViewById(R.id.name);
				holder.timeTextView = (TextView) view.findViewById(R.id.time);
				holder.contentTextView = (TextView) view.findViewById(R.id.content);
				holder.itemImageView = (ImageView) view.findViewById(R.id.item_image);
				holder.favorBtn = (CheckBox) view.findViewById(R.id.favor_btn);
				holder.favorCountTextView = (TextView) view.findViewById(R.id.favor_count);
				holder.deleteBtn = (ImageView) view.findViewById(R.id.delete_btn);
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

			// 点亮事件
			holder.favorBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
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
									LogTool.e("sssssss" + channel.getLight());
									holder.favorCountTextView.setText("" + channel.getLight());

								} else {
									channel.setLight(channel.getLight() - 1);
									// 标记为未亮
									channel.setIslight(0);
									holder.favorCountTextView.setText("" + channel.getLight());
								}
								holder.favorCountTextView.setVisibility(View.VISIBLE);
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
					AsyncHttpClientTool.post(ChannelPhotoActivity.this, "api/article/light", params, responseHandler);
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
						Intent intent = new Intent(ChannelPhotoActivity.this, ImageShowerActivity.class);
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
		final MyAlertDialog dialog = new MyAlertDialog(ChannelPhotoActivity.this);
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
				AsyncHttpClientTool.post(ChannelPhotoActivity.this, "api/article/delete", params, responseHandler);
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

	// 对评论点亮事件

	public void light(final int article_id, final int position) {
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
		AsyncHttpClientTool.post(ChannelPhotoActivity.this, "api/article/delete", params, responseHandler);

	}

	/** 
	 * 类描述 ：	频道详情页面点击频道名称，显示的菜单，显示全部和只显示精选
	 * 类名： ChoicenessMenuPopupWindow.java  
	 * Copyright:   Copyright (c)2015    
	 * Company:     zhangshuai   
	 * @author:     zhangshuai    
	 * @version:    1.0    
	 * 创建时间:    2015-8-11 下午2:35:59  
	*/
	public class ChoicenessMenuPopupWindow extends PopupWindow {
		private View conentView;
		private TextView titleTextView;
		private RadioButton allRadio;
		private RadioButton recommentRadio;

		public ChoicenessMenuPopupWindow(final Activity context, String title) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			conentView = inflater.inflate(R.layout.popupwindow_choiceness_menu, null);
			titleTextView = (TextView) conentView.findViewById(R.id.title);
			allRadio = (RadioButton) conentView.findViewById(R.id.all);
			recommentRadio = (RadioButton) conentView.findViewById(R.id.choiceness);
			titleTextView.setText(title);
			// 设置SelectPicPopupWindow的View
			this.setContentView(conentView);
			// 设置SelectPicPopupWindow弹出窗体的宽
			this.setWidth(LayoutParams.MATCH_PARENT);
			// 设置SelectPicPopupWindow弹出窗体的高
			this.setHeight(LayoutParams.WRAP_CONTENT);
			// 设置SelectPicPopupWindow弹出窗体可点击
			this.setFocusable(true);
			this.setOutsideTouchable(true);
			// 刷新状态
			this.update();
			// 实例化一个ColorDrawable颜色为半透明
			ColorDrawable dw = new ColorDrawable(0000000000);
			// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
			this.setBackgroundDrawable(dw);
			// 设置SelectPicPopupWindow弹出窗体动画效果
			this.setAnimationStyle(R.style.AnimationPreview);
			if (showRecommentOnly) {
				recommentRadio.setChecked(true);
			} else {
				allRadio.setChecked(true);
			}
			recommentRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						showRecommentOnly = true;
						pageNow = 0;
						channelListView.setRefreshing();
					} else {
						showRecommentOnly = false;
						pageNow = 0;
						channelListView.setRefreshing();
					}
					ChoicenessMenuPopupWindow.this.dismiss();
				}
			});
		}

		/**
		 * 显示popupWindow
		 * 
		 * @param parent
		 */
		public void showPopupWindow(View parent) {
			if (!this.isShowing()) {
				// 以下拉方式显示popupwindow
				this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 0);
			} else {
				this.dismiss();
			}
		}

		/**
		 * 关闭popupWindow
		 * 
		 * @param parent
		 */
		public void closePopupWindow(View parent) {
			if (this.isShowing()) {
				this.closePopupWindow(parent);
				this.dismiss();
			} else {
				this.dismiss();
			}
		}
	}

}
