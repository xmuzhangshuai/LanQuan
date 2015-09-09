package com.lanquan.jsonobject;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonMyChannel {
	private String icon;
	private String channel_title;

	public static JsonMyChannel getJsonMyChannel(JSONObject jsonObject) {
		JsonMyChannel jsonMyChannel = null;
		try {
			jsonMyChannel = new JsonMyChannel(jsonObject.getString("icon"), jsonObject.getString("channel_title"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonMyChannel;
	}

	public JsonMyChannel(String icon, String channel_title) {
		super();
		this.icon = icon;
		this.channel_title = channel_title;
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
