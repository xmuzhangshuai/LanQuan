package com.lanquan.jsonobject;

import java.util.Date;

public class JsonChannelComment {
	private int article_id;
	private int channel_id;
	private String image_url;
	private String message;
	private String latitude;
	private String longitude;
	private String address;
	private int light;
	private Date create_time;
	private Date update_time;
	private int user_id;
	private int status;
	private int recommend;// (0=>不推荐,1=>推荐)
	private String nickame;
	private String avatar;

	public JsonChannelComment(int article_id, int channel_id, String image_url, String message, String latitude, String longitude, String address, int light,
			Date create_time, Date update_time, int user_id, int status, int recommend, String nickame, String avatar) {
		super();
		this.article_id = article_id;
		this.channel_id = channel_id;
		this.image_url = image_url;
		this.message = message;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.light = light;
		this.create_time = create_time;
		this.update_time = update_time;
		this.user_id = user_id;
		this.status = status;
		this.recommend = recommend;
		this.nickame = nickame;
		this.avatar = avatar;
	}

	public int getArticle_id() {
		return article_id;
	}

	public void setArticle_id(int article_id) {
		this.article_id = article_id;
	}

	public int getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getLight() {
		return light;
	}

	public void setLight(int light) {
		this.light = light;
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

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
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

}
