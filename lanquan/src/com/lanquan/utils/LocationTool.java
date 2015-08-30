package com.lanquan.utils;

import android.content.Context;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class LocationTool {

	/**
	 * 初始化定位
	 */
	public static LocationClient initLocation(Context context) {
		LocationClient mLocationClient = new LocationClient(context);
		// 声明LocationClient类
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开GPS
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setScanSpan(5 * 60 * 60 * 1000);// 设置发起定位请求的间隔时间为3000ms
		mLocationClient.setLocOption(option);// 使用设置
		mLocationClient.start();// 开启定位SDK
		mLocationClient.requestLocation();// 开始请求位置
		return mLocationClient;
	}
}
