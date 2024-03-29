/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    https://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.loopj.android.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;

import com.lanquan.base.BaseApplication;
import com.lanquan.ui.LoginActivity;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.UserPreference;

import android.content.Context;
import android.content.Intent;

/**
 * Used to intercept and handle the responses from requests made using {@link AsyncHttpClient}. The
 * {@link #onSuccess(int, org.apache.http.Header[], String)} method is designed to be anonymously
 * overridden with your own response handling code. <p>&nbsp;</p> Additionally, you can override the
 * {@link #onFailure(int, org.apache.http.Header[], String, Throwable)}, {@link #onStart()}, and
 * {@link #onFinish()} methods as required. <p>&nbsp;</p> For example: <p>&nbsp;</p>
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient();
 * client.get("https://www.google.com", new TextHttpResponseHandler() {
 *     &#064;Override
 *     public void onStart() {
 *         // Initiated the request
 *     }
 *
 *     &#064;Override
 *     public void onSuccess(String responseBody) {
 *         // Successfully got a response
 *     }
 *
 *     &#064;Override
 *     public void onFailure(String responseBody, Throwable e) {
 *         // Response failed :(
 *     }
 *
 *     &#064;Override
 *     public void onFinish() {
 *         // Completed the request (either success or failure)
 *     }
 * });
 * </pre>
 */
public abstract class TextHttpResponseHandler extends AsyncHttpResponseHandler {

	private static final String LOG_TAG = "TextHttpRH";

	/**
	 * Creates new instance with default UTF-8 encoding
	 */
	public TextHttpResponseHandler() {
		this(DEFAULT_CHARSET);
	}

	/**
	 * Creates new instance with given string encoding
	 *
	 * @param encoding String encoding, see {@link #setCharset(String)}
	 */
	public TextHttpResponseHandler(String encoding) {
		super();
		setCharset(encoding);
	}

	/**
	 * Called when request fails
	 *
	 * @param statusCode     http response status line
	 * @param headers        response headers if any
	 * @param responseString string response of given charset
	 * @param throwable      throwable returned when processing request
	 */
	public abstract void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable);

	/**
	 * Called when request succeeds
	 *
	 * @param statusCode     http response status line
	 * @param headers        response headers if any
	 * @param responseString string response of given charset
	 */
	public abstract void onSuccess(int statusCode, Header[] headers, String responseString);

	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
		String response = getResponseString(responseBytes, getCharset());
		if (statusCode / 100 != 2) {
			JsonTool jsonTool = new JsonTool(response);
			if (jsonTool.getMessage().contains("access_token失效")) {
				context.startActivity(new Intent(context, LoginActivity.class));
				UserPreference userPreference = BaseApplication.getInstance().getUserPreference();
				userPreference.clear();
			}
		}
		onSuccess(statusCode, headers, response);
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
		String response = getResponseString(responseBytes, getCharset());
		if (statusCode != 0) {
			JsonTool jsonTool = new JsonTool(response);
			if (jsonTool.getMessage().contains("access_token失效") || jsonTool.getMessage().contains("该用户不存在")) {
				context.startActivity(new Intent(context, LoginActivity.class));
				UserPreference userPreference = BaseApplication.getInstance().getUserPreference();
				userPreference.clear();
			}
		}

		onFailure(statusCode, headers, response, throwable);
	}

	/**
	 * Attempts to encode response bytes as string of set encoding
	 *
	 * @param charset     charset to create string with
	 * @param stringBytes response bytes
	 * @return String of set encoding or null
	 */
	public static String getResponseString(byte[] stringBytes, String charset) {
		try {
			String toReturn = (stringBytes == null) ? null : new String(stringBytes, charset);
			if (toReturn != null && toReturn.startsWith(UTF8_BOM)) {
				return toReturn.substring(1);
			}
			return toReturn;
		} catch (UnsupportedEncodingException e) {
			AsyncHttpClient.log.e(LOG_TAG, "Encoding response into string failed", e);
			return null;
		}
	}
}
