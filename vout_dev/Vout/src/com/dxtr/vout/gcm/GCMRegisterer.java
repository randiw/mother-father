package com.dxtr.vout.gcm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.dxtr.vout.prop.ApplicationProperties;
import com.dxtr.vout.prop.URLAddress;
import com.dxtr.vout.tools.InternetConnectionListener;
import com.dxtr.vout.tools.JParser;
import com.dxtr.vout.tools.PostInternetTask;

public class GCMRegisterer {
	
    public static void registerGCM(Context context) {
        Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
		registrationIntent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
		registrationIntent.putExtra("sender", ApplicationProperties.GCM_PROJECT_ID);
		context.startService(registrationIntent);
    }
    
    public static void sendDeviceIdToServer(final Context context, final String deviceId){
    	PostInternetTask internetTask = new PostInternetTask(new InternetConnectionListener() {			
			public void onStart() {
			}
			
			public void onDone(String str) {
				JParser parser = new JParser(str);
				if (parser.getStatus()) {
					SharedPreferences pref  = context.getSharedPreferences(ApplicationProperties.NAME, 0);
					SharedPreferences.Editor editor = pref.edit();
		            editor.putString("device_id", deviceId);
		            editor.commit();
				}
			}
		});
    	internetTask.addPair("device_id", deviceId);
    	internetTask.execute(URLAddress.SET_DEVICE_ID);
    }
}