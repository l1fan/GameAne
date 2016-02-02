package com.l1fan.ane.downjoy;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Build;
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
		JSONObject init = getJsonData();
		String appId = init.optString(APPID);
		String appKey = init.optString(APPKEY);
		String merchantId = init.optString("merchantId");
		String serverSeqNum = init.optString("serverSeqNum","1");
		downjoy = Downjoy.getInstance(getActivity(), merchantId, appId, serverSeqNum, appKey, new InitListener() {

			@Override
			public void onInitComplete() {
				dispatchData(EVENT_INIT);
			}
		});
		downjoy.showDownjoyIconAfterLogined(true);
		lifeCycle();
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
		downjoy.openPaymentDialog(getActivity(), (float) (pay.optInt(AMOUNT)/100.00), pay.optString(PNAME), pay.optString(PNAME), pay.optString(ORDER_ID), pay.optString("serverName"), pay.optString("roleName"), new CallbackListener<String>() {

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
	
	public void exit(){
		downjoy.openExitDialog(getActivity(), new CallbackListener<String>() {
			
			@Override
			public void callback(int code, String arg1) {
				if (code == CallbackStatus.SUCCESS) {
					dispatchData("DOWNJOY_EXIT");
				}else{
					dispatchError("DOWNJOY_EXIT", "exit");
				}
			}
		});
	}
	
	public void downjoy() throws Exception{
		sdkcall(downjoy);
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
						if (downjoy != null) {
							downjoy.resume(arg0);
						}
					}

					@Override
					public void onActivityPaused(Activity arg0) {
						if (downjoy != null) {
							downjoy.pause();
						}
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
