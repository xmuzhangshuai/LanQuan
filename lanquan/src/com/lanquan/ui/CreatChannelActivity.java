package com.lanquan.ui;

import org.apache.http.Header;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

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
	private String type = "";// 0为图文频道，1为文字频道，2为打卡频道
	private String title;
	private String detail;
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
		if (type.equals("0")) {
			navTextView.setText("创建图文频道");
		} else if (type.equals("1")) {
			navTextView.setText("创建文字频道");
		} else if (type.equals("2")) {
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
		case R.id.right_btn_bg:
			attemptCreate();
			break;
		default:
			break;
		}
	}

	public void attemptCreate() {
		// Reset errors.
		titleEditText.setError(null);
		detailEditText.setError(null);
		View focusView = null;

		// Store values at the time of the login attempt.
		title = titleEditText.getText().toString();
		detail = detailEditText.getText().toString();

		boolean cancel = false;

		// Check for a valid password, if the user entered one.
		if (TextUtils.isEmpty(title)) {
			titleEditText.setError(getString(R.string.error_field_required));
			focusView = titleEditText;
			cancel = true;
		}

		// Check for a valid email address.
		else if (TextUtils.isEmpty(detail)) {
			detailEditText.setError(getString(R.string.error_field_required));
			focusView = detailEditText;
			cancel = true;
		} else if (detail.length() < 10) {
			detailEditText.setError("频道介绍至少为10个字");
			focusView = detailEditText;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			createChannel();
		}
	}

	/**
	 *创建频道 
	 */
	private void createChannel() {
		RequestParams params = new RequestParams();
		params.put("title", title);
		params.put("description", detail);
		params.put("type", type);
		params.put("access_token", userPreference.getAccess_token());
		final Dialog dialog = showProgressDialog("正在创建，请稍后...");
		dialog.setCancelable(false);
		dialog.show();
		rightBtn.setEnabled(false);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler("utf-8") {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				LogTool.i("创建频道:" + statusCode + response);
				JsonTool jsonTool = new JsonTool(response);
				String status = jsonTool.getStatus();
				if (status.equals(JsonTool.STATUS_SUCCESS)) {
					userPreference.setChannel_count(userPreference.getChannel_count() + 1);
					LogTool.i(jsonTool.getMessage());
					ToastTool.showShort(CreatChannelActivity.this, "创建成功");
					finish();
				} else {
					LogTool.e(jsonTool.getMessage());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("创建频道" + errorResponse);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				dialog.dismiss();
			}
		};
		AsyncHttpClientTool.post(CreatChannelActivity.this, "api/channel/create", params, responseHandler);
	}
}
