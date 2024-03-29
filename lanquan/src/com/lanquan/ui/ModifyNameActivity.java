package com.lanquan.ui;

import org.apache.http.Header;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.table.UserTable;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/** 
 * 类描述 ：修改昵称
 * 类名： ModifyNameActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-14 上午10:40:56  
*/
public class ModifyNameActivity extends BaseActivity implements OnClickListener {
	private EditText nameEditEditText;
	private TextView saveBtn;
	private View backBtn;
	private UserPreference userPreference;
	View focusView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modify_name);
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		nameEditEditText = (EditText) findViewById(R.id.name);
		saveBtn = (TextView) findViewById(R.id.publish_btn);
		backBtn = findViewById(R.id.left_btn_bg);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		saveBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		nameEditEditText.setText(userPreference.getU_nickname());
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		hideKeyboard();
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			hideKeyboard();
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.publish_btn:
			updateName(nameEditEditText.getText().toString().trim());
			break;
		default:
			break;
		}
	}

	/**
	* 隐藏软键盘
	*/
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(nameEditEditText.getWindowToken(), 0);
	}

	/**
	 * 修改昵称
	 * @param name
	 */
	public void updateName(final String name) {
		if (!name.equals(userPreference.getU_nickname())) {
			// 重置错误
			nameEditEditText.setError(null);

			// 存储用户值
			boolean cancel = false;
			// 检查旧密码
			if (TextUtils.isEmpty(name)) {
				nameEditEditText.setError(getString(R.string.error_field_required));
				focusView = nameEditEditText;
				cancel = true;
			}

			if (cancel) {
				// 如果错误，则提示错误
				focusView.requestFocus();
			} else {
				// 没有错误，则修改
				hideKeyboard();
				RequestParams params = new RequestParams();
				String access_token = userPreference.getAccess_token();
				params.put(UserTable.U_ACCESS_TOKEN, access_token);
				params.put(UserTable.U_NICKNAME, name);

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
						// TODO Auto-generated method stub

						JsonTool jsonTool = new JsonTool(response);
						String status = jsonTool.getStatus();
						String message = jsonTool.getMessage();
						if (status.equals("success")) {
							ToastTool.showShort(ModifyNameActivity.this, "修改成功！");
							LogTool.i(message);
							jsonTool.saveAccess_token();
							userPreference.setU_nickname(name);
							//修改成功之后不停留在当前activity
							finish();
							//动画效果往右滑出
							overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
						} else {
							LogTool.e("修改用户名之后的操作失败" + message);
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
						// TODO Auto-generated method stub
						LogTool.e("修改昵称" + statusCode + "===" + errorResponse);
					}
				};
				AsyncHttpClientTool.post(ModifyNameActivity.this, "api/user/modifyNickname", params, responseHandler);
			}
		} else {
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}
	}
}
