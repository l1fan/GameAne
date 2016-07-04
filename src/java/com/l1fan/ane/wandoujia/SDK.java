package com.l1fan.ane.wandoujia;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.l1fan.ane.SDKContext;
import com.wandoujia.mariosdk.plugin.api.api.WandouGamesApi;
import com.wandoujia.mariosdk.plugin.api.api.WandouGamesApi.ExitCallback;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnLoginFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnLogoutFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnPayFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.WandouAccountListener;
import com.wandoujia.mariosdk.plugin.api.model.model.LoginFinishType;
import com.wandoujia.mariosdk.plugin.api.model.model.LogoutFinishType;
import com.wandoujia.mariosdk.plugin.api.model.model.PayResult;
import com.wandoujia.mariosdk.plugin.api.model.model.UnverifiedPlayer;

public class SDK extends SDKContext {

	private WandouGamesApi wandouGamesApi;

	public void init() throws JSONException {
		regLifecycle();
		
		JSONObject json = getJsonData();
		Bundle md = getMetaData();
		long appKey = Long.valueOf(json.optString(APPKEY,md.getString(APPKEY)).replace("wdj.", ""));
		String secretKey = json.optString(APPSECRET,md.getString(APPSECRET));
		wandouGamesApi = new WandouGamesApi.Builder(getActivity(), appKey,
				secretKey).create();
		wandouGamesApi.setLogEnabled(true);
		wandouGamesApi.init(getActivity());

		dispatchData(EVENT_INIT);
		
		
		wandouGamesApi.addWandouAccountListener(new WandouAccountListener() {
			
			@Override
			public void onLogoutSuccess() {
				dispatchData(EVENT_LOGOUT);
			}
			
			@Override
			public void onLoginSuccess() {
//				JSONObject data = new JSONObject();
//				WandouPlayer uInfo = wandouGamesApi.getCurrentPlayerInfo();
//				try {
//					data.put(TOKEN, wandouGamesApi.getToken(600));
//					data.put(UID,  uInfo.getId());
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				dispatchData(EVENT_LOGIN, data);
			}
			
			@Override
			public void onLoginFailed(int code, String msg) {
				dispatchError(EVENT_LOGIN, msg+"["+code+"]");
			}
		});

	}
	
	public void userLogin(){
		
		wandouGamesApi.login(new OnLoginFinishedListener() {
			
			@Override
			public void onLoginFinished(LoginFinishType type, UnverifiedPlayer player) {
				if (type == LoginFinishType.CANCEL) {
					dispatchError(EVENT_LOGIN, CODE_ERR_CANCEL, "login cancel");
				}else{
					JSONObject data = new JSONObject();
					try {
						data.put(TOKEN, player.getToken());
						data.put(UID, player.getId());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN, data);
				}
			}
		});

	}
	
	public void userLogout(){
		wandouGamesApi.logout(new OnLogoutFinishedListener() {
			
			@Override
			public void onLoginFinished(LogoutFinishType type) {
				if (type == LogoutFinishType.LOGOUT_SUCCESS) {
					dispatchData(EVENT_LOGOUT);
				}else{
					dispatchError(EVENT_LOGOUT, "logout failed");
				}
			}
		});
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		String desc = pay.optString(PNAME);
		long price = pay.optLong(AMOUNT);
		String order = pay.optString(ORDER_ID);
		wandouGamesApi.pay(getActivity(), desc, price, 1, order,new OnPayFinishedListener() {
			
			@Override
			public void onPaySuccess(PayResult arg0) {
				dispatchData(EVENT_PAY);
			}
			
			@Override
			public void onPayFail(PayResult result) {
				dispatchError(EVENT_PAY, "pay fail:"+result.getStatus());
			}
		});
	}
	
	public void submitGameInfo(){
		//TODO
	}
	
	@Override
	public void dispose() {
         wandouGamesApi.exit(getActivity(), new ExitCallback() {
			
			@Override
			public void onGameExit() {
				
			}
			
			@Override
			public void onChannelExit() {
				
			}
		}, false);
		super.dispose();
	}
	
	@Override
	protected void onResume() {
		wandouGamesApi.onResume(getActivity());
	}
	
	@Override
	protected void onPause() {
		wandouGamesApi.onPause(getActivity());
	}
}

	
