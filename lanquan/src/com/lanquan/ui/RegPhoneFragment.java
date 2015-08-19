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
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseV4Fragment;
import com.lanquan.config.Constants.Config;
import com.lanquan.customwidget.MyAlertDialog;
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
 * 类描述 ：注册——电话和验证码页面
 * 类名： RegPhoneFragment.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-10 下午4:05:26  
*/
public class RegPhoneFragment extends BaseV4Fragment {
	/*************Views************/
	private View rootView;// 根View
	private View leftImageButton;// 导航栏左侧按钮
	private View rightImageButton;// 导航栏右侧按钮
	private Button authCodeButton;
	private EditText authCodeView;
	private EditText mPhoneView;// 手机号
	private UserPreference userPreference;
	private TextView leftNavigation;// 步骤

	private String mPhone;
	private String mAuthcode;
	private String responseAuthcode;// 手机获取到的验证码

	private int recLen;

	private Timer timer;
	private BroadcastReceiver smsReceiver;
	private IntentFilter filter2;
	private Handler handler;
	private String strContent;
	View focusView = null;

	private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

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
		getActivity().registerReceiver(smsReceiver, filter2);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(smsReceiver);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_phone, container, false);
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();// 初始化views
		initView();

		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		leftImageButton = (View) getActivity().findViewById(R.id.left_btn_bg);
		rightImageButton = (View) getActivity().findViewById(R.id.right_btn_bg);
		mPhoneView = (EditText) rootView.findViewById(R.id.phone);
		authCodeButton = (Button) rootView.findViewById(R.id.get_authcode);
		authCodeView = (EditText) rootView.findViewById(R.id.autncode);
		leftNavigation = (TextView) getActivity().findViewById(R.id.nav_text);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		rightImageButton.setVisibility(View.VISIBLE);
		// 显示用户手机号
		SIMCardInfo siminfo = new SIMCardInfo(getActivity());
		mPhoneView.setText(siminfo.getNativePhoneNumber());
		mPhone = mPhoneView.getText().toString();

		leftNavigation.setText("注册 1/2");
		leftImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vertifyToTerminate();
			}
		});

		rightImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attepmtAccount();
			}
		});

		authCodeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attemptAuthCode();
			}
		});

	}

	/**
	 * 验证验证码
	 */
	private void attemptAuthCode() {
		mPhoneView.setError(null);
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
		}
		if (cancel) {
			// 如果错误，则提示错误
			focusView.requestFocus();
			return;
		}

		getAuthCode();// 获取验证码

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

	/**
	 * 确认终止注册
	 */
	private void vertifyToTerminate() {
		final MyAlertDialog dialog = new MyAlertDialog(getActivity());
		dialog.setTitle("提示");
		dialog.setMessage("注册过程中退出，信息将不能保存。是否继续退出？");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				getActivity().finish();
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

	/**
	 * 验证输入
	 */
	private void attepmtAccount() {
		// 重置错误
		mPhoneView.setError(null);
		authCodeView.setError(null);

		// 存储用户值
		mPhone = mPhoneView.getText().toString();
		mAuthcode = authCodeView.getText().toString();
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
		} else if (!vertifyAuthCode(mAuthcode, responseAuthcode)) {
			authCodeView.setError("验证码不正确");
			focusView = authCodeView;
			cancel = true;
		}

		if (cancel) {
			// 如果错误，则提示错误
			focusView.requestFocus();
		} else {
			// 检查手机号是否被注册
			CheckPhone();
		}
	}

	/**
	 * 下一步
	 */
	private void next() {
		RegAccountFragment regSchoolFragment = new RegAccountFragment();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);
		transaction.replace(R.id.fragment_container, regSchoolFragment, "RegAccountFragment");
		transaction.addToBackStack(null);
		transaction.commit();
	}

	/**
	 * 获取验证码
	 * @return
	 */
	private void getAuthCode() {
		// RequestParams params = new RequestParams();
		// params.put(UserTable.U_TEL, mPhone);
		// TextHttpResponseHandler responseHandler = new
		// TextHttpResponseHandler() {
		//
		// @Override
		// public void onSuccess(int statusCode, Header[] headers, String
		// response) {
		// // TODO Auto-generated method stub
		//
		// if (response.length() == 6) {
		// responseAuthcode = response;
		// } else if (response.endsWith("-1")) {
		// ToastTool.showLong(getActivity(), "服务器出现异常，请稍后再试");
		// LogTool.e("获取验证码服务器返回-1");
		// } else if (response.endsWith("1")) {
		// ToastTool.showShort(getActivity(), "手机号码为空");
		// } else {
		// LogTool.e("获取验证码服务器错误");
		// }
		// }
		//
		// @Override
		// public void onFailure(int statusCode, Header[] headers, String
		// errorResponse, Throwable e) {
		// // TODO Auto-generated method stub
		// LogTool.e("验证码", "服务器错误,错误代码" + statusCode + "，  原因" +
		// errorResponse);
		// }
		//
		// @Override
		// public void onFinish() {
		// // TODO Auto-generated method stub
		// super.onFinish();
		// }
		// };
		// AsyncHttpClientTool.post("user/getValidateCode", params,
		// responseHandler);
		responseAuthcode = "111111";
	}

	/**
	 *检查手机号是否已经被注册
	 */
	public void CheckPhone() {
		// RequestParams params = new RequestParams();
		// params.put(UserTable.U_TEL, mPhoneView.getText().toString());
		// TextHttpResponseHandler responseHandler = new
		// TextHttpResponseHandler() {
		//
		// @Override
		// public void onSuccess(int statusCode, Header[] headers, String
		// response) {
		// // TODO Auto-generated method stub
		//
		// if (response.equals("1")) {
		// // 没有错误，则存储值
		// userPreference.setU_tel(mPhone);
		// next();
		// } else {
		// mPhoneView.setError("该手机号码已被注册");
		// focusView = mPhoneView;
		// focusView.requestFocus();
		// }
		// }
		//
		// @Override
		// public void onFailure(int statusCode, Header[] headers, String
		// errorResponse, Throwable e) {
		// // TODO Auto-generated method stub
		// LogTool.e("验证码", "服务器错误,错误代码" + statusCode + "，  原因" +
		// errorResponse);
		// }
		//
		// @Override
		// public void onFinish() {
		// // TODO Auto-generated method stub
		// super.onFinish();
		// }
		// };
		// AsyncHttpClientTool.post("user/getValidateCode", params,
		// responseHandler);
		next();
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
	 * 验证验证码
	 * @return
	 */
	private boolean vertifyAuthCode(String myAuthCode, String response) {
		if (!TextUtils.isEmpty(response)) {
			if (response.equals(myAuthCode)) {
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

}
