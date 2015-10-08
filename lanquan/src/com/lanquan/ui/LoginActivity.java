package com.lanquan.ui;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.config.Constants.QQConfig;
import com.lanquan.config.Constants.WeChatConfig;
import com.lanquan.config.Constants.WeiboConfig;
import com.lanquan.table.UserTable;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.CommonTools;
import com.lanquan.utils.DateTimeTools;
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
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * 类名称：LoginActivity 
 * 类描述：登录页面 
 * 创建人： 张帅 
 * 创建时间：2014-7-4 上午9:34:33
 * 
 */
public class LoginActivity extends BaseActivity {

	// UI references.
	private EditText mPhoneView;// 手机号
	private EditText mPasswordView;// 密码
	private View mProgressView;// 缓冲
	private TextView topNavigation;// 导航栏文字
	private View leftImageButton;// 导航栏左侧按钮
	private UserPreference userPreference;
	private TextView forgetPassword;// 忘记密码
	private TextView registNow;// 立即注册
	private ImageView login_wechat;//微信第三方登录
	private ImageView login_qq;//qq第三方登录
	private ImageView login_weibo;//微薄第三方登录
	private Button loginButton;// 登录
	View focusView = null;
	UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
	// List<JsonUser> jsonUsers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();

		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(LoginActivity.this, WeChatConfig.API_KEY, WeChatConfig.SECRIT_KEY);
		wxHandler.addToSocialSDK();
		//qq平台
		//参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(LoginActivity.this, QQConfig.API_KEY, QQConfig.SECRIT_KEY);
		qqSsoHandler.addToSocialSDK();
		//设置新浪SSO handler
		mController.getConfig().setSsoHandler(new SinaSsoHandler());

	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mPhoneView = (EditText) findViewById(R.id.phone);
		mPasswordView = (EditText) findViewById(R.id.password);
		mProgressView = findViewById(R.id.login_status);
		topNavigation = (TextView) findViewById(R.id.nav_text);
		leftImageButton = (View) findViewById(R.id.left_btn_bg);
		forgetPassword = (TextView) findViewById(R.id.forget_password);
		loginButton = (Button) findViewById(R.id.login);
		registNow = (TextView) findViewById(R.id.register);
		login_wechat = (ImageView) findViewById(R.id.loginwechat);
		login_qq = (ImageView) findViewById(R.id.loginqq);
		login_weibo = (ImageView) findViewById(R.id.loginweibo);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		String loginedPhone = userPreference.getU_tel();

		if (!TextUtils.isEmpty(loginedPhone)) {
			mPhoneView.setText(loginedPhone);
		} else {
			SIMCardInfo siminfo = new SIMCardInfo(LoginActivity.this);
			String number = siminfo.getNativePhoneNumber();
			mPhoneView.setText(number);
		}

		topNavigation.setText("登录");
		leftImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attemptLogin();
			}
		});

		forgetPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this, ForgetPassActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		registNow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		login_wechat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToastTool.showShort(LoginActivity.this, "微信第三方登录");
				mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
					@Override
					public void onStart(SHARE_MEDIA platform) {
						Toast.makeText(LoginActivity.this, "授权开始", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError(SocializeException e, SHARE_MEDIA platform) {
						Toast.makeText(LoginActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {
						Toast.makeText(LoginActivity.this, "授权完成", Toast.LENGTH_SHORT).show();
						//获取相关授权信息
						mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, new UMDataListener() {
							@Override
							public void onStart() {
								Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
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
									String avatar = info.get("headimgurl").toString();
									String nickname = info.get("nickname").toString();
									other_login("wx", WeChatConfig.API_KEY, avatar, nickname);
									Log.d("TestData", sb.toString());
								} else {
									Log.d("TestData", "发生错误：" + status);
								}
							}
						});
					}

					@Override
					public void onCancel(SHARE_MEDIA platform) {
						Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		login_qq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToastTool.showShort(LoginActivity.this, "qq第三方登录");
				mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.QQ, new UMAuthListener() {
					@Override
					public void onStart(SHARE_MEDIA platform) {
						Toast.makeText(LoginActivity.this, "授权开始", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError(SocializeException e, SHARE_MEDIA platform) {
						Toast.makeText(LoginActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {
						Toast.makeText(LoginActivity.this, "授权完成", Toast.LENGTH_SHORT).show();
						//获取相关授权信息
						mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, new UMDataListener() {
							@Override
							public void onStart() {
								Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
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
									String nickname = info.get("screen_name").toString();
									other_login("qq", QQConfig.API_KEY, avatar, nickname);

									Log.d("TestData", sb.toString());
								} else {
									Log.d("TestData", "发生错误：" + status);
								}
							}
						});
					}

					@Override
					public void onCancel(SHARE_MEDIA platform) {
						Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		login_weibo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToastTool.showShort(getApplicationContext(), "微博第三方登录");
				mController.doOauthVerify(LoginActivity.this, SHARE_MEDIA.SINA, new UMAuthListener() {
					@Override
					public void onError(SocializeException e, SHARE_MEDIA platform) {
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {
						if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
							Toast.makeText(LoginActivity.this, "授权成功.", Toast.LENGTH_SHORT).show();
							mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, new UMDataListener() {
								@Override
								public void onStart() {
									Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
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
										String nickname = info.get("screen_name").toString();
										other_login("weibo", WeiboConfig.API_KEY, avatar, nickname);
										Log.d("TestData", sb.toString());
									} else {
										Log.d("TestData", "发生错误：" + status);
									}
								}
							});
						} else {
							Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/**使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {

		// Reset errors.
		mPhoneView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String phone = mPhoneView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false;

		// Check for a valid password, if the user entered one.
		if (TextUtils.isEmpty(password)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (!CommonTools.isPassValid(password)) {
			mPasswordView.setError(getString(R.string.error_pattern_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		else if (TextUtils.isEmpty(phone)) {
			mPhoneView.setError(getString(R.string.error_field_required));
			focusView = mPhoneView;
			cancel = true;
		} else if (!CommonTools.isMobileNO(phone)) {
			mPhoneView.setError(getString(R.string.error_invalid_phone));
			focusView = mPhoneView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			login(phone, MD5For32.GetMD5Code(password));

			// 同时存入手机号和密码，用于记住密码登录使用
			userPreference.setU_tel(phone);
			userPreference.setU_password(MD5For32.GetMD5Code(password));
		}

	}

	// 登录
	private void login(String tel, final String pass) {

		RequestParams params = new RequestParams();
		params.put(UserTable.U_TEL, tel);
		params.put(UserTable.U_PASSWORD, pass);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				showProgress(true);
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
						showProgress(false);
						LogTool.e(message);
						mPasswordView.setError(message);
						focusView = mPasswordView;
						focusView.requestFocus();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				boolean cancel = false;
				LogTool.e("服务器错误" + errorResponse);
				if (statusCode != 0) {
					JsonTool jsonTool = new JsonTool(errorResponse);
					if (jsonTool.getStatus().equals("fail")) {
						mPhoneView.setError(jsonTool.getMessage());
						focusView = mPhoneView;
						cancel = true;
					}
				}
				if (cancel)
					focusView.requestFocus();
				showProgress(false);
			}
		};
		AsyncHttpClientTool.post("api/user/login", params, responseHandler);
	}

	// 第三方登录
	private void other_login(String source, String source_id, String avatar, String nickname) {

		RequestParams params = new RequestParams();
		params.put("source", source);
		params.put("source_id", source_id);
		params.put("avatar", avatar);
		params.put("nickname", nickname);
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				showProgress(true);
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
						showProgress(false);
						LogTool.e(message);
						mPasswordView.setError(message);
						focusView = mPasswordView;
						focusView.requestFocus();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("服务器错误aaa" + errorResponse);
				showProgress(false);
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
				showProgress(false);
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
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			LoginActivity.this.finish();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (show) {
			// 隐藏软键盘
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

}
