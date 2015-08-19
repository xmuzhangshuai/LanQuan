package com.lanquan.ui;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;

import android.app.ProgressDialog;
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
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/** 
 * 类描述 ：修改电话时填写验证码
 * 类名： AuthCodeActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-14 上午10:44:29  
*/ 
public class ModifyPhoneAuthCodeActivity extends BaseActivity {
	public final static String AUTHCODE = "authcode";
	/*************Views************/
	private TextView topNavigation;
	private View leftImageButton;
	private View rightImageButton;
	private int recLen;
	private Button authCodeButton;
	private EditText authCodeView;
	private Timer timer;
	ProgressDialog dialog;
	private BroadcastReceiver smsReceiver;
	private IntentFilter filter2;
	private Handler handler;
	private String strContent;
	private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";
	private UserPreference userPreference;
	private String mPhone;
	private String authcode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_auth_code);

		mPhone = getIntent().getStringExtra(UserTable.U_TEL);
		authcode = getIntent().getStringExtra(AUTHCODE);
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();

		timer = new Timer();
		timer.schedule(task, 1000, 1000);

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
		rightImageButton = (View) findViewById(R.id.right_btn_bg);
		authCodeButton = (Button) findViewById(R.id.again_authcode);
		authCodeView = (EditText) findViewById(R.id.autncode);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("填写验证码");
		recLen = Config.AUTN_CODE_TIME;
		authCodeButton.setEnabled(false);
		rightImageButton.setEnabled(false);
		leftImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		authCodeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				recLen = Config.AUTN_CODE_TIME;
				authCodeButton.setEnabled(false);
				getAuthCode();
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
		});

		rightImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (vertifyAuthCode(authCodeView.getText().toString())) {

					RequestParams params = new RequestParams();
					params.put(UserTable.U_TEL, mPhone);
					params.put(UserTable.U_ID, userPreference.getU_id());
					TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1, String arg2) {
							// TODO Auto-generated method stub
							if (arg0 == 200) {
								if (arg2.equals("1")) {
									userPreference.setU_tel(mPhone);
									ToastTool.showLong(ModifyPhoneAuthCodeActivity.this, "修改手机号码成功");
									finish();
									overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
								} else {
									ToastTool.showLong(ModifyPhoneAuthCodeActivity.this, "修改手机号码失败");
								}
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							ToastTool.showLong(ModifyPhoneAuthCodeActivity.this, "服务器错误");
						}
					};
					AsyncHttpClientTool.post("updateusertel", params, responseHandler);
				} else {
					authCodeView.setError("验证码错误");
					authCodeView.requestFocus();
				}
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
					if (content.length() > 0) {
						rightImageButton.setEnabled(true);
					} else {
						rightImageButton.setEnabled(false);
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
					rightImageButton.setEnabled(false);
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
		RequestParams params = new RequestParams();
		params.put(UserTable.U_TEL, mPhone);
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub

				LogTool.e("验证码", response);
				if (response.length() == 6) {
					authcode = response;
					ToastTool.showShort(ModifyPhoneAuthCodeActivity.this, "验证码已发送");
				} else if (response.endsWith("-1")) {
					ToastTool.showShort(ModifyPhoneAuthCodeActivity.this, "服务器错误");
				} else if (response.endsWith("1")) {
					ToastTool.showShort(ModifyPhoneAuthCodeActivity.this, "手机号码为空");
				} else {
					ToastTool.showShort(ModifyPhoneAuthCodeActivity.this, "服务器错误");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				ToastTool.showShort(ModifyPhoneAuthCodeActivity.this, "服务器错误");
				LogTool.e("验证码", "服务器错误,错误代码" + statusCode + "，  原因" + errorResponse);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
			}
		};
		AsyncHttpClientTool.post("getmessage", params, responseHandler);
	}
}
