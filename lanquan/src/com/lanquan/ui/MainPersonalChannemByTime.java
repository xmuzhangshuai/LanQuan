package com.lanquan.ui;

import java.util.List;

import com.lanquan.R;
import com.lanquan.base.BaseV4Fragment;
import com.lanquan.jsonobject.JsonMyArticle;
import com.lanquan.utils.DateTimeTools;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.LogTool;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * 类描述 ：按时间 类名： MainPersonalFragment.java Copyright: Copyright (c)2015
 * Company: zhangshuai
 * 
 * @author: zhangshuai
 * @version: 1.0 创建时间: 2015-8-21 下午2:02:00
 */
public class MainPersonalChannemByTime extends BaseV4Fragment {
	private View rootView;// 根View

	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;
	private TableLayout tableLayout;

	public MainPersonalChannemByTime() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_my_channel_by_time, container, false);

		findViewById();// 初始化views
		initView();

		return rootView;
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		tableLayout = (TableLayout) rootView.findViewById(R.id.tablelayout);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
	}

	public void setList(final List<JsonMyArticle> jsonMyArticleList) {
		if (jsonMyArticleList != null) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					tableLayout.removeAllViews();
					if (jsonMyArticleList != null) {
						for (int i = 0; i < jsonMyArticleList.size(); i++) {
							tableLayout.addView(getView(i, null, jsonMyArticleList.get(i)), i);
						}
					}
				}
			}).run();
		} else {
			LogTool.e("Time: setlist,为空");
		}
	}

	public View getView(int position, View convertView, final JsonMyArticle jsonMyArticle) {
		// TODO Auto-generated method stub
		View view = convertView;
		if (jsonMyArticle == null) {
			return null;
		}

		final ViewHolder holder;
		if (convertView == null) {
			view = LayoutInflater.from(getActivity()).inflate(R.layout.by_time_list_item, null);
			holder = new ViewHolder();
			holder.headImageView = (ImageView) view.findViewById(R.id.head_image);
			holder.titleTextView = (TextView) view.findViewById(R.id.title);
			holder.timeTextView = (TextView) view.findViewById(R.id.time);
			holder.contentTextView = (TextView) view.findViewById(R.id.content);
			holder.itemImageView = (ImageView) view.findViewById(R.id.item_image);
			holder.favorBtn = (CheckBox) view.findViewById(R.id.favor_btn);
			holder.favorCountTextView = (TextView) view.findViewById(R.id.favor_count);
			holder.deleteBtn = (ImageView) view.findViewById(R.id.delete_btn);
			holder.commentCountTextView = (TextView) view.findViewById(R.id.comment_count);
			view.setTag(holder); // 给View添加一个格外的数据
		} else {
			holder = (ViewHolder) view.getTag(); // 把数据取出来
		}

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//					goToDetail(jsonMyArticle.getChannel_id(), jsonMyArticle.get);
			}
		});

		// 设置头像
		if (!TextUtils.isEmpty(jsonMyArticle.getIcon()) && !jsonMyArticle.getIcon().equals("null")) {
			imageLoader.displayImage(jsonMyArticle.getIcon(), holder.headImageView, ImageLoaderTool.getCircleHeadImageOptions());
			// if (userPreference.getU_id() != channel.getC_userid()) {
			// // 点击头像进入详情页面
			// holder.headImageView.setOnClickListener(new OnClickListener()
			// {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// // Intent intent = new Intent(getActivity(),
			// // PersonDetailActivity.class);
			// // intent.putExtra(UserTable.U_ID,
			// // jsonPostItem.getP_userid());
			// // intent.putExtra(UserTable.U_NICKNAME,
			// // jsonPostItem.getP_username());
			// // intent.putExtra(UserTable.U_SMALL_AVATAR,
			// // jsonPostItem.getP_small_avatar());
			// // startActivity(intent);
			// // getActivity().overridePendingTransition(R.anim.zoomin2,
			// // R.anim.zoomout);
			// }
			// });
			// }
		}

		// 设置内容
		holder.contentTextView.setText(jsonMyArticle.getMessage());

		// 设置题目
		holder.titleTextView.setText(jsonMyArticle.getChannel_title());

		// 设置日期
		holder.timeTextView.setText(DateTimeTools.getInterval(jsonMyArticle.getCreate_time()));

		// 设置被赞次数
		// holder.favorCountTextView.setText("" + jsonMyArticle.getLight());

		// 设置图片
		if (!TextUtils.isEmpty(jsonMyArticle.getImage_url()) && !jsonMyArticle.getImage_url().equals("null")) {
			imageLoader.displayImage(jsonMyArticle.getImage_url(), holder.itemImageView, ImageLoaderTool.getImageOptions());
			holder.itemImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), ImageShowerActivity.class);
					intent.putExtra(ImageShowerActivity.SHOW_BIG_IMAGE, jsonMyArticle.getImage_url());
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.zoomin2, R.anim.zoomout);
				}
			});
		}

		return view;
	}

	private class ViewHolder {
		public ImageView headImageView;
		public TextView titleTextView;
		public TextView timeTextView;
		public TextView contentTextView;
		public CheckBox favorBtn;
		public TextView favorCountTextView;
		public ImageView deleteBtn;
		public TextView commentCountTextView;
		public ImageView itemImageView;
	}
}
