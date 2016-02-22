package com.l1fan.ane.utils;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {
	
	public void test() throws JSONException{
		JSONObject notify = getJsonData();
		final Activity context = getActivity();
		NotificationCompat.Builder builder = new Builder(context)
		.setSmallIcon(0)
		.setContentTitle(notify.optString("title"))
		.setContentText(notify.optString("text"));
		
		Intent resultIntent = new Intent(context, context.getClass());
		PendingIntent resultPendingIntent =
			    PendingIntent.getActivity(context,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		
		builder.setContentIntent(resultPendingIntent);
		
		
		Intent intent = new Intent(context,AlarmReceiver.class);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, notify.optLong("at"), notify.optLong("every"), operation);
		am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, notify.optLong("atMillis"), operation);
		
		Calendar.getInstance().setTime(date);
		int mNotificationId = 001;

		NotificationManager mNotifyMgr = 
		        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(mNotificationId, builder.build());
	}
	
	class AlarmReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			
		}
	}
}
