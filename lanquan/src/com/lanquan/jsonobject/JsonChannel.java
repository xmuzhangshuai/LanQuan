package com.lanquan.jsonobject;

import java.io.Serializable;
import java.util.Date;

public class JsonChannel implements Serializable {
	private int c_id;
	private int c_userid;
	private String c_username;
	private String c_small_avatar;
	private String c_large_avatar;
	private String c_title;
	// private String c_thumbnail;
	private String c_big_photo;
	private Date c_time;
	// private int c_comment_count;
	private int c_favor_count;

	public JsonChannel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JsonChannel(int c_id, int c_userid, String c_username, String c_small_avatar, String c_large_avatar,
			String c_title, String c_big_photo, Date c_time, int c_favor_count) {
		super();
		this.c_id = c_id;
		this.c_userid = c_userid;
		this.c_username = c_username;
		this.c_small_avatar = c_small_avatar;
		this.c_large_avatar = c_large_avatar;
		this.c_title = c_title;
		this.c_big_photo = c_big_photo;
		this.c_time = c_time;
		this.c_favor_count = c_favor_count;
	}

	public int getC_id() {
		return c_id;
	}

	public void setC_id(int c_id) {
		this.c_id = c_id;
	}

	public int getC_userid() {
		return c_userid;
	}

	public void setC_userid(int c_userid) {
		this.c_userid = c_userid;
	}

	public String getC_username() {
		return c_username;
	}

	public void setC_username(String c_username) {
		this.c_username = c_username;
	}

	public String getC_small_avatar() {
		return c_small_avatar;
	}

	public void setC_small_avatar(String c_small_avatar) {
		this.c_small_avatar = c_small_avatar;
	}

	public String getC_large_avatar() {
		return c_large_avatar;
	}

	public void setC_large_avatar(String c_large_avatar) {
		this.c_large_avatar = c_large_avatar;
	}

	public String getC_title() {
		return c_title;
	}

	public void setC_title(String c_title) {
		this.c_title = c_title;
	}

	//
	// public String getC_thumbnail() {
	// return c_thumbnail;
	// }
	//
	// public void setC_thumbnail(String c_thumbnail) {
	// this.c_thumbnail = c_thumbnail;
	// }

	public String getC_big_photo() {
		return c_big_photo;
	}

	public void setC_big_photo(String c_big_photo) {
		this.c_big_photo = c_big_photo;
	}

	public Date getC_time() {
		return c_time;
	}

	public void setC_time(Date c_time) {
		this.c_time = c_time;
	}

	// public int getC_comment_count() {
	// return c_comment_count;
	// }
	//
	// public void setC_comment_count(int c_comment_count) {
	// this.c_comment_count = c_comment_count;
	// }

	public int getC_favor_count() {
		return c_favor_count;
	}

	public void setC_favor_count(int c_favor_count) {
		this.c_favor_count = c_favor_count;
	}

}
