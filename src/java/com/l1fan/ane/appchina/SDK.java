package com.l1fan.ane.appchina;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.appchina.model.ErrorMsg;
import com.appchina.usersdk.Account;
import com.l1fan.ane.SDKContext;
import com.yyh.sdk.AccountCallback;
import com.yyh.sdk.CPInfo;
import com.yyh.sdk.InitCallback;
import com.yyh.sdk.LoginCallback;
import com.yyh.sdk.PayCallback;
import com.yyh.sdk.PayParam;
import com.yyh.sdk.YYHSDKAPI;


public class SDK extends SDKContext {

	public void init() throws JSONException{
		JSONObject init = getJsonData();
		CPInfo cp = new CPInfo();
		cp.loginId = init.optInt("loginId");
		cp.loginKey = init.optString("loginKey");
		cp.appid = init.optString(APPID);
		cp.appkey = init.optString(APPKEY);
		cp.orientation = init.optInt(ORIENTATION,CPInfo.PORTRAIT);
		
		YYHSDKAPI.initSDKAPI(getActivity(), cp, new InitCallback() {
			
			@Override
			public void onFinish() {
				dispatchData(EVENT_INIT);
				if (YYHSDKAPI.isLogined(getActivity())) {
					YYHSDKAPI.showToolbar(true);
				}
			}
			
			@Override
			public void onError(String paramString) {
				dispatchError(EVENT_INIT, "init fail:"+paramString);
			}
		}, new AccountCallback() {
			
			@Override
			public void onSwitchAccount(Account paramAccount1, Account paramAccount2) {
				dispatchLoginData(paramAccount2);
			}
			
			@Override
			public void onLogout() {
				dispatchData(EVENT_LOGOUT);
			}
		});
	}
	
	private void dispatchLoginData(Account account){
		JSONObject data = new JSONObject();
		try {
			data.put(TOKEN, account.ticket);
			data.put(UID, account.userId);
			data.put(UNAME, account.userName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		dispatchData(EVENT_LOGIN, data);
	}
	
	public void userLogin(){
		YYHSDKAPI.login(getActivity(), new LoginCallback() {
			
			@Override
			public void onLoginSuccess(Activity paramActivity, Account account) {
				dispatchLoginData(account);
				YYHSDKAPI.showToolbar(true);
			}
			
			@Override
			public void onLoginError(Activity paramActivity, ErrorMsg msg) {
				dispatchError(EVENT_LOGIN, msg.message);
			}
			
			@Override
			public void onLoginCancel() {
				dispatchError(EVENT_LOGIN, CODE_ERR_CANCEL,"login cancel");
			}
		});
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		PayParam params = new PayParam(pay.optInt(PID), pay.optInt(AMOUNT), 1, pay.optString(EXT), pay.optString(ORDER_ID),pay.optString(NOTIFY_URL));
		
		YYHSDKAPI.stratPay(getActivity(), params, new PayCallback() {
			
			@Override
			public void onPaySuccess(int paramInt, String paramString1,
					String paramString2) {
				dispatchData(EVENT_PAY);
			}
			
			@Override
			public void onPayFaild(int code, String msg) {
				dispatchError(EVENT_PAY, msg+":"+code);
			}
		});
	}
	
}
