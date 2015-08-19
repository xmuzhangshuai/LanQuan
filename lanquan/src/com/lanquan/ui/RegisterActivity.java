package com.lanquan.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseFragmentActivity;
import com.lanquan.customwidget.MyAlertDialog;
import com.lanquan.utils.ImageTools;
import com.lanquan.utils.LogTool;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

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
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {

			case CROP:
				Uri uri = null;
				if (data != null) {
					uri = data.getData();
					LogTool.i("Data");
				} else {
					LogTool.i("File");
					String fileName = getSharedPreferences("temp", Context.MODE_WORLD_WRITEABLE).getString("tempName", "");
					uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileName));
				}
				RegAccountFragment regAccountFragment = (RegAccountFragment) getSupportFragmentManager().findFragmentByTag("RegAccountFragment");
				regAccountFragment.cropImage(uri, 500, 500, CROP_PICTURE);
				break;

			case CROP_PICTURE:
				Bitmap photo = null;
				Uri photoUri = data.getData();
				if (photoUri != null) {
					// photo = BitmapFactory.decodeFile(photoUri.getPath());
					uploadImage(photoUri.getPath());
					LogTool.e("裁剪返回" + "photoUri");
				}
				if (photo == null) {
					Bundle extra = data.getExtras();
					if (extra != null) {
						photo = (Bitmap) extra.get("data");
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
						String fileName;
						// 删除上一次截图的临时文件
						SharedPreferences sharedPreferences = getSharedPreferences("temp", Context.MODE_WORLD_WRITEABLE);
						ImageTools.deletePhotoAtPathAndName(Environment.getExternalStorageDirectory().getAbsolutePath(),
								sharedPreferences.getString("crop_tempName", ""));

						// 保存本次截图临时文件名字
						fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
						Editor editor = sharedPreferences.edit();
						editor.putString("crop_tempName", fileName);
						editor.commit();

						uploadImage(ImageTools.savePhotoToSDCard(photo, Environment.getExternalStorageDirectory().getAbsolutePath(), fileName, 100)
								.getAbsolutePath());
						LogTool.e("裁剪返回" + "Bitmap");
					}
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 上传头像
	 * @param filePath
	 */
	public void uploadImage(String imageUrl) {
		// final Bitmap largeAvatar = BitmapFactory.decodeFile(filePath);
		// if (largeAvatar != null) {
		//
		// RequestParams params = new RequestParams();
		// int userId = userPreference.getU_id();
		// if (userId > -1) {
		// params.put(UserTable.U_ID, String.valueOf(userId));
		// try {
		// params.put(UserTable.U_LARGE_AVATAR, picFile);
		// } catch (FileNotFoundException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// TextHttpResponseHandler responseHandler = new
		// TextHttpResponseHandler() {
		// @Override
		// public void onSuccess(int statusCode, Header[] headers, String
		// response) {
		// // TODO Auto-generated method stub
		// if (statusCode == 200) {
		// ToastTool.showLong(ModifyDataActivity.this, "头像上传成功！");
		// largeAvatar.recycle();
		// // 删除本地头像
		// ImageTools.deleteImageByPath(filePath);
		// // 获取新头像地址
		// Map<String, String> map = FastJsonTool.getObject(response,
		// Map.class);
		// if (map != null) {
		// userPreference.setU_large_avatar(map.get(UserTable.U_LARGE_AVATAR));
		// userPreference.setU_small_avatar(map.get(UserTable.U_SMALL_AVATAR));
		// // 显示头像
		// ImageLoader.getInstance().displayImage(AsyncHttpClientTool.getAbsoluteUrl(map.get(UserTable.U_SMALL_AVATAR)),
		// headImage,
		// ImageLoaderTool.getHeadImageOptions(3));
		// } else {
		// LogTool.e("上传服务器出错" + response);
		// }
		// }
		// }
		//
		// @Override
		// public void onFailure(int statusCode, Header[] headers, String
		// errorResponse, Throwable e) {
		// // TODO Auto-generated method stub
		// LogTool.e("头像上传失败！");
		// // 删除本地头像
		// ImageTools.deleteImageByPath(filePath);
		// }
		// };
		// AsyncHttpClientTool.post("user/uploadHeadImg", params,
		// responseHandler);
		// }
		// } else {
		// ImageTools.deleteImageByPath(filePath);
		// }
		RegAccountFragment regAccountFragment = (RegAccountFragment) getSupportFragmentManager().findFragmentByTag("RegAccountFragment");
		regAccountFragment.showHeadImage(imageUrl);
	}
}