package com.lanquan.jsonobject;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonMyChannel {
	private String icon;
	private String channel_title;
	private int channel_id;
	private int type;// 频道类型 0=>图文频道 1=>文字频道 2=>打卡频道

	public static JsonMyChannel getJsonMyChannel(JSONObject jsonObject) {
		JsonMyChannel jsonMyChannel = null;
		try {
			jsonMyChannel = new JsonMyChannel(jsonObject.getString("icon"), jsonObject.getString("channel_title"), Integer.parseInt(jsonObject.getString("channel_id")),
					Integer.parseInt(jsonObject.getString("type")));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonMyChannel;
	}

	public JsonMyChannel(String icon, String channel_title, int channel_id, int type) {
		super();
		this.icon = icon;
		this.channel_title = channel_title;
		this.channel_id = channel_id;
		this.type = type;
	}

	public int getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getChannel_title() {
		return channel_title;
	}

	public void setChannel_title(String channel_title) {
		this.channel_title = channel_title;
	}

}
