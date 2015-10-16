package com.lanquan.utils;

import java.util.Date;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.table.UserTable;
import com.lanquan.ui.LoginActivity;
import com.lanquan.ui.LoginOrRegisterActivity;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 *
 * 项目名称：quanzi  
 * 类名称：ServerUtil  
 * 类描述：一些公用的网络操作类
 * @author zhangshuai
 * @date 创建时间：2015-7-7 下午4:26:35 
 *
 */
public class ServerUtil {
	private static ServerUtil instance;
	private UserPreference userPreference;

	public ServerUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 得到实例
	 * @param context
	 * @return
	 */
	public static ServerUtil getInstance() {
		if (instance == null) {
			instance = new ServerUtil();
			instance.userPreference = BaseApplication.getInstance().getUserPreference();
		}
		return instance;
	}

	/**
	 * 用户登录
	 */
	public <T> void login(final Context context, final Class<T> cls) {
		if (userPreference.getU_id() == -1) {
			Intent intent = new Intent(context, LoginOrRegisterActivity.class);
			context.startActivity(intent);
			((Activity) context).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			((Activity) context).finish();
		} else {
			getUserInfo(userPreference.getU_id() + "", context, cls);
		}
	}

	// 获取某个用户信息
	public <T> void getUserInfo(String userid, final Context context, final Class<T> cls) {

		RequestParams params = new RequestParams();
		params.put(UserTable.U_ID, userid);

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				LogTool.i(statusCode + "===" + response);
				JsonTool jsonTool = new JsonTool(response);
				JSONObject jsonObject = jsonTool.getJsonObject();

				String status = jsonTool.getStatus();
				if (status.equals("success")) {
					saveUser(jsonObject);
					Intent intent = new Intent(context, cls);
					context.startActivity(intent);
					((Activity) context).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
					((Activity) context).finish();
				} else {
					LogTool.e("status" + status);
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("服务器错误" + errorResponse);
			}
		};
		AsyncHttpClientTool.post(context, "api/user/user", params, responseHandler);
	}

	/**
	 * 存储自己的信息
	 */
	private void saveUser(final JSONObject jsonUserObject) {
		// TODO Auto-generated method stub
		userPreference.setUserLogin(true);

		try {
			String userInfo = jsonUserObject.getString("user_info");
			JSONObject userInfoJsonObject = new JSONObject(userInfo);

			String nickname = userInfoJsonObject.getString(UserTable.U_NICKNAME);
			String avatar = userInfoJsonObject.getString(UserTable.AVATAR);
			String create_time = userInfoJsonObject.getString(UserTable.U_CREATE_TIME);
			Date date = DateTimeTools.StringToDate(create_time);

			String userid = userInfoJsonObject.getString(UserTable.U_ID);

			// 取出频道相关信息
			int article_count = jsonUserObject.getInt(UserTable.ARTICLE_COUNT);
			int channel_count = jsonUserObject.getInt(UserTable.CHANNEL_COUNT);

			userPreference.setU_nickname(nickname);
			userPreference.setU_avatar(avatar);
			userPreference.setU_CreatTime(date);
			if (!userid.isEmpty()) {
				userPreference.setU_id(Integer.parseInt(userid));
			} else {
				LogTool.e("存储用户信息的id有误" + userid);
			}

			userPreference.setArticle_count(article_count);
			userPreference.setChannel_count(channel_count);
			// userPreference.printUserInfo();

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
