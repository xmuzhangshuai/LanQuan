package com.lanquan.ui;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseV4Fragment;
import com.lanquan.config.Constants.QQConfig;
import com.lanquan.config.Constants.WeChatConfig;
import com.lanquan.customwidget.MyAlertDialog;
import com.lanquan.table.UserTable;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.CommonTools;
import com.lanquan.utils.DateTimeTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
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
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

	private EditText mPhoneView;// 手机号
	private UserPreference userPreference;
	private TextView leftNavigation;// 步骤
	private ImageView loginwechat;
	private ImageView loginqq;
	private ImageView loginweibo;
	private Button authCodeButton;
	private String mPhone;

	View focusView = null;
	UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

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
		//

		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		leftImageButton = (View) getActivity().findViewById(R.id.left_btn_bg);
		rightImageButton = (View) getActivity().findViewById(R.id.right_btn_bg);
		mPhoneView = (EditText) rootView.findViewById(R.id.phone);
		leftNavigation = (TextView) getActivity().findViewById(R.id.nav_text);
		loginwechat = (ImageView) rootView.findViewById(R.id.loginwechat);
		loginqq = (ImageView) rootView.findViewById(R.id.loginqq);
		loginweibo = (ImageView) rootView.findViewById(R.id.loginweibo);
		authCodeButton = (Button) rootView.findViewById(R.id.get_authcode);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		rightImageButton.setVisibility(View.GONE);
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

		authCodeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attepmtPhone();
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
									other_login("wx", WeChatConfig.API_KEY, avatar);
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
									other_login("qq", QQConfig.API_KEY, avatar);

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
				//				设置新浪SSO handler
				mController.getConfig().setSsoHandler(new SinaSsoHandler());
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
									if (status == 200 && info != null) {
										StringBuilder sb = new StringBuilder();
										Set<String> keys = info.keySet();
										for (String key : keys) {
											sb.append(key + "=" + info.get(key).toString() + "\r\n");
										}
										Log.d("TestData", sb.toString());
									} else {
										Log.d("TestData", "发生错误：" + status);
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
		}

		if (cancel) {
			// 如果错误，则提示错误
			focusView.requestFocus();
		} else {
			userPreference.setU_tel(mPhone);
			getAuthCode();
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
					next();
				} else if (status.equals(JsonTool.STATUS_FAIL)) {
					boolean cancel = false;
					mPhoneView.setError(jsonTool.getMessage());
					focusView = mPhoneView;
					cancel = true;
					if (cancel) {
						focusView.requestFocus();
						authCodeButton.setText("获取验证码");
						authCodeButton.setEnabled(true);
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("服务器错误,错误代码" + statusCode + "，  原因：" + errorResponse);

				boolean cancel = false;
				JsonTool jsonTool = new JsonTool(errorResponse);
				if (jsonTool.getStatus().equals("fail")) {
					mPhoneView.setError(jsonTool.getMessage());
					focusView = mPhoneView;
					cancel = true;
				}
				if (cancel) {
					focusView.requestFocus();
					authCodeButton.setText("获取验证码");
					authCodeButton.setEnabled(true);
				}
			}
		};
		AsyncHttpClientTool.post("api/sms/send", params, responseHandler);
	}

	/**
	 * // 第三方登录
	 */
	private void other_login(String source, String source_id, String avatar) {

		RequestParams params = new RequestParams();
		params.put("source", source);
		params.put("source_id", source_id);
		params.put("avatar", avatar);
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				LogTool.i(statusCode + "===" + response);
				try {
					JsonTool jsonTool = new JsonTool(response);
					JSONObject jsonObject = jsonTool.getJsonObject();

					String status = jsonTool.getStatus();
					String message = jsonTool.getMessage();
					if (status.equals("success")) {
						jsonTool.saveAccess_token();
						// 登录成功后获取用户信息
						getUserInfo(jsonObject.getString("user_id"));
						LogTool.i(message);
					} else {
						LogTool.e(message);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("服务器错误aaa" + errorResponse);
			}
		};
		AsyncHttpClientTool.post("/api/user/thirdparty", params, responseHandler);
	}

	// 获取某个用户信息
	private void getUserInfo(String userid) {

		RequestParams params = new RequestParams();
		params.put(UserTable.U_ID, userid);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				LogTool.i(statusCode + "===" + response);
				JsonTool jsonTool = new JsonTool(response);
				JSONObject jsonObject = jsonTool.getJsonObject();

				String status = jsonTool.getStatus();
				if (status.equals("success")) {
					saveUser(jsonObject);
				} else {
					// LogTool.e(user_info);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("服务器错误" + errorResponse);
			}
		};
		AsyncHttpClientTool.post("api/user/user", params, responseHandler);

	}

	/**
	 * 存储自己的信息
	 */
	private void saveUser(final JSONObject jsonUserObject) {
		// TODO Auto-generated method stub
		userPreference.setUserLogin(true);

		try {
			String userInfo = jsonUserObject.getString("user_info");
			JSONObject userInfoJsonObject = new JSONObject(userInfo);

			String nickname = userInfoJsonObject.getString(UserTable.U_NICKNAME);
			String avatar = userInfoJsonObject.getString(UserTable.AVATAR);
			String create_time = userInfoJsonObject.getString(UserTable.U_CREATE_TIME);
			Date date = DateTimeTools.StringToDate(create_time);

			String userid = userInfoJsonObject.getString(UserTable.U_ID);

			// 取出频道相关信息
			int article_count = jsonUserObject.getInt(UserTable.ARTICLE_COUNT);
			int channel_count = jsonUserObject.getInt(UserTable.CHANNEL_COUNT);

			userPreference.setU_nickname(nickname);
			userPreference.setU_avatar(avatar);
			userPreference.setU_CreatTime(date);
			if (!userid.isEmpty()) {
				userPreference.setU_id(Integer.parseInt(userid));
			} else {
				LogTool.e("存储用户信息的id有误" + userid);
			}

			userPreference.setArticle_count(article_count);
			userPreference.setChannel_count(channel_count);
			userPreference.printUserInfo();
			startActivity(new Intent(getActivity(), MainActivity.class));
			getActivity().finish();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
