package com.l1fan.ane.m8868;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import cn.jugame.sdk.ISDKCallbackListener;
import cn.jugame.sdk.JugameSDK;
import cn.jugame.sdk.SDKOrientation;
import cn.jugame.sdk.SDKStatusCode;
import cn.jugame.sdk.entity.GameParams;
import cn.jugame.sdk.entity.vo.OrderInfo;
import cn.jugame.sdk.entity.vo.PaymentInfo;

import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {

	public void init() throws JSONException{
		JSONObject jd = getJsonData();
		Bundle md = getMetaData();
		
		GameParams params = new GameParams();
		params.setCpId(jd.optInt(CPID,md.getInt(CPID)));
		params.setGameId(jd.optInt(APPID,jd.optInt("gameId",md.getInt("gameId"))));
		
		boolean debugMode = jd.optBoolean(DEBUGMODE,md.getBoolean(DEBUGMODE));
		SDKOrientation ori = jd.optInt(ORIENTATION,md.getInt(ORIENTATION,1)) == 1 ? SDKOrientation.PORTRAIT : SDKOrientation.LANDSCAPE;
		JugameSDK.getInstance().initSDK(getActivity(), debugMode, ori, params, new ISDKCallbackListener<String>() {
			
			@Override
			public void callback(int code, String data) {
				if (code == SDKStatusCode.SUCCESS) {
					dispatchData(EVENT_INIT);
				}else{
					dispatchError(EVENT_INIT, "init fail:"+data);
				}
			}
		});
	}
	
	public void userLogin(){
		JugameSDK.getInstance().login(getActivity(), new ISDKCallbackListener<String>() {
			
			@Override
			public void callback(int code, String data) {
				if (code == SDKStatusCode.SUCCESS) {
					JSONObject login = new JSONObject();
					try {
						login.put(TOKEN, data);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN, login);
				}else{
					dispatchError(EVENT_LOGIN, "login fail:"+code+":"+data);
				}
			}
		});
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		PaymentInfo info = new PaymentInfo();
		info.setPayInfo(pay.optString(PNAME));
		info.setRoleId(pay.optString("roleId","1"));
		info.setServerId(pay.optInt("serverId",1));
		info.setExt(pay.optString(EXT)+"|"+pay.optString(ORDER_ID));
		info.setAmount(pay.optDouble(AMOUNT)/100);
		
		JugameSDK.getInstance().pay(getActivity(), info, new ISDKCallbackListener<OrderInfo>() {
			
			@Override
			public void callback(int code, OrderInfo order) {
				if (code == SDKStatusCode.SUCCESS) {
					dispatchData(EVENT_PAY);
				}else{
					dispatchError(EVENT_PAY, "pay fail:"+code);
				}
			}
		});
	}
}
