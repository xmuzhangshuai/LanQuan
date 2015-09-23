package com.lanquan.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.config.Constants.WeChatConfig;
import com.lanquan.utils.LogTool;
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
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/** 
 * 类描述 ：在设置页面点击告诉小伙伴跳出的平台选项
 * 类名： ShareActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-15 上午10:27:14  
*/
public class ShareMenuActivity extends BaseActivity implements OnClickListener {
	private View sina;
	private View weixinFriends;
	private View weixinQuan;
	// 首先在您的Activity中添加如下成员变量
	UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_menu);

		findViewById();
		initView();
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(ShareMenuActivity.this, WeChatConfig.API_KEY, WeChatConfig.SECRIT_KEY);
		wxHandler.addToSocialSDK();
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(ShareMenuActivity.this, WeChatConfig.API_KEY, WeChatConfig.SECRIT_KEY);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		sina = findViewById(R.id.menu1);
		weixinFriends = findViewById(R.id.menu2);
		weixinQuan = findViewById(R.id.menu3);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		sina.setOnClickListener(this);
		weixinFriends.setOnClickListener(this);
		weixinQuan.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.menu1:
			//设置新浪SSO handler
			mController.getConfig().setSsoHandler(new SinaSsoHandler());
			//确保未授权
			if (!OauthHelper.isAuthenticated(ShareMenuActivity.this, SHARE_MEDIA.SINA)) {
				mController.doOauthVerify(ShareMenuActivity.this, SHARE_MEDIA.SINA, new UMAuthListener() {
					@Override
					public void onStart(SHARE_MEDIA platform) {
						Toast.makeText(ShareMenuActivity.this, "授权开始", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError(SocializeException e, SHARE_MEDIA platform) {
						Toast.makeText(ShareMenuActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {
						Toast.makeText(ShareMenuActivity.this, "授权完成", Toast.LENGTH_SHORT).show();
						//获取相关授权信息或者跳转到自定义的分享编辑页面
						String uid = value.getString("uid");
						LogTool.i("uid" + uid);
					}

					@Override
					public void onCancel(SHARE_MEDIA platform) {
						Toast.makeText(ShareMenuActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
					}
				});
			}

			//设置分享内容
			//			mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能，http://www.umeng.com/social");
			//设置分享图片
			//			mController.setShareMedia(new UMImage(ShareMenuActivity.this, shareImageFile));
			//直接分享
			mController.directShare(ShareMenuActivity.this, SHARE_MEDIA.SINA, new SnsPostListener() {
				@Override
				public void onStart() {
					Toast.makeText(ShareMenuActivity.this, "分享开始", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						Toast.makeText(ShareMenuActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(ShareMenuActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
			break;
		case R.id.menu2:
			//设置微信好友分享内容
			WeiXinShareContent weixinContent = new WeiXinShareContent();
			//设置分享文字
			weixinContent.setShareContent("篮圈——频道分享");
			//设置title
			weixinContent.setTitle("频道分享");
			//设置分享内容跳转URL
			weixinContent.setTargetUrl("www.baidu.com");
			//设置分享图片
//			weixinContent.setShareImage(new UMImage(ShareMenuActivity.this, shareImageFile));
			mController.setShareMedia(weixinContent);
			//直接分享
			mController.directShare(ShareMenuActivity.this, SHARE_MEDIA.WEIXIN, new SnsPostListener() {
				@Override
				public void onStart() {
					Toast.makeText(ShareMenuActivity.this, "发送开始", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						Toast.makeText(ShareMenuActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(ShareMenuActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
					}
				}
			});

			break;
		case R.id.menu3:
			//设置微信朋友圈分享内容
			CircleShareContent circleMedia = new CircleShareContent();
			circleMedia.setShareContent("篮圈——频道分享");
			//设置朋友圈title
//			circleMedia.setShareImage(new UMImage(ShareMenuActivity.this, shareImageFile));
			//设置分享内容跳转URL
			circleMedia.setTargetUrl("http://www.baidu.com");
			mController.setShareMedia(circleMedia);

			//直接分享
			mController.directShare(ShareMenuActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, new SnsPostListener() {
				@Override
				public void onStart() {
					Toast.makeText(ShareMenuActivity.this, "分享开始", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
					if (eCode == StatusCode.ST_CODE_SUCCESSED) {
						Toast.makeText(ShareMenuActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(ShareMenuActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
					}
				}
			});

			break;

		default:
			break;
		}
	}

	public void cancel(View view) {
		finish();
	}

}
