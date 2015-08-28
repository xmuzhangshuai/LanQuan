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
import com.lanquan.table.UserTable;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

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
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
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
						if (statusCode == 200) {
							if (response.equals("1")) {
								ToastTool.showShort(ModifyNameActivity.this, "修改成功！");
								userPreference.setU_nickname(name);
								finish();
								overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
							} else if (response.equals("-1")) {
								LogTool.e("修改名字返回-1");
							}
						}
						LogTool.i("修改昵称"+statusCode+"==="+response);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
						// TODO Auto-generated method stub
						LogTool.e("修改昵称"+statusCode+"==="+errorResponse);
					}
				};
				AsyncHttpClientTool.post("api/user/modifyNickname", params, responseHandler);
			}
		} else {
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}
	}
}
