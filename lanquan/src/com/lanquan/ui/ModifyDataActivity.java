package com.lanquan.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseFragmentActivity;
import com.lanquan.config.Constants;
import com.lanquan.customwidget.MyMenuDialog;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.FileSizeUtil;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.ImageTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 类描述 ：修改资料 类名： ModifyDataActivity.java Copyright: Copyright (c)2015 Company:
 * zhangshuai
 * 
 * @author: zhangshuai
 * @version: 1.0 创建时间: 2015-8-14 上午9:57:13
 */
public class ModifyDataActivity extends BaseFragmentActivity implements OnClickListener {
	private TextView leftTextView;// 导航栏左侧文字
	private View leftButton;// 导航栏左侧按钮
	private ImageView headImageView;// 头像
	private ImageView cameraImage;
	private View changeHeadImageBtn;

	/*** 昵称 ***/
	private View nicknameView;
	private TextView nickNameTextView;

	/*** 修改密码 ***/
	private View passView;
	private TextView passTextView;

	/*** 更换手机号 ***/
	private View phoneView;
	private TextView phoneTextView;

	private UserPreference userPreference;
	private Uri lastPhotoUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_modify_data);

		userPreference = BaseApplication.getInstance().getUserPreference();
		findViewById();
		initView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		// 如果是直接从相册获取 
		case 1:
			if (data != null) {
				startPhotoCrop(data.getData());
			}
			break;
		// 如果是调用相机拍照时 
		case 2:
			if (new File(getImagePath()).exists()) {
				startPhotoCrop(Uri.fromFile(new File(getImagePath())));
			}
			break;
		// 取得裁剪后的图片并上传 
		case 3:
			uploadImage(lastPhotoUri.getPath());
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		leftTextView = (TextView) findViewById(R.id.nav_text);
		leftButton = findViewById(R.id.left_btn_bg);
		headImageView = (ImageView) findViewById(R.id.headimage);
		cameraImage = (ImageView) findViewById(R.id.camera_image);
		nicknameView = findViewById(R.id.modify_nickname);
		nickNameTextView = (TextView) findViewById(R.id.modify_nickname_text);
		passView = findViewById(R.id.modify_pass);
		passTextView = (TextView) findViewById(R.id.modify_pass_text);
		phoneView = findViewById(R.id.modify_phone);
		phoneTextView = (TextView) findViewById(R.id.modify_phone_text);
		changeHeadImageBtn = findViewById(R.id.change_headImageBtn);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		leftTextView.setText("编辑资料");
		leftButton.setOnClickListener(this);
		changeHeadImageBtn.setOnClickListener(this);
		nicknameView.setOnClickListener(this);
		passView.setOnClickListener(this);
		phoneView.setOnClickListener(this);
		imageLoader.displayImage(userPreference.getU_avatar(), headImageView, MainPersonalFragment.getCircleHeadImageOptions());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			break;
		case R.id.change_headImageBtn:
			showPicturePicker(ModifyDataActivity.this);
			break;
		case R.id.modify_nickname:
			intent = new Intent(ModifyDataActivity.this, ModifyNameActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.modify_pass:
			intent = new Intent(ModifyDataActivity.this, ModifyPassActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.modify_phone:
			intent = new Intent(ModifyDataActivity.this, ModifyPhoneActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;

		default:
			break;
		}
	}

	/**
	* 显示对话框，从拍照和相册选择图片来源
	* 
	* @param context
	* @param isCrop
	*/
	private void showPicturePicker(Context context) {

		final MyMenuDialog myMenuDialog = new MyMenuDialog(ModifyDataActivity.this);
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
					startActivityForResult(intent, 2);
					break;
				case 1:// 从相册选取
					myMenuDialog.dismiss();
					intent = new Intent(Intent.ACTION_PICK, null);
					intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
					startActivityForResult(intent, 1);
					break;

				default:
					break;
				}
			}
		};
		myMenuDialog.setListItemClickListener(listener);
		myMenuDialog.show();
	}

	/** 
	 * 裁剪图片方法实现 
	 * @param uri 
	 */
	public void startPhotoCrop(Uri uri) {
		lastPhotoUri = Uri.fromFile(new File(getLastImagePath()));

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		//下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪 
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 800);
		intent.putExtra("outputY", 800);
		intent.putExtra("noFaceDetection", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, lastPhotoUri);
		intent.putExtra("return-data", false);
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		startActivityForResult(intent, 3);
	}

	//获得新的路径
	private String getNewImagePath() {
		// 删除上一次截图的临时文件
		String path = null;
		SharedPreferences sharedPreferences = getSharedPreferences("temp", Context.MODE_PRIVATE);
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

	//获得最终截图路径
	private String getLastImagePath() {
		String path = null;
		SharedPreferences sharedPreferences = getSharedPreferences("temp", Context.MODE_PRIVATE);

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

	//获得路径
	private String getImagePath() {
		SharedPreferences sharedPreferences = getSharedPreferences("temp", Context.MODE_PRIVATE);
		return sharedPreferences.getString("tempPath", "");
	}

	/**
	 * 上传头像
	 * 
	 * @param filePath
	 */
	public void uploadImage(final String imageUrl) {
		File dir = new File(imageUrl);
		LogTool.e("图片地址" + imageUrl);
		int fileSize = (int) FileSizeUtil.getFileOrFilesSize(imageUrl, 2);
		LogTool.e("文件大小：" + fileSize + "KB");

		if (dir.exists() && !imageUrl.equals("/") && fileSize < 500 && fileSize > 0) {
			RequestParams params = new RequestParams();
			try {
				params.put("userfile", dir);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					LogTool.i("" + statusCode + response);
					JsonTool jsonTool = new JsonTool(response);
					String status = jsonTool.getStatus();
					if (status.equals(JsonTool.STATUS_SUCCESS)) {
						ToastTool.showLong(ModifyDataActivity.this, "头像上传成功！");
						JSONObject jsonObject = jsonTool.getJsonObject();
						if (jsonObject != null) {
							try {
								userPreference.setU_avatar(Constants.AppliactionServerIP + jsonObject.getString("url"));
								imageLoader.displayImage(userPreference.getU_avatar(), headImageView, ImageLoaderTool.getCircleHeadImageOptions());
								modifyHeadImage(jsonObject.getString("url"));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else if (status.equals(JsonTool.STATUS_FAIL)) {
						LogTool.e("上传头像fail");
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					LogTool.e("头像上传失败！" + errorResponse);
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					super.onFinish();
					// 删除本地头像
					ImageTools.deleteImageByPath(imageUrl);
					lastPhotoUri = null;
				}
			};
			AsyncHttpClientTool.post("api/file/upload", params, responseHandler);
		} else {
			LogTool.e("本地文件为空");
		}
	}

	/**
	 * 修改头像
	 */
	private void modifyHeadImage(String url) {
		RequestParams params = new RequestParams();
		params.put("avatar", url);
		params.put("access_token", userPreference.getAccess_token());
		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				LogTool.i("" + statusCode + response);
				JsonTool jsonTool = new JsonTool(response);
				String status = jsonTool.getStatus();
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
				LogTool.e("修改失败！" + errorResponse);
			}
		};
		AsyncHttpClientTool.post("api/user/modifyAvatar", params, responseHandler);
	}
}
