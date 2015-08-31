package com.lanquan.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lanquan.jsonobject.JsonChannel;
import com.lanquan.jsonobject.JsonChannelComment;

public class JsonChannelTools {
	String jsonString;

	public JsonChannelTools(String jsonString) {
		this.jsonString = jsonString;
	}

	public List<JsonChannelComment> getJsonChannelCommentList() {
		List<JsonChannelComment> jsonChannelComments = new ArrayList<JsonChannelComment>();
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonChannelComments.add(getJsonChannelComment((JSONObject) jsonArray.get(i)));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonChannelComments;
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

	private JsonChannelComment getJsonChannelComment(JSONObject object) {
		try {
			JsonChannelComment jsonChannelComment = new JsonChannelComment(Integer.parseInt(object.getString("article_id")), Integer.parseInt(object
					.getString("channel_id")), object.getString("image_url"), object.getString("message"), object.getString("latitude"),
					object.getString("longitude"), object.getString("address"), Integer.parseInt(object.getString("light")), DateTimeTools.StringToDate(object
							.getString("create_time")), DateTimeTools.StringToDate(object.getString("update_time")), Integer.parseInt(object
							.getString("user_id")), Integer.parseInt(object.getString("status")), Integer.parseInt(object.getString("recommend")),
					object.getString("nickname"), object.getString("avatar"), Integer.parseInt(object.getString("is_light")));
			return jsonChannelComment;
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;

	}
}
