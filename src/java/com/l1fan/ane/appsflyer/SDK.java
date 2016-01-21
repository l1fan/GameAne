package com.l1fan.ane.appsflyer;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.appsflyer.AppsFlyerLib;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {
	
	public void init() throws JSONException {
		JSONObject init = getJsonData();
		AppsFlyerLib.setAppsFlyerKey(init.optString("devKey","kFZnPcRtTVMmje5YeX5dAX"));
		AppsFlyerLib.sendTracking(getActivity());
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
}
