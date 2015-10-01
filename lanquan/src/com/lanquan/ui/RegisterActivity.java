package com.lanquan.ui;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseFragmentActivity;
import com.lanquan.config.Constants;
import com.lanquan.customwidget.MyAlertDialog;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.FileSizeUtil;
import com.lanquan.utils.ImageTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

/** 
 * 类描述 ：注册
 * 类名： RegisterActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-10 下午3:57:53  
*/
public class RegisterActivity extends BaseFragmentActivity {

	private FragmentTransaction fragmentTransaction;
	private FragmentManager fragmentManager;
	public static final int CROP = 2;
	public static final int CROP_PICTURE = 3;
	private UserPreference userPreference;

	private Uri lastPhotoUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();

		Fragment fragment = new RegPhoneFragment();
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_right_out);
		fragmentTransaction.replace(R.id.fragment_container, fragment);
		fragmentTransaction.commit();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		vertifyToTerminate();
	}

	/**
	 * 确认终止注册
	 */
	private void vertifyToTerminate() {
		final MyAlertDialog dialog = new MyAlertDialog(RegisterActivity.this);
		dialog.setTitle("提示");
		dialog.setMessage("注册过程中退出，信息将不能保存。是否继续退出？");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				finish();
				BaseApplication.getInstance().getUserPreference().clear();
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
	 * @param filePath
	 */
	public void uploadImage(final String imageUrl) {
		File dir = new File(imageUrl);
		LogTool.e("图片地址" + imageUrl);
		int fileSize = (int) FileSizeUtil.getFileOrFilesSize(imageUrl, 2);
		LogTool.e("文件大小：" + fileSize + "KB");
		final Dialog dialog = showProgressDialog("正在上传头像，请稍后...");
		dialog.setCancelable(false);

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
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					dialog.show();
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					LogTool.i("" + statusCode + response);
					JsonTool jsonTool = new JsonTool(response);
					String status = jsonTool.getStatus();
					if (status.equals(JsonTool.STATUS_SUCCESS)) {
						ToastTool.showLong(RegisterActivity.this, "头像上传成功！");
						JSONObject jsonObject = jsonTool.getJsonObject();
						if (jsonObject != null) {
							try {
								userPreference.setU_avatar(jsonObject.getString("url"));
								RegAccountFragment regAccountFragment = (RegAccountFragment) getSupportFragmentManager().findFragmentByTag("RegAccountFragment");
								regAccountFragment.showHeadImage(Constants.AppliactionServerIP + jsonObject.getString("url"));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else if (status.equals(JsonTool.STATUS_FAIL)) {
						LogTool.e("上传头像fail");
					}

					// 删除本地头像
					ImageTools.deleteImageByPath(imageUrl);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					LogTool.e("头像上传失败！" + errorResponse);
					// 删除本地头像
					ImageTools.deleteImageByPath(imageUrl);
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					super.onFinish();
					dialog.dismiss();
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
}