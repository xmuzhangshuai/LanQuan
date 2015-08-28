package com.lanquan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.base.BaseV4Fragment;
import com.lanquan.customwidget.MyAlertDialog;
import com.lanquan.utils.FileSizeUtil;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.UserPreference;

/**
 * 
 * 类名称：SettingMainFragment
 * 类描述：设置主页面
 * 创建人： 张帅
 * 创建时间：2014年8月17日 下午4:26:06
 *
 */
public class SettingMainFragment extends BaseV4Fragment implements OnClickListener {
	private View rootView;// 根View
	private TextView topNavigation;// 导航栏文字
	private View leftImageButton;// 导航栏左侧按钮

	private ImageView headImageView;
	private View settingData;// 资料
	private View settingGrade;// 聊天设置
	private View settingTellFriend;// 告诉小伙伴

	private View settingClearCache;// 清空缓存
	private RelativeLayout rl_switch_notification;// 设置新消息通知布局
	private ImageView iv_switch_open_notification;// 打开新消息通知imageView
	private ImageView iv_switch_close_notification;// 关闭新消息通知imageview
	private View settingLogout;// 退出

	private FragmentTransaction transaction;
	private UserPreference userPreference;
	private TextView cacheSize;
	private TextView version;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		transaction = getFragmentManager().beginTransaction();
		userPreference = BaseApplication.getInstance().getUserPreference();
		rootView = inflater.inflate(R.layout.fragment_setting_main, container, false);

		findViewById();
		initView();
		return rootView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		transaction = null;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		topNavigation = (TextView) rootView.findViewById(R.id.nav_text);
		leftImageButton = (View) rootView.findViewById(R.id.left_btn_bg);
		settingClearCache = rootView.findViewById(R.id.setting_clear_cache);
		headImageView = (ImageView) rootView.findViewById(R.id.headimage);
		settingLogout = rootView.findViewById(R.id.setting_logout);
		cacheSize = (TextView) rootView.findViewById(R.id.cache_size);
		rl_switch_notification = (RelativeLayout) rootView.findViewById(R.id.rl_switch_notification);
		iv_switch_open_notification = (ImageView) rootView.findViewById(R.id.iv_switch_open_notification);
		iv_switch_close_notification = (ImageView) rootView.findViewById(R.id.iv_switch_close_notification);
		settingData = rootView.findViewById(R.id.setting_data);
		settingGrade = rootView.findViewById(R.id.setting_grade);
		settingTellFriend = rootView.findViewById(R.id.setting_tell_friends);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		topNavigation.setText("设置");
		leftImageButton.setOnClickListener(this);
		rl_switch_notification.setOnClickListener(this);
		settingData.setOnClickListener(this);
		settingGrade.setOnClickListener(this);
		settingTellFriend.setOnClickListener(this);
		settingLogout.setOnClickListener(this);
		settingClearCache.setOnClickListener(this);
		rl_switch_notification.setOnClickListener(this);

		cacheSize.setText("" + FileSizeUtil.getFileOrFilesSize(imageLoader.getDiskCache().getDirectory().getAbsolutePath(), FileSizeUtil.SIZETYPE_MB) + "MB");
		imageLoader.displayImage(userPreference.getU_avatar(), headImageView, ImageLoaderTool.getCircleHeadImageOptions());
	}

	/**
	 * 退出登录
	 */
	private void logout() {
		// 设置用户不曾登录

		final MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity());
		myAlertDialog.setTitle("提示");
		myAlertDialog.setMessage("是否退出登录？退出登录后将不能接受消息。");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();

				Intent intent;
				// BaseApplication.getInstance().logout();
				userPreference.clear();
				intent = new Intent(getActivity(), LoginOrRegisterActivity.class);
				getActivity().startActivity(intent);
				getActivity().finish();
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
			}
		};
		myAlertDialog.setPositiveButton("退出", comfirm);
		myAlertDialog.setNegativeButton("取消", cancle);
		myAlertDialog.show();

	}

	/**
	 * 清楚缓存
	 */
	private void clearCache() {

		final MyAlertDialog myAlertDialog = new MyAlertDialog(getActivity());
		myAlertDialog.setTitle("提示");
		myAlertDialog.setMessage("是否清除缓存？");
		View.OnClickListener comfirm = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
				imageLoader.clearMemoryCache();
				imageLoader.clearDiskCache();
				cacheSize.setText("" + FileSizeUtil.getFileOrFilesSize(imageLoader.getDiskCache().getDirectory().getAbsolutePath(), FileSizeUtil.SIZETYPE_MB)
						+ "MB");
			}
		};
		View.OnClickListener cancle = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
			}
		};
		myAlertDialog.setPositiveButton("确定", comfirm);
		myAlertDialog.setNegativeButton("取消", cancle);
		myAlertDialog.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_btn_bg:
			getActivity().finish();
			break;
		case R.id.setting_data:
			getActivity().startActivity(new Intent(getActivity(), ModifyDataActivity.class));
			getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case R.id.setting_grade:
			// transaction.setCustomAnimations(R.anim.zoomin2, R.anim.zoomout);
			// transaction.replace(R.id.container, new SettingChatFragment());
			// transaction.addToBackStack("setting");
			// transaction.commit();
			break;
		case R.id.setting_tell_friends:
			getActivity().startActivity(new Intent(getActivity(), ShareMenuActivity.class));
			break;
		case R.id.setting_clear_cache:
			clearCache();
			break;
		case R.id.setting_logout:
			logout();
			break;
		case R.id.rl_switch_notification:
			if (iv_switch_open_notification.getVisibility() == View.VISIBLE) {
				iv_switch_open_notification.setVisibility(View.INVISIBLE);
				iv_switch_close_notification.setVisibility(View.VISIBLE);
				// PreferenceUtils.getInstance(getActivity()).setSettingMsgNotification(false);
			} else {
				iv_switch_open_notification.setVisibility(View.VISIBLE);
				iv_switch_close_notification.setVisibility(View.INVISIBLE);
				// PreferenceUtils.getInstance(getActivity()).setSettingMsgNotification(true);
			}
			break;
		default:
			break;
		}
	}

}
