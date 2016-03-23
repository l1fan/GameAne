package com.l1fan.ane.pyw;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.l1fan.ane.SDKContext;
import com.pengyouwan.sdk.api.ISDKEventCode;
import com.pengyouwan.sdk.api.ISDKEventExtraKey;
import com.pengyouwan.sdk.api.OnSDKEventListener;
import com.pengyouwan.sdk.api.PYWPlatform;
import com.pengyouwan.sdk.api.SDKConfig;
import com.pengyouwan.sdk.api.User;
import com.pengyouwan.sdk.utils.FloatViewTool;

public class SDK extends SDKContext {
	
	public void init() throws JSONException{
		regLifecycle();
		JSONObject jd = getJsonData();
		Bundle md = getMetaData();
		
		SDKConfig config = new SDKConfig();
		config.setGameKey(jd.optString(APPKEY,jd.optString("gameKey",md.getString("gameKey"))));
		config.setIsFullScreen(true);
		config.setActivityOrientation(jd.optInt(ORIENTATION,md.getInt(ORIENTATION,1)));
		config.allowChangeAccount(jd.optBoolean("allowChangeAccount",md.getBoolean("allowChangeAccount",true)));
		config.setRebootOnChangeAccount(false); // 切换账号时是否需要重启游戏
		
		PYWPlatform.initSDK(getActivity(), config, new OnSDKEventListener() {
			
			@Override
			public void onEvent(int code, Bundle data) {
				String erroMsg;
				switch (code) {
				case ISDKEventCode.CODE_LOGIN_SUCCESS:
					User user = (User) data.getSerializable(ISDKEventExtraKey.EXTRA_USER);
					JSONObject login = new JSONObject();
					try {
						login.put(UID, user.getUserId());
						login.put(TOKEN, user.getToken());
						login.put(UNAME, user.getUserName());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN, login);
					FloatViewTool.instance(getActivity()).showFloatView();
					break;
				case ISDKEventCode.CODE_CHANGE_ACCOUNT_SUCCESS:
					dispatchData(EVENT_LOGOUT);
					user = (User) data.getSerializable(ISDKEventExtraKey.EXTRA_USER);
					login = new JSONObject();
					try {
						login.put(UID, user.getUserId());
						login.put(TOKEN, user.getToken());
						login.put(UNAME, user.getUserName());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN, login);
					break;
				case ISDKEventCode.CODE_LOGIN_FAILD:
	                erroMsg = data.getString(ISDKEventExtraKey.EXTRA_ERRO);
					dispatchError(EVENT_LOGIN, "login fail:"+erroMsg);
					break;
				case ISDKEventCode.CODE_CHARGE_SUCCESS:
					dispatchData(EVENT_PAY);
					break;
				case ISDKEventCode.CODE_CHARGE_FAIL:
	                erroMsg = data.getString(ISDKEventExtraKey.EXTRA_ERRO);
					dispatchError(EVENT_PAY, "pay fail:"+erroMsg);
					break;
				default:
					break;
				}
			}
		});
		
        PYWPlatform.setDebug(jd.optBoolean(DEBUGMODE,md.getBoolean(DEBUGMODE,false)));
        dispatchData(EVENT_INIT);
	}
	
	@Override
	protected void onResume() {
		FloatViewTool.instance(getActivity()).showFloatView();
	}
	
	@Override
	protected void onPause() {
		FloatViewTool.instance(getActivity()).hideFloatView();
	}
	
	public void openUsercenter(){
		PYWPlatform.openUsercenter(getActivity());
	}
	
	public void userCenter(){
		PYWPlatform.openUsercenter(getActivity());
	}
	
	public void userLogin(){
		PYWPlatform.openLogin(getActivity());
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		float money = pay.optInt(AMOUNT)/100.00f;		
		long gameCurrency = pay.optLong("gameCurrency", 0);
        JSONObject params = new JSONObject();
        params.put("order_id", pay.optString(ORDER_ID));
        params.put("product_id", pay.optString(PID));
        params.put("product_desc", pay.optString(PNAME));
		PYWPlatform.openChargeCenter(getActivity(), money, gameCurrency, params.toString(),true);
	}
}
