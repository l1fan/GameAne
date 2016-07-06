package com.l1fan.ane.droi;

import org.json.JSONException;
import org.json.JSONObject;

import com.l1fan.ane.SDKContext;
import com.zhuoyou.pay.sdk.ZYGameManager;
import com.zhuoyou.pay.sdk.account.UserInfo;
import com.zhuoyou.pay.sdk.entity.PayParams;
import com.zhuoyou.pay.sdk.listener.IZYLoginCheckListener;
import com.zhuoyou.pay.sdk.listener.ZYInitListener;
import com.zhuoyou.pay.sdk.listener.ZYLoginListener;
import com.zhuoyou.pay.sdk.listener.ZYRechargeListener;

public class SDK extends SDKContext {

	protected String mToken;
	protected int mId;

	public void init(){
		dispatchData(EVENT_INIT);
	}
	
	public void userLogin(){
		ZYGameManager.login(getActivity(), new ZYLoginListener() {
			
			@Override
			public void logout() {
				dispatchData(EVENT_LOGOUT);
			}
			
			@Override
			public void login() {
				ZYGameManager.init(getActivity(), new ZYInitListener() {
					
					@Override
					public void iniSuccess(UserInfo uInfo) {
						JSONObject data = new JSONObject();
						try {
							mId =  uInfo.getOpenId();
							mToken = uInfo.getAccessToken();
							data.put(UID,mId);
							data.put(TOKEN, mToken);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						dispatchData(EVENT_LOGIN,data);						
					}
					
					@Override
					public void iniFail(String message) {
						dispatchError(EVENT_LOGIN, message);
					}
				});
				
				
			}
		}, ZYGameManager.LOIGN_THEME_PORTRAIT);
	}
	
	public void checkLogin(){
		ZYGameManager.loginCheck(getActivity(), mId, mToken, new IZYLoginCheckListener() {
			
			@Override
			public void checkResult(String code, String msg) {
				System.out.println("checkLogin:"+code);
				switch (code) {
				case "0":
					dispatchData("checkLogin",msg);
					break;
				default:
					dispatchError("checkLogin",msg);
					break;
				}
			}
		});
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		PayParams params = new PayParams();
		params.setAmount(pay.optInt(AMOUNT)/100);
		params.setOrderId(pay.optString(ORDER_ID));
		params.setExtraParam(pay.optString(EXT));
		params.setPropsName(pay.optString(PNAME));
		
		ZYGameManager.pay(params, getActivity(), new ZYRechargeListener() {
			
			@Override
			public void success(PayParams arg0, String arg1) {
				dispatchData(EVENT_PAY);
			}
			
			@Override
			public void fail(PayParams arg0, String errMsg) {
				dispatchError(EVENT_PAY, errMsg);
			}
		});
	}
	
	@Override
	public void dispose() {
		super.dispose();
		ZYGameManager.onDestroy(getActivity());
	}
	
}
