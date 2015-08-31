package com.lanquan.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
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

import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseFragmentActivity;
import com.lanquan.customwidget.MyMenuDialog;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.ImageTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/** 
 * 类描述 ：修改资料
 * 类名： ModifyDataActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-14 上午9:57:13  
*/
public class ModifyDataActivity extends BaseFragmentActivity implements OnClickListener {
	private TextView leftTextView;// 导航栏左侧文字
	private View leftButton;// 导航栏左侧按钮
	private ImageView headImageView;// 头像
	private ImageView cameraImage;
	private View changeHeadImageBtn;

	/***昵称***/
	private View nicknameView;
	private TextView nickNameTextView;

	/***修改密码***/
	private View passView;
	private TextView passTextView;

	/***更换手机号***/
	private View phoneView;
	private TextView phoneTextView;

	private UserPreference userPreference;

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
		imageLoader.displayImage(userPreference.getU_avatar(), headImageView, ImageLoaderTool.getCircleHeadImageOptions());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {

			case RegisterActivity.CROP:
				Uri uri = null;
				if (data != null) {
					uri = data.getData();
					LogTool.i("Data");
				} else {
					LogTool.i("File");
					String fileName = getSharedPreferences("temp", Context.MODE_WORLD_WRITEABLE).getString("tempName", "");
					uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileName));
				}
				cropImage(uri, 500, 500, RegisterActivity.CROP_PICTURE);
				break;

			case RegisterActivity.CROP_PICTURE:
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
	 * 上传头像
	 * @param filePath
	 */
	public void uploadImage(final String imageUrl) {
		File dir = new File(imageUrl);
		if (dir.exists()) {
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
								userPreference.setU_avatar(jsonObject.getString("url"));
								imageLoader.displayImage(userPreference.getU_avatar(), headImageView, ImageLoaderTool.getCircleHeadImageOptions());
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
			};
			AsyncHttpClientTool.post("api/file/upload", params, responseHandler);
		} else {
			LogTool.e("本地文件为空");
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
			// 类型码
			int REQUEST_CODE;

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:// 如果是拍照
					myMenuDialog.dismiss();
					Uri imageUri = null;
					String fileName = null;
					Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					REQUEST_CODE = RegisterActivity.CROP;

					// 删除上一次截图的临时文件
					SharedPreferences sharedPreferences = getSharedPreferences("temp", Context.MODE_WORLD_WRITEABLE);
					ImageTools.deletePhotoAtPathAndName(Environment.getExternalStorageDirectory().getAbsolutePath(),
							sharedPreferences.getString("tempName", ""));

					// 保存本次截图临时文件名字
					fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
					Editor editor = sharedPreferences.edit();
					editor.putString("tempName", fileName);
					editor.commit();
					imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileName));
					// 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
					openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(openCameraIntent, REQUEST_CODE);
					break;
				case 1:// 从相册选取
					myMenuDialog.dismiss();
					Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
					REQUEST_CODE = RegisterActivity.CROP;
					openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
					startActivityForResult(openAlbumIntent, REQUEST_CODE);
					break;

				default:
					break;
				}
			}
		};
		myMenuDialog.setListItemClickListener(listener);

		myMenuDialog.show();
	}

	// 截取图片
	public void cropImage(Uri uri, int outputX, int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", true); // intent.putExtra("return-data",
												// false);
		startActivityForResult(intent, requestCode);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		// intent.setDataAndType(photoUri, "image/*");
		// intent.putExtra("scale", true);
		// intent.putExtra("scaleUpIfNeeded", true);
	}
}
