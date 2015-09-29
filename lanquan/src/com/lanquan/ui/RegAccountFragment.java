package com.lanquan.ui;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseV4Fragment;
import com.lanquan.customwidget.MyMenuDialog;
import com.lanquan.table.UserTable;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.CommonTools;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.ImageTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.MD5For32;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 *
 * 项目名称：quanquan  
 * 类名称：RegAccountFragment  
 * 类描述：账号设置页面，手机号码、验证码和密码
 * @author zhangshuai
 * @date 创建时间：2015-4-7 下午2:51:08 
 *
 */
public class RegAccountFragment extends BaseV4Fragment {

	/*************Views************/
	private View rootView;// 根View
	private View leftImageButton;// 导航栏左侧按钮
	private View rightImageButton;// 导航栏右侧按钮
	private EditText nameView;// 手机号
	private EditText mPasswordView;// 密码
	private EditText mConformPassView;// 确认密码
	private View uploadHeadImageBtn;
	private ImageView headImage;// 头像
	private ImageView cameraImage;
	private TextView leftNavigation;// 步骤
	private Button registerBtn;

	private String mName;
	private String mPassword;
	private String mConformPass;
	private UserPreference userPreference;

	View focusView = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_reg_account, container, false);
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
		nameView = (EditText) rootView.findViewById(R.id.name);
		mPasswordView = (EditText) rootView.findViewById(R.id.password);
		mConformPassView = (EditText) rootView.findViewById(R.id.conform_password);
		registerBtn = (Button) rootView.findViewById(R.id.register);
		leftNavigation = (TextView) getActivity().findViewById(R.id.nav_text);
		uploadHeadImageBtn = (View) rootView.findViewById(R.id.gethead_btn);
		headImage = (ImageView) rootView.findViewById(R.id.headimage);
		cameraImage = (ImageView) rootView.findViewById(R.id.camera_image);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		leftNavigation.setText("注册 2/2");
		rightImageButton.setVisibility(View.GONE);
		headImage.setVisibility(View.GONE);
		cameraImage.setVisibility(View.VISIBLE);
		leftImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getFragmentManager().popBackStack();
			}
		});
		uploadHeadImageBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPicturePicker(getActivity());
			}
		});

		registerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attepmtAccount();
			}
		});

	}

	/**
	 * 验证输入
	 */
	private void attepmtAccount() {
		// 重置错误
		mPasswordView.setError(null);
		mConformPassView.setError(null);
		nameView.setError(null);

		// 存储用户值
		mName = nameView.getText().toString().trim();
		mPassword = mPasswordView.getText().toString();
		mConformPass = mConformPassView.getText().toString();
		boolean cancel = false;

		// 检查昵称
		if (TextUtils.isEmpty(mName)) {
			nameView.setError(getString(R.string.error_field_required));
			focusView = nameView;
			cancel = true;
		}

		// 检查密码
		else if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (!CommonTools.isPassValid(mPassword)) {
			mPasswordView.setError(getString(R.string.error_pattern_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// 检查重复密码
		else if (TextUtils.isEmpty(mConformPass)) {
			mConformPassView.setError(getString(R.string.error_field_required));
			focusView = mConformPassView;
			cancel = true;
		} else if (!mConformPass.equals(mPassword)) {
			mConformPassView.setError(getString(R.string.error_field_conform_pass));
			focusView = mConformPassView;
			cancel = true;
		}

		if (cancel) {
			// 如果错误，则提示错误
			focusView.requestFocus();
		} else {
			userPreference.setU_nickname(mName);
			userPreference.setU_password(MD5For32.GetMD5Code(mPassword));
			next();
		}
	}

	/**
	 * 下一步
	 */
	private void next() {

		RequestParams params = new RequestParams();
		String phone = userPreference.getU_tel();
		String nickName = userPreference.getU_nickname();
		String avatar = userPreference.getU_avatar();
		String vertifyCode = userPreference.getAuthCode();
		String pass = userPreference.getU_password();

		params.put(UserTable.U_TEL, phone);
		params.put(UserTable.U_NICKNAME, nickName);
		params.put(UserTable.AVATAR, avatar);
		params.put("verify_code", vertifyCode);
		params.put(UserTable.U_PASSWORD, pass);

		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage("正在注册");
		dialog.setCancelable(false);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				dialog.show();
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
				JsonTool jsonTool = new JsonTool(response);
				String status = jsonTool.getStatus();
				if (status.equals(JsonTool.STATUS_SUCCESS)) {
					LogTool.i(jsonTool.getMessage());
					jsonTool.saveAccess_token();
					userPreference.setUserLogin(true);
					getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
					getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					getActivity().finish();
				} else if (status.equals(JsonTool.STATUS_FAIL)) {
					LogTool.e(jsonTool.getMessage());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("注册服务器错误" + statusCode + errorResponse);
			}
		};
		AsyncHttpClientTool.post("api/user/register", params, responseHandler);
	}

	/**
	* 显示对话框，从拍照和相册选择图片来源
	* 
	* @param context
	* @param isCrop
	*/
	private void showPicturePicker(Context context) {

		final MyMenuDialog myMenuDialog = new MyMenuDialog(getActivity());
		myMenuDialog.setTitle("图片来源");
		ArrayList<String> list = new ArrayList<String>();
		list.add("拍照");
		list.add("相册");
		myMenuDialog.setMenuList(list);
		OnItemClickListener listener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent;
				switch (position) {
				case 0:// 如果是拍照
					myMenuDialog.dismiss();
					intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getNewImagePath())));
					getActivity().startActivityForResult(intent, 2);
					break;
				case 1:// 从相册选取
					myMenuDialog.dismiss();
					intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
					getActivity().startActivityForResult(intent, 1);
					break;

				default:
					break;
				}
			}
		};
		myMenuDialog.setListItemClickListener(listener);
		myMenuDialog.show();
	}

	//获得新的路径
	private String getNewImagePath() {
		// 删除上一次截图的临时文件
		String path = null;
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences("temp", Context.MODE_PRIVATE);
		ImageTools.deleteImageByPath(sharedPreferences.getString("tempPath", ""));

		// 保存本次截图临时文件名字
		File file = new File(Environment.getExternalStorageDirectory() + "/lanquan");
		if (!file.exists()) {
			file.mkdirs();
		}

		path = file.getAbsolutePath() + "/" + String.valueOf(System.currentTimeMillis()) + ".jpg";
		Editor editor = sharedPreferences.edit();
		editor.putString("tempPath", path);
		editor.commit();
		return path;
	}

	

	/**
	 * @param imageUrl
	 * 显示头像
	 */
	public void showHeadImage(String imageUrl) {
		ImageLoader.getInstance().displayImage(imageUrl, headImage, ImageLoaderTool.getCircleHeadImageOptions());
		headImage.setVisibility(View.VISIBLE);
		cameraImage.setVisibility(View.GONE);
	}

}
