package com.l1fan.ane.baofeng;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Nickname;

import com.baofeng.game.sdk.BallServiceManager;
import com.baofeng.game.sdk.CXPlatform;
import com.baofeng.game.sdk.CXResultCode;
import com.baofeng.game.sdk.bean.CxUserInfo;
import com.baofeng.game.sdk.listener.CxCallBack;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {

	public void init() throws JSONException{
		regLifecycle();
		
		JSONObject jd = getJsonData();
		Bundle md = getMetaData();
		String gameId = jd.optString(APPID,jd.optString("gameId",md.getString("gameId"))).replace("bf.", "");
		
		Activity activity = getActivity();
		int ret = CXPlatform.getInstance().initSDK(activity, gameId);
		switch (ret) {
		case CXResultCode.GAMEIDERROR:
			dispatchError(EVENT_INIT, "init fail: game id error");
			break;
		case CXResultCode.QUDAOIDERROR:
			dispatchError(EVENT_INIT, "init fail: qudao id error");
			break;
		case CXResultCode.INITSDKERROR:
			dispatchError(EVENT_INIT, "init failed");
			break;
		case CXResultCode.SUCCESSCODE:
			dispatchData(EVENT_INIT);
			CXPlatform.getInstance().setUserCenterCanWord(activity, true);
			break;
		default:
			break;
		}

		CXPlatform.getInstance().setCanGuestLogin(activity, true);
		CXPlatform.getInstance().setCanAutoLogin(activity, true);
	}
	
	public void userLogin(){
		CXPlatform.getInstance().login(getActivity(), new CxCallBack() {
			
			@Override
			public void loginSuccess(CxUserInfo user) {
				JSONObject data = new JSONObject();
				try {
					data.put(UID, user.getUser_id());
					data.put(TOKEN, user.getToken());
					data.put(UNAME, user.getUsername());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				dispatchData(EVENT_LOGIN,data);
			}
			
			@Override
			public void loginError(int errorCode, String errorMessage) {
				dispatchError(EVENT_LOGIN, "login fail:"+errorCode+":"+errorMessage);
			}
		});
	}
	
	public void userLogout(){
		CXPlatform.getInstance().logout(getActivity(), new CxCallBack() {
			@Override
			public void logoutSuccess() {
				dispatchData(EVENT_LOGOUT);
			}
			
			@Override
			public void logoutError(int errorCode, String errorMessage) {
				dispatchError(EVENT_LOGOUT, "logout fail:"+errorCode+":"+errorMessage);
			}
		});
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		CXPlatform.getInstance().pay(getActivity(), pay.optString(GAMESVR,"1"), pay.optString(PNAME), pay.optInt(AMOUNT)/100.00f, pay.optString(EXT), new CxCallBack() {
			@Override
			public void paymentScuess() {
				dispatchData(EVENT_PAY);
			}
			
			@Override
			public void paymentCancel() {
				dispatchError(EVENT_PAY, CODE_ERR_CANCEL, "cancel");
			}
			
			@Override
			public void paymentError(String errorString) {
				dispatchError(EVENT_PAY, "pay fail:"+errorString);
			}
			
		});
	}
	
	public void createRole() throws JSONException{
		JSONObject role = getJsonData();
		CXPlatform.getInstance().createRole(getActivity(), role.optString("roleName"), role.optInt("serverID"));
	}
	
	public void enterGame() throws JSONException{
		JSONObject role = getJsonData();
		CXPlatform.getInstance().enterGame(getActivity(), role.optString("roleName"), role.optInt("serverID"));
	}
	
	@Override
	protected void onResume() {
		BallServiceManager.showBall(getActivity());
	}
	
	@Override
	protected void onPause() {
		BallServiceManager.hideBall(getActivity());
	}
	
	@Override
	public void dispose() {
		BallServiceManager.destoryBall(getActivity());
	}
}
