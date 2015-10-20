package com.l1fan.ane.tongbutui;

import org.json.JSONException;
import org.json.JSONObject;

import com.l1fan.ane.SDKContext;
import com.tongbu.sdk.bean.TbUser;
import com.tongbu.sdk.configs.TbPlatform;
import com.tongbu.sdk.configs.TbPlatformSettings;
import com.tongbu.sdk.listener.OnInitFinishedListener;
import com.tongbu.sdk.listener.OnLoginFinishedListener;

public class SDK extends SDKContext {

	public void init() throws JSONException {
		TbPlatformSettings settings = new TbPlatformSettings();
		JSONObject json = getJsonData();
		settings.setAppId(json.optString(APPID));
		settings.setOrient(json.optInt(ORIENTATION,1));

		TbPlatform.getInstance().tbInit(getActivity(), settings, new OnInitFinishedListener() {

			@Override
			public void onInitFinished(int flag) {
				switch (flag) {
				case OnInitFinishedListener.FLAG_NORMAL:
					dispatchData(EVENT_INIT);
					break;
				case OnInitFinishedListener.FLAG_FORCE_CLOSE:
					dispatchError(EVENT_INIT, "force close");
					break;
				case OnInitFinishedListener.FLAG_CHECK_FAILED:
					dispatchError(EVENT_INIT, "check failed");
					break;
				default:
					break;
				}
			}
		});
	}

	public void userLogin() {
		TbPlatform.getInstance().tbLogin(getActivity(), new OnLoginFinishedListener() {

			@Override
			public void onLoginFinished(boolean success, TbUser paramTbUser) {
				if (success) {
					JSONObject json = new JSONObject();
					try {
						json.put(UID, paramTbUser.uid);
						json.put(TOKEN, paramTbUser.session);
						json.put(UNAME, paramTbUser.name);
						json.put("account", paramTbUser.account);
						dispatchData(EVENT_LOGIN, json);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void userLogout() {
		TbPlatform.getInstance().tbLogout(getActivity(), 1);
	}

	public void destory() {
		TbPlatform.getInstance().tbDestory(getActivity());
	}

}
