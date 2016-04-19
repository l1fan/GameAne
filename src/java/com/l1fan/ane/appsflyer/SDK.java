package com.l1fan.ane.appsflyer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {
	
	private Info mAdInfo;

	public void init() throws JSONException {
		JSONObject init = getJsonData();
		AppsFlyerLib.setAppsFlyerKey(init.optString("devKey","kFZnPcRtTVMmje5YeX5dAX"));
		AppsFlyerLib.sendTracking(getActivity());
		final Activity activity = getActivity();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					System.out.println("current thread:"+Thread.currentThread().getName());
					mAdInfo = AdvertisingIdClient.getAdvertisingIdInfo(activity);
					System.out.println("---dispatch---");
					dispatchData(EVENT_INIT, "google play adid init success");
				} catch (IllegalStateException
						| GooglePlayServicesRepairableException | IOException
						| GooglePlayServicesNotAvailableException e) {
					dispatchError(EVENT_INIT, e.getMessage());
					e.printStackTrace();
				}	
			}
		}).start();
		

	}
	
	public void track() throws JSONException{
		JSONObject track = getJsonData();
		String type = track.optString("type");
		Iterator<String> keys = track.keys();
		HashMap<String, Object> map = new HashMap<String,Object>();
		while (keys.hasNext()) {
			String key = keys.next();
			if (!"type".equals(key)) {
				map.put(key, track.get(key));
			}
		}
		
		System.out.println("type ->"+type+";eventValues->"+map.toString());
		AppsFlyerLib.trackEvent(getActivity(), type, map);
	}
	
	public String getAppsFlyerUID(){
		return AppsFlyerLib.getAppsFlyerUID(getActivity());
	}
	
	public String getAdID(){
		if (mAdInfo == null) {
			dispatchError(EVENT_ERROR, "google play adid info is null , please check init info");
			return "";
		}
		return mAdInfo.getId();

	}
	
	public boolean isAdEnable(){
		if (mAdInfo == null) {
			dispatchError(EVENT_ERROR, "google play adid info is null , please check init info");
			return false;
		}
		return !mAdInfo.isLimitAdTrackingEnabled();
	}
}
