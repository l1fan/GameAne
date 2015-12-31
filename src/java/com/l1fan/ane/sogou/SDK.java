package com.l1fan.ane.sogou;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.l1fan.ane.SDKContext;
import com.sogou.gamecenter.sdk.SogouGamePlatform;
import com.sogou.gamecenter.sdk.bean.SogouGameConfig;
import com.sogou.gamecenter.sdk.bean.UserInfo;
import com.sogou.gamecenter.sdk.listener.InitCallbackListener;
import com.sogou.gamecenter.sdk.listener.LoginCallbackListener;
import com.sogou.gamecenter.sdk.listener.OnExitListener;
import com.sogou.gamecenter.sdk.listener.PayCallbackListener;
import com.sogou.gamecenter.sdk.listener.SwitchUserListener;
import com.sogou.gamecenter.sdk.views.FloatMenu;

public class SDK extends SDKContext {

	private SogouGamePlatform mInstance;

	public void init() throws JSONException {
		JSONObject json = getJsonData();
		mInstance = SogouGamePlatform.getInstance();

		SogouGameConfig gameConfig = new SogouGameConfig();
		gameConfig.devMode = json.optBoolean(DEBUGMODE,false);
		gameConfig.gid = json.optInt(APPID);
		gameConfig.gameName = getAppName();
		gameConfig.appKey = json.optString(APPKEY);

		mInstance.prepare(getActivity(), gameConfig);
		mInstance.init(getActivity(), new InitCallbackListener() {

			@Override
			public void initSuccess() {
				dispatchData(EVENT_INIT);
				//createFloatBall();
			}

			private void createFloatBall() {
				FloatMenu fm = mInstance.createFloatMenu(getActivity(), false);
				fm.setParamsXY(10, 100);
				fm.show();
				fm.setSwitchUserListener(switchAccountListener);
			}

			@Override
			public void initFail(int arg0, String arg1) {
				dispatchError(EVENT_INIT, arg1);
			}
		});
		
	}

	public void userLogin() {
		mInstance.login(getActivity(), new LoginCallbackListener() {

			@Override
			public void loginSuccess(int code, UserInfo userInfo) {
				loginSucc(userInfo);
			}

			@Override
			public void loginFail(int code, String msg) {
				dispatchError(EVENT_LOGIN, msg);
			}
		});
	}
	
	private void loginSucc(UserInfo userInfo){
		JSONObject json = new JSONObject();
		try {
			json.put(UID, userInfo.getUserId());
			json.put(TOKEN, userInfo.getSessionKey());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		dispatchData(EVENT_LOGIN, json);
	}
	
	private SwitchUserListener switchAccountListener = new SwitchUserListener() {
		
		@Override
		public void switchSuccess(int arg0, UserInfo arg1) {
			loginSucc(arg1);
		}
		
		@Override
		public void switchFail(int arg0, String arg1) {
			dispatchError(EVENT_LOGIN, arg1);
		}
	};

	public void switchAccount() {
		mInstance.switchUser(getActivity(), switchAccountListener);
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		HashMap<String, Object> optData = new HashMap<String, Object>();
		optData.put("currency", pay.optString("currency","sogou币"));
		optData.put("rate", pay.optDouble("rate",1f));
		optData.put("amount", pay.optInt(AMOUNT)/100);
		optData.put("product_name", pay.optString(PNAME));
		optData.put("app_data", pay.optString(EXT));
		optData.put("hidechannel", pay.optString("hidechannel",""));
		optData.put("appmodes",pay.optBoolean("appmodes",false));
		mInstance.pay(getActivity(), optData, new PayCallbackListener() {
			
			@Override
			public void paySuccess(String orderId, String ext) {
				dispatchData(EVENT_PAY);
			}
			
			@Override
			public void payFail(int code, String arg1, String arg2) {
				dispatchError(EVENT_PAY, "pay fail:"+code);
			}
		});
	}

	public void userLogout() {
		mInstance.exit(new OnExitListener(getActivity()) {

			@Override
			public void onCompleted() {
				dispatchData(EVENT_LOGOUT);
			}
		});
	}

	public void destroy() {
		mInstance.onTerminate();
	}
}
