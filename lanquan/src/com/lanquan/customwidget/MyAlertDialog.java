package com.lanquan.customwidget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.lanquan.R;

public class MyAlertDialog {
	Context context;
	android.app.AlertDialog ad;
	TextView titleView;
	TextView messageView;
	TextView positiveButton;
	TextView negativeButton;
	boolean showCancle = true;
	boolean cancleable = true;
	boolean showTitle = true;

	public MyAlertDialog(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		ad = new android.app.AlertDialog.Builder(context).create();
		ad.show();
		//关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
		Window window = ad.getWindow();
		window.setContentView(R.layout.alert_dialog);
		titleView = (TextView) window.findViewById(R.id.title);
		messageView = (TextView) window.findViewById(R.id.message);
		negativeButton = (TextView) window.findViewById(R.id.cancle);
		positiveButton = (TextView) window.findViewById(R.id.confirm);
	}

	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}

	public void setTitle(int resId) {
		if (!showCancle) {
			titleView.setGravity(Gravity.LEFT);
		}
		titleView.setText(resId);
	}

	public void setTitle(String title) {
		if (!showCancle) {
			titleView.setGravity(Gravity.LEFT);
		}
		titleView.setText(title);
	}

	public void setShowCancel(boolean showCancel) {
		this.showCancle = showCancel;
	}

	public void setMessage(int resId) {
		if (!showTitle) {
			titleView.setVisibility(View.GONE);
		}
		messageView.setText(resId);
	}

	public void setMessage(String message) {
		if (!showTitle) {
			titleView.setVisibility(View.GONE);
		}
		messageView.setText(message);
	}

	public void setCancleable(boolean cancleable) {
		this.cancleable = cancleable;
	}

	/**
	 * 设置按钮
	 * @param text
	 * @param listener
	 */
	public void setPositiveButton(String text, final View.OnClickListener listener) {
		positiveButton.setText(text);
		positiveButton.setOnClickListener(listener);
		if (showCancle) {
			negativeButton.setVisibility(View.VISIBLE);
		} else {
			negativeButton.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置按钮
	 * @param text
	 * @param listener
	 */
	public void setNegativeButton(String text, final View.OnClickListener listener) {
		negativeButton.setText(text);
		negativeButton.setOnClickListener(listener);
	}

	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		ad.dismiss();
	}

	public void show() {
		ad.setCancelable(cancleable);
		ad.show();
	}
}
