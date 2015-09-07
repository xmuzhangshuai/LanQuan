package com.lanquan.zxingqrcode;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
/**
 * @author Ryan Tang
 *
 */
public final class EncodingHandler {
	private static final int BLACK = 0xff000000;
	
	
	//生成二维码
	public static Bitmap createQRCode(String str,int widthAndHeight) throws WriterException {
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();  
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); 
		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = BLACK;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		
		return bitmap;
	}
	
	
	//在二维码基础上继续重绘频道二维码
	public static Bitmap createChannelCode(Bitmap top, Bitmap icon, Bitmap content,Bitmap blue_content,Bitmap middle,Bitmap bottom){
		
		 Matrix matrix=new Matrix();  
         matrix.postScale(1.0f, 10.0f);  
         content = Bitmap.createBitmap(content, 0, 0, content.getWidth(),  
        		 content.getHeight(),matrix,true);  
		
         matrix.postScale(1.0f, 1.0f);  
         blue_content = Bitmap.createBitmap(blue_content, 0, 0, blue_content.getWidth(),  
        		 blue_content.getHeight(),matrix,true);  
         
		try {
			Bitmap src;
			src = createQRCode("帅哥就是帅！", 625);
			int width = top.getWidth(), height = 1500;
			
			System.out.println(width);
			//創建一個新的和SRC長度寬度一樣的位圖
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			
			
			int _top = top.getHeight();
			int _top_content = top.getHeight()+content.getHeight();
			int _top_content_middle = top.getHeight()+content.getHeight()+middle.getHeight();
			int _top_content_middle_bluecontent = top.getHeight()+content.getHeight()+middle.getHeight()+blue_content.getHeight();
			
			System.out.println(_top_content_middle_bluecontent+"height");
			//框架
			canvas.drawBitmap(top, 0, 0, null);
			canvas.drawBitmap(content, 0, _top, null);
			canvas.drawBitmap(middle, 0, _top_content, null);
			canvas.drawBitmap(blue_content, 0, _top_content_middle, null);
			canvas.drawBitmap(bottom, 0 , _top_content_middle_bluecontent, null);
			
			//頻道icon
			canvas.drawBitmap(icon, 50, 50, null);
			//頻道名稱
			Paint p = new Paint();
			p.setTextSize(50);
			
			p.setColor(Color.DKGRAY);
			int w_icon = icon.getWidth();
			int h_icon = icon.getHeight(); 
			
			canvas.drawText("怎么给篮球技术一个质的提高", 70 + w_icon, 70 + h_icon/2, p);
			
			
			//頻道內容
			p.setColor(Color.DKGRAY);
			p.setTextSize(40);
			canvas.drawText("如何从一个菜鸟蜕变成一个灌篮高手呢?", 50, top.getHeight()+content.getHeight()+middle.getHeight()+50, p);
			//二維碼
			
			
			int w_src = src.getWidth();
			int h_src = src.getHeight();
			canvas.drawBitmap(src, width/2-w_src/2,  _top_content_middle_bluecontent-50, null);
			
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
			System.out.println("調用");
			
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
