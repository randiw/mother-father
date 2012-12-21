package com.dxtr.vout.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageUtil {

//	public static Bitmap resizeImage(Bitmap bmp , int newWidth, int newHeight) {
//		try {
//		    
//		    if (bmp != null) {
//		        int width = bmp.getWidth();
//		        int height = bmp.getHeight();
//		        float scaleWidth = ((float) newWidth) / width;
//		        float scaleHeight = ((float) newHeight) / height;
//		        Matrix matrix = new Matrix();
//		        matrix.postScale(scaleWidth, scaleHeight);
//		        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
//		        return resizedBitmap;
//		    } else {
//		    	return null;
//		    }
//		} catch (Exception e) {
//		    e.printStackTrace();
//		    return null;
//		}
//	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
	
	public static File ConvertToFile(Context context, Bitmap bitmap, int quality, String filename){
		File f = new File(context.getCacheDir(), filename);
        try {
			f.createNewFile();
			//Convert bitmap to byte array
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        bitmap.compress(CompressFormat.JPEG, quality, bos);
	        byte[] bitmapdata = bos.toByteArray();
	        //write the bytes in file
	        FileOutputStream fos = new FileOutputStream(f);
	        fos.write(bitmapdata);
	        fos.flush();
	        fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return f;
	}
	
}
