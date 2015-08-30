package com.lanquan.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lanquan.jsonobject.JsonChannel;

public class JsonChannelTools {
	String jsonString;

	public JsonChannelTools(String jsonString) {
		this.jsonString = jsonString;
	}

	public List<JsonChannel> getJsonChannelList() {
		List<JsonChannel> jsonChannels = new ArrayList<JsonChannel>();
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonChannels.add(getJsonChannel((JSONObject) jsonArray.get(i)));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonChannels;
	}

	private JsonChannel getJsonChannel(JSONObject object) {
		try {
			JsonChannel jsonChannel = new JsonChannel(Integer.parseInt(object.getString("channel_id")), object.getString("title"),
					object.getString("description"), object.getString("recommend_background"), Integer.parseInt(object.getString("type")),
					Integer.parseInt(object.getString("user_id")), Integer.parseInt(object.getString("status")), DateTimeTools.StringToDate(object
							.getString("create_time")), DateTimeTools.StringToDate(object.getString("update_time")), Integer.parseInt(object
							.getString("recommend")), object.getString("icon"), Integer.parseInt(object.getString("from")), object.getString("nickname"),
					object.getString("avatar"));

			return jsonChannel;
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;

	}

}
