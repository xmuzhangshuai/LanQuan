package com.lanquan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.jsonobject.JsonChannel;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_channel_info);
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
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		navText.setText("返回");
		leftButton.setOnClickListener(this);
		channelShareView.setOnClickListener(this);
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
			startActivity(new Intent(ChannelInfoActivity.this, ShareQrCodeActivity.class));
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}
	}

}
