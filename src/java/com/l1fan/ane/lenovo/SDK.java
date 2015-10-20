package com.l1fan.ane.lenovo;

import org.json.JSONException;
import org.json.JSONObject;

import com.l1fan.ane.SDKContext;
import com.lenovo.lsf.gamesdk.LenovoGameApi;
import com.lenovo.lsf.gamesdk.LenovoGameApi.GamePayRequest;
import com.lenovo.lsf.gamesdk.LenovoGameApi.IAuthResult;
import com.lenovo.lsf.gamesdk.LenovoGameApi.IPayResult;
import com.lenovo.lsf.gamesdk.LenovoGameApi.IQuitCallback;

public class SDK extends SDKContext {

	private String mAppKey;
	private String mAppId;

	public void init() throws Exception {
		JSONObject json = getJsonData();
		mAppKey = json.optString(APPKEY);
		mAppId = json.optString(APPID);
		LenovoGameApi.doInit(getActivity(), mAppId);
		dispatchData(EVENT_INIT);
	}

	public void userLogin() {
		LenovoGameApi.doAutoLogin(getActivity(), new IAuthResult() {

			@Override
			public void onFinished(boolean ret, String data) {
				if (ret) {
					JSONObject json = new JSONObject();
					try {
						json.put(TOKEN, data);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN, json);
				} else {
					dispatchError(EVENT_LOGIN, "login fail");
				}
			}

		});
	}

	public void pay() {
		JSONObject data = new JSONObject();
		GamePayRequest payRequest = new GamePayRequest();
		payRequest.addParam("notifyurl", ""); //notifyurl当前版本暂时不用,传空string
		payRequest.addParam("appid", mAppId); 
		payRequest.addParam("waresid", data.optString(PID)); //商品编码，联想后台建立
		payRequest.addParam("exorderno", data.optString(ORDER_ID)); 
		payRequest.addParam("price", data.opt(AMOUNT));
		payRequest.addParam("cpprivateinfo", data.optString(EXT));
		LenovoGameApi.doPay(getActivity(), mAppKey, payRequest, new IPayResult(){

			@Override
			public void onPayResult(int resultCode, String signValue, String resultInfo) {
				if (LenovoGameApi.PAY_SUCCESS == resultCode) {
					dispatchData(EVENT_PAY, resultInfo);
				}else{
					dispatchError(EVENT_PAY, resultInfo);
				}
			}
			
		});
	}

	public void destroy() {
		final String event = getAction();
		LenovoGameApi.doQuit(getActivity(), new IQuitCallback() {

			@Override
			public void onFinished(String arg0) {
				dispatchData(event, arg0);
			}

		});
	}
}
