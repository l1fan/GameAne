package com.l1fan.ane.xx;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.flamingo.sdk.access.GPApiFactory;
import com.flamingo.sdk.access.GPExitResult;
import com.flamingo.sdk.access.GPPayResult;
import com.flamingo.sdk.access.GPSDKGamePayment;
import com.flamingo.sdk.access.GPSDKInitResult;
import com.flamingo.sdk.access.GPUserResult;
import com.flamingo.sdk.access.IGPApi;
import com.flamingo.sdk.access.IGPExitObsv;
import com.flamingo.sdk.access.IGPPayObsv;
import com.flamingo.sdk.access.IGPSDKInitObsv;
import com.flamingo.sdk.access.IGPUserObsv;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {
  
	private IGPApi mApi;

	public void init() throws JSONException{
		JSONObject jd = getJsonData();
		Bundle md = getMetaData();
		String appId = jd.optString(APPID,md.getString(APPID,"")).replace("xx.", "");
		String appKey = jd.optString(APPKEY,md.getString(APPKEY));
		mApi = GPApiFactory.getGPApi();
		mApi.setLogOpen(jd.optBoolean("logOpen",md.getBoolean("logOpen",false)));
		mApi.initSdk(getActivity(), appId, appKey, new IGPSDKInitObsv() {
			
			@Override
			public void onInitFinish(GPSDKInitResult result) {
				switch (result.mInitErrCode) {
				case GPSDKInitResult.GPInitErrorCodeNone:
					dispatchData(EVENT_INIT);
					break;
				case GPSDKInitResult.GPInitErrorCodeConfig:
					dispatchError(EVENT_INIT, "appId appKey config error");
					break;
				case GPSDKInitResult.GPInitErrorCodeNeedUpdate:
					dispatchError(EVENT_UPDATE,CODE_ERR_INVALID,"need update");
					break;
				case GPSDKInitResult.GPInitErrorCodeNet:
					dispatchError(EVENT_INIT, "network error");
					break;
				default:
					break;
				}
			}
		});
	}
	
	public void userLogin(){
		mApi.login(getActivity(), new IGPUserObsv() {
			
			@Override
			public void onFinish(GPUserResult result) {
				if (result.mErrCode == GPUserResult.USER_RESULT_LOGIN_SUCC) {
					JSONObject data = new JSONObject();
					try {
						data.put(UID, mApi.getLoginUin());
						data.put(TOKEN, mApi.getLoginToken());
						data.put(UNAME, mApi.getAccountName());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN, data);
					
				}else{
					dispatchError(EVENT_LOGIN, "login failed");
				}
			}
		});
	}
	
	public void userLogout(){
		mApi.logout();
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		GPSDKGamePayment info = new GPSDKGamePayment();
		info.mItemName = pay.optString(PNAME);
		info.mPaymentDes = pay.optString(PDESC,info.mItemName);
		info.mItemPrice = pay.optInt(AMOUNT)/100.00f;
		info.mItemOrigPrice = pay.optInt(AMOUNT)/100.00f;
		info.mItemCount = pay.optInt("itemCount",1);
		info.mItemId = pay.optString(PID);
		info.mSerialNumber = pay.optString(ORDER_ID);
		info.mReserved = pay.optString(EXT);
		info.mCurrentActivity = getActivity();
		
		mApi.buy(info, new IGPPayObsv() {
			
			@Override
			public void onPayFinish(GPPayResult result) {
				if (result.mErrCode == GPPayResult.GPSDKPayResultCodeSucceed) {
					dispatchData(EVENT_PAY);
				}else{
					dispatchError(EVENT_PAY, "pay error code:"+result.mErrCode);
				}
			}
		});
	}
	
	public void exit(){
		mApi.exit(new IGPExitObsv() {
			
			@Override
			public void onExitFinish(GPExitResult result) {
				if (result.mResultCode == GPExitResult.GPSDKExitResultCodeExitGame) {
					dispatchData("XX_EXIT");
				}else{
					dispatchError("XX_EXIT", "error code:"+result.mResultCode);
				}
			}
		});
	}
	
}
