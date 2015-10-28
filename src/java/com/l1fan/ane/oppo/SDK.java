package com.l1fan.ane.oppo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.l1fan.ane.SDKContext;
import com.nearme.gamecenter.open.api.ApiCallback;
import com.nearme.gamecenter.open.api.GameCenterSDK;
import com.nearme.gamecenter.open.api.GameCenterSettings;
import com.nearme.oauth.model.UserInfo;

public class SDK extends SDKContext {
	
	public void init() {
		
		JSONObject json = new JSONObject();
		String appKey = json.optString(APPKEY);
		String appSecret = json.optString(APPSECRET);
		
		GameCenterSettings gameCenterSettings = new GameCenterSettings(appKey,appSecret) {
			
			@Override
			public void onForceUpgradeCancel() {
				dispatchError(EVENT_ERROR, "need to re-login");
			}
			
			@Override
			public void onForceReLogin() {
				dispatchError(EVENT_UPDATE, "update cancel");
			}
		};
		
		GameCenterSettings.isDebugModel = json.optBoolean(DEBUGMODE);
		GameCenterSettings.isOritationPort = json.optBoolean(ORIENTATION);
		GameCenterSDK.init(gameCenterSettings, getActivity());
		dispatchData(EVENT_INIT);
	}
	
	public void userLogin() {
		
		final Activity activity = getActivity();
		final GameCenterSDK sdk = GameCenterSDK.getInstance();

		GameCenterSDK.setmCurrentContext(activity);
		sdk.doLogin(new ApiCallback() {
			
			@Override
			public void onSuccess(String content, int code) {
				final JSONObject json = new JSONObject();
				try {
					json.put(TOKEN, sdk.doGetAccessToken());
					sdk.doGetUserInfo(new ApiCallback() {
						
						@Override
						public void onSuccess(String content, int paramInt) {
							UserInfo ui = new UserInfo(content);
							try {
								json.put(UID, ui.id);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							dispatchData(EVENT_LOGIN,json);
						}
						
						@Override
						public void onFailure(String content, int paramInt) {
							dispatchError(EVENT_LOGIN, content);

						}
					}, activity);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				
			}
			
			@Override
			public void onFailure(String content, int code) {
				dispatchError(EVENT_LOGIN, content);
			}
		}, activity);
	}
	
	public void pay(){
		//TODO: unfinished
	}
	
	public void toolBarShow() {
		GameCenterSDK.getInstance().doShowSprite(getActivity());
	}
	
	public void toolBarHide(){
		GameCenterSDK.getInstance().doDismissSprite(getActivity());
	}
}
