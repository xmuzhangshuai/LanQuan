package com.lanquan.ui;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.customwidget.MyAlertDialog;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * 类描述 ：选择创建图文频道、文字频道和打卡频道的页面 类名： CreatChannelActivity.java Copyright: Copyright
 * (c)2015 Company: zhangshuai
 * 
 * @author: zhangshuai
 * @version: 1.0 创建时间: 2015-8-8 上午9:58:22
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
		switch (v.getId()) {
		case R.id.close_btn:
			finish();
			break;
		case R.id.photo_btn:
			showCreateDialog(0);
			break;

		case R.id.txt_btn:
			showCreateDialog(1);
			break;

		case R.id.punch_btn:
			showCreateDialog(2);
			break;

		default:
			break;
		}

	}

	// 传递评论Id,用于删除评论（如果是自己发表的）
	public void showCreateDialog(final int type) {
		final MyAlertDialog dialog = new MyAlertDialog(CreatChannelChooseActivity.this);
		if (type == 0) {
			dialog.setTitle("你将创建的是图文频道");
			dialog.setMessage("在该频道里你可以同时发图片和文字，也只发文字或者图片，频道一旦创建，属性不能修改，你确定吗？");
		} else if (type == 1) {
			dialog.setTitle("你将创建的是纯文字频道");
			dialog.setMessage("在该频道里只允许发文字，频道 一旦创建，属性不能修改，你确定吗？");

		} else if (type == 2) {
			dialog.setTitle("你将创建的是打卡频道");
			dialog.setMessage("在该频道里可以记录打卡，频道一旦创建，属性不能修改，你确定吗？");
		} else {
			return;
		}

		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent intent = new Intent(CreatChannelChooseActivity.this, CreatChannelActivity.class);
				intent.putExtra(CreatChannelActivity.CHANNAL_TYPE, "" + type);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				finish();
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
}
