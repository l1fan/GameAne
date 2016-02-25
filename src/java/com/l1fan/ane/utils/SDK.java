package com.l1fan.ane.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {
	
	static final String ACTION = "com.l1fan.action.alarm.start"; 
	
	private Activity context;
	
	public void localNotify() throws JSONException{
		context = getActivity();
		schedule();
	}
	
	public void cancel(){
		
	}
	
	private void schedule() throws JSONException{
		JSONObject notify = getJsonData();
		Intent intent = new Intent(ACTION);
		intent.putExtra("title", notify.optString("title"));
		intent.putExtra("text", notify.optString("text"));
		
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		System.out.println("cu ts : "+ System.currentTimeMillis());
		System.out.println("notify is:"+notify.toString());
		
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (notify.has("every")) {
			am.setRepeating(AlarmManager.RTC_WAKEUP, notify.optLong("at"), notify.optLong("every"), pi);
		}else{
		    am.set(AlarmManager.RTC_WAKEUP, notify.optLong("at"), pi);
		}
	}
}
