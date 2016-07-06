package com.l1fan.ane.cc;

import org.json.JSONException;
import org.json.JSONObject;

import com.l1fan.ane.SDKContext;
import com.lion.ccpay.sdk.CCPaySdk;
import com.lion.ccpay.sdk.OnAccountPwdChangeListener;
import com.lion.ccpay.sdk.OnLoginCallBack;
import com.lion.ccpay.sdk.OnLoginOutAction;
import com.lion.ccpay.sdk.OnPayListener;
import com.lion.ccpay.sdk.Stats;

public class SDK extends SDKContext {
	
	public void init() {
		CCPaySdk.getInstance().init(getActivity());
		regLifecycle();
		dispatchData(EVENT_INIT);
		CCPaySdk.getInstance().setOnAccountPwdChangeListener(new OnAccountPwdChangeListener() {
			
			@Override
			public void onAccountPwdChange() {
				CCPaySdk.getInstance().onOffline();
				dispatchData(EVENT_LOGOUT);
			}
		});
		
		CCPaySdk.getInstance().setOnLoginOutAction(new OnLoginOutAction() {
			
			@Override
			public void onLoginOut() {
				dispatchData(EVENT_LOGOUT);
			}
		});
	}
	
	public void userLogin(){
		CCPaySdk.getInstance().login(loginListener);
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		CCPaySdk.getInstance().pay(pay.optString(PID), pay.optString(ORDER_ID), String.valueOf(pay.optInt(AMOUNT)/100.00), new OnPayListener() {
			
			@Override
			public void onPayResult(int status, String tn, String money) {
				switch (status) {
				case OnPayListener.CODE_SUCCESS:
					dispatchData(EVENT_PAY);
					break;
				case OnPayListener.CODE_CANCEL:
					dispatchError(EVENT_PAY, CODE_ERR_CANCEL, "cancel");
					break;
				default:
					dispatchError(EVENT_PAY, "pay failed");
					break;
				}
			}
		});
	}
	
	private OnLoginCallBack loginListener = new OnLoginCallBack() {
		
		@Override
		public void onLoginSuccess(String uid, String token, String userName) {
			JSONObject data = new JSONObject();
			try {
				data.put(UID, uid);
				data.put(UNAME, token);
				data.put(TOKEN, userName);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			dispatchData(EVENT_LOGIN, data);
		}
		
		@Override
		public void onLoginFail() {
			dispatchError(EVENT_LOGIN, "login failed");
		}
		
		@Override
		public void onLoginCancel() {
			dispatchError(EVENT_LOGIN, CODE_ERR_CANCEL, "cancel");
		}
	};
	
	public void switchAccount(){
		CCPaySdk.getInstance().login(false, loginListener);
	}
	
	public void userLogout(){
		CCPaySdk.getInstance().onOffline();
		dispatchData(EVENT_LOGOUT);
	}
	
	@Override
	protected void onResume() {
		Stats.onResume(getActivity());
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		Stats.onPause(getActivity());
		super.onPause();
	}
	
	@Override
	public void dispose() {
		CCPaySdk.getInstance().onLogOutApp();
		super.dispose();
	}
}
