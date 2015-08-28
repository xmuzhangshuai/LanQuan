package com.lanquan.utils;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;

import com.lanquan.R;
import com.lanquan.table.UserTable;

;

public class UserPreference {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	public static final String USER_SHAREPREFERENCE = "userSharePreference";// 用户SharePreference
	private Context context;

	public UserPreference(Context context) {
		this.context = context;
		sp = context.getSharedPreferences(USER_SHAREPREFERENCE, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	// 打印用户信息
	public void printUserInfo() {
		LogTool.i("是否登录: " + getUserLogin());
		LogTool.i("用户ID: " + getU_id());
		LogTool.i("昵称: " + getU_nickname());
		LogTool.i("密码: " + getU_password());
		LogTool.i("手机号: " + getU_tel());
		LogTool.i("创建时间: " + DateTimeTools.DateToString(getU_CreatTime()));
		LogTool.i("头像: " + getU_avatar());
		LogTool.i("内容数量: " + getArticle_count());
		LogTool.i("频道数量: " + getChannel_count());
		LogTool.i("access_token: " + getAccess_token());
	}

	/**
	 * 清空数据
	 */
	public void clear() {
		String tel = getU_tel();
		editor.clear();
		setU_tel(tel);
		editor.commit();
	}

	// 记录用户是否登录
	public boolean getUserLogin() {
		return sp.getBoolean("login", false);
	}

	public void setUserLogin(boolean login) {
		editor.putBoolean("login", login);
		editor.commit();
	}

	// 用户ID
	public int getU_id() {
		return sp.getInt(UserTable.U_ID, -1);
	}

	public void setU_id(int u_id) {
		editor.putInt(UserTable.U_ID, u_id);
		editor.commit();
	}

	// 用户昵称
	public String getU_nickname() {
		return sp.getString(UserTable.U_NICKNAME, "");
	}

	public void setU_nickname(String u_nickname) {
		editor.putString(UserTable.U_NICKNAME, u_nickname);
		editor.commit();
	}

	// 密码
	public String getU_password() {
		return sp.getString(UserTable.U_PASSWORD, "");
	}

	public void setU_password(String u_password) {
		editor.putString(UserTable.U_PASSWORD, u_password);
		editor.commit();
	}

	// 手机号
	public String getU_tel() {
		return sp.getString(UserTable.U_TEL, "");
	}

	public void setU_tel(String u_tel) {
		editor.putString(UserTable.U_TEL, u_tel);
		editor.commit();
	}

	// 头像
	public String getU_avatar() {
		return sp.getString(UserTable.AVATAR, "drawable://" + R.drawable.headimage6);
	}

	public void setU_avatar(String u_small_avatar) {
		editor.putString(UserTable.AVATAR, u_small_avatar);
		editor.commit();
	}

	// access_token
	public String getAccess_token() {
		return sp.getString(UserTable.U_ACCESS_TOKEN, "");
	}

	public void setAccess_token(String access_token) {
		editor.putString(UserTable.U_ACCESS_TOKEN, access_token);
		editor.commit();
	}

	// 内容数量
	public int getArticle_count() {
		return sp.getInt(UserTable.ARTICLE_COUNT, -1);
	}

	public void setArticle_count(int count) {
		editor.putInt(UserTable.ARTICLE_COUNT, count);
		editor.commit();
	}

	// 频道数量
	public int getChannel_count() {
		return sp.getInt(UserTable.CHANNEL_COUNT, -1);
	}

	public void setChannel_count(int count) {
		editor.putInt(UserTable.CHANNEL_COUNT, count);
		editor.commit();
	}

	// 创建时间
	public Date getU_CreatTime() {
		Long time = sp.getLong(UserTable.U_CREATE_TIME, 0);
		if (time != 0) {
			return new Date(time);
		} else {
			return null;
		}
	}

	public void setU_CreatTime(Date creatTime) {
		if (creatTime != null) {
			editor.putLong(UserTable.U_CREATE_TIME, creatTime.getTime());
			editor.commit();
		}
	}

	// 性别
	// public String getU_gender() {
	// return sp.getString(UserTable.U_GENDER, "");
	// }
	//
	// public void setU_gender(String u_gender) {
	// editor.putString(UserTable.U_GENDER, u_gender);
	// editor.commit();
	// }

	// 邮箱
	// public String getU_email() {
	// return sp.getString(UserTable.U_EMAIL, "");
	// }
	//
	// public void setU_email(String u_email) {
	// editor.putString(UserTable.U_EMAIL, u_email);
	// editor.commit();
	// }

	// 年龄
	// public int getU_age() {
	// return sp.getInt(UserTable.U_AGE, -1);
	// }
	//
	// public void setU_age(int u_age) {
	// editor.putInt(UserTable.U_AGE, u_age);
	// editor.commit();
	// }

	// 大头像
	// public String getU_large_avatar() {
	// return sp.getString(UserTable.U_LARGE_AVATAR, "");
	// }
	//
	// public void setU_large_avatar(String u_large_avatar) {
	// editor.putString(UserTable.U_LARGE_AVATAR, u_large_avatar);
	// editor.commit();
	// }

	// 小头像
	// public String getU_small_avatar() {
	// return sp.getString(UserTable.U_SMALL_AVATAR, "drawable://" +
	// R.drawable.headimage6);
	// }
	//
	// public void setU_small_avatar(String u_small_avatar) {
	// editor.putString(UserTable.U_SMALL_AVATAR, u_small_avatar);
	// editor.commit();
	// }

	// 省份
	// public int getU_provinceid() {
	// return sp.getInt(UserTable.U_PROVINCEID, -1);
	// }
	//
	// public void setU_provinceid(int u_provinceid) {
	// ProvinceDbService provinceDbService =
	// ProvinceDbService.getInstance(context);
	// setPeovinceName(provinceDbService.getProNameById(u_provinceid));
	// editor.putInt(UserTable.U_PROVINCEID, u_provinceid);
	// editor.commit();
	// }
	//
	// public String getProvinceName() {
	// return sp.getString("ProvinceName", "");
	// }
	//
	// public void setPeovinceName(String name) {
	// editor.putString("ProvinceName", name);
	// editor.commit();
	// }
	//
	// // 城市
	// public int getU_cityid() {
	// return sp.getInt(UserTable.U_CITYID, -1);
	// }
	//
	// public void setU_cityid(int u_cityid) {
	// CityDbService cityDbService = CityDbService.getInstance(context);
	// City city =
	// cityDbService.cityDao.queryBuilder().where(Properties.CityID.eq(u_cityid)).unique();
	// if (city != null) {
	// setCityName(city.getCityName());
	// }
	// editor.putInt(UserTable.U_CITYID, u_cityid);
	// editor.commit();
	// }
	//
	// public String getCityName() {
	// return sp.getString("cityName", "");
	// }
	//
	// public void setCityName(String name) {
	// editor.putString("cityName", name);
	// editor.commit();
	// }
	//
	// // 学校
	// public int getU_schoolid() {
	// return sp.getInt(UserTable.U_SCHOOLID, -1);
	// }
	//
	// public void setU_schoolid(int u_schoolid) {
	// SchoolDbService schoolDbService = SchoolDbService.getInstance(context);
	// School school = schoolDbService.schoolDao.load((long) u_schoolid);
	// setSchoolName(school.getSchoolName());
	// editor.putInt(UserTable.U_SCHOOLID, u_schoolid);
	// editor.commit();
	// }
	//
	// public String getSchoolName() {
	// return sp.getString("schoolName", "");
	// }
	//
	// public void setSchoolName(String name) {
	// editor.putString("schoolName", name);
	// editor.commit();
	// }

	// 用户简介
	// public String getU_introduce() {
	// return sp.getString(UserTable.U_INTRODUCE, "");
	// }
	//
	// public void setU_introduce(String u_introduce) {
	// editor.putString(UserTable.U_INTRODUCE, u_introduce);
	// editor.commit();
	// }
}
