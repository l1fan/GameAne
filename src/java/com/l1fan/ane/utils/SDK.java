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
	
	//对应ios NSClendarUnit 枚举 ，2为每年，3为每月...  秒为单位
	static final long INTERVAL[] = {0,0,365*24*60*60,30*24*60*60,24*60*60,60*60,60,1,7*24*60*60};
	
	private Activity context;
	
	public void localNotify() throws JSONException{
		context = getActivity();
		schedule();
	}
	
	public void cancelLocalNotify(){
		String data =  getData();
		Intent intent = new Intent(ACTION);
		PendingIntent pi = PendingIntent.getBroadcast(context, data.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}
	
	public void cancelAllLocalNotify(){
		
	}
	
	private void schedule() throws JSONException{
		JSONObject notify = getJsonData();
		Intent intent = new Intent(ACTION);
		
		intent.putExtra("title", notify.optString("title"));
		intent.putExtra("text", notify.optString("text"));
		intent.putExtra("id", notify.optString("id"));

		String id =  notify.optString("id","0");
		PendingIntent pi = PendingIntent.getBroadcast(context, id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		System.out.println("cu ts : "+ System.currentTimeMillis());
		System.out.println("notify is:"+notify.toString());
		
		long interval;
		try{
			interval = INTERVAL[notify.optInt("every",0)] * 1000;
		}catch(Exception e){
			interval = 0;
		}
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, notify.optLong("at"), interval, pi);
	}
}
