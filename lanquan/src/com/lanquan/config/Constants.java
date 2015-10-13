package com.lanquan.config;

public class Constants {

	// 包名
	public static final String PACKAGENAME = "com.lanquan";

	// 域名或者是IP(旧)
	//	public static String AppliactionServerIP = "http://120.24.183.74";

	//	public static String AppliactionServerDomain = "http://120.24.183.74/index.php/";

	public static String AppliactionServerIP_Share = "http://www.theballer.cn";
	// 域名或者是IP
	public static String AppliactionServerIP = "http://www.theballer.cn/";
	//
	public static String AppliactionServerDomain = "http://www.theballer.cn/index.php/";

	public static String SignKey = "dfeb3d35bc3543rdc234";

	public static class Config {
		// 是否处于开发模式
		public static final boolean DEVELOPER_MODE = false;

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

}
