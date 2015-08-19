package com.lanquan.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;

/** 
 * 类描述 ：在设置页面点击告诉小伙伴跳出的平台选项
 * 类名： ShareActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-15 上午10:27:14  
*/
public class ShareMenuActivity extends BaseActivity implements OnClickListener {
	private View sina;
	private View weixinFriends;
	private View weixinQuan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_menu);

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		sina = findViewById(R.id.menu1);
		weixinFriends = findViewById(R.id.menu2);
		weixinQuan = findViewById(R.id.menu3);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		sina.setOnClickListener(this);
		weixinFriends.setOnClickListener(this);
		weixinQuan.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.menu1:

			break;
		case R.id.menu2:

			break;
		case R.id.menu3:

			break;

		default:
			break;
		}
	}

	public void cancel(View view) {
		finish();
	}

}
