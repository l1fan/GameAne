package com.l1fan.ane.youku;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.l1fan.ane.SDKContext;
import com.youku.gamesdk.act.YKCallBack;
import com.youku.gamesdk.act.YKCallBackWithContext;
import com.youku.gamesdk.act.YKInit;
import com.youku.gamesdk.act.YKPlatform;
import com.youku.gamesdk.data.Bean;
import com.youku.gamesdk.data.YKGameUser;
import com.youku.gamesdk.data.YKPayBean;

public class SDK extends SDKContext {
	
	public void init(){
		new YKInit(getActivity()).init(new YKCallBack() {
			
			@Override
			public void onSuccess(Bean paramBean) {
				dispatchData(EVENT_INIT);
			}
			
			@Override
			public void onFailed(String paramString) {
				//初始化失败failReason为失败原因,sdk初始化失败建议可以继续进行游戏的初始化
				dispatchData(EVENT_INIT);
			}
		});
	}
	
	public void userLogin(){
		YKPlatform.autoLogin(new YKCallBack() {
			
			@Override
			public void onSuccess(Bean bean) {
				YKGameUser user = (YKGameUser) bean;
				String token = user.getSession();
				String uname = user.getUserName();
				JSONObject data = new JSONObject();
				try {
					data.put(TOKEN, token);
					data.put(UNAME, uname);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dispatchData(EVENT_LOGIN, data);
				
				showToolbar();
			}
			
			@Override
			public void onFailed(String msg) {
				dispatchError(EVENT_LOGIN, "login fail:"+msg);
			}
			
			
		}, getActivity());
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		YKPayBean b = new YKPayBean();
		b.setAmount(pay.optString(AMOUNT));
		b.setAppOrderId(pay.optString(ORDER_ID));
		b.setNotifyUri(pay.optString(NOTIFY_URL));
		b.setProductId(pay.optString(PID));
		b.setProductName(pay.optString(PNAME));
		b.setAppExt1(pay.optString(EXT));
		
		YKPlatform.doPay(getActivity(), b, new YKCallBack() {
			
			@Override
			public void onSuccess(Bean paramBean) {
				dispatchData(EVENT_PAY);
			}
			
			@Override
			public void onFailed(String message) {
				dispatchError(EVENT_PAY, message);
			}
		});
	}
	
	public void userLogout() {
		YKPlatform.logout(getActivity());
		dispatchData(EVENT_LOGOUT);
		hideToolbar();
	}
	
	public void exit() {
		YKPlatform.quit(getActivity(), new YKCallBack() {
			
			@Override
			public void onSuccess(Bean paramBean) {
				dispatchData(EVENT_EXIT);
			}
			
			@Override
			public void onFailed(String paramString) {
				
			}
		});
	}
	
	public void showToolbar(){
		YKPlatform.startYKFloat(getActivity(), new YKCallBackWithContext() {
			
			@Override
			public void callback(Context context) {
				YKPlatform.logout(context);
				dispatchData(EVENT_LOGOUT);
			}
		});
	}
	
	public void hideToolbar(){
		YKPlatform.closeYKFloat(getActivity());
	}
}
