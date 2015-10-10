package com.lanquan.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.lanquan.R;
import com.lanquan.base.BaseApplication;
import com.lanquan.customwidget.MyMenuDialog;
import com.lanquan.utils.AsyncHttpClientTool;
import com.lanquan.utils.FileSizeUtil;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.ImageTools;
import com.lanquan.utils.JsonTool;
import com.lanquan.utils.LogTool;
import com.lanquan.utils.ToastTool;
import com.lanquan.utils.UserPreference;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/** 
 * 类描述 ：打卡的对话框
 * 类名： ChannelPunchCardActivity.java  
 * Copyright:   Copyright (c)2015    
 * Company:     zhangshuai   
 * @author:     zhangshuai    
 * @version:    1.0    
 * 创建时间:    2015-8-20 下午2:52:57  
*/
class PunchDialogFragment extends DialogFragment implements OnClickListener {
	private static final int TAKE_PICTURE = 300;
	private static final int CHOOSE_PICTURE = 301;
	private String photoUri;// 图片地址
	private View rootView;
	private TextView confirmBtn;// 确定
	private View uploadHeadImageBtn;
	private ImageView headImage;// 头像
	private ImageView cameraImage;
	private EditText contenteEditText;
	private TextView locationText;//定位
	private String detailLocation;// 详细地址
	private ImageLoader imageLoader;
	private ProgressDialog dialog;
	private UserPreference userPreference;
	private String latitude;
	private String longtitude;
	private int channel_id;
	private ChannelPunchCardActivity channelPunchCardActivity;

	public PunchDialogFragment() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		channelPunchCardActivity = (ChannelPunchCardActivity) getActivity();
		return super.onCreateDialog(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		imageLoader = ImageLoader.getInstance();
		detailLocation = getArguments().getString("detailLocation");
		latitude = getArguments().getString("latitude");
		longtitude = getArguments().getString("longtitude");
		channel_id = getArguments().getInt("channel_id");
		userPreference = BaseApplication.getInstance().getUserPreference();
		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_dialog_punch, container, false);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		findViewById();
		initView();
		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		LogTool.e("requestCode" + requestCode + "resultCode" + resultCode + "data" + data);
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK && resultCode != 2) {
			photoUri = null;
			return;
		}

		if (requestCode == TAKE_PICTURE) {// 拍照
			PunchDialogFragment prev = (PunchDialogFragment) getFragmentManager().findFragmentByTag("punch_dialog");
			if (prev != null) {
				prev.setImage();
			}
		} else if (requestCode == CHOOSE_PICTURE) {// 相册
			try {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = channelPunchCardActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				photoUri = cursor.getString(columnIndex);
				LogTool.i(cursor.getString(columnIndex));
				cursor.close();
				setImage();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	private void findViewById() {
		confirmBtn = (TextView) rootView.findViewById(R.id.confirm);
		uploadHeadImageBtn = (View) rootView.findViewById(R.id.gethead_btn);
		headImage = (ImageView) rootView.findViewById(R.id.headimage);
		cameraImage = (ImageView) rootView.findViewById(R.id.camera_image);
		locationText = (TextView) rootView.findViewById(R.id.location);
		contenteEditText = (EditText) rootView.findViewById(R.id.content);
	}

	private void initView() {
		locationText.setText(detailLocation);
		confirmBtn.setOnClickListener(this);
		headImage.setVisibility(View.GONE);
		cameraImage.setVisibility(View.VISIBLE);
		uploadHeadImageBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPicturePicker(channelPunchCardActivity);
			}
		});
	}

	public void setImage() {
		if (photoUri != null && photoUri.length() > 0) {
			headImage.setVisibility(View.VISIBLE);
			cameraImage.setVisibility(View.GONE);
			imageLoader.displayImage("file://" + photoUri, headImage, ImageLoaderTool.getCircleHeadImageOptions());
		} else {
			LogTool.e("图片地址为空");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.confirm:
			String contentString = contenteEditText.getText().toString();
			if (contentString == null || contentString.isEmpty()) {
				contentString = "嘿！我在这儿！";
			}
			if (photoUri != null && photoUri.length() > 0) {
				uploadImage(photoUri, contentString);
			} else {
				punchCard(contentString, "");
			}
			PunchDialogFragment.this.dismiss();
			break;
		default:
			break;
		}
	}

	/**
	 * 打卡
	 */
	private void punchCard(String content, String imageUrl) {
		RequestParams params = new RequestParams();
		params.put("message", content);
		params.put("image_url", imageUrl);
		params.put("address", detailLocation);
		params.put("latitude", latitude);
		params.put("longitude", longtitude);
		params.put("access_token", userPreference.getAccess_token());
		params.put("channel_id", channel_id);
		if (dialog == null) {
			dialog = new ProgressDialog(channelPunchCardActivity);
			dialog.setMessage("请稍后...");
			dialog.setCancelable(false);
			dialog.show();
		}

		TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				dialog.dismiss();
				super.onFinish();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, String response) {
				// TODO Auto-generated method stub
				JsonTool jsonTool = new JsonTool(response);
				String status = jsonTool.getStatus();
				if (status.equals(JsonTool.STATUS_SUCCESS)) {
					userPreference.setArticle_count(userPreference.getArticle_count() + 1);
					channelPunchCardActivity.refreshData();
					ToastTool.showShort(channelPunchCardActivity, jsonTool.getMessage());
				} else if (status.equals(JsonTool.STATUS_FAIL)) {
					LogTool.e(jsonTool.getMessage());
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
				// TODO Auto-generated method stub
				LogTool.e("打卡错误" + statusCode + errorResponse);
			}
		};
		AsyncHttpClientTool.post(channelPunchCardActivity, "api/article/create", params, responseHandler);
	}

	/**
	 * 上传图片
	 * @param filePath
	 */
	public void uploadImage(final String imageUrl, final String content) {
		File f = new File(imageUrl);
		int size = (int) FileSizeUtil.getFileOrFilesSize(imageUrl, FileSizeUtil.SIZETYPE_KB);
		if (!f.exists() || size < 1) {
			return;
		}
		String tempPath = Environment.getExternalStorageDirectory() + "/lanquan/image";
		String photoName = "temp" + ".jpg";
		File file = ImageTools.compressBySizeAndQuality(tempPath, photoName, imageUrl, 400);
		LogTool.e("图片大小：" + FileSizeUtil.getAutoFileOrFilesSize(file.getAbsolutePath()));
		dialog = new ProgressDialog(channelPunchCardActivity);
		dialog.setMessage("正在打卡...");
		dialog.setCancelable(false);

		if (file.exists()) {
			RequestParams params = new RequestParams();
			try {
				params.put("userfile", file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					dialog.show();
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String response) {
					// TODO Auto-generated method stub
					LogTool.i("" + statusCode + response);
					JsonTool jsonTool = new JsonTool(response);
					String status = jsonTool.getStatus();
					if (status.equals(JsonTool.STATUS_SUCCESS)) {
						JSONObject jsonObject = jsonTool.getJsonObject();
						if (jsonObject != null) {
							String url;
							try {
								url = jsonObject.getString("url");
								LogTool.i("url" + url);
								// 图片上传成功后调用评论
								punchCard(content, url);
								photoUri = "";
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else if (status.equals(JsonTool.STATUS_FAIL)) {
						LogTool.e("上传fail");
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
					// TODO Auto-generated method stub
					LogTool.e("图像上传失败！" + errorResponse);
					dialog.dismiss();
				}
			};
			AsyncHttpClientTool.post(channelPunchCardActivity, "api/file/upload", params, responseHandler);
		} else {
			LogTool.e("本地文件为空");
		}
	}

	/**
	* 从相册选择图片
	*/
	private void choosePhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, CHOOSE_PICTURE);
	}

	/**
	 * 拍照
	 */
	private void takePhoto() {
		try {
			File uploadFileDir = new File(Environment.getExternalStorageDirectory(), "/lanquan/image");
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (!uploadFileDir.exists()) {
				uploadFileDir.mkdirs();
			}
			String fileName;

			// 删除上一次的临时文件
			SharedPreferences sharedPreferences = channelPunchCardActivity.getSharedPreferences("temp", Context.MODE_WORLD_WRITEABLE);
			ImageTools.deletePhotoAtPathAndName(Environment.getExternalStorageDirectory().getAbsolutePath(), sharedPreferences.getString("tempName", ""));

			// 保存本次截图临时文件名字
			fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
			Editor editor = sharedPreferences.edit();
			editor.putString("tempName", fileName);
			editor.commit();

			File picFile = new File(uploadFileDir, fileName);

			if (!picFile.exists()) {
				picFile.createNewFile();
			}

			photoUri = picFile.getAbsolutePath();
			Uri takePhotoUri = Uri.fromFile(picFile);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoUri);
			startActivityForResult(cameraIntent, TAKE_PICTURE);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	* 显示对话框，从拍照和相册选择图片来源
	* 
	* @param context
	* @param isCrop
	*/
	private void showPicturePicker(Context context) {

		final MyMenuDialog myMenuDialog = new MyMenuDialog(channelPunchCardActivity);
		myMenuDialog.setTitle("图片来源");
		ArrayList<String> list = new ArrayList<String>();
		list.add("拍照");
		list.add("相册");
		myMenuDialog.setMenuList(list);
		OnItemClickListener listener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:// 如果是拍照
					myMenuDialog.dismiss();
					takePhoto();
					break;
				case 1:// 从相册选取
					myMenuDialog.dismiss();
					choosePhoto();
					break;

				default:
					break;
				}
			}
		};
		myMenuDialog.setListItemClickListener(listener);

		myMenuDialog.show();
	}

}
