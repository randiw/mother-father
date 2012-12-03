package com.dxtr.vout.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class AppUtils {

	public static int GetScreenWidth(Activity act) {
		DisplayMetrics dm = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
}
