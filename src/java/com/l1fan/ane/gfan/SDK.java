package com.l1fan.ane.gfan;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.l1fan.ane.SDKContext;
import com.mappn.sdk.pay.GfanPay;
import com.mappn.sdk.pay.GfanPayCallback;
import com.mappn.sdk.pay.GfanPayInitCallback;
import com.mappn.sdk.pay.model.Order;
import com.mappn.sdk.uc.GfanUCCallback;
import com.mappn.sdk.uc.GfanUCenter;
import com.mappn.sdk.uc.User;

public class SDK extends SDKContext {

	public void init(){
		Activity activity = getActivity();
		GfanPay.getInstance(activity.getApplicationContext()).init(activity, new GfanPayInitCallback() {
			
			@Override
			public void onSuccess() {
				dispatchData(EVENT_INIT);
			}
			
			@Override
			public void onError() {
				dispatchError(EVENT_INIT, "init failed");
			}
		});;
	}
	
	public void userLogin(){
		GfanUCenter.login(getActivity(), new GfanUCCallback() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onSuccess(User user, int type) {
				if (type == GfanUCenter.RETURN_TYPE_LOGIN) {
					JSONObject data = new JSONObject();
					try {
						data.put(UID, user.getUid());
						data.put(UNAME, user.getUserName());
						data.put(TOKEN, user.getToken());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN, data);
					
				}
			}
			
			@Override
			public void onError(int paramInt) {
				dispatchError(EVENT_LOGIN, "login failed");
			}

		});
	}
	
	public void userLogout(){
		GfanUCenter.logout(getActivity());
		dispatchData(EVENT_LOGOUT);
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		String pname = pay.optString(PNAME);
		String pdesc = pay.optString(PDESC,pname);
		int price = pay.optInt(AMOUNT)/10;
		String orderId = pay.optString(ORDER_ID);
		Order order = new Order(pname, pdesc, price, orderId);
		
		GfanPay.getInstance(getActivity()).pay(order, new GfanPayCallback() {
			
			@Override
			public void onSuccess(User user, Order order) {
				dispatchData(EVENT_PAY, "number is:"+order.getNumber());
			}
			
			@Override
			public void onError(User user) {
				dispatchError(EVENT_PAY,"pay failed");
			}
		});
	}
}
