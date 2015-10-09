package com.lanquan.jsonobject;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.lanquan.utils.DateTimeTools;

import android.text.StaticLayout;

//我的消息实体
public class JsonMyMessage {

	// 消息主体
	private int message_id;
	private String message;// 内容
	private int from_user_id;
	private int to_user_id;
	private int is_read;
	private Date create_time;
	private Date update_time;
	private int object_type;
	private int object_id;
	private String content;
	private String image_url;// 图片
	private String nickname;
	private String avatar;
	private int channel_id;
	private int type;

	public JsonMyMessage(int message_id, String message, int from_user_id, int to_user_id, int is_read, Date create_time, Date update_time, int object_type, int object_id,
			String content, String image_url, String nickname, String avatar, int channel_id, int type) {
		super();
		this.message_id = message_id;
		this.message = message;
		this.from_user_id = from_user_id;
		this.to_user_id = to_user_id;
		this.is_read = is_read;
		this.create_time = create_time;
		this.update_time = update_time;
		this.object_type = object_type;
		this.object_id = object_id;
		this.content = content;
		this.image_url = image_url;
		this.nickname = nickname;
		this.avatar = avatar;
		this.channel_id = channel_id;
		this.type = type;
	}

	public static JsonMyMessage getJsonMyMessage(JSONObject jsonObject) {
		JsonMyMessage jsonMyMessage = null;
		try {
			jsonMyMessage = new JsonMyMessage(Integer.parseInt(jsonObject.getString("message_id")), jsonObject.getString("message"),
					Integer.parseInt(jsonObject.getString("from_user_id")), Integer.parseInt(jsonObject.getString("to_user_id")), Integer.parseInt(jsonObject.getString("is_read")),
					DateTimeTools.StringToDate(jsonObject.getString("create_time")), DateTimeTools.StringToDate(jsonObject.getString("update_time")),
					Integer.parseInt(jsonObject.getString("object_type")), Integer.parseInt(jsonObject.getString("object_id")), jsonObject.getString("content"),
					jsonObject.getString("image_url"), jsonObject.getString("nickname"), jsonObject.getString("avatar"), Integer.parseInt(jsonObject.getString("channel_id")),
					Integer.parseInt(jsonObject.getString("type")));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonMyMessage;
	}

	public int getMessage_id() {
		return message_id;
	}

	public void setMessage_id(int message_id) {
		this.message_id = message_id;
	}

	public String getMessage() {
		return message;
	}

	public int getObject_id() {
		return object_id;
	}

	public void setObject_id(int object_id) {
		this.object_id = object_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getFrom_user_id() {
		return from_user_id;
	}

	public void setFrom_user_id(int from_user_id) {
		this.from_user_id = from_user_id;
	}

	public int getTo_user_id() {
		return to_user_id;
	}

	public void setTo_user_id(int to_user_id) {
		this.to_user_id = to_user_id;
	}

	public int getIs_read() {
		return is_read;
	}

	public void setIs_read(int is_read) {
		this.is_read = is_read;
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

	public int getObject_type() {
		return object_type;
	}

	public void setObject_type(int object_type) {
		this.object_type = object_type;
	}

	public int getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(int channel_id) {
		this.channel_id = channel_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

}
