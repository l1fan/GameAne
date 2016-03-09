package com.l1fan.ane.m4399;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import cn.m4399.operate.OperateCenter;
import cn.m4399.operate.OperateCenter.OnLoginFinishedListener;
import cn.m4399.operate.OperateCenter.OnQuitGameListener;
import cn.m4399.operate.OperateCenter.OnRechargeFinishedListener;
import cn.m4399.operate.OperateCenterConfig;
import cn.m4399.operate.OperateCenterConfig.PopLogoStyle;
import cn.m4399.operate.OperateCenterConfig.PopWinPosition;
import cn.m4399.operate.User;

import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {

	private OperateCenter mOpeCenter;

	public void init() throws JSONException{
		Activity activity = getActivity();
		JSONObject init = getJsonData();
		Bundle md = getMetaData();
		mOpeCenter = OperateCenter.getInstance();
		OperateCenterConfig opeConfig = new OperateCenterConfig.Builder(activity)
		    .setGameKey(init.optString(APPKEY,md.getString(APPKEY)).replace("m.", ""))
		    .setDebugEnabled(init.optBoolean(DEBUGMODE,md.getBoolean(DEBUGMODE,false)))
		    .setOrientation(init.optInt(ORIENTATION,md.getInt(ORIENTATION,ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)))
		    .setSupportExcess(false)
		    .setPopLogoStyle(PopLogoStyle.POPLOGOSTYLE_ONE)
		    .setPopWinPosition(PopWinPosition.POS_LEFT).build();
		
		mOpeCenter.setConfig(opeConfig);

		mOpeCenter.init(activity,new OperateCenter.OnInitGloabListener() {
		    // 初始化结束执行后回调
		    @Override
		    public void onInitFinished(boolean isLogin, User userInfo) {
		        //assert(isLogin == mOpeCenter.isLogin());
		        dispatchData(EVENT_INIT);
		    }

		    // 注销帐号的回调， 包括个人中心里的注销和logout()注销方式
		    // fromUserCenter区分是否是从个人中心注销的，若是则为true，不是为false
		    @Override
		    public void onUserAccountLogout(boolean fromUserCenter, int resultCode) {
		    	dispatchData(EVENT_LOGOUT, ""+OperateCenter.getResultMsg(resultCode));
		    }

		    // 个人中心里切换帐号的回调
		    @Override
		    public void onSwitchUserAccountFinished(User userInfo) {
		    	loginSuc(userInfo);
		    }

		});

	}
	
	private void loginSuc(User userInfo){
    	JSONObject data = new JSONObject();
    	try {
			data.put(UID, userInfo.getUid());
			data.put(TOKEN, userInfo.getState());
			data.put(UNAME, userInfo.getName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	dispatchData(EVENT_LOGIN, data);
	}
	
	public void userLogin(){
		mOpeCenter.login(getActivity(), new OnLoginFinishedListener() {
			
			@Override
			public void onLoginFinished(boolean success, int resultCode,
					User userInfo) {
				if (success) {
					loginSuc(userInfo);
				}else{
					dispatchError(EVENT_LOGIN, ""+OperateCenter.getResultMsg(resultCode));
				}
			}
		});
	}
	
	public void switchAccount(){
		mOpeCenter.switchAccount(getActivity(), new OnLoginFinishedListener() {
			
			@Override
			public void onLoginFinished(boolean success, int resultCode,
					User userInfo) {
				if (success) {
					loginSuc(userInfo);
				}else{
					dispatchError(EVENT_LOGIN, ""+OperateCenter.getResultMsg(resultCode));
				}
			}
		});
	}
	
	public void userLogout(){
		mOpeCenter.logout();
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		mOpeCenter.recharge(getActivity(),pay.optInt(AMOUNT)/100, pay.optString(ORDER_ID), pay.optString(PNAME), new OnRechargeFinishedListener() {
			
			@Override
			public void onRechargeFinished(boolean success, int resultCode, String msg) {
				if (success) {
					dispatchData(EVENT_PAY);
				}else{
					dispatchError(EVENT_PAY, msg+":"+OperateCenter.getResultMsg(resultCode));
				}
			}
		});
	}
	
	public void exit(){
		mOpeCenter.shouldQuitGame(getActivity(), new OnQuitGameListener() {
			
			@Override
			public void onQuitGame(boolean shouldQuit) {
				if (shouldQuit) {
					dispatchData("M4399_EXIT");
				}else{
					dispatchError("M4399_EXIT", "no exit");
				}
			}
		});
	}
}
