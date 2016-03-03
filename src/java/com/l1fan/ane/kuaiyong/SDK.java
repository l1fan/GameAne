package com.l1fan.ane.kuaiyong;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.anguotech.sdk.bean.PayInfo;
import com.anguotech.sdk.bean.UserInfoLogin;
import com.anguotech.sdk.interfaces.InitCallBack;
import com.anguotech.sdk.interfaces.LoginCallBack;
import com.anguotech.sdk.interfaces.PayCallBack;
import com.anguotech.sdk.manager.AnGuoManager;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {

	private String mPayID;

	public void init() throws JSONException {
		JSONObject json = getJsonData();
		Bundle md = getMetaData();
		mPayID = json.optString("payID",String.valueOf(md.get("payID")));
		AnGuoManager.Instance().Init(getActivity(), json.optString(APPKEY,md.getString(APPKEY)), new InitCallBack() {

			@Override
			public void onCancel() {
			}

			@Override
			public void onFailed(String arg0) {
				dispatchError(EVENT_INIT, arg0);
			}

			@Override
			public void onSuccess(String arg0) {
				dispatchData(EVENT_INIT, arg0);
			}
		});
	}

	/**
	 * 
	 */
	public void userLogin() {
		AnGuoManager.Instance().Login(new LoginCallBack() {

			@Override
			public void onLoginCancel() {
			}

			@Override
			public void onLoginFailed(String arg0) {
				dispatchError(EVENT_LOGIN, arg0);
			}

			@Override
			public void onLoginSuccess(UserInfoLogin arg0) {
				JSONObject json = new JSONObject();
				try {
					json.put(UID, arg0.getUid());
					json.put(TOKEN, arg0.getToken_key());
					json.put(UNAME, arg0.getUsername());
					dispatchData(EVENT_LOGIN, json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onLogoutCancel() {

			}

			@Override
			public void onLogoutFailed(String arg0) {
				dispatchError(EVENT_LOGOUT, arg0);
			}

			@Override
			public void onLogoutSuccess(String arg0) {
				dispatchData(EVENT_LOGOUT, arg0);
			}

		});
	}

	public void userLogout() {
		AnGuoManager.Instance().isShowBobble(getActivity(), false);
		AnGuoManager.Instance().Logout();
	}

	public void pay() throws Exception {
		PayInfo payInfo = new PayInfo();
		JSONObject json = getJsonData();
		payInfo.setFee(json.optInt(AMOUNT) / 100);
		payInfo.setGame(json.optString("payID",mPayID));
		payInfo.setSubject(json.optString(PNAME));
		payInfo.setDealSeq(json.optString(ORDER_ID));
		payInfo.setGamesvr(json.optString(GAMESVR));
//		payInfo.setUid(json.optString("uid"));
//		payInfo.setNotifyUrl(json.optString("notifyUrl"));
//		payInfo.setPkgid(json.optString("packId"));

		AnGuoManager.Instance().Pay(payInfo, new PayCallBack() {

			@Override
			public void onCancel() {
				dispatchError(EVENT_PAY, CODE_ERR_CANCEL, "cancel");
			}

			@Override
			public void onFail(Object arg0) {
				dispatchError(EVENT_PAY, String.valueOf(arg0));
			}

			@Override
			public void onSuccess(Object arg0) {
				dispatchData(EVENT_PAY, String.valueOf(arg0));
			}

		});
	}
}
