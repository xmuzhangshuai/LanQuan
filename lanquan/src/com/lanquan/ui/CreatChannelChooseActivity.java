package com.lanquan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;

/** 
 * 类描述 ：选择创建图文频道、文字频道和打卡频道的页面
 * 类名： CreatChannelActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-8 上午9:58:22  
*/
public class CreatChannelChooseActivity extends BaseActivity implements OnClickListener {
	private ImageView closeBtn;
	View bg;
	private ImageView photoBtn;// 图文频道
	private ImageView txtBtn;// 文字频道
	private ImageView punchBtn;// 打卡频道

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_channel_choose);

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		closeBtn = (ImageView) findViewById(R.id.close_btn);
		bg = findViewById(R.id.bg);
		photoBtn = (ImageView) findViewById(R.id.photo_btn);
		txtBtn = (ImageView) findViewById(R.id.txt_btn);
		punchBtn = (ImageView) findViewById(R.id.punch_btn);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		closeBtn.setOnClickListener(this);
		photoBtn.setOnClickListener(this);
		txtBtn.setOnClickListener(this);
		punchBtn.setOnClickListener(this);
		bg.getBackground().setAlpha(240);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(CreatChannelChooseActivity.this, CreatChannelActivity.class);
		switch (v.getId()) {
		case R.id.close_btn:
			finish();
			break;
		case R.id.photo_btn:
			intent.putExtra(CreatChannelActivity.CHANNAL_TYPE, "0");
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;

		case R.id.txt_btn:
			intent.putExtra(CreatChannelActivity.CHANNAL_TYPE, "1");
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;

		case R.id.punch_btn:
			intent.putExtra(CreatChannelActivity.CHANNAL_TYPE, "2");
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;

		default:
			break;
		}

	}

}
