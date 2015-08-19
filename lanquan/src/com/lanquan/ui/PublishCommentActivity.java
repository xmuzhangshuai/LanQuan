package com.lanquan.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.customwidget.MyAlertDialog;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.ImageTools;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.UserPreference;
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

	private UserPreference userPreference;
	private String imagePath;

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
		// String content = publishEditeEditText.getText().toString().trim();
		// List<File> photoFiles = new ArrayList<File>();
		// // File[] photoFiles = new File[] {};
		// for (int i = 0; i < 6; i++) {
		// if (!TextUtils.isEmpty(photoUris[i])) {
		// LogTool.i("地址", photoUris[i]);
		// photoFiles.add(new File(photoUris[i]));
		// }
		// }
		//
		// dialog = showProgressDialog("正在发布，请稍后...");
		// dialog.setCancelable(false);
		//
		// RequestParams params = new RequestParams();
		// TextHttpResponseHandler responseHandler = new
		// TextHttpResponseHandler("utf-8") {
		//
		// @Override
		// public void onSuccess(int statusCode, Header[] headers, String
		// response) {
		// // TODO Auto-generated method stub
		// if (statusCode == 200) {
		// if (response.equals("1")) {
		// ToastTool.showShort(PublishCommentActivity.this, "发布成功！");
		// finish();
		// overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_right_out);
		// } else {
		// LogTool.e("服务器处理失败" + response);
		// }
		//
		// }
		// }
		//
		// @Override
		// public void onFailure(int statusCode, Header[] headers, String
		// errorResponse, Throwable e) {
		// // TODO Auto-generated method stub
		// ToastTool.showShort(PublishCommentActivity.this, "发布失败！");
		// LogTool.e("发布时失败！" + statusCode + "\n");
		// }
		//
		// @Override
		// public void onFinish() {
		// // TODO Auto-generated method stub
		// super.onFinish();
		// if (dialog != null) {
		// dialog.dismiss();
		// }
		// }
		//
		// @Override
		// public void onCancel() {
		// // TODO Auto-generated method stub
		// super.onCancel();
		// if (dialog != null) {
		// dialog.dismiss();
		// }
		// }
		//
		// };
		// params.put(PostTable.P_USERID, userPreference.getU_id());
		// params.put(PostTable.P_CONTENT, content);
		// for (int i = 0; i < photoFiles.size(); i++) {
		// File file = photoFiles.get(i);
		// if (file != null && file.exists()) {
		// try {
		// params.put(PostTable.P_BIG_PHOTO + i, file);
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }
		// if (!content.isEmpty() || photoFiles.size() > 0) {
		// if (photoFiles.size() > 0) {
		// AsyncHttpClientTool.post("post/add", params, responseHandler);
		// } else {
		// AsyncHttpClientTool.post("post/add_no_pic", params, responseHandler);
		// }
		//
		// } else {
		// ToastTool.showLong(PublishCommentActivity.this, "请填写内容或图片");
		// dialog.dismiss();
		// }

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
