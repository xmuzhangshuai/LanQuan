package com.lanquan.ui;

import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.FlagToString;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseV4Fragment;
import com.lanquan.config.Constants;
import com.lanquan.config.Constants.Config;
import com.lanquan.config.Constants.QQConfig;
import com.lanquan.config.Constants.WeChatConfig;
import com.lanquan.config.Constants.WeiboConfig;
import com.lanquan.customwidget.MyAlertDialog;
import com.lanquan.table.UserTable;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.CommonTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.MD5For32;
import com.lanquan.utils.SIMCardInfo;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

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
	private ImageView loginwechat;
	private ImageView loginqq;
	private ImageView loginweibo;

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
	UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

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
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(getActivity(), WeChatConfig.API_KEY, WeChatConfig.SECRIT_KEY);
		wxHandler.addToSocialSDK();
		//qq平台
		//参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(), QQConfig.API_KEY, QQConfig.SECRIT_KEY);
		qqSsoHandler.addToSocialSDK();
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

		loginwechat = (ImageView) rootView.findViewById(R.id.loginwechat);
		loginqq = (ImageView) rootView.findViewById(R.id.loginqq);
		loginweibo = (ImageView) rootView.findViewById(R.id.loginweibo);
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
		loginwechat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToastTool.showShort(getActivity(), "微信第三方登录");
				mController.doOauthVerify(getActivity(), SHARE_MEDIA.WEIXIN, new UMAuthListener() {
					@Override
					public void onStart(SHARE_MEDIA platform) {
						Toast.makeText(getActivity(), "授权开始", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError(SocializeException e, SHARE_MEDIA platform) {
						Toast.makeText(getActivity(), "授权错误", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {
						Toast.makeText(getActivity(), "授权完成", Toast.LENGTH_SHORT).show();
						//获取相关授权信息
						mController.getPlatformInfo(getActivity(), SHARE_MEDIA.WEIXIN, new UMDataListener() {
							@Override
							public void onStart() {
								Toast.makeText(getActivity(), "获取平台数据开始...", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onComplete(int status, Map<String, Object> info) {
								if (status == 200 && info != null) {
									StringBuilder sb = new StringBuilder();
									Set<String> keys = info.keySet();
									for (String key : keys) {
										sb.append(key + "=" + info.get(key).toString() + "\r\n");
									}
									String avatar = info.get("headimgurl").toString();
									//other_login("wx", WeChatConfig.API_KEY, avatar);
									Log.d("TestData", sb.toString());
								} else {
									Log.d("TestData", "发生错误：" + status);
								}
							}
						});
					}

					@Override
					public void onCancel(SHARE_MEDIA platform) {
						Toast.makeText(getActivity(), "授权取消", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		loginqq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToastTool.showShort(getActivity(), "qq第三方登录");
				mController.doOauthVerify(getActivity(), SHARE_MEDIA.QQ, new UMAuthListener() {
					@Override
					public void onStart(SHARE_MEDIA platform) {
						Toast.makeText(getActivity(), "授权开始", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError(SocializeException e, SHARE_MEDIA platform) {
						Toast.makeText(getActivity(), "授权错误", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {
						Toast.makeText(getActivity(), "授权完成", Toast.LENGTH_SHORT).show();
						//获取相关授权信息
						mController.getPlatformInfo(getActivity(), SHARE_MEDIA.QQ, new UMDataListener() {
							@Override
							public void onStart() {
								Toast.makeText(getActivity(), "获取平台数据开始...", Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onComplete(int status, Map<String, Object> info) {
								if (status == 200 && info != null) {
									StringBuilder sb = new StringBuilder();
									Set<String> keys = info.keySet();
									for (String key : keys) {
										sb.append(key + "=" + info.get(key).toString() + "\r\n");
									}
									//如果第三方登录成功，获取avatar以及appid直接登录
									String avatar = info.get("profile_image_url").toString();
//									other_login("qq", QQConfig.API_KEY, avatar);

									Log.d("TestData", sb.toString());
								} else {
									Log.d("TestData", "发生错误：" + status);
								}
							}
						});
					}

					@Override
					public void onCancel(SHARE_MEDIA platform) {
						Toast.makeText(getActivity(), "授权取消", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		loginweibo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToastTool.showShort(getActivity(), "微博第三方登录");
				mController.doOauthVerify(getActivity(), SHARE_MEDIA.SINA, new UMAuthListener() {
					@Override
					public void onError(SocializeException e, SHARE_MEDIA platform) {
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {
						if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
							Toast.makeText(getActivity(), "授权成功.", Toast.LENGTH_SHORT).show();
							mController.getPlatformInfo(getActivity(), SHARE_MEDIA.SINA, new UMDataListener() {
							    @Override
							    public void onStart() {
							        Toast.makeText(getActivity(), "获取平台数据开始...", Toast.LENGTH_SHORT).show();
							    }                                              
							    @Override
							        public void onComplete(int status, Map<String, Object> info) {
							            if(status == 200 && info != null){
							                StringBuilder sb = new StringBuilder();
							                Set<String> keys = info.keySet();
							                for(String key : keys){
							                   sb.append(key+"="+info.get(key).toString()+"\r\n");
							                }
							              //如果第三方登录成功，获取avatar以及appid直接登录
											String avatar = info.get("profile_image_url").toString();
//											other_login("weibo", WeiboConfig.API_KEY, avatar);
							                Log.d("TestData",sb.toString());
							            }else{
							               Log.d("TestData","发生错误："+status);
							           }
							        }
							});
						} else {
							Toast.makeText(getActivity(), "授权失败", Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onCancel(SHARE_MEDIA platform) {
					}

					@Override
					public void onStart(SHARE_MEDIA platform) {
					}
				});
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

		//如果可以获取到验证码，才进行计时
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
		RequestParams params = new RequestParams();
		params.put(UserTable.U_TEL, mPhone);
		params.put("type", 0);
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				JsonTool jsonTool = new JsonTool(response);
				String status = jsonTool.getStatus();
				LogTool.i("短信验证码==" + status + response);
				if (status.equals(JsonTool.STATUS_SUCCESS)) {
					LogTool.i(jsonTool.getMessage());
					jsonTool.saveAccess_token();
				} else if (status.equals(JsonTool.STATUS_FAIL)) {
					LogTool.e(jsonTool.getMessage());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("验证码", "服务器错误,错误代码" + statusCode + "，  原因" + errorResponse);

				boolean cancel = false;
				JsonTool jsonTool = new JsonTool(errorResponse);
				if (jsonTool.getStatus().equals("fail")) {
					mPhoneView.setError(jsonTool.getMessage());
					focusView = mPhoneView;
					cancel = true;
				}
				if (cancel) {
					focusView.requestFocus();
					timer.cancel();
					authCodeButton.setText("获取验证码");
					authCodeButton.setEnabled(true);
				}
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
			}
		};
		AsyncHttpClientTool.post("api/sms/send", params, responseHandler);
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
		userPreference.setU_tel(mPhone);
		userPreference.setAuthCode(mAuthcode);
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

	//	/**
	//	 * 验证验证码
	//	 * @return
	//	 */
	//	private boolean vertifyAuthCode(String myAuthCode, String response) {
	//		if (!TextUtils.isEmpty(response)) {
	//			if (response.equals(myAuthCode)) {
	//				return true;
	//			} else {
	//				return false;
	//			}
	//		} else {
	//			return false;
	//		}
	//	}

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
