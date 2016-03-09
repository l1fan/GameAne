package com.l1fan.ane.tt;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.l1fan.ane.SDKContext;
import com.yiyou.gamesdk.container.TTGameSDK;
import com.yiyou.gamesdk.outer.IOperateCallback;
import com.yiyou.gamesdk.outer.consts.TTCodeDef;
import com.yiyou.gamesdk.outer.model.GameParamInfo;
import com.yiyou.gamesdk.outer.model.OrderInfo;
import com.yiyou.gamesdk.outer.model.PaymentInfo;
import com.yiyou.gamesdk.outer.util.Log;
import com.yiyou.gamesdk.outer.util.StringUtils;

public class SDK extends SDKContext {

	public void init() throws JSONException{
		regLifecycle();
		
		JSONObject jd = getJsonData();
		Bundle md = getMetaData();
		
		GameParamInfo info = new GameParamInfo();
		info.setCpId(jd.optString(CPID,md.getString(CPID)));
		info.setGameId(jd.optInt(APPID,md.getInt(APPID)));
		
		int ori = jd.optInt(ORIENTATION,md.getInt(ORIENTATION,1));
		boolean debug = jd.optBoolean(DEBUGMODE,md.getBoolean(DEBUGMODE,false));
		
		TTGameSDK.defaultSDK().initSDK(getActivity(), ori, Log.LogLevel.Verbose,debug , info, new IOperateCallback<String>() {
			
			@Override
			public void onResult(int code, String result) {
				if (code == TTCodeDef.SUCCESS) {
					dispatchData(EVENT_INIT);
				}else{
					dispatchError(EVENT_INIT, "init fail:"+result);
				}
			}
		});
		
		TTGameSDK.defaultSDK().setLogoutNotifyListener(new IOperateCallback<String>() {
			
			@Override
			public void onResult(int code, String result) {
				if (code == TTCodeDef.SUCCESS) {
					dispatchData(EVENT_LOGOUT);
				}
			}
		});
	}
	
	public void userLogin(){
		TTGameSDK.defaultSDK().login(getActivity(), new IOperateCallback<String>() {
			
			@Override
			public void onResult(int code, String result) {
				if (code == TTCodeDef.SUCCESS) {
					JSONObject data = new JSONObject();
					TTGameSDK sdk = TTGameSDK.defaultSDK();
					try {
						data.put(UID, sdk.getUid());
						data.put(TOKEN, sdk.getSession());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN,data);
					TTGameSDK.defaultSDK().createFloatButton(getActivity());
				}else{
					dispatchError(EVENT_LOGIN, "login fail:"+result+":"+code);
				}
			}
		}, getAppName());
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		PaymentInfo info = new PaymentInfo();
		info.setCpFee(pay.optInt(AMOUNT)/100.00f);
		info.setCpOrderId(pay.optString(ORDER_ID));
		info.setServerId(pay.optString(GAMESVR,"1"));
		info.setExInfo(pay.optString(EXT));
		info.setSubject(pay.optString(PNAME));
		info.setBody(pay.optString(PDESC,info.getSubject()));
		info.setPayMethod(PaymentInfo.PAY_METHOD_ALL);
		info.setCpCallbackUrl(pay.optString(NOTIFY_URL));
		
		TTGameSDK.defaultSDK().pay(info, getActivity(), new IOperateCallback<OrderInfo>() {
			
			@Override
			public void onResult(int code, OrderInfo result) {
				if (code == TTCodeDef.SUCCESS) {
					dispatchData(EVENT_PAY);
				}else{
					dispatchError(EVENT_PAY, "pay fail:"+code);
				}
			}
		});
	}
	
	public void submitGameRoleInfo() throws JSONException{
		JSONObject role = getJsonData();
		TTGameSDK.defaultSDK().submitGameRoleInfo(role.optString("serverName"), role.optString("roleID"), role.optString("roleName"),role.optInt("roleLevel"));
	}
	
	public void userLogout(){
		TTGameSDK.defaultSDK().logout();
	}
	
	@Override
	protected void onResume() {
		if (!StringUtils.isBlank(TTGameSDK.defaultSDK().getSession())) {
				TTGameSDK.defaultSDK().createFloatButton(getActivity());
		}
	}
	
	@Override
	protected void onPause() {
	    TTGameSDK.defaultSDK().destroyFloatButton(getActivity());
	}
	
	@Override
	public void dispose() {
		TTGameSDK.defaultSDK().exitSDK();
	}
}
