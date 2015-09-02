package com.lanquan.ui;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.config.DefaultKeys;
import com.lanquan.customwidget.MyAlertDialog;
import com.lanquan.table.UserTable;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.ImageTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LocationTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

/** 
 * 类描述 ：发布评论
 * 类名： PublishCommentActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-18 下午3:26:04  
*/
public class PublishCommentActivity extends BaseActivity implements OnClickListener {

	private EditText publishEditeEditText;
	private View publishBtn;
	private View backBtn;
	private ImageView publishImageView;
	private TextView textCountTextView;

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private UserPreference userPreference;
	private String imagePath;
	private String detailLocation;// 详细地址
	private String latitude;
	private String longtitude;
	private int channel_id;

	/**************用户变量**************/
	public static final int NUM = 200;
	Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_publish_comment);
		userPreference = BaseApplication.getInstance().getUserPreference();
		imagePath = getIntent().getStringExtra("path");
		mLocationClient = LocationTool.initLocation(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);

		channel_id = getIntent().getIntExtra("channel_id", -1);

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		publishImageView = (ImageView) findViewById(R.id.publish_image);
		publishEditeEditText = (EditText) findViewById(R.id.publish_content);
		publishBtn = findViewById(R.id.right_btn_bg);
		backBtn = findViewById(R.id.left_btn_bg);
		textCountTextView = (TextView) findViewById(R.id.count);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		publishBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);

		/** 
		 * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转 
		 */
		int degree = ImageTools.readPictureDegree(imagePath);
		BitmapFactory.Options opts = new BitmapFactory.Options();// 获取缩略图显示到屏幕上
		opts.inSampleSize = 2;
		Bitmap cbitmap = BitmapFactory.decodeFile(imagePath, opts);

		/** 
		 * 把图片旋转为正的方向 
		 */
		Bitmap newbitmap = ImageTools.rotaingImageView(degree, cbitmap);
		publishImageView.setImageBitmap(newbitmap);

		// 设置编辑框事件
		publishEditeEditText.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int number = NUM - s.length();
				textCountTextView.setText("" + number + "字");
				selectionStart = publishEditeEditText.getSelectionStart();
				selectionEnd = publishEditeEditText.getSelectionEnd();
				if (temp.length() > NUM) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionStart;
					publishEditeEditText.setText(s);
					publishEditeEditText.setSelection(tempSelection);// 设置光标在最后
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		giveUpPublish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();// 关闭定位SDK
			mLocationClient = null;
		}
	}

	/**
	 * 位置监听类
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {
				latitude = "" + location.getLatitude();
				longtitude = "" + location.getLongitude();
				detailLocation = location.getAddrStr();// 详细地址
			}

		}
	}

	/**
	 * 放弃发布
	 */
	private void giveUpPublish() {
		final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
		myAlertDialog.setTitle("提示");
		myAlertDialog.setMessage("放弃发布？  ");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
			}
		};
		myAlertDialog.setPositiveButton("确定", comfirm);
		myAlertDialog.setNegativeButton("取消", cancle);
		myAlertDialog.show();
	}

	/**
	 * 发布
	 */
	private void publish() {
		if (channel_id > 0) {
			uploadImage(imagePath);
		}
	}

	/**
	 * 上传图片
	 * @param filePath
	 */
	public void uploadImage(final String imageUrl) {
		String tempPath = Environment.getExternalStorageDirectory() + "/lanquan/image";
		String photoName = "temp" + ".jpg";
		File file = ImageTools.compressForFile(tempPath, photoName, imageUrl, 400);
		dialog = showProgressDialog("正在发布...");
		dialog.setCancelable(false);

		if (file.exists()) {
			RequestParams params = new RequestParams();
			try {
				params.put("userfile", file);
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
						JSONObject jsonObject = jsonTool.getJsonObject();
						if (jsonObject != null) {
							String url;
							try {
								url = jsonObject.getString("url");
								LogTool.i("url" + url);
								// 图片上传成功后调用评论
								comment(url);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else if (status.equals(JsonTool.STATUS_FAIL)) {
						LogTool.e("上传fail");
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					LogTool.e("图像上传失败！" + errorResponse);
				}
			};
			AsyncHttpClientTool.post("api/file/upload", params, responseHandler);
		} else {
			LogTool.e("本地文件为空");
		}
	}

	/**
	 * 发布评论
	 */
	public void comment(String url) {
		RequestParams params = new RequestParams();
		params.put("message", publishEditeEditText.getText().toString().trim());
		params.put("image_url", url);
		params.put("address", detailLocation);
		params.put("latitude", latitude);
		params.put("longitude", longtitude);
		params.put("access_token", userPreference.getAccess_token());
		params.put("channel_id", channel_id);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

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
					userPreference.setUserLogin(true);
					finish();
				} else if (status.equals(JsonTool.STATUS_FAIL)) {
					LogTool.e(jsonTool.getMessage());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("创建评论错误" + statusCode + errorResponse);
			}
		};
		AsyncHttpClientTool.post("api/article/create", params, responseHandler);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			giveUpPublish();
			break;
		case R.id.right_btn_bg:
			publish();
			break;
		case R.id.publish_image:

			break;
		default:
			break;
		}
	}

}
