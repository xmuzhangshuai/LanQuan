package com.lanquan.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lanquan.R;

/** 
 * 类描述 ：	频道详情页面点击频道名称，显示的菜单，显示全部和只显示精选
 * 类名： ChoicenessMenuPopupWindow.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-11 下午2:35:59  
*/
public class ChoicenessMenuPopupWindow extends PopupWindow {
	private View conentView;
	private TextView titleTextView;

	public ChoicenessMenuPopupWindow(final Activity context, String title) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		conentView = inflater.inflate(R.layout.popupwindow_choiceness_menu, null);
		titleTextView = (TextView) conentView.findViewById(R.id.title);
		titleTextView.setText(title);
		// 设置SelectPicPopupWindow的View
		this.setContentView(conentView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(dw);
		// mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimationPreview);
		// LinearLayout addTaskLayout = (LinearLayout)
		// conentView.findViewById(R.id.add_task_layout);
		// LinearLayout teamMemberLayout = (LinearLayout)
		// conentView.findViewById(R.id.team_member_layout);
		// addTaskLayout.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// ChoicenessMenuPopupWindow.this.dismiss();
		// }
		// });
		//
		// teamMemberLayout.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// ChoicenessMenuPopupWindow.this.dismiss();
		// }
		// });
	}

	/**
	 * 显示popupWindow
	 * 
	 * @param parent
	 */
	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			// 以下拉方式显示popupwindow
			this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 0);
		} else {
			this.dismiss();
		}
	}

	/**
	 * 关闭popupWindow
	 * 
	 * @param parent
	 */
	public void closePopupWindow(View parent) {
		if (this.isShowing()) {
			this.closePopupWindow(parent);
			this.dismiss();
		} else {
			this.dismiss();
		}
	}
}
