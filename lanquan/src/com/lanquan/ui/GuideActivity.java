package com.lanquan.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.base.BaseApplication;
import com.lanquan.db.CopyDataBase;
import com.lanquan.utils.LocationTool;
import com.lanquan.utils.ServerUtil;
import com.lanquan.utils.SharePreferenceUtil;
import com.lanquan.utils.UserPreference;

/**
 * 类名称：GuideActivity
 * 类描述：引导页面，首次运行进入首次引导页面，GuidePagerActivity;
 * 用户没有登录则进入登录/注册页面；
 * 用户已经登录过则进入主页面加载页，
 * 期间完成程序的初始化工作。 
 * 创建人： 张帅
 * 创建时间：2015-4-4 上午8:32:52
 * 
 */

public class GuideActivity extends BaseActivity {
	public SharedPreferences locationPreferences;// 记录用户位置
	SharedPreferences.Editor locationEditor;
	private SharePreferenceUtil sharePreferenceUtil;
	private UserPreference userPreference;
	private String province;// 省份
	private String city;// 城市
	private String detailLocation;// 详细地址

	private String latitude;
	private String longtitude;
	LocationTool locationTool;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		// 获取启动的次数
		sharePreferenceUtil = new SharePreferenceUtil(this, SharePreferenceUtil.USE_COUNT);
		int count = sharePreferenceUtil.getUseCount();
		userPreference = BaseApplication.getInstance().getUserPreference();

		// 开启百度推送服务
		// PushManager.startWork(getApplicationContext(),
		// PushConstants.LOGIN_TYPE_API_KEY,
		// Constants.BaiduPushConfig.API_KEY);
		// 基于地理位置推送，可以打开支持地理位置的推送的开关
		// PushManager.enableLbs(getApplicationContext());
		// 设置标签
		// List<String> tags = new ArrayList<String>();
		// String gender = userPreference.getU_gender();

		// if (!TextUtils.isEmpty(gender)) {
		// tags.add(gender);
		// PushManager.setTags(this, tags);
		// }

		/************ 初始化友盟服务 **************/
		// MobclickAgent.updateOnlineConfig(this);
		// new FeedbackAgent(this).sync();

		if (count == 0) {// 如果是第一次登陆，则启动向导页面
			// 第一次运行拷贝数据库文件
			new initDataBase().execute();
			sharePreferenceUtil.setUseCount(++count);// 次数加1
			startActivity(new Intent(GuideActivity.this, GuidePagerActivity.class));
		} else {// 如果不是第一次使用,则不启动向导页面，显示欢迎页面。
			if (userPreference.getUserLogin()) {// 如果是已经登陆过
				setContentView(R.layout.activity_guide);
				findViewById();
				initView();
				ServerUtil.getInstance().login(GuideActivity.this, MainActivity.class);
			} else {// 如果用户没有登录过或者已经注销
				startActivity(new Intent(GuideActivity.this, LoginOrRegisterActivity.class));
				// startActivity(new Intent(GuideActivity.this,
				// MainActivity.class));
				// SchoolDbService.getInstance(getApplication()).getSchoolNameById(831);
			}
			sharePreferenceUtil.setUseCount(++count);// 次数加1
		}
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return this.getString(R.string.version_name) + version;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getString(R.string.can_not_find_version_name);
		}
	}

	/**
	 * 类名称：initDataBase 类描述：拷贝数据库 创建人： 张帅 创建时间：2014年7月8日 下午4:51:58
	 * 
	 */
	public class initDataBase extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			new CopyDataBase(GuideActivity.this).copyDataBase();// 拷贝数据库
			return null;
		}
	}
}
