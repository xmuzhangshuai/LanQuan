package com.lanquan.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.config.Constants;
import com.lanquan.config.Constants.WeChatConfig;
import com.lanquan.utils.FileSizeUtil;
import com.lanquan.utils.ImageTools;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.UserPreference;
import com.lanquan.zxingqrcode.EncodingHandler;
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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 类描述 ：分享频道二维码 类名： ShareQrCodeActivity.java 
 * Copyright: Copyright (c)2015
 * Company: zhangshuai
 * 
 * @author: zhangshuai
 * @version: 1.0 创建时间: 2015-8-18 下午10:08:26
 */
public class ShareQrCodeActivity extends BaseActivity implements android.view.View.OnClickListener {

	private ImageView qrImageView;
	private TextView weiboView;
	private TextView wechatView;
	private TextView friendView;
	private TextView photoView;
	private UserPreference userPreference;
	Bitmap qrCodeBitmap = null;
	private View left_btn_bg;// 导航条左边按钮
	private TextView topNavText;// 导航条文字
	private ImageView channelIcon;
	private TextView channelTitleTextView;
	private TextView channelDetailTextView;
	private String imagePath;
	private String channelTitle;
	private String channelDetail;
	private int channelId;
	// 首先在您的Activity中添加如下成员变量
	UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	private File shareImageFile;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_share_qrcode);

		userPreference = BaseApplication.getInstance().getUserPreference();

		findViewById();
		initView();
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(ShareQrCodeActivity.this, WeChatConfig.API_KEY, WeChatConfig.SECRIT_KEY);
		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(ShareQrCodeActivity.this, WeChatConfig.API_KEY, WeChatConfig.SECRIT_KEY);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		qrImageView = (ImageView) findViewById(R.id.qrcode);
		weiboView = (TextView) findViewById(R.id.weibo);
		wechatView = (TextView) findViewById(R.id.wechat);
		friendView = (TextView) findViewById(R.id.friend);
		photoView = (TextView) findViewById(R.id.photo);
		left_btn_bg = findViewById(R.id.left_btn_bg);
		topNavText = (TextView) findViewById(R.id.nav_text);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavText.setText("分享二维码");
		// 获取频道名称以及频道简介
		channelTitle = getIntent().getStringExtra("channelName");
		channelDetail = getIntent().getStringExtra("channelInfo");
		channelId = getIntent().getIntExtra("channelid", 0);
		//		Bitmap top = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_top);
		Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.lanquan_checked);
		//		Bitmap content = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_content);
		//		Bitmap blue_content = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_content_blue);
		//		Bitmap middle = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_middle);

		//		Bitmap bottom = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_card_bleow);

		Bitmap qrcodebg = BitmapFactory.decodeResource(getResources(), R.drawable.sharebarcode);
		// qrCodeBitmap = EncodingHandler.createQRCode("帅哥就是帅！", 800);
		//		qrCodeBitmap = EncodingHandler.createChannelCode(top, icon, content, blue_content, middle, bottom, channelTitle, channelDetail, "帅哥就是帅");
		qrCodeBitmap = EncodingHandler.createChannelCode(qrcodebg, icon, channelTitle, channelDetail,
				Constants.AppliactionServerDomain + "/wap/channel_article/index/" + channelId);
		LogTool.i("宽度" + qrCodeBitmap.getWidth() + "  高度：" + qrCodeBitmap.getHeight());
		qrImageView.setImageBitmap(qrCodeBitmap);
		//图片压缩
		LogTool.e("压缩前的图片大小" + ImageTools.getBitmapsize(qrCodeBitmap));
		shareImageFile = ImageTools.compressBySizeAndQuality(Environment.getExternalStorageDirectory().getAbsolutePath(), "sharecode.jpeg", qrCodeBitmap, 100);
		LogTool.e("压缩后的图片大小" + FileSizeUtil.getAutoFileOrFilesSize(shareImageFile.getPath()));

		left_btn_bg.setOnClickListener(this);
		weiboView.setOnClickListener(this);
		wechatView.setOnClickListener(this);
		friendView.setOnClickListener(this);
		photoView.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (qrCodeBitmap != null) {
			qrCodeBitmap.recycle();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//设置分享内容
		//		mController.setShareMedia(new UMImage(ShareQrCodeActivity.this, qrCodeBitmap));
		//		if (shareImageFile.exists()) {
		//			mController.setShareMedia(new UMImage(ShareQrCodeActivity.this, shareImageFile));
		//		} else {
		//			return;
		//		}

		switch (v.getId()) {
		case R.id.left_btn_bg:
			finish();
			break;
		case R.id.weibo:
			//设置新浪SSO handler
			mController.getConfig().setSsoHandler(new SinaSsoHandler());
			//确保未授权
			if (!OauthHelper.isAuthenticated(ShareQrCodeActivity.this, SHARE_MEDIA.SINA)) {
				mController.doOauthVerify(ShareQrCodeActivity.this, SHARE_MEDIA.SINA, new UMAuthListener() {
					@Override
					public void onStart(SHARE_MEDIA platform) {
						Toast.makeText(ShareQrCodeActivity.this, "授权开始", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError(SocializeException e, SHARE_MEDIA platform) {
						Toast.makeText(ShareQrCodeActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {
						Toast.makeText(ShareQrCodeActivity.this, "授权完成", Toast.LENGTH_SHORT).show();
						//获取相关授权信息或者跳转到自定义的分享编辑页面
						String uid = value.getString("uid");
						LogTool.i("uid" + uid);
					}

					@Override
					public void onCancel(SHARE_MEDIA platform) {
						Toast.makeText(ShareQrCodeActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
					}
				});
			}

			//设置分享内容
			mController.setShareContent(channelTitle + Constants.AppliactionServerDomain + "/wap/channel_article/index/" + channelId);
			//设置分享图片
			mController.setShareMedia(new UMImage(ShareQrCodeActivity.this, shareImageFile));
			//直接分享
			mController.directShare(ShareQrCodeActivity.this, SHARE_MEDIA.SINA, new SnsPostListener() {
				@Override
				public void onStart() {
					//Toast.makeText(ShareQrCodeActivity.this, "分享开始", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						Toast.makeText(ShareQrCodeActivity.this, "分享成功", Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(ShareQrCodeActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
			break;
		case R.id.wechat:
			//设置微信好友分享内容
			WeiXinShareContent weixinContent = new WeiXinShareContent();
			//设置分享文字
			weixinContent.setShareContent(channelTitle);
			//设置title
			weixinContent.setTitle(channelTitle);
			//设置分享内容跳转URL
			weixinContent.setTargetUrl(Constants.AppliactionServerDomain + "/wap/channel_article/index/" + channelId);
			//设置分享图片
			weixinContent.setShareImage(new UMImage(ShareQrCodeActivity.this, shareImageFile));
			mController.setShareMedia(weixinContent);
			//直接分享
			mController.directShare(ShareQrCodeActivity.this, SHARE_MEDIA.WEIXIN, new SnsPostListener() {
				@Override
				public void onStart() {
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						Toast.makeText(ShareQrCodeActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(ShareQrCodeActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
					}
				}
			});

			break;
		case R.id.friend:
			//设置微信朋友圈分享内容
			CircleShareContent circleMedia = new CircleShareContent();

			//设置title
			circleMedia.setTitle(channelTitle);
			circleMedia.setShareContent(channelTitle);
			//设置朋友圈title
			circleMedia.setShareImage(new UMImage(ShareQrCodeActivity.this, shareImageFile));
			//设置分享内容跳转URL
			circleMedia.setTargetUrl(Constants.AppliactionServerDomain + "/wap/channel_article/index/" + channelId);

			mController.setShareMedia(circleMedia);

			//直接分享
			mController.directShare(ShareQrCodeActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, new SnsPostListener() {
				@Override
				public void onStart() {
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						Toast.makeText(ShareQrCodeActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(ShareQrCodeActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
					}
				}
			});

			break;
		case R.id.photo:
			Toast.makeText(ShareQrCodeActivity.this, "正在保存...", Toast.LENGTH_SHORT).show();
			try {

				saveImageToGallery(ShareQrCodeActivity.this,qrCodeBitmap);
				Toast.makeText(ShareQrCodeActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
//				MediaStore.Images.Media.insertImage(getContentResolver(), qrCodeBitmap, System.currentTimeMillis() + "", "");
//				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
				
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(ShareQrCodeActivity.this, "保存不成功", Toast.LENGTH_SHORT).show();
				LogTool.e("保存出问题" + e);
			}
			break;
		default:
			break;
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
	public static void saveImageToGallery(Context context, Bitmap bmp) {
		// 首先保存图片
		File appDir = new File(Environment.getExternalStorageDirectory(), "lanquan");
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String fileName = System.currentTimeMillis() + ".jpg";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 最后通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
	}

}
