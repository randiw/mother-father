package com.dxtr.vout.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.dxtr.vout.R;

public class NotificationUtil {
	
	private static NotificationUtil instance;
	private NotificationManager _mNotificationManager;
	private int NOTIFICATION_ID = 0;
	private int _unreadNotificationNum;
	
	private NotificationUtil(){
	}
	
	public static NotificationUtil getInstance() {
		if (instance == null) {
			instance = new NotificationUtil();
		}
		return instance;
	}
	
	public void show(Context context, String alert, String title, String content, Class<?> destClass){
		Intent notifyIntent = new Intent(context, destClass);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, notifyIntent, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);

	    Notification noti = new NotificationCompat.Builder(context)
	        .setContentTitle(alert)
	        .setContentText(content)
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(pIntent)
	        .build();
	    NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	    
	    long[] vibrate = { 100, 100, 200, 300 };
	    noti.vibrate = vibrate;
	    noti.defaults = Notification.DEFAULT_ALL;
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;
	    noti.number = _unreadNotificationNum;
	    notificationManager.notify(NOTIFICATION_ID, noti);
	    _unreadNotificationNum++;
	}
	
	public void clear(){
		_unreadNotificationNum = 0;
		if(_mNotificationManager != null) _mNotificationManager.cancelAll();
	}
}
