package com.lanquan.ui;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.lanquan.utils.LogTool;
import com.lanquan.utils.SIMCardInfo;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/** 
 * 类描述 ：忘记密码，输入手机号获取验证码验证账号
 * 类名： ForgetPassActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-11 上午10:28:04  
*/
public class ForgetPassActivity extends BaseActivity implements OnClickListener {

	private TextView topNavigation;// 导航栏文字
	private View leftImageButton;// 导航栏左侧按钮
	private Button nextBtn;// 导航栏右侧按钮
	private EditText mPhoneView;// 手机号
	private EditText authCodeView;// 验证码
	private TextView regNowTextView;// 立即注册
	private int recLen;
	private Button authCodeButton;
	private Timer timer;
	private BroadcastReceiver smsReceiver;
	private IntentFilter filter2;
	private Handler handler;
	private String strContent;
	private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";
	private String mPhone;
	private String authcode;
	private boolean cancel = false;
	private View focusView = null;
	private UserPreference userPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forget_pass);
		userPreference = BaseApplication.getInstance().getUserPreference();

		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				authCodeView.setText(strContent);
			};
		};

		findViewById();
		initView();

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
					LogTool.d("验证码", "message     " + message);
					// 短息的手机号。。+86开头？
					String from = sms.getOriginatingAddress();
					LogTool.d("验证码", "from     " + from);
					// Time time = new Time();
					// time.set(sms.getTimestampMillis());
					// String time2 = time.format3339(true);
					// Log.d("logo", from + "   " + message + "  " + time2);
					// strContent = from + "   " + message;
					// handler.sendEmptyMessage(1);
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		nextBtn = (Button) findViewById(R.id.next);
		mPhoneView = (EditText) findViewById(R.id.phone);
		authCodeButton = (Button) findViewById(R.id.again_authcode);
		authCodeView = (EditText) findViewById(R.id.autncode);
		regNowTextView = (TextView) findViewById(R.id.reg_now);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("忘记密码");
		leftImageButton.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		regNowTextView.setOnClickListener(this);
		nextBtn.setEnabled(false);

		String loginedPhone = userPreference.getU_tel();
		if (!TextUtils.isEmpty(loginedPhone)) {
			mPhoneView.setText(loginedPhone);
		} else {
			SIMCardInfo siminfo = new SIMCardInfo(ForgetPassActivity.this);
			String number = siminfo.getNativePhoneNumber();
			mPhoneView.setText(number);
		}

		recLen = Config.AUTN_CODE_TIME;

		authCodeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attemptPhone();
			}
		});

		// 输入验证码时事件
		authCodeView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String content = authCodeView.getText().toString();
				if (content != null) {
					if (content.length() == 6) {
						nextBtn.setEnabled(true);
					} else {
						nextBtn.setEnabled(false);
					}
				}
			}
		});
	}

	/**
	 * 控制计时
	 */
	final Handler timeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				authCodeButton.setText("请在" + recLen + "s内输入验证码");
				if (recLen < 0) {
					timer.cancel();
					authCodeButton.setText("重新获取验证码");
					authCodeButton.setEnabled(true);
					nextBtn.setEnabled(false);
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

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(smsReceiver);
	}

	private void attemptPhone() {
		// 重置错误
		mPhoneView.setError(null);

		// 存储用户值
		mPhone = mPhoneView.getText().toString();

		// 检查手机号
		if (TextUtils.isEmpty(mPhone)) {
			mPhoneView.setError(getString(R.string.error_field_required));
			focusView = mPhoneView;
			cancel = true;
		} else if (!CommonTools.isMobileNO(mPhone)) {
			mPhoneView.setError(getString(R.string.error_phone));
			focusView = mPhoneView;
			cancel = true;
		} else {
			// 检查手机号是否被注册
//			RequestParams params = new RequestParams();
//			params.put(UserTable.U_TEL, mPhone);
//			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
//
//				@Override
//				public void onSuccess(int arg0, Header[] arg1, String arg2) {
//					// TODO Auto-generated method stub
//					if (arg0 == 200) {
//						if (arg2.equals("-1")) {
//							getAuthCode();
//
//							recLen = Config.AUTN_CODE_TIME;
//							authCodeButton.setEnabled(false);
//							timer = new Timer();
//							timer.schedule(new TimerTask() {
//
//								@Override
//								public void run() {
//									// TODO Auto-generated method stub
//									recLen--;
//									Message message = new Message();
//									message.what = 1;
//									timeHandler.sendMessage(message);
//								}
//							}, 1000, 1000);
//						} else {
//							mPhoneView.setError(getString(R.string.no_reg_phone));
//							mPhoneView.requestFocus();
//						}
//					}
//				}
//
//				@Override
//				public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
//					// TODO Auto-generated method stub
//					ToastTool.showLong(ForgetPassActivity.this, "服务器错误");
//				}
//			};
//			AsyncHttpClientTool.post("regist/telrepeat", params, responseHandler);
			
			getAuthCode();

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

		if (cancel) {
			// 如果错误，则提示错误
			focusView.requestFocus();
		}
	}

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

	/**
	 * 获取验证码
	 * @return
	 */
	private void getAuthCode() {
//		RequestParams params = new RequestParams();
//		params.put(UserTable.U_TEL, mPhone);
//		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
//			@Override
//			public void onStart() {
//				// TODO Auto-generated method stub
//				super.onStart();
//			}
//
//			@Override
//			public void onSuccess(int statusCode, Header[] headers, String response) {
//				// TODO Auto-generated method stub
//				LogTool.i("验证码", response);
//				if (response.length() == 6) {
//					ToastTool.showShort(ForgetPassActivity.this, "验证码已发送");
//					authcode = response;
//				} else if (response.endsWith("-1")) {
//					ToastTool.showShort(ForgetPassActivity.this, "服务器错误");
//				} else if (response.endsWith("1")) {
//					ToastTool.showShort(ForgetPassActivity.this, "手机号码为空");
//				} else {
//					LogTool.e("服务器返回错误");
//				}
//			}
//
//			@Override
//			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
//				// TODO Auto-generated method stub
//				LogTool.e("验证码", "服务器错误,错误代码" + statusCode + "，  原因" + errorResponse);
//			}
//
//			@Override
//			public void onFinish() {
//				// TODO Auto-generated method stub
//				super.onFinish();
//			}
//		};
//		AsyncHttpClientTool.post("user/getValidateCode", params, responseHandler);
		authcode = "111111";
	}

	/**
	 * 验证验证码
	 * @return
	 */
	private boolean vertifyAuthCode(String code) {
		if (!TextUtils.isEmpty(authcode)) {
			if (authcode.equals(code)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.next:
			if (vertifyAuthCode(authCodeView.getText().toString())) {
				Intent intent = new Intent(ForgetPassActivity.this, ResetPassActivity.class);
				intent.putExtra(UserTable.U_TEL, mPhone);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else {
				authCodeView.setError("验证码错误");
				authCodeView.requestFocus();
			}
			break;
		case R.id.reg_now:
			Intent intent = new Intent(ForgetPassActivity.this, RegisterActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}
	}
}
