package com.l1fan.ane.paojiao;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.l1fan.ane.SDKContext;
import com.paojiao.sdk.PJSDK;
import com.paojiao.sdk.bean.UserBean;
import com.paojiao.sdk.listener.LoginListener;
import com.paojiao.sdk.listener.PayListener;
import com.paojiao.sdk.listener.SplashListener;

public class SDK extends SDKContext {

	public void init() throws JSONException{
		regLifecycle();
		JSONObject jd = getJsonData();
		Bundle md = getMetaData();
		
		int appId = jd.optInt(APPID,md.getInt(APPID));
		String appKey = jd.optString(APPKEY,md.getString(APPKEY));
		
		PJSDK.setSplashListener(new SplashListener() {
			
			@Override
			public void onSplashComplete() {
				dispatchData(EVENT_INIT);
			}
		});
		
		PJSDK.initialize(getActivity(), appId, appKey, true);

	}
	
	public void userLogin(){
		PJSDK.doLogin(new LoginListener(){
			
			@Override
			public void onSuccess(UserBean user) {
				JSONObject data = new JSONObject();
				try {
					data.put(UID, user.getUid());
					data.put(UNAME, user.getUserName());
					data.put(TOKEN, user.getToken());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dispatchData(EVENT_LOGIN, data);
				
			}
			
			@Override
			public void onFailure() {
				dispatchError(EVENT_LOGIN, "login failed");
			}
		});
	}
	
	@Override
	protected void onPause() {
		PJSDK.hideFloatingView();
	}
	
	@Override
	protected void onResume() {
		PJSDK.showFloatingView();
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		PJSDK.doPay(pay.optString(PNAME), pay.optInt(AMOUNT)/100.00f, pay.optString(EXT), pay.optString(ORDER_ID),new PayListener() {
			
			@Override
			public void onPaySuccess() {
				dispatchData(EVENT_PAY);
			}
			
			@Override
			public void onPayFailure() {
				dispatchError(EVENT_PAY, "pay failed");
			}
			
			@Override
			public void onPayCancel() {
				dispatchError(EVENT_PAY, CODE_ERR_CANCEL, "pay cancel");
			}
		});
	}
	
}
