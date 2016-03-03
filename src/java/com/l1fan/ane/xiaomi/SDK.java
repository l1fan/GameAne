package com.l1fan.ane.xiaomi;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.l1fan.ane.SDKContext;
import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.MiErrorCode;
import com.xiaomi.gamecenter.sdk.OnLoginProcessListener;
import com.xiaomi.gamecenter.sdk.OnPayProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiAccountInfo;
import com.xiaomi.gamecenter.sdk.entry.MiAppInfo;
import com.xiaomi.gamecenter.sdk.entry.MiAppType;
import com.xiaomi.gamecenter.sdk.entry.MiBuyInfoOnline;

public class SDK extends SDKContext {

	public void init() throws JSONException {
		MiAppInfo appInfo = new MiAppInfo();
		JSONObject json = getJsonData();
		Bundle md = getMetaData();
		appInfo.setAppId(json.optString(APPID,md.getString(APPID,"")).replace("mi.", ""));
		appInfo.setAppKey(json.optString(APPKEY,md.getString(APPKEY,"")).replace("mi.", ""));
		appInfo.setAppType(MiAppType.online); // 网游
		MiCommplatform.Init(getActivity(), appInfo);
		dispatchData(EVENT_INIT);
	}

	public void userLogin() {
		MiCommplatform.getInstance().miLogin(getActivity(), new OnLoginProcessListener() {

			@Override
			public void finishLoginProcess(int code, MiAccountInfo arg1) {
				switch (code) {
				case MiErrorCode.MI_XIAOMI_GAMECENTER_SUCCESS:
					JSONObject json = new JSONObject();
					try {
						json.put(UID, arg1.getUid());
						json.put(TOKEN, arg1.getSessionId());
						json.put("nikename", arg1.getNikename());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN,json);
					break;
				case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_CANCEL:
					dispatchError(EVENT_LOGIN, CODE_ERR_CANCEL,"login cancel");
					break;
				case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_LOGIN_FAIL:
				case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_ACTION_EXECUTED:
				default:
					dispatchError(EVENT_LOGIN, "login fail ["+code+"]");
					break;
				}
			}
		});
	}

	public void pay() throws JSONException{
		MiBuyInfoOnline online = new MiBuyInfoOnline();
		JSONObject json = getJsonData();
		online.setCpOrderId(json.optString(ORDER_ID)); //订单号唯一(不为空)
		online.setCpUserInfo(json.optString(EXT)); //此参数在用户支付成功后会透传给CP的服务器
		online.setMiBi(json.optInt(AMOUNT)/100); //必须是大于1的整数, 10代表10米币,即10元人民币(不为空)

		Bundle mBundle = new Bundle();
		//mBundle.putString(GameInfoField.GAME_USER_BALANCE, "1000" );  //用户余额
		//mBundle.putString(GameInfoField.GAME_USER_GAMER_VIP, "vip0");  //vip 等级
//		mBundle.putString(GameInfoField.GAME_USER_LV, "20");          //角色等级
//		mBundle.putString(GameInfoField.GAME_USER_PARTY_NAME, "猎人");  //工会，帮派
//		mBundle.putString(GameInfoField.GAME_USER_ROLE_NAME, "meteor"); //角色名称
//		mBundle.putString(GameInfoField.GAME_USER_ROLEID, "123456");   //角色id
//		mBundle.putString(GameInfoField.GAME_USER_SERVER_NAME, "峡谷");  //所在服务器
		MiCommplatform.getInstance().miUniPayOnline(getActivity(), online, mBundle, new OnPayProcessListener(){
		    @Override
		    public void finishPayProcess(int code) {
		        switch(code) {
		        case MiErrorCode.MI_XIAOMI_GAMECENTER_SUCCESS:
		             dispatchData(EVENT_PAY);
		             break;
		        case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_PAY_CANCEL:
		        case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_PAY_FAILURE:
		        case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_ACTION_EXECUTED:
		        default :
		        	 dispatchError(EVENT_PAY, "pay fail ["+code+"]");
		             break;
		        }
		    }
		});
	}
}
