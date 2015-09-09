package com.lanquan.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.UserPreference;
import com.lanquan.zxingqrcode.EncodingHandler;

/** 
 * 类描述 ：分享频道二维码
 * 类名： ShareQrCodeActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-18 下午10:08:26  
*/
public class ShareQrCodeActivity extends BaseActivity implements android.view.View.OnClickListener {

	private ImageView qrImageView;
	private UserPreference userPreference;
	Bitmap qrCodeBitmap = null;
	private View left_btn_bg;// 导航条左边按钮
	private TextView topNavText;// 导航条文字
	private ImageView channelIcon;
	private TextView channelTitleTextView;
	private TextView channelDetailTextView;
	private String imagePath;
	private String channelTitle;
	private String channelDetail;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_share_qrcode);

		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		qrImageView = (ImageView) findViewById(R.id.qrcode);
		left_btn_bg = findViewById(R.id.left_btn_bg);
		topNavText = (TextView) findViewById(R.id.nav_text);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavText.setText("分享二维码");
		//获取频道名称以及频道简介
		channelTitle = getIntent().getStringExtra("channelName");
		channelDetail = getIntent().getStringExtra("channelInfo");
		Bitmap top = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_top);
		Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.lanquan_checked);
		Bitmap content = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_content);
		Bitmap blue_content = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_content_blue);
		Bitmap middle = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_middle);
		
		Bitmap bottom = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_card_bleow);
		//			qrCodeBitmap = EncodingHandler.createQRCode("帅哥就是帅！", 800);
					qrCodeBitmap = EncodingHandler.createChannelCode(top,icon,content,blue_content, middle,bottom,channelTitle,channelDetail,"帅哥就是帅");
					qrImageView.setImageBitmap(qrCodeBitmap);
		
//		if (!TextUtils.isEmpty(userPreference.getU_tel())) {
//
//			try {
//				qrCodeBitmap = EncodingHandler.createQRCode(userPreference.getU_tel(), 800);
//				qrImageView.setImageBitmap(qrCodeBitmap);
//			} catch (WriterException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else {
//			LogTool.e("号码为空");
//		}

		left_btn_bg.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (qrCodeBitmap != null) {
			qrCodeBitmap.recycle();
		}
		super.onDestroy();
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
