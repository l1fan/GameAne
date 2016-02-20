package com.l1fan.ane.gionee;

import org.json.JSONException;
import org.json.JSONObject;

import com.gionee.gamesdk.AccountInfo;
import com.gionee.gamesdk.GamePayer;
import com.gionee.gamesdk.GamePlatform;
import com.gionee.gamesdk.GamePlatform.LoginListener;
import com.gionee.gamesdk.OrderInfo;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {

	private String mApiKey;

	public void init() throws JSONException{
		JSONObject init = getJsonData();
		mApiKey = init.optString(APPKEY,init.optString("apiKey"));
		GamePlatform.init(getActivity(), mApiKey);
		dispatchData(EVENT_INIT);
	}
	
	public void userLogin() {
		GamePlatform.loginAccount(getActivity(), true, new LoginListener() {
			
			@Override
			public void onSuccess(AccountInfo arg0) {
				JSONObject data = new JSONObject();
				try {				
					data.put(UID, arg0.mPlayerId);
					data.put(TOKEN, arg0.mToken);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dispatchData(EVENT_LOGIN,data);
			}
			
			@Override
			public void onError(Exception arg0) {
				dispatchError(EVENT_LOGIN, arg0.getMessage());
			}
			
			@Override
			public void onCancel() {
				
			}
		});
	}
	
	public void pay() throws Exception  {
		JSONObject json = getJsonData();
		GamePayer payer = new GamePayer(getActivity());
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setApiKey(json.optString(mApiKey));
		orderInfo.setOutOrderNo(json.optString(ORDER_ID));
		orderInfo.setSubmitTime(json.optString("submitTime"));
		payer.pay(orderInfo, payer.new GamePayCallback(){
			@Override
			public void onPaySuccess() {
				dispatchData(EVENT_PAY);
				super.onPaySuccess();
			}
			
			@Override
			public void onPayFail(String stateCode) {
				dispatchError(EVENT_PAY, stateCode);
				super.onPayFail(stateCode);
			}
		});
	}
	
	
}
