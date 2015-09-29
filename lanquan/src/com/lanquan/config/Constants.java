package com.lanquan.config;

import java.util.ArrayList;
import java.util.List;

public class Constants {

	// 包名
	public static final String PACKAGENAME = "com.quanzi";

	// public static String testIP = "http://192.168.1.101/";

	// 域名或者是IP
	public static String AppliactionServerIP = "http://120.24.183.74/";

	public static String AppliactionServerDomain = "http://120.24.183.74/index.php/";

	// 域名或者是IP
//	public static String AppliactionServerIP = "http://120.25.62.195/";
//
//	public static String AppliactionServerDomain = "http://120.25.62.195/index.php/";

	public static String SignKey = "dfeb3d35bc3543rdc234";

	public static class Config {
		// 是否处于开发模式
		public static final boolean DEVELOPER_MODE = true;

		// 接受验证码时间为120s
		public static int AUTN_CODE_TIME = 120;

		// 照片缩小比例
		public static final int SCALE = 5;

		// 总共有多少页
		public static final int NUM_PAGE = 6;

		// 每页20个表情,还有最后一个删除button
		public static int NUM = 20;

		public static int PAGE_NUM = 20;

		// 聊天每次刷新纪录条数
		public static int LOAD_MESSAGE_COUNT = 20;
	}

	// 情感状态
	public static class LoveState {
		// 单身
		public static final String SINGLE = "single";
		// 恋爱
		public static final String INLOVE = "in_love";
		// 已婚
		public static final String MARRIED = "married";
	}

	// 评论类型
	public static class CommentType {
		// 评论
		public static final String COMMENT = "COMMENT";
		// 回复
		public static final String REPLY = "REPLY";
	}

	// 身份
	public static class Identity {
		// 学生
		public static final String STUDENT = "student";
		// 校友
		public static final String SCHOOLMATE = "schoolmate";
	}

	// 审核状态
	public static class VertifyState {
		// 通过
		public static final int PASSED = 2;
		// 正在审核
		public static final int VERTIFING = 1;
		// 未通过
		public static final int NOTPASSED = -1;
		// 未提交
		public static final int NOTSUBMIT = 0;
	}

	// 活动种类
	public static class ActivityType {
		public static final String JIANGZUO = "讲座";
		public static final String ZHAOPIN = "招聘";
		public static final String SHETUAN = "社团";
		public static final String YINYUE = "音乐";
		public static final String DIANYING = "电影";
		public static final String YUNDONG = "运动";
		public static final String JUHUI = "聚会";
		public static final String ZHANLAN = "展览";
		public static final String GONGYI = "公益";
		public static final String YOUHUIQUAN = "优惠券";

		public static List<String> getList() {
			List<String> temp = new ArrayList<String>();
			temp.add(JIANGZUO);
			temp.add(ZHAOPIN);
			temp.add(SHETUAN);
			temp.add(YINYUE);
			temp.add(DIANYING);
			temp.add(YUNDONG);
			temp.add(JUHUI);
			temp.add(ZHANLAN);
			temp.add(GONGYI);
			temp.add(YOUHUIQUAN);
			return temp;
		}
	}

	public static class BaiduPushConfig {
		public final static String API_KEY = "bcB8dCkZh0GVtMTdpyTTabj3";
		public final static String SECRIT_KEY = "y75ge4lbEEGth1nwbTveiGr7yHARKnm2";
	}

	public static class WeChatConfig {
		public final static String API_KEY = "wx33f81b034c7f3e04";
		public final static String SECRIT_KEY = "b003237ea4d4d1df9089c97feef5ff76";
	}

	public static class WeiboConfig {
		public final static String API_KEY = "4169059323";
		public final static String SECRIT_KEY = "9825238066521372a2f776fa08b651e2";
	}

	public static class QQConfig {
		public final static String API_KEY = "1104862406";
		public final static String SECRIT_KEY = "VHSnIwPNZJcnMqj7";
	}

	public static class Extra {
		public static final String IMAGES = "com.nostra13.example.universalimageloader.IMAGES";
		public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
	}

	public static class Gender {
		public static final String MALE = "男";
		public static final String FEMALE = "女";
	}

}
