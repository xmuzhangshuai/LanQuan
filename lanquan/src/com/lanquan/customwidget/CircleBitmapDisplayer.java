package com.lanquan.customwidget;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/** 
 * 类描述 ：显示原型图片的ImageLoader使用的显示器
 * 类名： CircleBitmapDisplayer.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-7 下午3:49:34  
*/
public class CircleBitmapDisplayer implements BitmapDisplayer {

	protected final int margin;

	public CircleBitmapDisplayer() {
		this(0);
	}

	public CircleBitmapDisplayer(int margin) {
		this.margin = margin;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		if (!(imageAware instanceof ImageViewAware)) {
			throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
		}

		imageAware.setImageDrawable(new CircleDrawable(bitmap, margin));
	}

}
