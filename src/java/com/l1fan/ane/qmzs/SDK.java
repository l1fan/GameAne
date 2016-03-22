package com.l1fan.ane.qmzs;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

import com.l1fan.ane.SDKContext;
import com.qmyxzs.sdk.QMCode;
import com.qmyxzs.sdk.QMGameSdk;
import com.qmyxzs.sdk.iface.IResponse;
import com.qmyxzs.sdk.info.GameBill;
import com.qmyxzs.sdk.info.OrderInfo;
import com.qmyxzs.sdk.info.UserInfo;

public class SDK extends SDKContext {

	public void init(){
		//QMGameSdk.getInstance().setSdkParams(arg0, arg1, arg2, arg3, arg4, arg5);
		QMGameSdk.getInstance().init(getActivity(), new IResponse<String>() {
			
			@Override
			public void onResponse(int code, String msg, String data) {
				switch (code) {
				case QMCode.CODE_INIT_SUCCESS:
					dispatchData(EVENT_INIT);
					break;
				case QMCode.CODE_INIT_FAILED:
					dispatchError(EVENT_INIT, msg);
					break;
				default:
					break;
				}
			}
		});
		
		//true is landscape ,false is portrait
		QMGameSdk.getInstance().setOrientation(false); 
		
		QMGameSdk.getInstance().setInternalLoginResponse(new IResponse<UserInfo>() {
			
			@Override
			public void onResponse(int code, String msg, UserInfo data) {
				switch (code) {
				case QMCode.CODE_LOGIN_SUCCESS:
					dispatchData(EVENT_LOGOUT);
					break;
				case QMCode.CODE_LOGIN_FAILED:
				case QMCode.CODE_UNINIT:
				case QMCode.CODE_UNLOGIN:
					dispatchError(EVENT_LOGIN, msg);
					break;
				default:
					break;
				}
			}
		});
		
	}
	
	public void userLogin(){
		QMGameSdk.getInstance().login(new IResponse<UserInfo>() {
			
			@Override
			public void onResponse(int code, String msg, UserInfo data) {
				switch (code) {
				case QMCode.CODE_LOGIN_SUCCESS:
					if (data != null) {
						JSONObject login = new JSONObject();
						try {
							login.put(UID, data.getAccountId());
							login.put(TOKEN, data.getsId());
							login.put(UNAME, data.getQmId());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dispatchData(EVENT_LOGIN, login);
						QMGameSdk.getInstance().openFloatView();
					}else{
						dispatchError(EVENT_LOGIN, "data is null:"+msg);
					}
					break;
				case QMCode.CODE_LOGIN_FAILED:
				case QMCode.CODE_UNINIT:
				case QMCode.CODE_UNLOGIN:
					dispatchError(EVENT_LOGIN, msg);
					break;
				default:
					break;
				}
			}
		});
	}
	
	public void switchAccount(){
		QMGameSdk.getInstance().switchAccount(new IResponse<UserInfo>() {
			
			@Override
			public void onResponse(int code, String msg, UserInfo data) {
				switch (code) {
				case QMCode.CODE_LOGIN_SUCCESS:
					dispatchData(EVENT_LOGOUT);
					break;
				case QMCode.CODE_LOGIN_FAILED:
				case QMCode.CODE_UNINIT:
				case QMCode.CODE_UNLOGIN:
					dispatchError(EVENT_LOGIN, msg);
					break;
				default:
					break;
				}
			}
		});
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		GameBill bill = new GameBill();
		bill.setBillId(pay.optString(ORDER_ID));
		bill.setAmount(pay.optInt(AMOUNT)/100.00);
		bill.setBillTitle(pay.optString(PNAME));
		bill.setBillMsg(pay.optString(PDESC,bill.getBillTitle()));
		bill.setCallbackInfo(pay.optString(EXT));
		bill.setNotifyUrl(pay.optString(NOTIFY_URL));
		
		QMGameSdk.getInstance().pay(bill, new IResponse<OrderInfo>() {
			
			@Override
			public void onResponse(int code, String msg, OrderInfo order) {
				switch (code) {
				case QMCode.CODE_PAY_SUCCESS:
					dispatchData(EVENT_PAY);
					break;
				default:
					dispatchError(EVENT_PAY, msg);
					break;
				}
			}
		});
	}
	
	public void toolBarShow() {
		QMGameSdk.getInstance().openFloatView();
	}
	
	public void toolBarHide() {
		QMGameSdk.getInstance().closeFloatView();
	}
	
	@Override
	public void dispose() {
		QMGameSdk.getInstance().destroy();
	}
}
