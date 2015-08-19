package com.lanquan.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.utils.UserPreference;

/** 
 * 类描述 ：创建频道
 * 类名： CreatChannelActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-8 下午3:37:12  
*/
public class CreatChannelActivity extends BaseActivity implements OnClickListener {
	public final static String CHANNAL_TYPE = "type";

	private View leftButton;// 导航栏左侧按钮
	private View rightBtn;// 右侧按钮
	private TextView rightBtnText;// 右侧按钮文字
	private TextView navTextView;// 导航名称
	private EditText titleEditText;// 频道名称
	private EditText detailEditText;// 频道介绍
	private String type = "";// 1为图文频道，2为文字频道，3为打卡频道
	private UserPreference userPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_creat_channel);
		userPreference = BaseApplication.getInstance().getUserPreference();
		type = getIntent().getStringExtra(CHANNAL_TYPE);
		if (type == null || type.isEmpty()) {
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
		navTextView = (TextView) findViewById(R.id.nav_text);
		rightBtn = findViewById(R.id.right_btn_bg);
		rightBtnText = (TextView) findViewById(R.id.right_btn_text);
		titleEditText = (EditText) findViewById(R.id.channel_title);
		detailEditText = (EditText) findViewById(R.id.channel_info);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		rightBtnText.setText("创建");
		leftButton.setOnClickListener(this);
		rightBtn.setOnClickListener(this);
		if (type.equals("1")) {
			navTextView.setText("创建图文频道");
		} else if (type.equals("2")) {
			navTextView.setText("创建文字频道");
		} else if (type.equals("3")) {
			navTextView.setText("创建打卡频道");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			break;

		default:
			break;
		}
	}

}
