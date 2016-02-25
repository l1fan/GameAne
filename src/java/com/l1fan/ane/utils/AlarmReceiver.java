package com.l1fan.ane.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

public class AlarmReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("receive alarm:"+intent.getExtras().toString());
		if (SDK.ACTION.equals(intent.getAction())) {
			System.out.println("icon:"+context.getApplicationInfo().icon);
			NotificationCompat.Builder builder = new Builder(context)
			.setSmallIcon(context.getApplicationInfo().icon)
			.setContentTitle(intent.getStringExtra("title"))
			.setContentText(intent.getStringExtra("text"));
			
			Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);

			PendingIntent lanuchPendingIntent =
				    PendingIntent.getActivity(context,0,launchIntent,PendingIntent.FLAG_UPDATE_CURRENT);
			
			builder.setContentIntent(lanuchPendingIntent);

			NotificationManager mNotifyMgr = 
			        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotifyMgr.notify(0, builder.build());
		}
	}
}