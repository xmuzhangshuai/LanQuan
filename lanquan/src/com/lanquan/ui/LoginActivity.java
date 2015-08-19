package com.lanquan.ui;

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

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.jsonobject.JsonUser;
import com.lanquan.utils.CommonTools;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.MD5For32;
import com.lanquan.utils.SIMCardInfo;
import com.lanquan.utils.UserPreference;

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
		View focusView = null;

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
			showProgress(true);
			login(phone, MD5For32.GetMD5Code(password));
		}

	}

	// 登录
	private void login(String tel, final String pass) {

		// RequestParams params = new RequestParams();
		// params.put(UserTable.U_TEL, tel);
		// params.put(UserTable.U_PASSWORD, pass);
		//
		// TextHttpResponseHandler responseHandler = new
		// TextHttpResponseHandler() {
		//
		// @Override
		// public void onStart() {
		// // TODO Auto-generated method stub
		// super.onStart();
		// showProgress(true);
		// }
		//
		// @Override
		// public void onFinish() {
		// // TODO Auto-generated method stub
		// showProgress(false);
		// super.onFinish();
		// }
		//
		// @Override
		// public void onSuccess(int statusCode, Header[] headers, String
		// response) {
		// // TODO Auto-generated method stub
		// if (statusCode == 200) {
		// if (!response.isEmpty()) {
		// if (response.equals("-1")) {
		// mPasswordView.setError("电话或密码错误！");
		// mPasswordView.requestFocus();
		// showProgress(false);
		// } else {
		// JsonUser user = FastJsonTool.getObject(response, JsonUser.class);
		// if (user != null) {
		// saveUser(user);// 更新用户信息
		// loginHuanxin("" + user.getU_id(), user.getU_password());
		// } else {
		// LogTool.e("登录返回出错,user为空" + response);
		// }
		// }
		// }
		// }
		// }
		//
		// @Override
		// public void onFailure(int statusCode, Header[] headers, String
		// errorResponse, Throwable e) {
		// // TODO Auto-generated method stub
		// LogTool.e("服务器错误" + errorResponse);
		// }
		// };
		// AsyncHttpClientTool.post("user/login", params, responseHandler);
//		userPreference.setUserLogin(true);
		startActivity(new Intent(LoginActivity.this, MainActivity.class));
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	/**
	 * 存储自己的信息
	 */
	private void saveUser(final JsonUser user) {
		// TODO Auto-generated method stub
		// LogTool.i("ServerUtil", "存储自身信息");
		// userPreference.setU_id(user.getU_id());
		// userPreference.setU_nickname(user.getU_nickname());
		// userPreference.setU_password(user.getU_password());
		// userPreference.setU_gender(user.getU_gender());
		// userPreference.setU_tel(user.getU_tel());
		// userPreference.setU_email(user.getU_email());
		// userPreference.setU_birthday(user.getU_birthday());
		// userPreference.setU_age(user.getU_age());
		// userPreference.setU_large_avatar(user.getU_large_avatar());
		// userPreference.setU_small_avatar(user.getU_small_avatar());
		// userPreference.setU_identity(user.getU_identity());
		// userPreference.setU_love_state(user.getU_love_state());
		// userPreference.setU_provinceid(user.getU_provinceid());
		// userPreference.setU_cityid(user.getU_cityid());
		// userPreference.setU_schoolid(user.getU_schoolid());
		// userPreference.setU_interests(user.getU_interest_items());
		// userPreference.setU_skills(user.getU_skill_items());
		// userPreference.setU_industry(user.getU_industry_item());
		// userPreference.setU_introduce(user.getU_introduce());
		// userPreference.setU_student_number(user.getU_student_number());
		// userPreference.setU_student_pass(user.getU_stundet_pass());
		// userPreference.setMyConcerned_count(user.getU_my_concern_count());
		// userPreference.setMyFollower_count(user.getU_my_follower_count());
		// userPreference.setMyFavor_count(user.getU_my_favor_count());
		// userPreference.setNewMyFollower_count(user.getU_new_follower_count());
		// userPreference.setUserLogin(true);
	}

	/**
	 * 登录环信
	 */
	// private void loginHuanxin(String userName, String password) {
	// EMChatManager.getInstance().login(userName, password, new EMCallBack()
	// {// 回调
	// @Override
	// public void onSuccess() {
	// LogTool.i("环信", "登陆环信成功！");
	// Intent intent = new Intent(LoginActivity.this, MainActivity.class);
	// startActivity(intent);
	// overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	// showProgress(false);
	// }
	//
	// @Override
	// public void onProgress(int progress, String status) {
	//
	// }
	//
	// @Override
	// public void onError(int code, String message) {
	// LogTool.e("环信", "登陆聊天服务器失败！");
	// showProgress(false);
	// }
	// });
	// }

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
