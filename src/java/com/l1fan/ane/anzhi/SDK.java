package com.l1fan.ane.anzhi;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.anzhi.usercenter.sdk.AnzhiUserCenter;
import com.anzhi.usercenter.sdk.inter.AnzhiCallback;
import com.anzhi.usercenter.sdk.inter.AzOutGameInter;
import com.anzhi.usercenter.sdk.inter.InitSDKCallback;
import com.anzhi.usercenter.sdk.inter.KeybackCall;
import com.anzhi.usercenter.sdk.item.CPInfo;
import com.anzhi.usercenter.sdk.item.UserGameInfo;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext implements AnzhiCallback {

	private String uname;
	private String uid;

	public void init() throws JSONException{
    	CPInfo info = new CPInfo();
    	JSONObject data = getJsonData();
    	Bundle md = getMetaData();
    	info.setAppKey(data.optString(APPKEY,md.getString(APPKEY)));
    	info.setSecret(data.optString(APPSECRET,md.getString(APPSECRET)));
    	info.setGameName(data.optString(APPNAME,getAppName()));
    	info.setChannel("AnZhi");
    	
    	AnzhiUserCenter anzhi = AnzhiUserCenter.getInstance();
    	anzhi.azinitSDK(getActivity(), info, new InitSDKCallback() {

			@Override
			public void initSdkCallcack() {
				dispatchData(EVENT_INIT);
			}
		}, new AzOutGameInter() {
			
			@Override
			public void azOutGameInter(int arg) {

	            switch (arg) {
	            case AzOutGameInter.KEY_OUT_GAME:// 退出游戏
	                AnzhiUserCenter.getInstance().removeFloaticon(getActivity());
	                dispatchData(EVENT_LOGOUT);
	                break;
	            case AzOutGameInter.KEY_CANCEL: // 取消退出游戏
	                break;
	            default:
	                break;
	            }
			}
		});

    	/**0 横屏,1 竖屏,4 根据物理感应来选择方向 */
    	anzhi.setActivityOrientation(data.optInt(ORIENTATION,md.getInt(ORIENTATION,1)));
    	anzhi.setOpendTestLog(data.optBoolean(SDKLOG,md.getBoolean(SDKLOG,false)));
    	anzhi.setKeybackCall(new KeybackCall() {
			
			@Override
			public void KeybackCall(String arg) {
				System.out.println("key back is:"+arg);
				if ("gamePay".equals(arg)) {
					dispatchError(EVENT_PAY, CODE_ERR_CANCEL, "cancel");
				}
			}
		});
    	anzhi.setCallback(this);
    }
    
	@Override
	public void onCallback(CPInfo cpInfo, String result) {
		try {
			JSONObject json = new JSONObject(result);
			String key = json.optString("callback_key");
			if ("key_pay".equals(key)) {
				System.out.println("keypay json :"+json);
				int code = json.optInt("code"); 
				if (code == 200) {
					dispatchData(EVENT_PAY);
				}else {
					dispatchError(EVENT_PAY, json.optString("desc"));
				}
			}else if("key_logout".equals(key)){
				dispatchData(EVENT_LOGOUT);
			}else if ("key_login".equals(key)) {
				int code = json.optInt("code");
				if (code == 200) {
					JSONObject data = new JSONObject();
					uid = json.getString("uid");
					data.put(UID, uid);
					data.put(TOKEN, json.getString("sid"));
					uname = json.getString("login_name");
					data.put(UNAME, uname);
					dispatchData(EVENT_LOGIN, data);
				}else{
					dispatchError(EVENT_LOGIN, json.optString("desc"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void submitGameInfo() throws JSONException{
		JSONObject role = getJsonData();
		UserGameInfo info = new UserGameInfo();
		info.setNickName(uname);
		info.setUid(uid);
		info.setAppName(getAppName());
		info.setGameArea(role.optString(GAMESVR));
		info.setGameLevel(role.optString(ROLELEVEL));
		info.setUserRole(role.optString(ROLENAME));
		info.setMemo(role.optString("memo","sb"));
		AnzhiUserCenter.getInstance().submitGameInfo(getActivity(), info);
	}
    
	public void toolBarHide(){
		AnzhiUserCenter.getInstance().dismissFloaticon();
	}
	
	public void toolBarShow(){
		AnzhiUserCenter.getInstance().showFloaticon();
	}
    
    public void userLogin(){
    	AnzhiUserCenter.getInstance().login(getActivity(), true);
    }
    
    public void userLogout(){
    	AnzhiUserCenter.getInstance().logout(getActivity());
    }
    
    public void anzhi() throws Exception{
    	sdkcall(AnzhiUserCenter.getInstance());
    }
    
    public void pay() throws JSONException{
    	JSONObject json = getJsonData();
    	float price = (float) (json.optDouble(AMOUNT)/100.00);
    	String payDesc = json.optString(PNAME);
    	String cbInfo = json.optString(ORDER_ID);
    	AnzhiUserCenter.getInstance().pay(getActivity(), 0, price, payDesc, cbInfo);
    }


}
