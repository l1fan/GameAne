package com.l1fan.ane.downjoy;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.downjoy.CallbackListener;
import com.downjoy.CallbackStatus;
import com.downjoy.Downjoy;
import com.downjoy.InitListener;
import com.downjoy.LoginInfo;
import com.downjoy.LogoutListener;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {

	private Downjoy downjoy;

	public void init() throws JSONException {
		regLifecycle();

		JSONObject init = getJsonData();
		Bundle md = getMetaData();
		String appId = init.optString(APPID,String.valueOf(md.get(APPID)));
		String appKey = init.optString(APPKEY,md.getString(APPKEY));
		String merchantId = init.optString("merchantId",String.valueOf(md.get("merchantId")));
		String serverSeqNum = init.optString("serverSeqNum",String.valueOf(md.get("serverSeqNum")));
		
		downjoy = Downjoy.getInstance(getActivity(), merchantId, appId, serverSeqNum, appKey, new InitListener() {

			@Override
			public void onInitComplete() {
				dispatchData(EVENT_INIT);
			}
		});
		downjoy.showDownjoyIconAfterLogined(true);

	}



	public void userLogin() {
		downjoy.openLoginDialog(getActivity(), new CallbackListener<LoginInfo>() {

			@Override
			public void callback(int status, LoginInfo data) {
				if (status == CallbackStatus.SUCCESS && data != null) {
					JSONObject json = new JSONObject();
					try {
						json.put(UID, data.getUmid());
						json.put(UNAME, data.getUserName());
						json.put(TOKEN, data.getToken());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN, json);
					
					downjoy.setLogoutListener(new LogoutListener() {
						
						@Override
						public void onLogoutSuccess() {
							dispatchData(EVENT_LOGOUT);
						}
						
						@Override
						public void onLogoutError(String arg0) {
							dispatchError(EVENT_LOGOUT, "logout fail:"+arg0);
						}
					});
					
				} else if (status == CallbackStatus.FAIL && data != null) {
					dispatchError(EVENT_LOGIN, data.getMsg());
				} else if (status == CallbackStatus.CANCEL && data != null) {
					dispatchError(EVENT_LOGIN, CODE_ERR_CANCEL, data.getMsg());
				}
			}
		});
	}
	
	@Override
	public boolean isSupportUserCenter() {
		return true;
	}
	
	public void userCenter(){
		downjoy.openMemberCenterDialog(getActivity());
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		downjoy.openPaymentDialog(getActivity(), (float) (pay.optInt(AMOUNT)/100.00), pay.optString(PNAME), pay.optString(PNAME), pay.optString(ORDER_ID), pay.optString("serverName"), pay.optString("playerName"), new CallbackListener<String>() {

			@Override
			public void callback(int code, String data) {
				if (code == CallbackStatus.SUCCESS) {
					dispatchData(EVENT_PAY);
				}else{
					dispatchError(EVENT_PAY, "pay failed");
				}
			}
		} );
		
	}
	
	public void userLogout(){
		downjoy.logout(getActivity());
	}
	
	public void destroy(){
		downjoy.openExitDialog(getActivity(), new CallbackListener<String>() {
			
			@Override
			public void callback(int code, String arg1) {
				if (code == CallbackStatus.SUCCESS) {
					dispatchData(EVENT_DESTROY);
				}else{
					dispatchError(EVENT_DESTROY, CODE_ERR_CANCEL,arg1+":"+code);
				}
			}
		});
	}
	
	public void downjoy() throws Exception{
		sdkcall(downjoy);
	}
	
	@Override
	protected void onResume() {
		downjoy.resume(getActivity());
	}
	
	@Override
	protected void onPause() {
		downjoy.pause();
	}
}
