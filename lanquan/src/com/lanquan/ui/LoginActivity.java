package com.lanquan.ui;

import java.util.Date;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.location.f;
import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.table.UserTable;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.CommonTools;
import com.lanquan.utils.DateTimeTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.MD5For32;
import com.lanquan.utils.SIMCardInfo;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

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
	private Button loginButton;// 登录
	View focusView = null;

	// List<JsonUser> jsonUsers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		userPreference = BaseApplication.getInstance().getUserPreference();
		findViewById();
		initView();

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
				LogTool.e("服务器错误" + errorResponse);
				showProgress(false);
			}
		};
		AsyncHttpClientTool.post("api/user/login", params, responseHandler);
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
