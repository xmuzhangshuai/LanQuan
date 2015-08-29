package com.lanquan.ui;

import org.apache.http.Header;

import android.app.Dialog;
import android.content.Intent;
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

/** 
 * 类描述 ：修改密码
 * 类名： ModifyPassActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-14 上午10:41:11  
*/
public class ModifyPassActivity extends BaseActivity implements OnClickListener {
	private TextView saveBtn;
	private View backBtn;
	private EditText mOldPassView;// 旧密码
	private EditText mNewPassView;// 新密码
	private EditText mConformPassView;// 确认密码
	private UserPreference userPreference;

	private String oldPass;
	private String newPass;
	private String confirmPass;
	View focusView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modify_pass);
		userPreference = BaseApplication.getInstance().getUserPreference();
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		saveBtn = (TextView) findViewById(R.id.publish_btn);
		backBtn = findViewById(R.id.left_btn_bg);
		mOldPassView = (EditText) findViewById(R.id.oldpass);
		mNewPassView = (EditText) findViewById(R.id.newpass);
		mConformPassView = (EditText) findViewById(R.id.conform_password);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		saveBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
	}

	private void attemptModifyPass() {
		// 重置错误
		mOldPassView.setError(null);
		mNewPassView.setError(null);
		mConformPassView.setError(null);

		// 存储用户值
		oldPass = mOldPassView.getText().toString();
		newPass = mNewPassView.getText().toString();
		confirmPass = mConformPassView.getText().toString();
		boolean cancel = false;

		// 检查旧密码
		if (TextUtils.isEmpty(oldPass)) {
			mOldPassView.setError(getString(R.string.error_field_required));
			focusView = mOldPassView;
			cancel = true;
		} else if (!CommonTools.isPassValid(oldPass)) {
			mOldPassView.setError(getString(R.string.error_pattern_password));
			focusView = mOldPassView;
			cancel = true;
		}

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
			// 没有错误，则修改
			RequestParams params = new RequestParams();
			params.put(UserTable.U_PASSWORD, MD5For32.GetMD5Code(oldPass));
			params.put(UserTable.U_NEW_PASSWORD, MD5For32.GetMD5Code(newPass));
			params.put(UserTable.U_TEL, userPreference.getU_tel());
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
				Dialog dialog;

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					dialog = showProgressDialog("请稍后...");
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					dialog.dismiss();
					super.onFinish();
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					JsonTool jsonTool = new JsonTool(response);
					String status = jsonTool.getStatus();
					if (status.equals(JsonTool.STATUS_SUCCESS)) {
						ToastTool.showShort(ModifyPassActivity.this, "修改成功！");
						LogTool.i(jsonTool.getMessage());
						jsonTool.saveAccess_token();
						userPreference.setU_password(MD5For32.GetMD5Code(newPass));
						finish();

					} else if (status.equals(JsonTool.STATUS_FAIL)) {
						LogTool.e(jsonTool.getMessage());
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					LogTool.e("修改密码服务器错误" + statusCode + errorResponse);
					mOldPassView.setError("旧密码不正确");
					focusView = mOldPassView;
					focusView.requestFocus();
				}
			};
			AsyncHttpClientTool.post("api/user/modifyPassword", params, responseHandler);
		}
	}

	/**
	 * 修改密码后重新登录
	 */
	private void reLogin() {
		// 设置用户不曾登录
		// BaseApplication.getInstance().logout();
		userPreference.clear();
		Intent intent = new Intent(ModifyPassActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			break;
		case R.id.publish_btn:
			attemptModifyPass();
			break;
		default:
			break;
		}
	}
}
