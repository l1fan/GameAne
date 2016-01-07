package com.l1fan.ane.oppo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Build;
import android.os.Bundle;

import com.l1fan.ane.SDKContext;
import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.callback.ApiCallback;
import com.nearme.game.sdk.callback.GameExitCallback;
import com.nearme.game.sdk.common.model.biz.GameCenterSettings;
import com.nearme.game.sdk.common.model.biz.PayInfo;

public class SDK extends SDKContext {
	
	public void init() {
		lifeCycle();
		JSONObject json = new JSONObject();
		String appKey = json.optString(APPKEY);
		String appSecret = json.optString(APPSECRET);
		boolean debug = json.optBoolean(DEBUGMODE,false);
		boolean ori	= json.optBoolean(ORIENTATION,true);
		GameCenterSettings gameCenterSettings = new GameCenterSettings(false,appKey,appSecret,debug,ori);
		
		GameCenterSDK.init(gameCenterSettings, getActivity());
		dispatchData(EVENT_INIT);
	}
	
	public void userLogin() {
		
		GameCenterSDK.getInstance().doLogin(getActivity(), new ApiCallback() {
			
			@Override
			public void onSuccess(String msg) {
				try {
					JSONObject json = new JSONObject(msg);
					String token = json.getString("token"); 
					String ssoid = json.getString("ssoid");
					
					JSONObject data = new JSONObject();
					data.put(TOKEN, token);
					data.put(UID, ssoid);
					dispatchData(EVENT_LOGIN, data);
				} catch (JSONException e) {
					dispatchError(EVENT_LOGIN, e.getMessage());
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(String msg, int code) {
				dispatchError(EVENT_LOGIN, "login fail:"+msg+":"+code);
			}
		});
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		
		PayInfo payInfo = new PayInfo(pay.optString(ORDER_ID), pay.optString(EXT), pay.optInt(AMOUNT));
		payInfo.setProductName(pay.optString(PNAME));
		payInfo.setCallbackUrl(pay.optString(NOTIFY_URL));
		
		GameCenterSDK.getInstance().doPay(getActivity(), payInfo, new ApiCallback() {
			
			@Override
			public void onSuccess(String msg) {
				dispatchData(EVENT_PAY,"pay success:"+msg);
			}
			
			@Override
			public void onFailure(String msg, int code) {
				dispatchError(EVENT_PAY, "pay fail:"+msg+":"+code);
			}
		});
	}
	
	@Override
	public void dispose() {
		super.dispose();
		GameCenterSDK.getInstance().onExit(getActivity(), new GameExitCallback() {
			
			@Override
			public void exitGame() {
				
			}
		});
	}
	
	private void lifeCycle() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return;
		}
		getActivity().getApplication().registerActivityLifecycleCallbacks(
				new ActivityLifecycleCallbacks() {

					@Override
					public void onActivityStopped(Activity arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onActivityStarted(Activity arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onActivitySaveInstanceState(Activity arg0,
							Bundle arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onActivityResumed(Activity arg0) {
						GameCenterSDK.getInstance().onResume(arg0);
					}

					@Override
					public void onActivityPaused(Activity arg0) {
						GameCenterSDK.getInstance().onPause();
					}

					@Override
					public void onActivityDestroyed(Activity arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onActivityCreated(Activity arg0, Bundle arg1) {
						// TODO Auto-generated method stub

					}
				});		
	}
	
}
