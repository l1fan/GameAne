package com.l1fan.ane.tongbutui;

import org.json.JSONException;
import org.json.JSONObject;

import com.l1fan.ane.SDKContext;
import com.tongbu.sdk.bean.TbUser;
import com.tongbu.sdk.configs.TbPlatform;
import com.tongbu.sdk.configs.TbPlatformSettings;
import com.tongbu.sdk.configs.TbToolBarPlace;
import com.tongbu.sdk.listener.OnInitFinishedListener;
import com.tongbu.sdk.listener.OnLoginFinishedListener;
import com.tongbu.sdk.listener.OnSwitchAccountListener;
import com.tongbu.sdk.widget.TbFloatToolBar;

public class SDK extends SDKContext {

	private TbFloatToolBar toolBar;

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
		
		toolBar = TbFloatToolBar.create(getActivity(), TbToolBarPlace.POSITION_TOP_LEFT);
	}

	public void userLogin() {
		TbPlatform.getInstance().tbLogin(getActivity(), new OnLoginFinishedListener() {

			@Override
			public void onLoginFinished(boolean success, TbUser paramTbUser) {
				if (success) {
					
					dispatchLoginData(paramTbUser);
					toolBar.show();
					TbPlatform.getInstance().setOnSwitchAccountListener(new OnSwitchAccountListener() {
						
						@Override
						public void onSwitchAccountFinished(int flag, TbUser user) {
							switch (flag) {
							case SWITCH_ACCOUNT_SUCCESS:
								dispatchLoginData(user);
								break;
							case SWITCH_ACCOUNT_LOGOUT:
								dispatchData(EVENT_LOGOUT);
								break;
							default:
								break;
							}
						}
					});
				}
			}
			
			public void dispatchLoginData(TbUser user){
				JSONObject json = new JSONObject();
				try {
					json.put(UID, user.uid);
					json.put(TOKEN, user.session);
					json.put(UNAME, user.name);
					json.put("account", user.account);
					dispatchData(EVENT_LOGIN, json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void userLogout() {
		TbPlatform.getInstance().tbLogout(getActivity(), 1);
		dispatchData(EVENT_LOGOUT);
		toolBar.hide();
	}

	@Override
	public void dispose() {
		super.dispose();
		TbPlatform.getInstance().tbDestory(getActivity());
		if(toolBar != null) toolBar.recycle();
	}
	
}
