package com.lanquan.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class CopyDataBase {
	@SuppressLint("SdCardPath")
	private static String DATABASE_PATH = "/data/data/com.lanquan/databases/";
	public static SQLiteDatabase database;
	public static String dbName = "lanquan.db";
	private Context myContext;

	public CopyDataBase(Context paramContext) {
		this.myContext = paramContext;
	}

	public void copyDataBase() {
		// 检查 SQLite 数据库文件是否存在
		if (!new File(DATABASE_PATH + dbName).exists()) {
			// 如 SQLite 数据库文件不存在，再检查一下 database 目录是否存在
			File localFile = new File(DATABASE_PATH);
			// 如 database 目录不存在，新建该目录
			if (!localFile.exists())
				localFile.mkdir();
			try {
				// 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
				InputStream localInputStream = this.myContext.getAssets().open(dbName);
				// 输出流
				FileOutputStream localFileOutputStream = new FileOutputStream(DATABASE_PATH + dbName);
				// 文件写入
				byte[] arrayOfByte = new byte[1024];
				while (true) {
					int i = localInputStream.read(arrayOfByte);
					if (i <= 0) {
						// 关闭流
						localFileOutputStream.flush();
						localFileOutputStream.close();
						localInputStream.close();
						return;
					}
					localFileOutputStream.write(arrayOfByte, 0, i);
				}
			} catch (FileNotFoundException localFileNotFoundException) {
				localFileNotFoundException.printStackTrace();
				return;
			} catch (IOException localIOException) {
				localIOException.printStackTrace();
			}
		}

	}
}
