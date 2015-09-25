package com.lanquan.jsonobject;

import java.io.Serializable;
import java.util.Date;

public class JsonChannel implements Serializable {
	private int channel_id;
	private String title;
	private String description;
	private String recommend_background;
	private int type;// 频道类型 0=>图文频道 1=>文字频道 2=>打卡频道
	private int user_id;
	private int status;
	private Date create_time;
	private Date update_time;
	private int recommend;// (0=>不推荐,1=>推荐)
	private String icon;
	private int from;// 0,1(0=>前台APP创建 1=>后台创建)
	private String nickame;
	private String avatar;
	private int is_focus;

	public JsonChannel(int channel_id, String title, String description, String recommend_background, int type, int user_id, int status, Date create_time,
			Date update_time, int recommend, String icon, int from, String nickame, String avatar) {
		super();
		this.channel_id = channel_id;
		this.title = title;
		this.description = description;
		this.recommend_background = recommend_background;
		this.type = type;
		this.user_id = user_id;
		this.status = status;
		this.create_time = create_time;
		this.update_time = update_time;
		this.recommend = recommend;
		this.icon = icon;
		this.from = from;
		this.nickame = nickame;
		this.avatar = avatar;
	}

	public JsonChannel(int channel_id, String title, String description, String recommend_background, int type, int user_id, int status, Date create_time,
			Date update_time, int recommend, String icon, int from, String nickame, String avatar, int is_foucus) {
		super();
		this.channel_id = channel_id;
		this.title = title;
		this.description = description;
		this.recommend_background = recommend_background;
		this.type = type;
		this.user_id = user_id;
		this.status = status;
		this.create_time = create_time;
		this.update_time = update_time;
		this.recommend = recommend;
		this.icon = icon;
		this.from = from;
		this.nickame = nickame;
		this.avatar = avatar;
		this.is_focus = is_foucus;
	}


	public int getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRecommend_background() {
		return recommend_background;
	}

	public void setRecommend_background(String recommend_background) {
		this.recommend_background = recommend_background;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public String getNickame() {
		return nickame;
	}

	public void setNickame(String nickame) {
		this.nickame = nickame;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getIs_focus() {
		return is_focus;
	}

	public void setIs_focus(int is_focus) {
		this.is_focus = is_focus;
	}

	public JsonChannel() {
		super();
		// TODO Auto-generated constructor stub
	}
}
