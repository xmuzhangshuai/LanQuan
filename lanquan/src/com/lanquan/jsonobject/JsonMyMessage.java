package com.lanquan.jsonobject;

import java.util.Date;

//我的消息实体
public class JsonMyMessage {

	// 消息主体
	private int messageid;
	private String image;// 图片
	private String content;// 内容

	// 点赞人的具体内容
	private int userid;
	private String username;
	private String gender;
	private String small_avatar;
	private Date favortime;

	public JsonMyMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JsonMyMessage(int messageid, String image, String content, int userid, String username, String gender, String small_avatar, Date favortime) {
		super();
		this.messageid = messageid;
		this.image = image;
		this.content = content;
		this.userid = userid;
		this.username = username;
		this.gender = gender;
		this.small_avatar = small_avatar;
		this.favortime = favortime;
	}

	public int getMessageid() {
		return messageid;
	}

	public void setMessageid(int messageid) {
		this.messageid = messageid;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getSmall_avatar() {
		return small_avatar;
	}

	public void setSmall_avatar(String small_avatar) {
		this.small_avatar = small_avatar;
	}

	public Date getFavortime() {
		return favortime;
	}

	public void setFavortime(Date favortime) {
		this.favortime = favortime;
	}

}
