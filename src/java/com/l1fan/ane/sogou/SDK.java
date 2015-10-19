package com.l1fan.ane.sogou;

import org.json.JSONException;
import org.json.JSONObject;

import com.l1fan.ane.SDKContext;
import com.sogou.gamecenter.sdk.SogouGamePlatform;
import com.sogou.gamecenter.sdk.bean.SogouGameConfig;
import com.sogou.gamecenter.sdk.bean.UserInfo;
import com.sogou.gamecenter.sdk.listener.InitCallbackListener;
import com.sogou.gamecenter.sdk.listener.LoginCallbackListener;
import com.sogou.gamecenter.sdk.listener.OnExitListener;
import com.sogou.gamecenter.sdk.listener.SwitchUserListener;

public class SDK extends SDKContext {

	private SogouGamePlatform mInstance;

	public void init() throws JSONException {
		JSONObject json = getJsonData();
		mInstance = SogouGamePlatform.getInstance();

		SogouGameConfig gameConfig = new SogouGameConfig();
		gameConfig.devMode = json.optBoolean(DEBUGMODE);
		gameConfig.gid = json.optInt(APPID);
		gameConfig.gameName = json.optString(APPNAME);
		gameConfig.appKey = json.optString(APPKEY);

		mInstance.prepare(getActivity(), gameConfig);
		mInstance.init(getActivity(), new InitCallbackListener() {

			@Override
			public void initSuccess() {
				dispatchData(EVENT_INIT);
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
				JSONObject json = new JSONObject();
				try {
					json.put(UID, userInfo.getUserId());
					json.put(TOKEN, userInfo.getSessionKey());
				} catch (JSONException e) {
					e.printStackTrace();
				}

				dispatchData(EVENT_LOGIN, json);

			}

			@Override
			public void loginFail(int code, String msg) {
				dispatchError(EVENT_LOGIN, msg);
			}
		});
	}
	
	public void switchAccount(){
		mInstance.switchUser(getActivity(), new SwitchUserListener() {
			
			@Override
			public void switchSuccess(int arg0, UserInfo arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void switchFail(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void userLogout(){
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
