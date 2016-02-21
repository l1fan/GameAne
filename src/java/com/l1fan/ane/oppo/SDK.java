package com.l1fan.ane.oppo;

import org.json.JSONException;
import org.json.JSONObject;

import com.l1fan.ane.SDKContext;
import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.callback.ApiCallback;
import com.nearme.game.sdk.callback.GameExitCallback;
import com.nearme.game.sdk.common.model.biz.GameCenterSettings;
import com.nearme.game.sdk.common.model.biz.PayInfo;
import com.nearme.game.sdk.common.model.biz.ReportUserGameInfoParam;

public class SDK extends SDKContext {
	
	private String mAppId;

	public void init() throws JSONException {
		regLifecycle();
		JSONObject init = getJsonData();
		String appId = init.optString(APPID);
		String appKey = init.optString(APPKEY);
		String appSecret = init.optString(APPSECRET);
		boolean debug = init.optBoolean(DEBUGMODE,false);
		boolean ori	= init.optBoolean(ORIENTATION,true);
		GameCenterSettings gameCenterSettings = new GameCenterSettings(false,appKey,appSecret,debug,ori);
		GameCenterSDK.init(gameCenterSettings, getActivity());
		mAppId = appId;
		dispatchData(EVENT_INIT);
	}
	
	public void userLogin() {
		
		GameCenterSDK.getInstance().doLogin(getActivity(), new ApiCallback() {
			
			@Override
			public void onSuccess(String msg) {
				
				GameCenterSDK.getInstance().doGetTokenAndSsoid(new ApiCallback() {
					
					@Override
					public void onSuccess(String resultMsg) {
						try {
							JSONObject json = new JSONObject(resultMsg);
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
					public void onFailure(String paramString, int paramInt) {
						dispatchError(EVENT_LOGIN, "login fail:"+paramString+":"+paramInt);

					}
				});
				System.out.println("login result is "+msg);
				
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
	
	public void sendRoleInfo() throws JSONException{
		JSONObject role = getJsonData();
		ReportUserGameInfoParam params = new ReportUserGameInfoParam(role.optString("gameId",mAppId), role.optString("service"), role.optString("role"), role.optString("grade"));
		GameCenterSDK.getInstance().doReportUserGameInfoData(params, new ApiCallback() {
			
			@Override
			public void onSuccess(String paramString) {
				dispatchData("SEND_ROLE_INFO");
			}
			
			@Override
			public void onFailure(String msg, int code) {
				dispatchError("SEND_ROLE_INFO", "send role info fail:"+msg+":"+code);
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
	
	@Override
	protected void onResume() {
		GameCenterSDK.getInstance().onResume(getActivity());
	}
	
	@Override
	protected void onPause() {
		GameCenterSDK.getInstance().onPause();
	}
}
