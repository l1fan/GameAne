package com.l1fan.ane.gfan;

import org.json.JSONException;
import org.json.JSONObject;

import com.l1fan.ane.SDKContext;
import com.mappn.sdk.Gfan;
import com.mappn.sdk.gfanpay.GfanPay.Listener;
import com.mappn.sdk.gfanpay.GfanPayResult;
import com.mappn.sdk.init.InitControl;
import com.mappn.sdk.uc.LoginControl;
import com.mappn.sdk.uc.LoginResult;

public class SDK extends SDKContext {

	public void init(){
		Gfan.init(getActivity(), new InitControl.Listener() {
			
			@Override
			public void onComplete() {
				dispatchData(EVENT_INIT);
			}
		});
		
	}
	
	public void userLogin(){
		Gfan.login(getActivity(), new LoginControl.Listener() {
			
			@Override
			public void onComplete(LoginResult result) {
				switch (result.getLoginType()) {
				case Common:
				case Quick:
					JSONObject data = new JSONObject();
					try {
						data.put(UID, result.getUserId());
						data.put(UNAME, result.getUserName());
						data.put(TOKEN, result.getToken());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN, data);
					break;					
				default:
					break;
				}
			}
		});
	}
	
	@Override
	public boolean isSupportUserLogout() {
		return false;
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		String pname = pay.optString(PNAME);
		String pdesc = pay.optString(PDESC,pname);
		int price = pay.optInt(AMOUNT)/10;
		String orderId = pay.optString(ORDER_ID);
		
		Gfan.pay(getActivity(),orderId, price, pname, pdesc, pay.optString(EXT), new Listener() {
			
			@Override
			public void onComplete(GfanPayResult result) {
				switch (result.getStatusCode()) {
				case Success:
					dispatchData(EVENT_PAY);
					break;
				case UserBreak:
					dispatchError(EVENT_PAY, CODE_ERR_CANCEL,"pay cancel");
					break;
				case Fail:
					dispatchError(EVENT_PAY, "pay failed");
					break;
				default:
					break;
				}
			}
		});
	}
}
