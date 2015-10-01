package com.lanquan.ui;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
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
import com.lanquan.config.Constants.Config;
import com.lanquan.table.UserTable;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.CommonTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/** 
 * 类描述 ：修改电话
 * 类名： ModifyPhoneActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-14 上午10:40:38  
*/
public class ModifyPhoneActivity extends BaseActivity implements OnClickListener {
	private TextView topNavigation;// 导航栏文字
	private View leftImageButton;// 导航栏左侧按钮
	private View changeBtn;// 导航栏右侧按钮
	private EditText mPhoneView;// 手机号
	private String mPhone;
	private boolean cancel = false;
	private View focusView = null;
	private UserPreference userPreference;
	private int recLen;
	private Timer timer;
	private BroadcastReceiver smsReceiver;
	private IntentFilter filter2;
	private Handler handler;
	private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";
	private String strContent;
	private String mAuthcode;
	private EditText authCodeView;
	private Button authCodeButton;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modify_phone);
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(smsReceiver);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		changeBtn = (View) findViewById(R.id.change);
		mPhoneView = (EditText) findViewById(R.id.phone);
		authCodeView = (EditText) findViewById(R.id.autncode);
		authCodeButton = (Button) findViewById(R.id.get_authcode);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("修改手机号");
		leftImageButton.setOnClickListener(this);
		changeBtn.setOnClickListener(this);
		authCodeButton.setOnClickListener(this);
		dialog = new ProgressDialog(ModifyPhoneActivity.this);
		// 拦截短信内容
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				authCodeView.setText(strContent);
			};
		};

		filter2 = new IntentFilter();
		filter2.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter2.setPriority(Integer.MAX_VALUE);
		smsReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Object[] objs = (Object[]) intent.getExtras().get("pdus");
				for (Object obj : objs) {
					byte[] pdu = (byte[]) obj;
					SmsMessage sms = SmsMessage.createFromPdu(pdu);
					// 短信的内容
					String message = sms.getMessageBody();
					// 短息的手机号。。+86开头？
					String from = sms.getOriginatingAddress();
					if (!TextUtils.isEmpty(from)) {
						String code = patternCode(message);
						if (!TextUtils.isEmpty(code)) {
							strContent = code;
							handler.sendEmptyMessage(1);
						}
					}
				}
			}
		};
		registerReceiver(smsReceiver, filter2);

	}

	/**
	 * 验证输入
	 */
	private void attepmtPhone() {
		// 重置错误
		mPhoneView.setError(null);

		// 存储用户值
		mPhone = mPhoneView.getText().toString();
		boolean cancel = false;

		// 检查手机号
		if (TextUtils.isEmpty(mPhone)) {
			mPhoneView.setError(getString(R.string.error_field_required));
			focusView = mPhoneView;
			cancel = true;
		} else if (!CommonTools.isMobileNO(mPhone)) {
			mPhoneView.setError(getString(R.string.error_phone));
			focusView = mPhoneView;
			cancel = true;
		} else if (mPhone.equals(userPreference.getU_tel())) {
			mPhoneView.setError("手机号没有变化");
			focusView = mPhoneView;
			cancel = true;
		}

		if (cancel) {
			// 如果错误，则提示错误
			focusView.requestFocus();
		} else {
			getAuthCode(mPhone);
		}
	}

	private void attemptChange() {
		// 重置错误
		mPhoneView.setError(null);
		authCodeView.setError(null);
		boolean cancel = false;
		// 存储用户值
		mPhone = mPhoneView.getText().toString();
		mAuthcode = authCodeView.getText().toString();

		// 检查手机号
		if (TextUtils.isEmpty(mPhone)) {
			mPhoneView.setError(getString(R.string.error_field_required));
			focusView = mPhoneView;
			cancel = true;
		} else if (!CommonTools.isMobileNO(mPhone)) {
			mPhoneView.setError(getString(R.string.error_phone));
			focusView = mPhoneView;
			cancel = true;
		}
		// 检查验证码
		else if (TextUtils.isEmpty(mAuthcode)) {
			authCodeView.setError(getString(R.string.error_field_required));
			focusView = authCodeView;
			cancel = true;
		} else if (mAuthcode.length() != 6) {
			authCodeView.setError("验证码长度为6位");
			focusView = authCodeView;
			cancel = true;
		} else if (mPhone.equals(userPreference.getU_tel())) {
			mPhoneView.setError("手机号没有变化");
			focusView = mPhoneView;
			cancel = true;
		}

		if (cancel) {
			// 如果错误，则提示错误
			focusView.requestFocus();
		} else {
//			getAccess(mAuthcode, mAuthcode);
			change(mAuthcode);
		}
	}

	/**
	 * 获取验证码
	 * @return
	 */
	private void getAuthCode(String phone) {
		RequestParams params = new RequestParams();
		params.put(UserTable.U_TEL, phone);
		params.put("type", 0);
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				recLen = Config.AUTN_CODE_TIME;
				authCodeButton.setEnabled(false);

				timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						recLen--;
						Message message = new Message();
						message.what = 1;
						timeHandler.sendMessage(message);
					}
				}, 1000, 1000);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				JsonTool jsonTool = new JsonTool(response);
				String status = jsonTool.getStatus();
				LogTool.i("短信验证码==" + status + response);
				if (status.equals(JsonTool.STATUS_SUCCESS)) {
					LogTool.i(jsonTool.getMessage());
				} else if (status.equals(JsonTool.STATUS_FAIL)) {
					ToastTool.showLong(ModifyPhoneActivity.this, jsonTool.getMessage());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("服务器错误,错误代码" + statusCode + "，  原因：" + errorResponse);
				try {
					JSONObject jsonObject = new JSONObject(errorResponse);
					String status = jsonObject.getString("status");
					if (status.equals(JsonTool.STATUS_FAIL)) {
						mPhoneView.setError(jsonObject.getString("error_message"));
						mPhoneView.requestFocus();
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		};
		AsyncHttpClientTool.post("api/sms/send", params, responseHandler);
	}

	/**
	 * 获取访问令牌
	 * @param newPhone
	 */
//	private void getAccess(final String newPhone, String code) {
//		RequestParams params = new RequestParams();
//
//		params.put("phone", newPhone);
//		params.put("verify_code", code);
//
//		dialog.setMessage("正在变更...");
//		dialog.setCancelable(false);
//
//		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
//
//			@Override
//			public void onStart() {
//				// TODO Auto-generated method stub
//				super.onStart();
//				dialog.show();
//			}
//
//			@Override
//			public void onSuccess(int statusCode, Header[] headers, String response) {
//				// TODO Auto-generated method stub
//				LogTool.e("变更手机号获取权限：" + response);
//				JsonTool jsonTool = new JsonTool(response);
//				String status = jsonTool.getStatus();
//				if (status.equals(JsonTool.STATUS_SUCCESS)) {
//					jsonTool.saveAccess_token();
//					change(newPhone);
//				} else if (status.equals(JsonTool.STATUS_FAIL)) {
//					LogTool.e(jsonTool.getMessage());
//					ToastTool.showShort(ModifyPhoneActivity.this, jsonTool.getMessage());
//				}
//			}
//
//			@Override
//			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
//				// TODO Auto-generated method stub
//				LogTool.e("获取访问令牌服务器错误" + statusCode + errorResponse);
//				try {
//					JSONObject jsonObject = new JSONObject(errorResponse);
//					String status = jsonObject.getString("status");
//					if (status.equals(JsonTool.STATUS_FAIL)) {
//						authCodeView.setError(jsonObject.getString("message"));
//						authCodeView.requestFocus();
//					}
//				} catch (JSONException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				dialog.dismiss();
//			}
//		};
//		AsyncHttpClientTool.post("api/sms/login", params, responseHandler);
//	}

	/**
	 * 变更手机号
	 */
	private void change(final String newPhone) {
		RequestParams params = new RequestParams();

		params.put("new_phone", newPhone);
		params.put("access_token", userPreference.getAccess_token());

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				dialog.dismiss();
				super.onFinish();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				LogTool.e("变更手机号：" + response);
				JsonTool jsonTool = new JsonTool(response);
				String status = jsonTool.getStatus();
				if (status.equals(JsonTool.STATUS_SUCCESS)) {
					ToastTool.showShort(ModifyPhoneActivity.this, "变更成功！");
					jsonTool.saveAccess_token();
					userPreference.setU_tel(newPhone);
					finish();
				} else if (status.equals(JsonTool.STATUS_FAIL)) {
					LogTool.e(jsonTool.getMessage());
					ToastTool.showShort(ModifyPhoneActivity.this, jsonTool.getMessage());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("变更手机号服务器错误" + statusCode + errorResponse);
			}
		};
		AsyncHttpClientTool.post("api/user/modifyPhone", params, responseHandler);
	}

	/**
	* 控制计时
	*/
	final Handler timeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				authCodeButton.setText("剩余" + recLen + "s");
				if (recLen < 0) {
					timer.cancel();
					authCodeButton.setText("重新获取");
					authCodeButton.setEnabled(true);
				}
			}
		}
	};

	/**
	 * 计时器
	 */
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			recLen--;
			Message message = new Message();
			message.what = 1;
			timeHandler.sendMessage(message);
		}
	};

	/**
	 * 匹配短信中间的6个数字（验证码等）
	 * 
	 * @param patternContent
	 * @return
	 */
	private String patternCode(String patternContent) {
		if (TextUtils.isEmpty(patternContent)) {
			return null;
		}
		Pattern p = Pattern.compile(patternCoder);
		Matcher matcher = p.matcher(patternContent);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
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
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.change:
			attemptChange();
			break;
		case R.id.get_authcode:
			attepmtPhone();
			break;
		default:
			break;
		}
	}

}
