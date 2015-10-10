package com.lanquan.ui;

import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseV4Fragment;
import com.lanquan.jsonobject.JsonChannel;
import com.lanquan.jsonobject.JsonMyChannel;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * 类描述 ：按频道 类名： MainPersonalFragment.java Copyright: Copyright (c)2015
 * Company: zhangshuai
 * 
 * @author: zhangshuai
 * @version: 1.0 创建时间: 2015-8-21 下午2:02:18
 */
public class MainPersonalChannelByChannel extends BaseV4Fragment {
	private View rootView;// 根View

	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;
	private TableLayout tableLayout;
	private UserPreference userPreference;

	public MainPersonalChannelByChannel() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userPreference = BaseApplication.getInstance().getUserPreference();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_my_channel_by_channel, container, false);

		findViewById();// 初始化views
		initView();

		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		tableLayout = (TableLayout) rootView.findViewById(R.id.tablelayout_channel);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}

	public void setList(final List<JsonMyChannel> jsonMyChannelList) {
		if (jsonMyChannelList != null) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					tableLayout.removeAllViews();
					if (jsonMyChannelList != null) {
						for (int i = 0; i < jsonMyChannelList.size(); i++) {
							tableLayout.addView(getView(i, null, jsonMyChannelList.get(i)), i);
						}
					}
				}
			}).run();

		} else {
			LogTool.e("Time: setlist,为空");
		}
	}

	public View getView(int position, View convertView, final JsonMyChannel jsonChannel) {
		// TODO Auto-generated method stub
		View view = convertView;
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

	/**
	 * 进入详情页面
	 */
	private void goToDetail(int channelId, final int type) {

		RequestParams params = new RequestParams();
		params.put("channel_id", channelId);
		if (userPreference.getUserLogin()) {
			params.put("access_token", userPreference.getAccess_token());
		}
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
						JsonChannel jsonChannel = JsonChannel.getJsonChannelByJsonStringWihtFocus(data);
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
}
