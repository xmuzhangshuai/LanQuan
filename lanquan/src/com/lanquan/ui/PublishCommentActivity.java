package com.lanquan.ui;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.config.Constants;
import com.lanquan.config.Constants.WeChatConfig;
import com.lanquan.customwidget.MyAlertDialog;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.FileSizeUtil;
import com.lanquan.utils.ImageTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LocationTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import android.app.Dialog;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
	private String channeltitle;
	private String commentcontent;

	private CheckBox wechat;
	private CheckBox wechatcircle;
	private CheckBox weibo;

	/**************用户变量**************/
	public static final int NUM = 200;
	Dialog dialog;
	// 首先在您的Activity中添加如下成员变量
	UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

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
		channeltitle = getIntent().getStringExtra("channeltitle");
		commentcontent = getIntent().getStringExtra("commentcontent");

		findViewById();
		initView();

		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(PublishCommentActivity.this, WeChatConfig.API_KEY, WeChatConfig.SECRIT_KEY);
		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(PublishCommentActivity.this, WeChatConfig.API_KEY, WeChatConfig.SECRIT_KEY);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		publishImageView = (ImageView) findViewById(R.id.publish_image);
		publishEditeEditText = (EditText) findViewById(R.id.publish_content);
		publishBtn = findViewById(R.id.right_btn_bg);
		backBtn = findViewById(R.id.left_btn_bg);
		textCountTextView = (TextView) findViewById(R.id.count);

		wechat = (CheckBox) findViewById(R.id.weinxin);
		wechatcircle = (CheckBox) findViewById(R.id.pengyouquan);
		weibo = (CheckBox) findViewById(R.id.weibo);
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
		opts.inSampleSize = 3;
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
				//				NUM = (int) (NUM - publishEditeEditText.getTextSize());
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

		//给评论内容赋值
		publishEditeEditText.setText(commentcontent);
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
			//发布后分享
			sharecomment();
		}
	}

	/**
	 * 上传图片
	 * @param filePath
	 */
	public void uploadImage(final String imageUrl) {
		String tempPath = Environment.getExternalStorageDirectory() + "/lanquan/image";
		String photoName = "temp" + ".jpg";
		File file = null;
		dialog = showProgressDialog("正在发布...");
		dialog.setCancelable(false);
		dialog.show();

		try {
			file = ImageTools.compressBySizeAndQuality(tempPath, photoName, imageUrl, 400);
		} catch (Exception e) {
			// TODO: handle exception
			dialog.dismiss();
			return;
		}

		LogTool.e("图片大小：" + FileSizeUtil.getAutoFileOrFilesSize(file.getAbsolutePath()));

		if (file != null && file.exists()) {
			RequestParams params = new RequestParams();
			try {
				params.put("userfile", file);
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
					dialog.dismiss();
				}
			};
			AsyncHttpClientTool.post("api/file/upload", params, responseHandler);
		} else {
			dialog.dismiss();
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
				LogTool.i("发布评论" + response);
				JsonTool jsonTool = new JsonTool(response);
				String status = jsonTool.getStatus();
				if (status.equals(JsonTool.STATUS_SUCCESS)) {
					userPreference.setArticle_count(userPreference.getArticle_count() + 1);
					LogTool.i(jsonTool.getMessage());
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
		AsyncHttpClientTool.post(PublishCommentActivity.this, "api/article/create", params, responseHandler);

	}

	/**
	 * 发布评论后分享
	 * @param 
	 */

	public void sharecomment() {
		if (wechat.isChecked()) {
			//设置微信好友分享内容
			WeiXinShareContent weixinContent = new WeiXinShareContent();
			//设置分享文字
			weixinContent.setShareContent("篮圈--篮球人的天堂圣地");
			//设置title
			weixinContent.setTitle("篮圈--篮球人的天堂圣地");
			//设置分享内容跳转URL
			weixinContent.setTargetUrl(Constants.AppliactionServerDomain + "/wap/channel_article/index/" + channel_id);
			//设置分享图片
			weixinContent.setShareImage(new UMImage(PublishCommentActivity.this, R.drawable.ic_launcher));
			mController.setShareMedia(weixinContent);
			//直接分享
			mController.directShare(PublishCommentActivity.this, SHARE_MEDIA.WEIXIN, new SnsPostListener() {
				@Override
				public void onStart() {
					Toast.makeText(PublishCommentActivity.this, "发送开始", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						Toast.makeText(PublishCommentActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(PublishCommentActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
		if (wechatcircle.isChecked()) {
			//设置微信朋友圈分享内容
			CircleShareContent circleMedia = new CircleShareContent();
			circleMedia.setShareContent("篮圈——频道分享");
			//设置title
			circleMedia.setTitle("篮圈--篮球人的天堂圣地");
			circleMedia.setShareContent("篮圈--篮球人的天堂圣地");
			//设置朋友圈title
			circleMedia.setShareImage(new UMImage(PublishCommentActivity.this, R.drawable.ic_launcher));
			//设置分享内容跳转URL
			circleMedia.setTargetUrl(Constants.AppliactionServerDomain + "/wap/channel_article/index/" + channel_id);
			mController.setShareMedia(circleMedia);

			//直接分享
			mController.directShare(PublishCommentActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, new SnsPostListener() {
				@Override
				public void onStart() {
					Toast.makeText(PublishCommentActivity.this, "分享开始", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						Toast.makeText(PublishCommentActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(PublishCommentActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
					}
				}
			});

		}
		if (weibo.isChecked()) {
			//设置新浪SSO handler
			mController.getConfig().setSsoHandler(new SinaSsoHandler());
			//确保未授权
			if (!OauthHelper.isAuthenticated(PublishCommentActivity.this, SHARE_MEDIA.SINA)) {
				mController.doOauthVerify(PublishCommentActivity.this, SHARE_MEDIA.SINA, new UMAuthListener() {
					@Override
					public void onStart(SHARE_MEDIA platform) {
						Toast.makeText(PublishCommentActivity.this, "授权开始", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError(SocializeException e, SHARE_MEDIA platform) {
						Toast.makeText(PublishCommentActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {
						Toast.makeText(PublishCommentActivity.this, "授权完成", Toast.LENGTH_SHORT).show();

					}

					@Override
					public void onCancel(SHARE_MEDIA platform) {
						Toast.makeText(PublishCommentActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
					}
				});
			}

			//获取相关授权信息或者跳转到自定义的分享编辑页面
			//设置分享内容
			mController.setShareContent(channeltitle + Constants.AppliactionServerDomain + "/wap/channel_article/index/" + channel_id);
			//设置分享图片
			mController.setShareMedia(new UMImage(PublishCommentActivity.this, R.drawable.ic_launcher));
			//直接分享
			mController.directShare(PublishCommentActivity.this, SHARE_MEDIA.SINA, new SnsPostListener() {
				@Override
				public void onStart() {
					Toast.makeText(PublishCommentActivity.this, "分享开始", Toast.LENGTH_SHORT).show();
				}
				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						Toast.makeText(PublishCommentActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(PublishCommentActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
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
