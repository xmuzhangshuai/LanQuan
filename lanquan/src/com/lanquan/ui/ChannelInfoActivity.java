package com.lanquan.ui;

import org.apache.http.Header;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.jsonobject.JsonChannel;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/** 
 * 类描述 ：频道信息
 * 类名： ChannelInfoActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-10 上午11:31:21  
*/
public class ChannelInfoActivity extends BaseActivity implements OnClickListener {
	private View leftButton;// 导航栏左侧按钮
	private TextView navText;
	private View channelShareView;
	private JsonChannel jsonChannel;
	private TextView nicknameTextView;
	private TextView channelNameTextView;
	private TextView channelDescription;
	private Button cancelConcernBtn;

	private UserPreference userPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_channel_info);

		userPreference = BaseApplication.getInstance().getUserPreference();
		jsonChannel = (JsonChannel) getIntent().getSerializableExtra(ChannelPhotoActivity.JSONCHANNEL);
		if (jsonChannel == null) {
			finish();
			return;
		}

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		leftButton = findViewById(R.id.left_btn_bg);
		navText = (TextView) findViewById(R.id.nav_text);
		channelShareView = findViewById(R.id.channel_share);
		nicknameTextView = (TextView) findViewById(R.id.nickname);
		channelNameTextView = (TextView) findViewById(R.id.channel_name_txt);
		channelDescription = (TextView) findViewById(R.id.channel_explian_txt);
		cancelConcernBtn = (Button) findViewById(R.id.cancel_concern_btn);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		navText.setText("返回");
		leftButton.setOnClickListener(this);
		channelShareView.setOnClickListener(this);
		cancelConcernBtn.setOnClickListener(this);
		nicknameTextView.setText(jsonChannel.getNickame());
		channelNameTextView.setText(jsonChannel.getTitle());
		channelDescription.setText(jsonChannel.getDescription());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			break;
		case R.id.channel_share:
			startActivity(new Intent(ChannelInfoActivity.this, ShareQrCodeActivity.class).putExtra("channelName", channelNameTextView.getText())
					.putExtra("channelInfo", channelDescription.getText()).putExtra("channelid", jsonChannel.getChannel_id()));

			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.cancel_concern_btn:
			concern();
			break;
		default:
			break;
		}
	}

	/**
	 *关注/取消关注 
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
					if (jsonChannel.getIs_focus() == 1) {
						cancelConcernBtn.setText("关注");
						jsonChannel.setIs_focus(0);
					} else if (jsonChannel.getIs_focus() == 0) {
						cancelConcernBtn.setText("取消关注");
						jsonChannel.setIs_focus(1);
					}
					Intent mIntent = new Intent();
					mIntent.putExtra("is_focus", jsonChannel.getIs_focus());
					setResult(2, mIntent);
					ToastTool.showShort(ChannelInfoActivity.this, jsonTool.getMessage());
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
		AsyncHttpClientTool.post(ChannelInfoActivity.this, "api/channel/focus", params, responseHandler);
	}
}
