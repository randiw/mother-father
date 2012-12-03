package com.dxtr.vout.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageUtils {

	public static Bitmap resizeImage(Bitmap bmp , int newWidth, int newHeight) {
		try {
		    
		    if (bmp != null) {
		        int width = bmp.getWidth();
		        int height = bmp.getHeight();
		        float scaleWidth = ((float) newWidth) / width;
		        float scaleHeight = ((float) newHeight) / height;
		        Matrix matrix = new Matrix();
		        matrix.postScale(scaleWidth, scaleHeight);
		        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
		        return resizedBitmap;
		    } else {
		    	return null;
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    return null;
		}
	}
	
}
