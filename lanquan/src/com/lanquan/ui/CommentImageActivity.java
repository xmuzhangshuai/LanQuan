package com.lanquan.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.lanquan.R;
import com.lanquan.base.BaseActivity;
import com.lanquan.utils.ImageLoaderTool;
import com.lanquan.utils.ImageTools;
import com.lanquan.utils.LogTool;

public class CommentImageActivity extends BaseActivity implements OnClickListener {
	private static final int TAKE_PICTURE = 0;
	private static final int CHOOSE_PICTURE = 1;
	private View takePhoto;
	private View choosePhoto;
	private String photoUri;// 图片地址
	private int channel_id;
	private String channeltitle;
	private String commentcontent;
	private List<String> recentImages;
	private View imageContainer;
	private ImageView[] recentImageViews = new ImageView[10];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_image);
		recentImages = new ArrayList<String>();
		getRecentPhotos();
		channel_id = getIntent().getIntExtra("channel_id", -1);
		channeltitle = getIntent().getStringExtra("channeltitle");
		commentcontent = getIntent().getStringExtra("commentcontent");
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		takePhoto = findViewById(R.id.takePhoto);
		choosePhoto = findViewById(R.id.choosePhoto);
		imageContainer = findViewById(R.id.image_container);
		recentImageViews[0] = (ImageView) findViewById(R.id.imageView1);
		recentImageViews[1] = (ImageView) findViewById(R.id.imageView2);
		recentImageViews[2] = (ImageView) findViewById(R.id.imageView3);
		recentImageViews[3] = (ImageView) findViewById(R.id.imageView4);
		recentImageViews[4] = (ImageView) findViewById(R.id.imageView5);
		recentImageViews[5] = (ImageView) findViewById(R.id.imageView6);
		recentImageViews[6] = (ImageView) findViewById(R.id.imageView7);
		recentImageViews[7] = (ImageView) findViewById(R.id.imageView8);
		recentImageViews[8] = (ImageView) findViewById(R.id.imageView9);
		recentImageViews[9] = (ImageView) findViewById(R.id.imageView10);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		takePhoto.setOnClickListener(this);
		choosePhoto.setOnClickListener(this);
		if (recentImages.size() < 1) {
			imageContainer.setVisibility(View.GONE);
		} else {
			try {// 防止内存溢出造成的崩溃
				for (int i = 0; i < recentImages.size(); i++) {
					final String path = recentImages.get(i);
					recentImageViews[i].setVisibility(View.VISIBLE);
					imageLoader.displayImage("file://" + path, recentImageViews[i], ImageLoaderTool.getRecentImageOptions());
					recentImageViews[i].setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (channel_id > 0) {
								Intent intent = new Intent(CommentImageActivity.this, PublishCommentActivity.class);
								intent.putExtra("path", path);
								intent.putExtra("channel_id", channel_id);
								intent.putExtra("commentcontent", commentcontent);
								intent.putExtra("channeltitle", channeltitle);
								startActivity(intent);
								finish();
							}

						}
					});
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Intent intent = null;
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == TAKE_PICTURE) {// 拍照
			intent = new Intent(CommentImageActivity.this, PublishCommentActivity.class);
			intent.putExtra("path", photoUri);
			intent.putExtra("channel_id", channel_id);
			intent.putExtra("channeltitle", channeltitle);
		} else if (requestCode == CHOOSE_PICTURE) {// 相册
			try {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = CommentImageActivity.this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				photoUri = cursor.getString(columnIndex);
				LogTool.i(cursor.getString(columnIndex));
				cursor.close();
				intent = new Intent(CommentImageActivity.this, PublishCommentActivity.class);
				intent.putExtra("path", photoUri);
				intent.putExtra("channel_id", channel_id);
				intent.putExtra("channeltitle", channeltitle);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		if (intent != null && !TextUtils.isEmpty(photoUri)) {
			startActivity(intent);
			finish();
		}else {
			LogTool.e("CommentImageActivity photoUri为空");
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
			SharedPreferences sharedPreferences = getSharedPreferences("temp", Context.MODE_PRIVATE);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.takePhoto:
			takePhoto();
			break;
		case R.id.choosePhoto:
			choosePhoto();
			break;
		default:
			break;
		}
	}

	public void cancel(View view) {
		finish();
	}

	/**
	 * 获取最近的照片
	 */
	private void getRecentPhotos() {
		String sdcardPath = Environment.getExternalStorageDirectory().toString();

		ContentResolver mContentResolver = CommentImageActivity.this.getContentResolver();
		Cursor mCursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA },
				MediaStore.Images.Media.MIME_TYPE + "=? OR " + MediaStore.Images.Media.MIME_TYPE + "=?", new String[] { "image/jpeg", "image/png" },
				MediaStore.Images.Media._ID + " DESC"); // 按图片ID降序排列

		while (mCursor.moveToNext()) {
			// 打印LOG查看照片ID的值
			// long id =
			// mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
			// Log.i("MediaStore.Images.Media_ID=", id + "");

			// 过滤掉不需要的图片，只获取拍照后存储照片的相册里的图片
			String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
			if (path.startsWith(sdcardPath + "/DCIM")) {
				if (recentImages.size() < 11) {
					recentImages.add(path);
				} else {
					break;
				}
			}
		}
		mCursor.close();
	}
}
