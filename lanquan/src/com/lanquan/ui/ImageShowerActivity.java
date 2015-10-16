package com.lanquan.ui;

import android.os.Bundle;
import android.view.Window;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.photoview.PhotoView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * 类名称：ImageShowerActivity
 * 类描述：点击图片，全屏显示头像
 * 创建人： 张帅
 * 创建时间：2014年7月23日 下午9:39:47
 *
 */
public class ImageShowerActivity extends BaseActivity {
	public static final String SHOW_BIG_IMAGE = "showBigImage";
	public static final String CACHE = "cache_on_memory";
	private PhotoView imageView;
	private String url;
	private boolean cacahe = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_shower);

		url = getIntent().getStringExtra(SHOW_BIG_IMAGE);
		cacahe = getIntent().getBooleanExtra(CACHE, true);
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		imageView = (PhotoView) findViewById(R.id.imageview);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		imageLoader.displayImage(url, imageView, getImageOptions());
	}

	/**
	 * 普通图片设置
	 * @return
	 */
	protected DisplayImageOptions getImageOptions() {
		DisplayImageOptions options = null;
		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading)// 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.image_error) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.image_error) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(cacahe) // 设置下载的图片是否缓存在内存中
				.imageScaleType(ImageScaleType.NONE_SAFE)
				.build(); // 创建配置过得DisplayImageOption对象
		return options;
	}

}
