package com.lanquan.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * 
 * 项目名称：lanquan 
 * 类名称：DensityUtil 
 * 类描述： 工具类，获取屏幕分辨率、进行dp、px的装换 
 * 创建人：张帅  
 * 创建时间：2013-12-5 下午18:45:21 
 * 修改人：张帅   
 * 修改时间：2013-12-15 下午3:56:28 
 * 修改备注：
 * 
 * @version
 * 
 */
public class DensityUtil {
	private static DisplayMetrics dm;
	private static WindowManager wm;

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dpToPx(Resources res, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取手机屏幕宽度值，单位px
	 */
	public static int getScreenWidthforPX(Context context) {
		dm = new DisplayMetrics();
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;// 屏幕宽度
	}

	/**
	 * 获取手机屏幕高度值，单位px
	 */
	public static int getScreenHeightforPX(Context context) {
		dm = new DisplayMetrics();
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;// 屏幕高度
	}

	/**
	 * 获取手机屏幕宽度值，单位dp
	 */
	public static int getScreenWidthforDP(Context context) {
		int ScreenWidth = getScreenWidthforPX(context);
		ScreenWidth = DensityUtil.px2dip(context, ScreenWidth);
		return ScreenWidth;
	}

	/**
	 * 获取手机屏幕高度值，单位dp
	 */
	public static int getScreenHeightforDP(Context context) {
		int ScreenHeight = getScreenHeightforPX(context);
		ScreenHeight = DensityUtil.px2dip(context, ScreenHeight);
		return ScreenHeight;
	}

	/**
	 * 获取手机状态栏高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}
}
