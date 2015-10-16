package com.lanquan.ui;

import org.apache.http.Header;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.table.UserTable;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.CommonTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.MD5For32;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.socialize.net.v;

/** 
 * 类描述 ：重置密码页面
 * 类名： ResetPassActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-11 上午10:49:27  
*/
public class ResetPassActivity extends BaseActivity implements OnClickListener {

	private TextView topNavigation;// 导航栏文字
	private View leftImageButton;// 导航栏左侧按钮
	private Button resetBtn;// 导航栏右侧按钮
	private TextView phoneView;
	private EditText mNewPassView;// 新密码
	private EditText mConformPassView;// 确认密码
	private UserPreference userPreference;

	private String newPass;
	private String confirmPass;
	private String mPhone;
	View focusView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reset_pass);
		userPreference = BaseApplication.getInstance().getUserPreference();
		mPhone = getIntent().getStringExtra(UserTable.U_TEL);
		if (mPhone == null || mPhone.isEmpty()) {
			finish();
		}

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) findViewById(R.id.nav_text);
		phoneView = (TextView) findViewById(R.id.reg_now);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		resetBtn = (Button) findViewById(R.id.resetBtn);
		mNewPassView = (EditText) findViewById(R.id.newpass);
		mConformPassView = (EditText) findViewById(R.id.conform_password);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("重置密码");
		phoneView.setText("+" + mPhone + "的验证码");
		leftImageButton.setOnClickListener(this);
		resetBtn.setOnClickListener(this);
	}

	private void attemptPass() {
		// 重置错误
		mNewPassView.setError(null);
		mConformPassView.setError(null);

		// 存储用户值
		newPass = mNewPassView.getText().toString();
		confirmPass = mConformPassView.getText().toString();
		boolean cancel = false;

		// 检查密码
		if (TextUtils.isEmpty(newPass)) {
			mNewPassView.setError(getString(R.string.error_field_required));
			focusView = mNewPassView;
			cancel = true;
		} else if (!CommonTools.isPassValid(newPass)) {
			mNewPassView.setError(getString(R.string.error_pattern_password));
			focusView = mNewPassView;
			cancel = true;
		}

		// 检查重复密码
		else if (TextUtils.isEmpty(confirmPass)) {
			mConformPassView.setError(getString(R.string.error_field_required));
			focusView = mConformPassView;
			cancel = true;
		} else if (!confirmPass.equals(newPass)) {
			mConformPassView.setError(getString(R.string.error_field_conform_pass));
			focusView = mConformPassView;
			cancel = true;
		}

		if (cancel) {
			// 如果错误，则提示错误
			focusView.requestFocus();
		} else {
			RequestParams params = new RequestParams();
//			params.put(UserTable.U_NEW_PASSWORD, MD5For32.GetMD5Code(newPass));
			params.put(UserTable.U_NEW_PASSWORD, newPass);
			params.put(UserTable.U_ACCESS_TOKEN, userPreference.getAccess_token());

			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub

					LogTool.i("重置密码" + statusCode + response);
					JsonTool jsonTool = new JsonTool(response);
					String status = jsonTool.getStatus();
					if (status.equals(JsonTool.STATUS_SUCCESS)) {
//						userPreference.setU_password(MD5For32.GetMD5Code(newPass));
						userPreference.setU_password(newPass);
						jsonTool.saveAccess_token();
						reLogin();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					LogTool.e("重置密码", "服务器错误,错误代码" + statusCode + "，  原因" + errorResponse);
					//
					//					JsonTool jsonTool = new JsonTool(errorResponse);
					//					String status = jsonTool.getStatus();
					//					if (status.equals(JsonTool.STATUS_SUCCESS)) {
					//						LogTool.e("重置密码失败" + status);
					//					} else if (status.equals(JsonTool.STATUS_FAIL)) {
					//						LogTool.e("重置密码失败" + status);
					//					}
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					super.onFinish();
				}
			};
			AsyncHttpClientTool.post(ResetPassActivity.this, "api/user/resetPassword", params, responseHandler);
			reLogin();
		}
	}

	/**
	 * 修改密码后重新登录
	 */
	private void reLogin() {
		// 设置用户不曾登录
		userPreference.clear();
		Intent intent = new Intent(ResetPassActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.resetBtn:
			attemptPass();
			break;
		default:
			break;
		}
	}

}
