package com.l1fan.ane.downjoy;

import org.json.JSONException;
import org.json.JSONObject;

import com.downjoy.CallbackListener;
import com.downjoy.CallbackStatus;
import com.downjoy.Downjoy;
import com.downjoy.InitListener;
import com.downjoy.LoginInfo;
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
	}

	public void userLogin() {
		downjoy.openLoginDialog(getActivity(), new CallbackListener<LoginInfo>() {

			@Override
			public void callback(int status, LoginInfo data) {
				if (status == CallbackStatus.SUCCESS && data != null) {
					JSONObject json = new JSONObject();
					try {
						json.put(UID, data.getUmid());
						json.put(TOKEN, data.getToken());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN, json);
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
	
	public void pay(){
		//TODO downjoy pay
	}
	
	public void userLogout(){
		downjoy.logout(getActivity());
	}
	
	public void downjoy() throws Exception{
		sdkcall(downjoy);
	}
}
