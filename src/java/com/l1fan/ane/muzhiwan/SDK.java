package com.l1fan.ane.muzhiwan;

import org.json.JSONException;
import org.json.JSONObject;

import com.l1fan.ane.SDKContext;
import com.muzhiwan.sdk.core.MzwSdkController;
import com.muzhiwan.sdk.core.callback.MzwInitCallback;
import com.muzhiwan.sdk.core.callback.MzwLoignCallback;
import com.muzhiwan.sdk.core.callback.MzwPayCallback;
import com.muzhiwan.sdk.service.MzwOrder;

public class SDK extends SDKContext {

	public void init() throws JSONException{
		
		JSONObject init = getJsonData();
		int ori = init.optInt(ORIENTATION,MzwSdkController.ORIENTATION_VERTICAL);
		
		MzwSdkController.getInstance().init(getActivity(), ori, new MzwInitCallback() {
			
			@Override
			public void onResult(int code, String msg) {
				if (code == 1) {
					dispatchData(EVENT_INIT);
				}else{
					dispatchError(EVENT_INIT, "init fail:"+msg);
				}
			}
		});
	}
	
	public void userLogin(){
		MzwSdkController.getInstance().doLogin(new MzwLoignCallback() {
			
			@Override
			public void onResult(int code, String msg) {
				if (code == 1) {
					JSONObject data = new JSONObject();
					try {
						data.put(TOKEN, msg);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN, data);
				}else{
					dispatchError(EVENT_LOGIN, "login fail: "+msg+":code"+code);
				}
			}
		});
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		MzwOrder order = new MzwOrder();
		order.setProductname(pay.optString(PNAME));
		order.setProductdesc(pay.optString("description",order.getProductname()));
		order.setProductid(pay.optString(PID));
		order.setMoney(pay.optInt(AMOUNT)/100);
		order.setExtern(pay.optString(EXT, pay.optString(ORDER_ID)));
		MzwSdkController.getInstance().doPay(order,new MzwPayCallback() {
			
			@Override
			public void onResult(int code, MzwOrder order) {
				if (code == 1) {
					dispatchData(EVENT_PAY);
				}else{
					dispatchError(EVENT_PAY, "pay failed");
				}
			}
		});
	}
	
	public void userLogout(){
		MzwSdkController.getInstance().doLogout();
		dispatchData(EVENT_LOGOUT);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		MzwSdkController.getInstance().destory();
	}
}
